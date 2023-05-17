/*
 * Copyright (C) 2023 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.lib.math.classification.roc.plot;

import edu.pitt.dbmi.lib.math.classification.plot.AbstractXYStatPlot;
import edu.pitt.dbmi.lib.math.classification.plot.PlotShapeFactory;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * May 17, 2023 10:19:51 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractROCCurvePlot extends AbstractXYStatPlot {

    protected XYLineAndShapeRenderer fourtyFiveDegreeLineRenderer;

    protected static final Shape CIRCLE = PlotShapeFactory.createCircle(0.25);

    protected XYSeriesCollection pointDataset;
    protected XYSeriesCollection shapeDataset;

    protected XYLineAndShapeRenderer pointRender;
    protected XYLineAndShapeRenderer shapeRender;

    protected Map<String, XYSeries> dataSeriesMap;  // render line and color
    protected Map<String, XYSeries> dataShapeSeriesMap;  // render shape and color

    public AbstractROCCurvePlot(String title, String xAxisLabel, String yAxisLabel) {
        super(title, xAxisLabel, yAxisLabel);

        fourtyFiveDegreeLineRenderer = new XYLineAndShapeRenderer();
        fourtyFiveDegreeLineRenderer.setSeriesShapesVisible(0, false);  // do not show shape for random line
        fourtyFiveDegreeLineRenderer.setSeriesVisibleInLegend(0, false);
        fourtyFiveDegreeLineRenderer.setSeriesStroke(0, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        fourtyFiveDegreeLineRenderer.setSeriesPaint(0, Color.GRAY);

        XYSeries fourtyFiveDegreeSeries = new XYSeries("");
        fourtyFiveDegreeSeries.add(-0.02, -0.02);
        fourtyFiveDegreeSeries.add(1.02, 1.02);
        plot.setRenderer(0, fourtyFiveDegreeLineRenderer);
        plot.setDataset(0, new XYSeriesCollection(fourtyFiveDegreeSeries));

        pointDataset = new XYSeriesCollection();
        plot.setDataset(1, pointDataset);

        shapeDataset = new XYSeriesCollection();
        plot.setDataset(2, shapeDataset);

        pointRender = new XYLineAndShapeRenderer();
        plot.setRenderer(1, pointRender);

        shapeRender = new XYLineAndShapeRenderer();
        shapeRender.setSeriesVisibleInLegend(2, false);
        plot.setRenderer(2, shapeRender);

        dataSeriesMap = new HashMap<String, XYSeries>();
        dataShapeSeriesMap = new HashMap<String, XYSeries>();
    }

    public void setFourtyFiveDegreeLineVisible(boolean visible) {
        fourtyFiveDegreeLineRenderer.setSeriesVisible(0, visible);
    }

    public boolean isFourtyFiveDegreeLineVisible() {
        return fourtyFiveDegreeLineRenderer.getSeriesShapesVisible(0);
    }

    public void setDataSeriesLine(final String key, final BasicStroke line) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesStroke(pointDataset.indexOf(series), line);
    }

    public void setDataSeriesColor(final String key, final Color color) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesPaint(pointDataset.indexOf(series), color);

        series = dataShapeSeriesMap.get(key);
        shapeRender.setSeriesPaint(shapeDataset.indexOf(series), color);
    }

    public void setDataSeriesVisible(final String key, final boolean flag) {
        XYSeries series = dataSeriesMap.get(key);
        pointRender.setSeriesVisible(pointDataset.indexOf(series), flag);

        series = dataShapeSeriesMap.get(key);
        shapeRender.setSeriesVisible(shapeDataset.indexOf(series), flag);
    }

    public void setDataSeriesShape(final String key, final Shape shape) {
        XYSeries series = dataShapeSeriesMap.get(key);
        shapeRender.setSeriesShape(shapeDataset.indexOf(series), shape);
    }

    public void setDataSeriesName(final String key, final String name) {
        XYSeries series = dataSeriesMap.get(key);
        series.setKey(name);
        series.fireSeriesChanged();

        series = dataShapeSeriesMap.get(key);
        series.setKey(name);
        series.fireSeriesChanged();
    }

    public void addDataSeries(
            final String key, final String label,
            final double[] xPoints, final double[] yPoints,
            final Color color, final Shape shape, final BasicStroke line) {
        // data points
        XYSeries plotDataPoints = createPlotDataPoints(label, xPoints, yPoints);
        pointDataset.addSeries(plotDataPoints);
        int seriesIndex = pointDataset.indexOf(plotDataPoints);
        pointRender.setSeriesPaint(seriesIndex, color);
        pointRender.setSeriesShape(seriesIndex, CIRCLE);
        pointRender.setSeriesStroke(seriesIndex, line);
        pointRender.setSeriesVisibleInLegend(seriesIndex, false);
        dataSeriesMap.put(key, plotDataPoints);

        // data shape points
        XYSeries plotDataShapPoints = createPlotDataShapePoints(label, xPoints, yPoints);
        shapeDataset.addSeries(plotDataShapPoints);
        seriesIndex = shapeDataset.indexOf(plotDataShapPoints);
        shapeRender.setSeriesShape(seriesIndex, shape);
        shapeRender.setSeriesPaint(seriesIndex, color);
        shapeRender.setSeriesLinesVisible(seriesIndex, false);
        shapeRender.setSeriesVisibleInLegend(seriesIndex, true);
        dataShapeSeriesMap.put(key, plotDataShapPoints);

        chart.fireChartChanged();
    }

    private XYSeries createPlotDataShapePoints(final String label, final double[] xPoints, final double[] yPoints) {
        XYSeries series = new XYSeries(label);

        int numOfPoints = xPoints.length;
        if (numOfPoints <= 10) {
            for (int i = 0; i < numOfPoints; i++) {
                series.add(xPoints[i], yPoints[i]);
            }
        } else {
            // the end points
            double minX = Double.MAX_VALUE;
            double minY = 0;
            double maxX = Double.MIN_VALUE;
            double maxY = 0;

            boolean[] addedInInterval = new boolean[9];
            for (int i = 0; i < numOfPoints; i++) {
                double xPoint = xPoints[i];

                // find min endpoints and max endpoints
                if (xPoint < minX) {
                    minX = xPoint;
                    minY = yPoints[i];
                }
                if (xPoint > maxX) {
                    maxX = xPoint;
                    maxY = yPoints[i];
                }

                if (xPoint > 0.0 && xPoint <= 0.1) {
                    if (!addedInInterval[0]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[0] = true;
                    }
                } else if (xPoint > 0.1 && xPoint <= 0.2) {
                    if (!addedInInterval[1]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[1] = true;
                    }
                } else if (xPoint > 0.2 && xPoint <= 0.3) {
                    if (!addedInInterval[2]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[2] = true;
                    }
                } else if (xPoint > 0.4 && xPoint <= 0.5) {
                    if (!addedInInterval[3]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[3] = true;
                    }
                } else if (xPoint > 0.5 && xPoint <= 0.6) {
                    if (!addedInInterval[4]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[4] = true;
                    }
                } else if (xPoint > 0.6 && xPoint <= 0.7) {
                    if (!addedInInterval[5]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[5] = true;
                    }
                } else if (xPoint > 0.7 && xPoint <= 0.8) {
                    if (!addedInInterval[6]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[6] = true;
                    }
                } else if (xPoint > 0.8 && xPoint <= 0.9) {
                    if (!addedInInterval[7]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[7] = true;
                    }
                } else if (xPoint > 0.9 && xPoint <= 1.0) {
                    if (!addedInInterval[8]) {
                        series.add(xPoints[i], yPoints[i]);
                        addedInInterval[8] = true;
                    }
                }
            }

            // add end points
            series.add(minX, minY);
            series.add(maxX, maxY);
        }

        // add end points
        series.add(0.0, 0.0);
        series.add(1.0, 1.0);

        return series;
    }

    private XYSeries createPlotDataPoints(final String label, final double[] xPoints, final double[] yPoints) {
        XYSeries series = new XYSeries(label);

        // add plot points
        for (int i = 0; i < xPoints.length; i++) {
            series.add(xPoints[i], yPoints[i]);
        }

        // add end points
        series.add(0.0, 0.0);
        series.add(1.0, 1.0);

        return series;
    }

}
