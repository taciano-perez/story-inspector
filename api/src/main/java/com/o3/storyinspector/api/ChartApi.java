package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Chart;
import com.o3.storyinspector.storydom.Block;
import com.o3.storyinspector.storydom.Book;
import com.o3.storyinspector.storydom.Chapter;
import com.o3.storyinspector.storydom.Emotion;
import com.o3.storyinspector.storydom.constants.EmotionType;
import com.o3.storyinspector.storydom.io.XmlReader;
import com.o3.storyinspector.storydom.util.StoryDomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/charts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChartApi {

    final Logger logger = LoggerFactory.getLogger(ChartApi.class);

    @Autowired
    private JdbcTemplate db;

    @GetMapping("/{id}/posneg/")
    public Chart one(@PathVariable final Long id) {
        logger.trace("CHART SENTIMENT BOOK ID=[" + id + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(id, db);
        final Chart chart;
        try {
            chart = buildSentimentChartFromBook(bookDAO);
        } catch (Exception e) {
            final String errMsg = "Unexpected error when building posneg chart. Book id: " +
                    id + "Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }

        return chart;
    }

    @GetMapping("/{id}/{emotionName}/")
    public Chart one(@PathVariable final Long id, @PathVariable final String emotionName) {
        logger.trace("CHART EMOTION BOOK ID=[" + id + "], EMOTION=[" + emotionName + "]");
        final BookDAO bookDAO = BookDAO.findByBookId(id, db);
        final Chart chart;
        try {
            final EmotionType emotionType = EmotionType.emotionTypeFor(emotionName);
            chart = buildEmotionChartFromBook(bookDAO, emotionType);
        } catch (Exception e) {
            final String errMsg = "Unexpected error when building emotion chart. Book id: " +
                    id + " ,emotion:" + emotionName + " Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            return null;
        }

        return chart;
    }

    private static Chart buildSentimentChartFromBook(final BookDAO bookDAO) throws JAXBException, ParseException {
        final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
        final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
        final List<String> labels = new ArrayList<>();
        final List<String> blocks = new ArrayList<>();
        final List<Double> scores = new ArrayList<>();
        int counter = 0;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                counter++;
                final double sentimentScore = StoryDomUtils.getFormatter().parse(block.getSentimentScore()).doubleValue();
                labels.add("#" + counter);
                blocks.add(block.getBody());
                scores.add(sentimentScore);
            }
        }
        return new Chart(bookDAO.getTitle(), bookDAO.getAuthor(), labels, blocks, scores);
    }

    private static Chart buildEmotionChartFromBook(final BookDAO bookDAO, final EmotionType emotionType) throws JAXBException {
        final double maxEmotionScore = StoryDomUtils.getMaxEmotionScore(bookDAO.asBook());
        final String annotatedStoryDom = bookDAO.getAnnotatedStoryDom();
        final Book book = XmlReader.readBookFromXmlStream(new StringReader(annotatedStoryDom));
        final List<String> labels = new ArrayList<>();
        final List<String> blocks = new ArrayList<>();
        final List<Double> scores = new ArrayList<>();
        int counter = 0;
        for (final Chapter chapter : book.getChapters()) {
            for (final Block block : chapter.getBlocks()) {
                counter++;
                final Emotion emotion = StoryDomUtils.findEmotion(emotionType, block.getEmotions());
                final double emotionScore = emotion.getScore().doubleValue();
                final double normalizedEmotionScore = emotionScore / maxEmotionScore;
                labels.add("#" + counter);
                blocks.add(block.getBody());
                scores.add(normalizedEmotionScore);
            }
        }
        return new Chart(bookDAO.getTitle(), bookDAO.getAuthor(), labels, blocks, scores);
    }

}
