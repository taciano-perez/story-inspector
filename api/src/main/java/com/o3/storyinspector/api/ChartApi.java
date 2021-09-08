package com.o3.storyinspector.api;

import com.o3.storyinspector.db.BookDAO;
import com.o3.storyinspector.domain.Chart;
import com.o3.storyinspector.storydom.constants.EmotionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

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
            chart = Chart.buildSentimentChartFromBook(bookDAO.asBook());
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
            chart = Chart.buildEmotionChartFromBook(bookDAO.asBook(), emotionType);
        } catch (Exception e) {
            final String errMsg = "Unexpected error when building emotion chart. Book id: " +
                    id + " ,emotion:" + emotionName + " Exception: " + e.getLocalizedMessage();
            logger.error(errMsg);
            e.printStackTrace();
            return null;
        }

        return chart;
    }

}
