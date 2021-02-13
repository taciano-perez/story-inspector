package com.o3.storyinspector.api;

import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
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

    @Autowired
    private GoogleId idValidator;

    @Autowired
    private ApiUtils apiUtils;

    @RequestMapping(value = "/book", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<Object> submit(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("author") String author,
                                         @RequestParam("id_token") String idToken) throws IOException {
        logger.trace(String.format("TITLE - %s", title));
        logger.trace(String.format("AUTHOR - %s", author));
        logger.trace(String.format("FILE - %s", file));
        logger.trace(String.format("ID_TOKEN - %s", idToken));
        final String content = new String(file.getBytes());
        logger.trace(String.format("CONTENT - %s", content));

        final UserInfo userInfo = idValidator.retrieveUserInfo(idToken);
        if (userInfo != null) {
            logger.trace("User name: " + userInfo.getName() + ", email: " + userInfo.getEmail());
            final Long bookId = BookDAO.saveBook(db, userInfo.getId(), userInfo.getEmail(), title, author, content, null, null, null);
            logger.trace(String.format("BOOK ID - %s", bookId));

            apiUtils.callAsyncApiWithParameter(ApiUtils.API_PROCESS_BOOK_ENDPOINT, "ID", bookId.toString());
            return ResponseEntity.ok().build();
        } else {
            logger.error("Could not upload book, error authenticating user. Title: " + title + ", author:" + author + ", token: " + idToken);
            return ResponseEntity.unprocessableEntity().build();
        }
    }

}
