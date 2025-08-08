package app.video.download.boardExport.service;


import app.video.download.UGC.domain.UGC;
import app.video.download.board.domain.Board;
import app.video.download.boardExport.controller.port.BoardExportService;
import app.video.download.boardExport.controller.request.BoardExportRequest;
import app.video.download.boardExport.controller.response.BoardExportResponse;
import app.video.download.boardExport.domain.BoardExport;
import app.video.download.boardExport.service.port.BoardExportRepository;
import app.video.download.boardExport.service.port.BoardRepository;
import app.video.download.boardExport.service.port.UGCRepository;
import app.video.download.boardExport.service.port.VideoTaskRepository;
import app.video.download.global.domain.Status;
import app.video.download.global.error.ErrorCode;
import app.video.download.global.exception.CustomException;
import app.video.download.global.port.S3Service;
import app.video.download.global.port.VideoProcessingService;
import app.video.download.member.domain.Member;
import app.video.download.videoTask.domain.VideoTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardExportServiceImpl implements BoardExportService {
    
    private final VideoTaskRepository videoTaskRepository;
    private final BoardRepository boardRepository;
    private final BoardExportRepository boardExportRepository;
    private final VideoProcessingService videoProcessingService;
    private final S3Service s3Service;
    private final UGCRepository ugcRepository;

    @Override
    public BoardExportResponse exportBoardVideos(Long boardId, BoardExportRequest request, Member member) {
        log.info("Starting board export for boardId: {}, member: {}", boardId, member.getId());
        
        // Validate board exists and belongs to member
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        
        if (!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.SC_FORBIDDEN);
        }
        
        // Get all completed videos from board (ordered by creation time)
        List<VideoTask> completedVideos = getCompletedVideosByBoardId(boardId);
        
        if (completedVideos.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        log.info("Found {} completed videos for export", completedVideos.size());
        
        try {
            // Get UGC records for the completed video tasks to get actual MP4 URLs
            List<UGC> ugcRecords = ugcRepository.findByVideoTaskIn(completedVideos);
            log.info("Found {} UGC records for {} video tasks", ugcRecords.size(), completedVideos.size());
            
            // Extract MP4 video URLs from UGC records
            List<String> videoUrls = ugcRecords.stream()
                    .map(UGC::getUrl)
                    .filter(url -> url != null && !url.trim().isEmpty()) // Filter out null/empty URLs
                    .collect(Collectors.toList());
            
            // Debug logging for UGC records
            for (int i = 0; i < ugcRecords.size(); i++) {
                UGC ugc = ugcRecords.get(i);
                log.info("UGC {}: ID={}, URL={}, VideoTaskID={}", 
                    i + 1, ugc.getId(), ugc.getUrl(), 
                    ugc.getVideoTask() != null ? ugc.getVideoTask().getId() : "null");
            }
            
            // Check if we have any valid video URLs after filtering
            if (videoUrls.isEmpty()) {
                log.error("No valid MP4 URLs found in UGC records");
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
            
            log.info("Valid MP4 URLs for concatenation: {}", videoUrls.size());
            
            // Concatenate videos
            String quality = request.getExportSettings().getQuality();
            MultipartFile concatenatedVideo = videoProcessingService.concatenateVideos(videoUrls, quality);
            
            // Upload to S3
            String videoUrl = s3Service.uploadFile(concatenatedVideo);
            
            // Calculate total duration (approximate)
            double totalDuration = completedVideos.size() * 2.0; // Assuming 2 seconds per video
            
            // Save export record
            BoardExport export = BoardExport.create(
                    boardId, 
                    videoUrl, 
                    totalDuration, 
                    concatenatedVideo.getSize(), 
                    member
            );
            boardExportRepository.save(export);
            
            log.info("Board export completed successfully. URL: {}", videoUrl);
            
            return BoardExportResponse.completed(videoUrl);
            
        } catch (Exception e) {
            log.error("Failed to export board videos: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<BoardExport> getExportHistory(Long boardId) {
        return boardExportRepository.findByBoardIdOrderByCreatedAtDesc(boardId);
    }
    
    private List<VideoTask> getCompletedVideosByBoardId(Long boardId) {
        // Get ALL video tasks for this board with COMPLETED status (no pagination limit)
        List<VideoTask> completedVideos = videoTaskRepository.findAllByBoardIdAndStatus(boardId, Status.COMPLETED);
        log.info("Found {} completed videos for board {}", completedVideos.size(), boardId);
        
        // Debug logging: print each video's details
        for (int i = 0; i < completedVideos.size(); i++) {
            VideoTask video = completedVideos.get(i);
            log.info("Video {}: ID={}, URL={}, createdAt={}", 
                i + 1, video.getId(), video.getImageUrl(), video.getCreatedAt());
        }
        
        return completedVideos;
    }
}