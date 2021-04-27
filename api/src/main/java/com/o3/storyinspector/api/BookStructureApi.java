package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.BookStructure;
import com.o3.storyinspector.domain.Chapter;
import com.o3.storyinspector.storydom.Character;
import com.o3.storyinspector.storydom.*;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.util.StoryDomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookstructure")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookStructureApi {

    final static Logger logger = LoggerFactory.getLogger(BookStructureApi.class);

    private static final double FK_GRADE_UNKNOWN = -256;

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/{bookId}")
    public BookStructure one(@PathVariable final Long bookId) {
        logger.trace("BOOK STRUCTURE BOOK ID=[" + bookId + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(bookId, db);
        BookStructure bookStructure;
        try {
            final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
            final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
            book.setAuthor(bookDAO.getAuthor());    // FIXME: parse this at the appropriate spot
            bookStructure = buildFromBook(book);
        } catch (final Exception e) {
            final String errMsg = "Unexpected error when building book structure report. Book bookId: " +
                    bookId + ", Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return null;
        }

        return bookStructure;
    }

    private static BookStructure buildFromBook(final Book book) {
        final List<Chapter> chapterList = new ArrayList<>();
        long bookWordcount = 0;
        long id = 1;
        for (final com.o3.storyinspector.storydom.Chapter chapter : book.getChapters()) {
            final Metadata chapterMetadata = chapter.getMetadata();
            final long chapterWordcount = Long.parseLong(chapterMetadata.getWordCount());
            final BigDecimal chapterFkGrade = chapter.getMetadata().getFkGrade();
            bookWordcount += chapterWordcount;
            chapterList.add(new Chapter(id++,
                    chapter.getTitle(),
                    chapterWordcount,
                    (chapterFkGrade == null) ? FK_GRADE_UNKNOWN : chapterFkGrade.doubleValue(),
                    chapterMetadata.getCharacters().getCharacters().stream().map(Character::getName).collect(Collectors.toList()),
                    chapterMetadata.getLocations().getLocations().stream().map(Location::getName).collect(Collectors.toList()),
                    identifyDominantEmotions(chapter)));
        }
        return new BookStructure(
                book.getTitle(),
                book.getAuthor(),
                bookWordcount,
                chapterList
        );
    }

    public static List<EmotionType> identifyDominantEmotions(final com.o3.storyinspector.storydom.Chapter chapter) {
        final Map<EmotionType, Double> avgScoresByEmotion = new HashMap<>();
        double totalAccumulatedScore = 0;
        for (final EmotionType emotionType: EmotionType.values()) {
            double avgScore = calculateAvgChapterEmotionScore(chapter, emotionType);
            avgScoresByEmotion.put(emotionType, avgScore);
            totalAccumulatedScore += avgScore;
        }
        final List<EmotionType> dominantEmotions = new ArrayList<>();
        for (final EmotionType emotionType: EmotionType.values()) {
            final double emotionWeight = avgScoresByEmotion.get(emotionType) / totalAccumulatedScore;
            if (emotionWeight >= 0.2) { // dominant emotion has >= 20% weight
                dominantEmotions.add(emotionType);
            }
        }
        logger.debug("dominant emotions: " + dominantEmotions);
        return dominantEmotions;
    }

    private static double calculateAvgChapterEmotionScore(final com.o3.storyinspector.storydom.Chapter chapter, final EmotionType emotionType) {
        int blockCount = 0;
        double accumulatedEmotionScore = 0;
        for (final Block block : chapter.getBlocks()) {
            blockCount++;
            final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
            final double emotionScore = emotion.getScore().doubleValue();
            accumulatedEmotionScore += emotionScore;
        }
        return accumulatedEmotionScore / blockCount;
    }

}
