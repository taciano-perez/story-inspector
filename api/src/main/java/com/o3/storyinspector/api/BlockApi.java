package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Block;
import com.o3.storyinspector.domain.Blocks;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/blocks")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BlockApi {

    final Logger logger = LoggerFactory.getLogger(BlockApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/list/{bookId}")
    public Blocks findAllByBook(@PathVariable final Long bookId) {
        logger.trace("LIST ALL BLOCKS bookId: " + bookId);
        final List<Block> blockList = new ArrayList<>();
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            Integer chapterId = 1;
            Integer blockId = 1;
            for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
                final String chapterTitle = "Chapter #" + chapterId++ + " " + chapter.getTitle();
                for (final com.o3.storyinspector.storydom.Block domBlock : chapter.getBlocks()) {
                    blockList.add(new Block(blockId++, domBlock, chapterTitle));
                }
            }
            return new Blocks(bookDAO.getTitle(), bookDAO.getAuthor(), blockList);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when listing book blocks. Book bookId: " +
                    bookId + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return null;
        }
    }

}
