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

import edu.pitt.dbmi.lib.math.classification.roc.ROC;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

/**
 *
 * May 17, 2023 11:30:40 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ROCCurvePlot extends AbstractROCCurvePlot {

    public ROCCurvePlot() {
        this("");
    }

    public ROCCurvePlot(String title) {
        this(title, "1-Specificity (FPR)", "Sensitivity (TPR)");
    }

    public ROCCurvePlot(String title, String xAxisLabel, String yAxisLabel) {
        super(title, xAxisLabel, yAxisLabel);
    }

    public void add(ROC roc, String key, String label, Color color, Shape shape, BasicStroke line) {
        addDataSeries(key, label, roc.getFalsePositiveRates(), roc.getTruePositiveRates(), color, shape, line);
    }

}
