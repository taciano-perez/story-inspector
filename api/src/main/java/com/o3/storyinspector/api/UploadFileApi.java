package com.o3.storyinspector.api;

import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
import com.o3.storyinspector.api.util.ApiUtils;
import com.o3.storyinspector.bookimporter.msword.DocImporter;
import com.o3.storyinspector.bookimporter.msword.DocxImporter;
import com.o3.storyinspector.db.BookDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/upload")
public class UploadFileApi {

    final Logger logger = LoggerFactory.getLogger(UploadFileApi.class);
    
    // Security: Define allowed file types and size limits
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".txt", ".docx", ".doc", ".epub"
    ));
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "text/plain",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/msword", // .doc
        "application/epub+zip" // .epub
    ));
    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final long MIN_FILE_SIZE = 10; // 10 bytes minimum

    @Autowired
    private JdbcTemplate db;

    @Autowired
    private GoogleId idValidator;

    @RequestMapping(value = "/book-preview", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitPreview(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("author") String author,
                                         @RequestParam("id_token") String idToken) throws IOException {
        logger.trace(String.format("TITLE - %s", title));
        logger.trace(String.format("AUTHOR - %s", author));
        logger.trace(String.format("FILE - %s", file));
        logger.trace(String.format("ID_TOKEN - %s", idToken));
        
        // Security: Validate file before processing
        ResponseEntity<?> validationResult = validateFile(file);
        if (validationResult != null) {
            return validationResult;
        }
        
        // Security: Validate input parameters
        if (title == null || title.trim().isEmpty() || title.length() > 250) {
            logger.warn("Invalid title provided");
            return ResponseEntity.badRequest().body("Title must be between 1 and 250 characters");
        }
        if (author == null || author.trim().isEmpty() || author.length() > 250) {
            logger.warn("Invalid author provided");
            return ResponseEntity.badRequest().body("Author must be between 1 and 250 characters");
        }
        
        final String content = getFileContent(file);
        logger.trace(String.format("CONTENT LENGTH - %d", content.length()));

        final UserInfo userInfo = idValidator.retrieveUserInfo(idToken);
        logger.trace("User name: " + userInfo.getName() + ", email: " + userInfo.getEmail());
        final Long bookId = BookDAO.saveBook(db, userInfo.getId(), userInfo.getEmail(), title, author, content, null, null, null);
        logger.trace(String.format("BOOK ID - %s", bookId));

        ApiUtils.callApiWithParameter(ApiUtils.API_CREATE_DOM + "/" + idToken, idToken, "ID", bookId.toString());
        return ResponseEntity.ok(bookId);
    }

    @RequestMapping(value = "/book", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> submit(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("author") String author,
                                         @RequestParam("id_token") String idToken) throws IOException {
        logger.trace(String.format("TITLE - %s", title));
        logger.trace(String.format("AUTHOR - %s", author));
        logger.trace(String.format("FILE - %s", file));
        logger.trace(String.format("ID_TOKEN - %s", idToken));
        
        // Security: Validate file before processing
        ResponseEntity<?> validationResult = validateFile(file);
        if (validationResult != null) {
            return validationResult;
        }
        
        // Security: Validate input parameters
        if (title == null || title.trim().isEmpty() || title.length() > 250) {
            logger.warn("Invalid title provided");
            return ResponseEntity.badRequest().body("Title must be between 1 and 250 characters");
        }
        if (author == null || author.trim().isEmpty() || author.length() > 250) {
            logger.warn("Invalid author provided");
            return ResponseEntity.badRequest().body("Author must be between 1 and 250 characters");
        }
        
        final String content = getFileContent(file);
        logger.trace(String.format("CONTENT LENGTH - %d", content.length()));

        final UserInfo userInfo = idValidator.retrieveUserInfo(idToken);
        logger.trace("User name: " + userInfo.getName() + ", email: " + userInfo.getEmail());
        final Long bookId = BookDAO.saveBook(db, userInfo.getId(), userInfo.getEmail(), title, author, content, null, null, null);
        logger.trace(String.format("BOOK ID - %s", bookId));

        ApiUtils.callAsyncApiWithParameter(ApiUtils.API_PROCESS_BOOK_ENDPOINT, idToken, "ID", bookId.toString());
        return ResponseEntity.ok().build();
    }

    /**
     * Security: Validates uploaded file for type, size, and content safety
     */
    private ResponseEntity<?> validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Empty or null file uploaded");
            return ResponseEntity.badRequest().body("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("File too large: {} bytes", file.getSize());
            return ResponseEntity.badRequest().body("File size cannot exceed 3MB");
        }
        
        if (file.getSize() < MIN_FILE_SIZE) {
            logger.warn("File too small: {} bytes", file.getSize());
            return ResponseEntity.badRequest().body("File is too small to be valid");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            logger.warn("Filename is null or empty");
            return ResponseEntity.badRequest().body("Filename cannot be empty");
        }

        // Security: Normalize filename to prevent path traversal
        filename = Paths.get(filename).getFileName().toString();
        
        // Check file extension
        String lowercaseFilename = filename.toLowerCase();
        boolean validExtension = ALLOWED_EXTENSIONS.stream()
            .anyMatch(ext -> lowercaseFilename.endsWith(ext));
        
        if (!validExtension) {
            logger.warn("Invalid file extension: {}", filename);
            return ResponseEntity.badRequest().body("File type not supported. Allowed types: " + ALLOWED_EXTENSIONS);
        }

        // Check MIME type if available
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            logger.warn("Invalid content type: {} for file: {}", contentType, filename);
            return ResponseEntity.badRequest().body("Invalid file type detected");
        }

        logger.trace("File validation passed for: {}", filename);
        return null; // null means validation passed
    }

    private String getFileContent(final MultipartFile file) throws IOException {
        final String filename = file.getOriginalFilename();
        if (filename == null) {
            logger.error("Filename cannot be null.");
            throw new IOException("Invalid filename");
        }
        
        // Security: Normalize filename
        final String normalizedFilename = Paths.get(filename).getFileName().toString();
        logger.trace("Processing file: " + normalizedFilename);
        
        // Security: Use case-insensitive comparison and handle files safely
        final String lowercaseFilename = normalizedFilename.toLowerCase();
        
        try {
            if (lowercaseFilename.endsWith(".docx")) {
                return DocxImporter.getDocxContentAsString(file.getInputStream());
            } else if (lowercaseFilename.endsWith(".doc")) {
                return DocImporter.getDocContentAsString(file.getInputStream());
            } else {
                // For text files and other formats
                String content = new String(file.getBytes(), "UTF-8");
                
                // Security: Basic content validation - reject files with suspicious content
                if (content.contains("<script>") || content.contains("javascript:") || 
                    content.contains("<?php") || content.contains("<%")) {
                    logger.warn("Potentially malicious content detected in file: {}", normalizedFilename);
                    throw new IOException("File content appears to contain executable code");
                }
                
                return content;
            }
        } catch (Exception e) {
            logger.error("Error processing file content for: " + normalizedFilename, e);
            throw new IOException("Failed to process file content: " + e.getMessage());
        }
    }

}
