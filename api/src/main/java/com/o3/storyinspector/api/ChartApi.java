package com.o3.storyinspector.api;

import com.o3.storyinspector.api.user.GoogleId;
import com.o3.storyinspector.api.user.UserInfo;
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
public class ChartApi {

    final Logger logger = LoggerFactory.getLogger(ChartApi.class);

    @Autowired
    private JdbcTemplate db;

    @Autowired
    private GoogleId userValidator;

    @GetMapping("/{id}/posneg/")
    public Chart one(@PathVariable final Long id, @RequestParam("id_token") final String idToken) {
        logger.trace("CHART SENTIMENT BOOK ID=[" + id + "]");
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(id, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
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
    public Chart one(@PathVariable final Long id, @PathVariable final String emotionName, @RequestParam("id_token") final String idToken) {
        logger.trace("CHART EMOTION BOOK ID=[" + id + "], EMOTION=[" + emotionName + "]");
        final UserInfo user = userValidator.retrieveUserInfo(idToken);
        final BookDAO bookDAO = BookDAO.findByBookId(id, db);
        if (!user.isAdmin()) user.emailMatches(bookDAO.getUserEmail());
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
