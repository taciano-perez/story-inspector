package com.o3.storyinspector.gui.reportarea.sentence;

import com.google.common.collect.ImmutableList;
import com.o3.storyinspector.gui.core.domain.Block;
import com.o3.storyinspector.gui.core.domain.Blocks;
import com.o3.storyinspector.gui.core.domain.Sentence;
import com.o3.storyinspector.gui.utils.IconUtils;
import com.o3.storyinspector.gui.utils.StringFormatter;
import com.o3.storyinspector.gui.view.DoughnutChart;
import com.o3.storyinspector.storydom.Book;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SentenceVarietyReportTab extends Tab {

    static final Logger LOGGER = LoggerFactory.getLogger(SentenceVarietyReportTab.class);

    static String BUCKET_LT_10 = "< 10 words";
    static String BUCKET_10_TO_19 = "10-19 words";
    static String BUCKET_20_TO_29 = "20-29 words";
    static String BUCKET_30_TO_39 = "30-39 words";
    static String BUCKET_GT_40 = "40+ words";

    static List<String> BUCKETS = ImmutableList.of(BUCKET_LT_10,
            BUCKET_10_TO_19,
            BUCKET_20_TO_29,
            BUCKET_30_TO_39,
            BUCKET_GT_40);

    Book book;
    final List<TextFlow> pages = new ArrayList<>();

    public SentenceVarietyReportTab(final Book book) {
        super(book.getTitle() + " (Sentence Variety)");
        this.setGraphic(IconUtils.getIcon(FontAwesome.Glyph.EDIT));
        this.book = book;

        final Task<Blocks> calculateSentenceLength = new Task<>() {
            @Override
            protected Blocks call() {
                LOGGER.info("starting to process sentence variety report");
                final Blocks blocks = Blocks.buildBlocks(book, book.getTitle(), book.getAuthor());
                LOGGER.info("finished processing sentence variety report");
                return blocks;
            }
        };

        // show spinning wheel
        final ProgressIndicator progressIndicator = new ProgressIndicator();
        final Label statusLabel = new Label("Calculating sentence length...");
        final VBox loadingMessage = new VBox(5, statusLabel, progressIndicator);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(loadingMessage);
        StackPane.setAlignment(loadingMessage, Pos.TOP_LEFT);
        StackPane.setAlignment(loadingMessage, Pos.CENTER);
        this.setContent(stackPane);

        // fill tab content after calculations
        calculateSentenceLength.setOnSucceeded(e -> {
            final List<Block> blockList = calculateSentenceLength.getValue().getBlocks();

            VBox stackedTitledPanes = new VBox();

            // summary
            final VBox summaryTabContents = new VBox();

            final long numOfSentences = blockList.stream()
                    .mapToLong(b -> (b.getSentences().size()))
                    .sum();

            final List<Integer> sentenceLenghts = blockList.stream()
                    .flatMap(b -> b.getSentences().stream())
                    .map(Sentence::getWordCount)
                    .collect(Collectors.toList());
            final double stdDev = computeStandardDeviation(sentenceLenghts);

            final Label label1 = new Label(String.format("This book has %s sentences, with %s sentence variety.\nThis chart shows the sentences grouped by length (in number of words) for the entire book.", StringFormatter.formatInteger(numOfSentences), Blocks.getSentenceVarietyMessage(stdDev, null)));
            summaryTabContents.getChildren().add(label1);

            // summary chart
            final Map<String, Integer> bookBuckets = calculateSentenceBuckets(blockList);
            final DoughnutChart summaryChart = buildChart(bookBuckets);
            summaryTabContents.getChildren().add(summaryChart);
            addTooltips(bookBuckets, summaryChart);

            // summary tab
            final TitledPane summaryTab = new TitledPane("Summary", summaryTabContents);
            summaryTab.setExpanded(true);
            stackedTitledPanes.getChildren().add(summaryTab);

            // page tabs
            blockList.sort(Comparator.comparingDouble(b -> {
                final List<Integer> blockSentenceLengths = b.getSentences().stream()
                        .map(Sentence::getWordCount)
                        .collect(Collectors.toList());
                return computeStandardDeviation(blockSentenceLengths);
            }));
            for (final Block block : blockList) {
                LOGGER.info("Processing block #" + block.getId());

                // header
                final VBox box = new VBox();
                final Label readabilityLabel1 = new Label("Sentence Variety: " + Blocks.getSentenceVarietyMessage(getBlockStdDev(block), block));
                readabilityLabel1.setStyle("-fx-font-weight: bold");
                box.getChildren().add(readabilityLabel1);

                // page chart
                final Map<String, Integer> pageBuckets = calculateSentenceBuckets(block);
                final DoughnutChart pageChart = buildChart(pageBuckets);
                box.getChildren().add(pageChart);
                addTooltips(pageBuckets, pageChart);

                // page text
                final TextFlow page = new TextFlow();
                page.setBackground(new Background(new BackgroundFill(Color.WHITE,
                        CornerRadii.EMPTY, Insets.EMPTY)));
                pages.add(page);
                for (final Sentence sentence : block.getSentences()) {
                    final Text sentenceText = new Text(sentence.getBody() + " ");
                    sentenceText.setFill(getColorForSentenceLength(sentence.getWordCount()));
                    page.getChildren().add(sentenceText);

                    // tooltip
                    Tooltip tooltip = new Tooltip(StringFormatter.formatInteger(sentence.getWordCount()) + " words");
                    tooltip.setShowDelay(Duration.ZERO);
                    tooltip.setShowDuration(Duration.INDEFINITE);
                    Tooltip.install(sentenceText, tooltip);
                }
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
            scrollPane.fitToHeightProperty().set(true);

            this.setContent(scrollPane);
        });

        // trigger calculation
        new Thread(calculateSentenceLength).start();
        LOGGER.info("Completed sentence variety tab creation");
    }

    private static DoughnutChart buildChart(final Map<String, Integer> buckets) {
        final DoughnutChart chart = new DoughnutChart(
                FXCollections.observableArrayList(buckets.entrySet()
                        .stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())));
        chart.setLegendVisible(true);
//        chart.setLabelsVisible(false);
        return chart;
    }

    private static void addTooltips(final Map<String, Integer> buckets, final DoughnutChart chart) {
        final Iterator<Integer> iterator = buckets.values().iterator();
        final double totalSentences = buckets.values().stream().mapToDouble(value -> value).sum();
        for (final Object item: chart.getData()) {
            final PieChart.Data data = (PieChart.Data) item;
            final double bucketPercentage = iterator.next() / totalSentences;
            final Tooltip tooltip = new Tooltip(StringFormatter.formatPercentage(bucketPercentage));
            tooltip.setShowDelay(Duration.seconds(0));
            tooltip.setShowDuration(Duration.INDEFINITE);
            Tooltip.install(data.getNode(), tooltip);
        }
    }

    private static Map<String, Integer> calculateSentenceBuckets(final Block block) {
        final Map<String, Integer> buckets = new HashMap<>();
        BUCKETS.forEach(bucket -> buckets.put(bucket, 0));
        for (final Sentence sentence : block.getSentences()) {
            final Integer numOfWords = sentence.getWordCount();
            if (numOfWords < 10) {
                buckets.put(BUCKET_LT_10, buckets.get(BUCKET_LT_10)+1);
            } else if (numOfWords >= 10 && numOfWords <= 19) {
                buckets.put(BUCKET_10_TO_19, buckets.get(BUCKET_10_TO_19)+1);
            } else if (numOfWords >= 20 && numOfWords <= 29) {
                buckets.put(BUCKET_20_TO_29, buckets.get(BUCKET_20_TO_29)+1);
            } else if (numOfWords >= 30 && numOfWords <= 39) {
                buckets.put(BUCKET_30_TO_39, buckets.get(BUCKET_30_TO_39)+1);
            } else if (numOfWords >= 40) {
                buckets.put(BUCKET_GT_40, buckets.get(BUCKET_GT_40)+1);
            }
        }
        return buckets;
    }

    private static Map<String, Integer> calculateSentenceBuckets(final List<Block> blockList) {
        final Map<String, Integer> buckets = new HashMap<>();
        BUCKETS.forEach(bucket -> buckets.put(bucket, 0));
        for (final Block block : blockList) {
            final Map<String, Integer> blockBuckets = calculateSentenceBuckets(block);
            BUCKETS.forEach(bucket -> buckets.put(bucket, buckets.get(bucket) + blockBuckets.get(bucket)));
        }
        return buckets;
    }

    private static Color getColorForSentenceLength(final int numOfWords) {
        if (numOfWords < 10) {
            return Color.web("#f3622d");
        } else if (numOfWords >= 10 && numOfWords <=19) {
            return Color.web("#fba71b");
        } else if (numOfWords >= 20 && numOfWords <=29) {
            return Color.web("#57b757");
        } else if (numOfWords >= 30 && numOfWords <=39) {
            return Color.web("#41a9c9");
        } else if (numOfWords >= 40) {
            return Color.web("#4258c9");
        } else {
            return Color.web("#c84164");
        }
    }

    private static double getBlockStdDev(final Block block) {
        final List<Integer> blockSentenceLengths = block.getSentences().stream()
                .map(Sentence::getWordCount)
                .collect(Collectors.toList());
        return computeStandardDeviation(blockSentenceLengths);
    }

    private static double computeStandardDeviation(final List<Integer> collection) {
        if (collection.size() == 0) {
            return Double.NaN;
        }

        final double average =
                collection.stream()
                        .mapToDouble((x) -> x.doubleValue())
                        .summaryStatistics()
                        .getAverage();

        final double rawSum =
                collection.stream()
                        .mapToDouble((x) -> Math.pow(x.doubleValue() - average,
                                2.0))
                        .sum();

        return Math.sqrt(rawSum / (collection.size() - 1));
    }

}
