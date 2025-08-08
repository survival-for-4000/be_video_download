package app.video.download.global.infrastructure;

import app.video.download.global.error.ErrorCode;
import app.video.download.global.exception.CustomException;
import app.video.download.global.port.VideoProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VideoProcessingServiceImpl implements VideoProcessingService {

    @Override
    public MultipartFile extractLatestFrameFromVideo(String videoUrl) {
        log.info("Starting latest frame extraction from video - URL: {}", videoUrl);
        
        Path tempVideoFile = null;
        Path frameImageFile = null;
        
        try {
            // Step 1: Download video from URL
            log.info("Step 1: Downloading video from URL...");
            tempVideoFile = downloadVideo(videoUrl);
            log.info("✅ Video download successful: {} bytes", Files.size(tempVideoFile));
            
            // Step 2: Extract last frame
            log.info("Step 2: Extracting latest frame...");
            frameImageFile = extractLatestFrame(tempVideoFile);
            log.info("✅ Latest frame extraction successful: {} bytes", Files.size(frameImageFile));
            
            // Step 3: Convert to MultipartFile
            log.info("Step 3: Converting to MultipartFile...");
            byte[] imageBytes = Files.readAllBytes(frameImageFile);
            MultipartFile multipartFile = new CustomMultipartFile(
                "latest_frame.jpg",
                "latest_frame.jpg", 
                "image/jpeg",
                imageBytes
            );
            log.info("✅ MultipartFile created successfully: {} bytes", multipartFile.getSize());
            
            return multipartFile;
            
        } catch (IOException e) {
            log.error("❌ IO Error during video processing: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            log.error("❌ FFmpeg process interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("❌ Unexpected error during video processing: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Always clean up temporary files
            cleanupTempFiles(tempVideoFile, frameImageFile);
        }
    }

    @Override
    public MultipartFile extractFrameFromVideo(String videoUrl, double captureTimeSeconds) {
        log.info("Starting video frame extraction - URL: {}, captureTime: {}s", videoUrl, captureTimeSeconds);
        
        Path tempVideoFile = null;
        Path frameImageFile = null;
        
        try {
            // Step 1: Download video from URL
            log.info("Step 1: Downloading video from URL...");
            tempVideoFile = downloadVideo(videoUrl);
            log.info("✅ Video download successful: {} bytes", Files.size(tempVideoFile));
            
            // Step 2: Extract frame at specified time
            log.info("Step 2: Extracting frame at {}s...", captureTimeSeconds);
            frameImageFile = extractFrame(tempVideoFile, captureTimeSeconds);
            log.info("✅ Frame extraction successful: {} bytes", Files.size(frameImageFile));
            
            // Step 3: Convert to MultipartFile
            log.info("Step 3: Converting to MultipartFile...");
            byte[] imageBytes = Files.readAllBytes(frameImageFile);
            MultipartFile multipartFile = new CustomMultipartFile(
                "frame.jpg",
                "frame.jpg", 
                "image/jpeg",
                imageBytes
            );
            log.info("✅ MultipartFile created successfully: {} bytes", multipartFile.getSize());
            
            return multipartFile;
            
        } catch (IOException e) {
            log.error("❌ IO Error during video processing: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            log.error("❌ FFmpeg process interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("❌ Unexpected error during video processing: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Always clean up temporary files
            cleanupTempFiles(tempVideoFile, frameImageFile);
        }
    }
    
    private Path downloadVideo(String videoUrl) throws IOException {
        log.info("🔄 Downloading video from URL: {}", videoUrl);
        
        // Add null and empty check
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            log.error("❌ Video URL is null or empty: {}", videoUrl);
            throw new IllegalArgumentException("Video URL must not be null or empty");
        }
        
        try {
            URL url = new URL(videoUrl);
            Path tempFile = Files.createTempFile("video_" + UUID.randomUUID(), ".mp4");
            log.info("📁 Created temp file: {}", tempFile);
            
            try (InputStream inputStream = url.openStream()) {
                long bytesDownloaded = Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                log.info("✅ Video downloaded successfully: {} bytes to {}", bytesDownloaded, tempFile);
                return tempFile;
            }
        } catch (IOException e) {
            log.error("❌ Failed to download video from URL: {} - Error: {}", videoUrl, e.getMessage());
            throw e;
        }
    }
    
    private Path extractLatestFrame(Path videoFile) throws IOException, InterruptedException {
        log.info("🎬 Extracting latest frame from video: {}", videoFile);
        
        // Find FFmpeg executable
        String ffmpegPath = findFFmpeg();
        log.info("✅ Using FFmpeg at: {}", ffmpegPath);
        
        Path outputImageFile = Files.createTempFile("latest_frame_" + UUID.randomUUID(), ".jpg");
        log.info("📁 Created output file: {}", outputImageFile);
        
        // Build FFmpeg command to extract last frame
        String[] command = {
            ffmpegPath,
            "-i", videoFile.toString(),
            "-vf", "select='isnan(next_selected_t)'", // Selects the very last frame
            "-vframes", "1",
            "-y", // Overwrite output file
            "-q:v", "2", // High quality
            "-update", "1", // Update single output file
            outputImageFile.toString()
        };
        
        log.info("🔧 FFmpeg command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // Read process output for debugging
        StringBuilder ffmpegOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ffmpegOutput.append(line).append("\n");
                log.debug("FFmpeg: {}", line);
            }
        }
        
        int exitCode = process.waitFor();
        log.info("FFmpeg exit code: {}", exitCode);
        
        if (exitCode != 0) {
            log.error("❌ FFmpeg failed with exit code: {}", exitCode);
            log.error("FFmpeg output:\n{}", ffmpegOutput.toString());
            
            // Try alternative method to get last frame
            return extractLastFrameAlternative(videoFile, ffmpegPath);
        }
        
        // Verify output file exists and has content
        if (!Files.exists(outputImageFile) || Files.size(outputImageFile) == 0) {
            log.error("❌ Latest frame extraction failed - output file is empty or doesn't exist");
            // Try alternative method
            return extractLastFrameAlternative(videoFile, ffmpegPath);
        }
        
        log.info("✅ Latest frame extracted successfully: {} bytes", Files.size(outputImageFile));
        return outputImageFile;
    }
    
    private Path extractLastFrameAlternative(Path videoFile, String ffmpegPath) throws IOException, InterruptedException {
        log.info("🔄 Trying alternative method to extract last frame");
        
        Path outputImageFile = Files.createTempFile("last_frame_alt_" + UUID.randomUUID(), ".jpg");
        
        // Alternative: seek to end and extract frame
        String[] command = {
            ffmpegPath,
            "-sseof", "-0.1", // Seek to 1 second before end
            "-i", videoFile.toString(),
            "-vframes", "1",
            "-y", // Overwrite output file
            "-q:v", "2", // High quality
            outputImageFile.toString()
        };
        
        log.info("🔧 Alternative FFmpeg command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // Read process output
        StringBuilder ffmpegOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ffmpegOutput.append(line).append("\n");
                log.debug("FFmpeg Alt: {}", line);
            }
        }
        
        int exitCode = process.waitFor();
        log.info("Alternative FFmpeg exit code: {}", exitCode);
        
        if (exitCode != 0) {
            log.error("❌ Alternative FFmpeg failed with exit code: {}", exitCode);
            log.error("Alternative FFmpeg output:\n{}", ffmpegOutput.toString());
            throw new RuntimeException("Failed to extract last frame with both methods. Output: " + ffmpegOutput.toString());
        }
        
        // Verify output file
        if (!Files.exists(outputImageFile) || Files.size(outputImageFile) == 0) {
            log.error("❌ Alternative method failed - output file is empty or doesn't exist");
            throw new RuntimeException("Alternative method failed - no output file generated");
        }
        
        log.info("✅ Last frame extracted with alternative method: {} bytes", Files.size(outputImageFile));
        return outputImageFile;
    }
    
    private Path extractFrame(Path videoFile, double captureTimeSeconds) throws IOException, InterruptedException {
        log.info("🎬 Extracting frame at {}s from video: {}", captureTimeSeconds, videoFile);
        
        // Find FFmpeg executable
        String ffmpegPath = findFFmpeg();
        log.info("✅ Using FFmpeg at: {}", ffmpegPath);
        
        Path outputImageFile = Files.createTempFile("frame_" + UUID.randomUUID(), ".jpg");
        log.info("📁 Created output file: {}", outputImageFile);
        
        // Build FFmpeg command
        String[] command = {
            ffmpegPath,
            "-i", videoFile.toString(),
            "-ss", String.valueOf(captureTimeSeconds),
            "-vframes", "1",
            "-y", // Overwrite output file
            "-q:v", "2", // High quality
            outputImageFile.toString()
        };
        
        log.info("🔧 FFmpeg command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // Read process output for debugging
        StringBuilder ffmpegOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ffmpegOutput.append(line).append("\n");
                log.debug("FFmpeg: {}", line);
            }
        }
        
        int exitCode = process.waitFor();
        log.info("FFmpeg exit code: {}", exitCode);
        
        if (exitCode != 0) {
            log.error("❌ FFmpeg failed with exit code: {}", exitCode);
            log.error("FFmpeg output:\n{}", ffmpegOutput.toString());
            throw new RuntimeException("FFmpeg failed with exit code: " + exitCode + "\nOutput: " + ffmpegOutput.toString());
        }
        
        // Verify output file exists and has content
        if (!Files.exists(outputImageFile) || Files.size(outputImageFile) == 0) {
            log.error("❌ Frame extraction failed - output file is empty or doesn't exist");
            throw new RuntimeException("Frame extraction failed - no output file generated");
        }
        
        log.info("✅ Frame extracted successfully: {} bytes", Files.size(outputImageFile));
        return outputImageFile;
    }
    
    private String findFFmpeg() {
        // Common FFmpeg locations
        String[] possiblePaths = {
            "ffmpeg", // In PATH
            "/usr/bin/ffmpeg", // Standard Linux location
            "/usr/local/bin/ffmpeg", // Common Linux location
            "/opt/homebrew/bin/ffmpeg", // Homebrew on Apple Silicon
            "/usr/local/homebrew/bin/ffmpeg", // Homebrew on Intel Mac
            "/opt/local/bin/ffmpeg" // MacPorts
        };
        
        for (String path : possiblePaths) {
            try {
                ProcessBuilder pb = new ProcessBuilder(path, "-version");
                Process process = pb.start();
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("Found FFmpeg at: {}", path);
                    return path;
                }
            } catch (Exception e) {
                log.debug("FFmpeg not found at: {} - {}", path, e.getMessage());
            }
        }
        
        throw new RuntimeException("FFmpeg not found in any common locations. Please install FFmpeg or add it to PATH.");
    }
    
    @Override
    public MultipartFile concatenateVideos(List<String> videoUrls, String quality) {
        log.info("Starting video concatenation - {} videos, quality: {}", videoUrls.size(), quality);
        
        if (videoUrls.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        List<Path> videoFiles = new ArrayList<>();
        Path concatFile = null;
        Path outputFile = null;
        
        try {
            // Step 1: Download all videos
            log.info("Step 1: Downloading {} videos...", videoUrls.size());
            for (int i = 0; i < videoUrls.size(); i++) {
                String videoUrl = videoUrls.get(i);
                log.info("Downloading video {} of {}: {}", i + 1, videoUrls.size(), videoUrl);
                Path videoFile = downloadVideo(videoUrl);
                videoFiles.add(videoFile);
            }
            log.info("✅ All videos downloaded successfully");
            
            // Step 2: Create concat file for FFmpeg
            log.info("Step 2: Creating FFmpeg concat file...");
            concatFile = createConcatFile(videoFiles);
            log.info("✅ Concat file created: {}", concatFile);
            
            // Step 3: Concatenate videos using FFmpeg
            log.info("Step 3: Concatenating videos...");
            outputFile = concatenateWithFFmpeg(concatFile, quality);
            log.info("✅ Video concatenation successful: {} bytes", Files.size(outputFile));
            
            // Step 4: Convert to MultipartFile
            log.info("Step 4: Converting to MultipartFile...");
            byte[] videoBytes = Files.readAllBytes(outputFile);
            MultipartFile multipartFile = new CustomMultipartFile(
                "concatenated_video.mp4",
                "concatenated_video.mp4",
                "video/mp4",
                videoBytes
            );
            log.info("✅ MultipartFile created successfully: {} bytes", multipartFile.getSize());
            
            return multipartFile;
            
        } catch (IOException e) {
            log.error("❌ IO Error during video concatenation: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            log.error("❌ FFmpeg process interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("❌ Unexpected error during video concatenation: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // Clean up all temporary files
            List<Path> allTempFiles = new ArrayList<>(videoFiles);
            if (concatFile != null) allTempFiles.add(concatFile);
            if (outputFile != null) allTempFiles.add(outputFile);
            cleanupTempFiles(allTempFiles.toArray(new Path[0]));
        }
    }
    
    private Path createConcatFile(List<Path> videoFiles) throws IOException {
        Path concatFile = Files.createTempFile("concat_" + UUID.randomUUID(), ".txt");
        
        try (BufferedWriter writer = Files.newBufferedWriter(concatFile)) {
            for (Path videoFile : videoFiles) {
                // Escape single quotes and write file path
                String escapedPath = videoFile.toString().replace("'", "'\"'\"'");
                writer.write("file '" + escapedPath + "'");
                writer.newLine();
            }
        }
        
        log.info("Created concat file with {} video entries", videoFiles.size());
        return concatFile;
    }
    
    private Path concatenateWithFFmpeg(Path concatFile, String quality) throws IOException, InterruptedException {
        String ffmpegPath = findFFmpeg();
        Path outputFile = Files.createTempFile("concatenated_" + UUID.randomUUID(), ".mp4");
        
        // Build quality settings
        String[] qualitySettings = getQualitySettings(quality);
        
        // Build FFmpeg command
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-f");
        command.add("concat");
        command.add("-safe");
        command.add("0");
        command.add("-i");
        command.add(concatFile.toString());
        
        // Add quality settings
        for (String setting : qualitySettings) {
            command.add(setting);
        }
        
        command.add("-y"); // Overwrite output file
        command.add(outputFile.toString());
        
        log.info("🔧 FFmpeg concatenation command: {}", String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // Read process output
        StringBuilder ffmpegOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ffmpegOutput.append(line).append("\n");
                log.debug("FFmpeg: {}", line);
            }
        }
        
        int exitCode = process.waitFor();
        log.info("FFmpeg concatenation exit code: {}", exitCode);
        
        if (exitCode != 0) {
            log.error("❌ FFmpeg concatenation failed with exit code: {}", exitCode);
            log.error("FFmpeg output:\n{}", ffmpegOutput.toString());
            throw new RuntimeException("Video concatenation failed with exit code: " + exitCode + "\nOutput: " + ffmpegOutput.toString());
        }
        
        // Verify output file
        if (!Files.exists(outputFile) || Files.size(outputFile) == 0) {
            log.error("❌ Video concatenation failed - output file is empty or doesn't exist");
            throw new RuntimeException("Video concatenation failed - no output file generated");
        }
        
        log.info("✅ Videos concatenated successfully: {} bytes", Files.size(outputFile));
        return outputFile;
    }
    
    private String[] getQualitySettings(String quality) {
        switch (quality.toLowerCase()) {
            case "high":
                return new String[]{"-c:v", "libx264", "-preset", "medium", "-crf", "18", "-c:a", "aac", "-b:a", "192k"};
            case "medium":
                return new String[]{"-c:v", "libx264", "-preset", "fast", "-crf", "23", "-c:a", "aac", "-b:a", "128k"};
            case "low":
                return new String[]{"-c:v", "libx264", "-preset", "ultrafast", "-crf", "28", "-c:a", "aac", "-b:a", "96k"};
            default:
                return new String[]{"-c:v", "libx264", "-preset", "medium", "-crf", "23", "-c:a", "aac", "-b:a", "128k"};
        }
    }
    
    private void cleanupTempFiles(Path... files) {
        for (Path file : files) {
            try {
                if (file != null && Files.exists(file)) {
                    Files.delete(file);
                    log.debug("Deleted temp file: {}", file);
                }
            } catch (IOException e) {
                log.warn("Failed to delete temp file: {}", file, e);
            }
        }
    }
}