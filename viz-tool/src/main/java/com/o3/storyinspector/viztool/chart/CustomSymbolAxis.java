package com.o3.storyinspector.viztool.chart;

import org.jfree.chart.axis.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.SerialUtils;
import org.jfree.data.Range;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CustomSymbolAxis extends NumberAxis implements Serializable {

    private static final long serialVersionUID = 7216330468770619716L;
    public static final Paint DEFAULT_GRID_BAND_PAINT = new Color(232, 234, 232, 128);
    public static final Paint DEFAULT_GRID_BAND_ALTERNATE_PAINT = new Color(0, 0, 0, 0);
    private Map<Integer, String> symbols;
    private boolean gridBandsVisible;
    private transient Paint gridBandPaint;
    private transient Paint gridBandAlternatePaint;

    public CustomSymbolAxis(String label, Map<Integer, String> symbols) {
        super(label);
        this.symbols = symbols;
        this.gridBandsVisible = true;
        this.gridBandPaint = DEFAULT_GRID_BAND_PAINT;
        this.gridBandAlternatePaint = DEFAULT_GRID_BAND_ALTERNATE_PAINT;
        this.setAutoTickUnitSelection(false, false);
        this.setAutoRangeStickyZero(false);
    }

    public String[] getSymbols() {
        String[] result = new String[this.symbols.values().size()];
        result = (String[]) ((String[]) this.symbols.values().toArray(result));
        return result;
    }

    public boolean isGridBandsVisible() {
        return this.gridBandsVisible;
    }

    public void setGridBandsVisible(boolean flag) {
        this.gridBandsVisible = flag;
        this.fireChangeEvent();
    }

    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    public void setGridBandPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.gridBandPaint = paint;
        this.fireChangeEvent();
    }

    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    public void setGridBandAlternatePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.gridBandAlternatePaint = paint;
        this.fireChangeEvent();
    }

    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        throw new UnsupportedOperationException();
    }

    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState info = new AxisState(cursor);
        if (this.isVisible()) {
            info = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        }

        if (this.gridBandsVisible) {
            this.drawGridBands(g2, plotArea, dataArea, edge, info.getTicks());
        }

        return info;
    }

    protected void drawGridBands(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, java.util.List ticks) {
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.drawGridBandsHorizontal(g2, plotArea, dataArea, true, ticks);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            this.drawGridBandsVertical(g2, plotArea, dataArea, true, ticks);
        }

        g2.setClip(savedClip);
    }

    protected void drawGridBandsHorizontal(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, java.util.List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double yy = dataArea.getY();
        double outlineStrokeWidth = 1.0D;
        Stroke outlineStroke = this.getPlot().getOutlineStroke();
        if (outlineStroke != null && outlineStroke instanceof BasicStroke) {
            outlineStrokeWidth = (double) ((BasicStroke) outlineStroke).getLineWidth();
        }

        for (Iterator iterator = ticks.iterator(); iterator.hasNext(); currentGridBandIsDark = !currentGridBandIsDark) {
            ValueTick tick = (ValueTick) iterator.next();
            double xx1 = this.valueToJava2D(tick.getValue() - 0.5D, dataArea, RectangleEdge.BOTTOM);
            double xx2 = this.valueToJava2D(tick.getValue() + 0.5D, dataArea, RectangleEdge.BOTTOM);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }

            Rectangle2D band = new Rectangle2D.Double(Math.min(xx1, xx2), yy + outlineStrokeWidth, Math.abs(xx2 - xx1), dataArea.getMaxY() - yy - outlineStrokeWidth);
            g2.fill(band);
        }

    }

    protected void drawGridBandsVertical(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, java.util.List ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double xx = dataArea.getX();
        double outlineStrokeWidth = 1.0D;
        Stroke outlineStroke = this.getPlot().getOutlineStroke();
        if (outlineStroke != null && outlineStroke instanceof BasicStroke) {
            outlineStrokeWidth = (double) ((BasicStroke) outlineStroke).getLineWidth();
        }

        for (Iterator iterator = ticks.iterator(); iterator.hasNext(); currentGridBandIsDark = !currentGridBandIsDark) {
            ValueTick tick = (ValueTick) iterator.next();
            double yy1 = this.valueToJava2D(tick.getValue() + 0.5D, dataArea, RectangleEdge.LEFT);
            double yy2 = this.valueToJava2D(tick.getValue() - 0.5D, dataArea, RectangleEdge.LEFT);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }

            Rectangle2D band = new Rectangle2D.Double(xx + outlineStrokeWidth, Math.min(yy1, yy2), dataArea.getMaxX() - xx - outlineStrokeWidth, Math.abs(yy2 - yy1));
            g2.fill(band);
        }

    }

    protected void autoAdjustRange() {
        Plot plot = this.getPlot();
        if (plot != null) {
            if (plot instanceof ValueAxisPlot) {
                double upper = (double) (this.symbols.size() - 1);
                double lower = 0.0D;
                double range = upper - lower;
                double minRange = this.getAutoRangeMinimumSize();
                if (range < minRange) {
                    upper = (upper + lower + minRange) / 2.0D;
                    lower = (upper + lower - minRange) / 2.0D;
                }

                double upperMargin = 0.5D;
                double lowerMargin = 0.5D;
                if (this.getAutoRangeIncludesZero()) {
                    if (this.getAutoRangeStickyZero()) {
                        if (upper <= 0.0D) {
                            upper = 0.0D;
                        } else {
                            upper += upperMargin;
                        }

                        if (lower >= 0.0D) {
                            lower = 0.0D;
                        } else {
                            lower -= lowerMargin;
                        }
                    } else {
                        upper = Math.max(0.0D, upper + upperMargin);
                        lower = Math.min(0.0D, lower - lowerMargin);
                    }
                } else if (this.getAutoRangeStickyZero()) {
                    if (upper <= 0.0D) {
                        upper = Math.min(0.0D, upper + upperMargin);
                    } else {
                        upper += upperMargin * range;
                    }

                    if (lower >= 0.0D) {
                        lower = Math.max(0.0D, lower - lowerMargin);
                    } else {
                        lower -= lowerMargin;
                    }
                } else {
                    upper += upperMargin;
                    lower -= lowerMargin;
                }

                this.setRange(new Range(lower, upper), false, false);
            }

        }
    }

    public java.util.List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        java.util.List ticks = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            ticks = this.refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            ticks = this.refreshTicksVertical(g2, dataArea, edge);
        }

        return ticks;
    }

    protected java.util.List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        java.util.List ticks = new ArrayList();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = this.getTickUnit().getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0D;
        double previousDrawnTickLabelLength = 0.0D;
        if (count <= 500) {
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double) i * size;
                double xx = this.valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel;
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = this.valueToString(currentTickValue);
                }

                Rectangle2D bounds = TextUtils.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = this.isVerticalTickLabels() ? bounds.getHeight() : bounds.getWidth();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0D;
                    if (Math.abs(xx - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }

                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = xx;
                    previousDrawnTickLabelLength = tickLabelLength;
                }

                double angle = 0.0D;
                TextAnchor rotationAnchor;
                TextAnchor anchor;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = 1.5707963267948966D;
                    } else {
                        angle = -1.5707963267948966D;
                    }
                } else if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                } else {
                    anchor = TextAnchor.TOP_CENTER;
                    rotationAnchor = TextAnchor.TOP_CENTER;
                }

                Tick tick = new NumberTick(currentTickValue, tickLabel, anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }

        return ticks;
    }

    protected java.util.List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = this.getTickUnit().getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0D;
        double previousDrawnTickLabelLength = 0.0D;
        if (count <= 500) {
            for (int i = 0; i < count; ++i) {
                double currentTickValue = lowestTickValue + (double) i * size;
                double yy = this.valueToJava2D(currentTickValue, dataArea, edge);
                NumberFormat formatter = this.getNumberFormatOverride();
                String tickLabel;
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = this.valueToString(currentTickValue);
                }

                Rectangle2D bounds = TextUtils.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = this.isVerticalTickLabels() ? bounds.getWidth() : bounds.getHeight();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0D;
                    if (Math.abs(yy - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }

                if (tickLabelsOverlapping) {
                    tickLabel = "";
                } else {
                    previousDrawnTickLabelPos = yy;
                    previousDrawnTickLabelLength = tickLabelLength;
                }

                double angle = 0.0D;
                TextAnchor rotationAnchor;
                TextAnchor anchor;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -1.5707963267948966D;
                    } else {
                        angle = 1.5707963267948966D;
                    }
                } else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }

                Tick tick = new NumberTick(currentTickValue, tickLabel, anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }

        return ticks;
    }

    public String valueToString(double value) {
        String strToReturn;
        try {
            strToReturn = this.symbols.get((int) value);
        } catch (IndexOutOfBoundsException var5) {
            strToReturn = "";
        }

        return strToReturn;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof org.jfree.chart.axis.SymbolAxis)) {
            return false;
        } else {
            org.jfree.chart.axis.SymbolAxis that = (org.jfree.chart.axis.SymbolAxis) obj;
            if (!this.getSymbols().equals(that.getSymbols())) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writePaint(this.gridBandPaint, stream);
        SerialUtils.writePaint(this.gridBandAlternatePaint, stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.gridBandPaint = SerialUtils.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtils.readPaint(stream);
    }

}
