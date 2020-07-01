package com.o3.storyinspector.viztool.sentiment;

import java.awt.*;

public class SentimentColor {

    public static String getSentimentColorCode(final double sentimentScore) {
        final Color color = new Color(getRed(sentimentScore), getGreen(sentimentScore), getBlue(sentimentScore));
        return getHTMLColorString(color);
    }

    private static int getRed(final double sentimentScore) {
        if (sentimentScore > 0) {
            return (int) ((1 - sentimentScore) * 255);
        } else {
            return 255;
        }
    }

    private static int getGreen(final double sentimentScore) {
        if (sentimentScore < 0) {
            return (int) ((1 - (sentimentScore * -1)) * 255);
        } else {
            return 255;
        }
    }

    private static int getBlue(final double sentimentScore) {
        return 0;
    }

    private static String getHTMLColorString(Color color) {
        String red = Integer.toHexString(color.getRed());
        String green = Integer.toHexString(color.getGreen());
        String blue = Integer.toHexString(color.getBlue());

        return "#" +
                (red.length() == 1 ? "0" + red : red) +
                (green.length() == 1 ? "0" + green : green) +
                (blue.length() == 1 ? "0" + blue : blue);
    }

}
