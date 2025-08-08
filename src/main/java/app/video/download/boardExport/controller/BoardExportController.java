package app.video.download.boardExport.controller;

import app.video.download.boardExport.controller.port.BoardExportService;
import app.video.download.boardExport.controller.request.BoardExportRequest;
import app.video.download.boardExport.controller.response.BoardExportResponse;
import app.video.download.boardExport.domain.BoardExport;
import app.video.download.global.dto.GlobalResponse;
import app.video.download.member.domain.Member;
import app.video.download.oauth.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardExportController {
    
    private final BoardExportService boardExportService;

    @PostMapping("/{boardId}/export")
    public GlobalResponse<BoardExportResponse> exportBoardVideos(
            @PathVariable Long boardId,
            @RequestBody BoardExportRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        Member member = principalDetails.user();
        BoardExportResponse response = boardExportService.exportBoardVideos(boardId, request, member);
        return GlobalResponse.success(response);
    }

    @GetMapping("/{boardId}/exports")
    @PreAuthorize("isAuthenticated()")
    public GlobalResponse<List<BoardExport>> getExportHistory(
            @PathVariable Long boardId
    ) {
        List<BoardExport> exports = boardExportService.getExportHistory(boardId);
        return GlobalResponse.success(exports);
    }
}