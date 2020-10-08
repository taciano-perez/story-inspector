package com.o3.storyinspector.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.Statement;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UploadFileApi {

    @Autowired
    private JdbcTemplate db;

    @RequestMapping(value = "/book", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity submit(@RequestParam("file") MultipartFile file,
                                 @RequestParam("title") String title,
                                 @RequestParam("author") String author) throws IOException {
        System.out.println(String.format("TITLE - %s", title));
        System.out.println(String.format("AUTHOR - %s", author));
        System.out.println(String.format("FILE - %s", file));
        final String content = new String(file.getBytes());
        System.out.println(String.format("CONTENT - %s", content));

        final long pk = saveBook(title, author, content);
        System.out.println(String.format("PK - %s", pk));

        return ResponseEntity.ok().build();
    }

    private long saveBook(final String title, final String author, final String rawInput) {
        final String sql = "INSERT INTO books (title, author, raw_input) VALUES (?, ?, ?)";
        final KeyHolder holder = new GeneratedKeyHolder();
        db.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, author);
            final Reader reader = new StringReader(rawInput);
            ps.setClob(3, reader);
            return ps;
        }, holder);
        final Number key = holder.getKey();
        if (key != null) {
            return key.longValue();
        }
        throw new RuntimeException("No generated primary key returned.");
    }
}
