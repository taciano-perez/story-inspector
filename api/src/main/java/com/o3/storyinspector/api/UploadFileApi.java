package com.o3.storyinspector.api;

import com.o3.storyinspector.api.util.ApiUtils;
import com.o3.storyinspector.db.BookDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UploadFileApi {

    final Logger logger = LoggerFactory.getLogger(UploadFileApi.class);

    @Autowired
    private JdbcTemplate db;

    @RequestMapping(value = "/book", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<Object> submit(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("author") String author) throws IOException {
        logger.trace(String.format("TITLE - %s", title));
        logger.trace(String.format("AUTHOR - %s", author));
        logger.trace(String.format("FILE - %s", file));
        final String content = new String(file.getBytes());
        logger.trace(String.format("CONTENT - %s", content));

        final Long bookId = BookDAO.saveBook(db, title, author, content);
        logger.trace(String.format("BOOK ID - %s", bookId));

        ApiUtils.callAsyncApiWithParameter(ApiUtils.API_PROCESS_BOOK_ENDPOINT, "ID", bookId.toString());

        return ResponseEntity.ok().build();
    }

}
