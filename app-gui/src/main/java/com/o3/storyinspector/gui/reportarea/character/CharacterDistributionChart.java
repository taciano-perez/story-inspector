package com.o3.storyinspector.gui.reportarea.character;

import com.o3.storyinspector.gui.core.domain.Character;
import com.o3.storyinspector.gui.core.domain.Characters;
import com.o3.storyinspector.gui.utils.StringFormatter;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.Comparator;

public class CharacterDistributionChart extends BarChart {

    static int TRIM_LEN = 20;

    public static CharacterDistributionChart build(final Characters characters) {
        // chapters axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(-45);
        // number of words axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("% Chapters");

        final CharacterDistributionChart chart = new CharacterDistributionChart(xAxis, yAxis);

        // add data
        Series dataSeries1 = new Series();
        characters.getCharacters().stream()
                .sorted(Comparator.comparingDouble(Character::getTotalPercentageOfChapters).reversed())
                .forEach(character -> {
            final Data data = new Data<>(StringFormatter.trimText(character.getName(), TRIM_LEN),
                    character.getTotalPercentageOfChapters());
            dataSeries1.getData().add(data);
        });
        chart.getData().add(dataSeries1);

        // tooltip (hover box)
        dataSeries1.getData()
                .forEach( item -> {
                    final Data data = (Data) item;
                    final String percentage = StringFormatter.formatPercentage((double)data.getYValue());
                    final Tooltip tooltip = new Tooltip(data.getXValue() + "\n" + percentage + " of all chapters");
                    tooltip.setShowDelay(Duration.seconds(0));
                    tooltip.setShowDuration(Duration.INDEFINITE);
                    Tooltip.install(data.getNode(), tooltip);
                });

        // formatting
        chart.setLegend(null);
        chart.setMinHeight(300);
        chart.setMinWidth(35 * characters.getCharacters().size());

        return chart;
    }

    private CharacterDistributionChart(Axis axis, Axis axis1) {
        super(axis, axis1);
    }
}
