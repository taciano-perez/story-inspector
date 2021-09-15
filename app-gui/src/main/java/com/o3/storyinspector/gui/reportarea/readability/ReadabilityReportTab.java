package com.o3.storyinspector.gui.reportarea.readability;

import com.o3.storyinspector.gui.core.domain.Block;
import com.o3.storyinspector.gui.core.domain.Blocks;
import com.o3.storyinspector.gui.core.domain.Sentence;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.gui.utils.StringFormatter;
import com.o3.storyinspector.storydom.Book;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class ReadabilityReportTab extends Tab {

    static final Logger LOGGER = LoggerFactory.getLogger(ReadabilityReportTab.class);

    Book book;
    final List<TextFlow> pages = new ArrayList<>();

    public ReadabilityReportTab(final Book book) {
        super(book.getTitle() + " (Readability)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.PENCIL));
        this.book = book;

        final Task<Blocks> calculateReadability = new Task<>() {
            @Override
            protected Blocks call() {
                LOGGER.info("starting to process readability report");
                final Blocks blocks = Blocks.buildBlocks(book, book.getTitle(), book.getAuthor());
                LOGGER.info("finished processing readability report");
                return blocks;
            }
        };

        // show spinning wheel
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        final Label statusLabel = new Label("Calculating readability scores...");
        final VBox loadingMessage = new VBox(5, statusLabel, progressIndicator);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(loadingMessage);
        StackPane.setAlignment(loadingMessage, Pos.TOP_LEFT);
        StackPane.setAlignment(loadingMessage, Pos.CENTER);
        this.setContent(stackPane);

        // fill tab content after readability is calculated
        calculateReadability.setOnSucceeded(e -> {
            final List<Block> blockList = calculateReadability.getValue().getBlocks();

            final VBox stackedTitledPanes = new VBox();

            // summary
            final VBox summaryTabContents = new VBox();

            final DoubleSummaryStatistics blockStats = blockList.stream()
                    .mapToDouble(Block::getFkGrade)
                    .summaryStatistics();
            final double fkAvgGrade = Math.floor(blockStats.getAverage());
            final Label avgReadabilityLabel = new Label("Average Flesch-Kincaid Grade: " + StringFormatter.formatInteger(fkAvgGrade));
            summaryTabContents.getChildren().add(avgReadabilityLabel);
            final Label avgReadabilityLabel2 = new Label("Average Readability: " + Blocks.getFkGradeMessage(fkAvgGrade));
            summaryTabContents.getChildren().add(avgReadabilityLabel2);

            final long numBlocksAboveAvg = blockList.stream()
                    .filter(block -> (Math.floor(block.getFkGrade()) > fkAvgGrade))
                    .count();
            final Label avgReadabilityLabel3 = new Label(numBlocksAboveAvg + " pages out of " + blockList.size() + " are more complex than this average.");
            summaryTabContents.getChildren().add(avgReadabilityLabel3);

            final Label avgReadabilityLabel4 = new Label("\nThe most complex sentences of each page are highlighted in red below.\nPages are ordered from the most difficult to the easiest to read.");
            summaryTabContents.getChildren().add(avgReadabilityLabel4);

            final TitledPane summaryTab = new TitledPane("Summary", summaryTabContents);
            summaryTab.setExpanded(true);
            stackedTitledPanes.getChildren().add(summaryTab);

            // pages
            blockList.sort((Comparator.comparingDouble(Block::getFkGrade).reversed()));
            for (final Block block : blockList) {
                LOGGER.info("Processing block #" + block.getId());
                double blockThreshold = Math.floor(block.getFkGrade());
                blockThreshold = (blockThreshold >= 8) ? blockThreshold : 8;
                final TextFlow page = new TextFlow();
                pages.add(page);
                for (final Sentence sentence : block.getSentences()) {
                    final Text sentenceText = new Text(sentence.getBody() + " ");
                    if (sentence.getFkGrade() > blockThreshold) {
                        sentenceText.setStyle("-fx-text-background-color: #f18973");  // does nothing
                        sentenceText.setFill(Color.RED);
                    }
                    page.getChildren().add(sentenceText);

                    // tooltip
                    Tooltip tooltip = new Tooltip("Flesch-Kincaid Grade: " + StringFormatter.formatInteger(sentence.getFkGrade()) + "\n" + Blocks.getFkGradeMessage(sentence.getFkGrade()));
                    tooltip.setShowDelay(Duration.ZERO);
                    tooltip.setShowDuration(Duration.INDEFINITE);
                    Tooltip.install(sentenceText, tooltip);
                }
                final VBox box = new VBox();
                final Label readabilityLabel1 = new Label("Flesch-Kincaid Grade: " + StringFormatter.formatInteger(block.getFkGrade()));
                final Label readabilityLabel2 = new Label("Readability: " + Blocks.getFkGradeMessage(block.getFkGrade()));
                readabilityLabel1.setStyle("-fx-font-weight: bold");
                readabilityLabel2.setStyle("-fx-font-weight: bold");
                box.getChildren().add(readabilityLabel1);
                box.getChildren().add(readabilityLabel2);
                box.getChildren().add(page);
                final String tabTitle = "Page #" + block.getId() +
                        " (Chapter " + block.getChapter().getId() + ": " +
                        block.getChapter().getTitle() + ")";
                final TitledPane tab1 = new TitledPane(tabTitle, box);
                tab1.setExpanded(true);
                stackedTitledPanes.getChildren().add(tab1);
                LOGGER.info("Finished processing block #" + block.getId());
            }
            // scrollable content
            final ScrollPane scrollPane = new ScrollPane(stackedTitledPanes);
            scrollPane.fitToWidthProperty().set(true);

            this.setContent(scrollPane);
        });

        // trigger readability calculation
        new Thread(calculateReadability).start();
        LOGGER.info("Completed readability tab creation");
    }

}
