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

import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.plot.PlotColors;
import edu.pitt.dbmi.lib.math.classification.roc.DeLongROCCurve;
import edu.pitt.dbmi.lib.math.classification.roc.ROC;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoader;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoaderTest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * May 17, 2023 11:36:06 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ROCCurvePlotTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of saveImageAsPNG method, of class HosmerLemeshowPlot.
     */
    @Test
    public void testSaveImageAsPNG() throws Exception {

        ROCCurvePlot rocCurvePlot = new ROCCurvePlot("Test ROC Curve Plot");
        rocCurvePlot.add(getROC("/data/data.csv", Delimiters.COMMA), "", "data", PlotColors.BLUE);
        rocCurvePlot.add(getROC("/data/data3.csv", Delimiters.COMMA), "", "data3", PlotColors.DARK_ORANGE);
        rocCurvePlot.add(getROC("/data/data4.csv", Delimiters.COMMA), "", "data4", PlotColors.MAGENTA);
        rocCurvePlot.add(getROC("/data/edge_data.csv", Delimiters.COMMA), "", "edge_data", PlotColors.DARK_VIOLET);

        String dirOut = Files.createDirectory(Paths.get(tempDir.toString(), "plot")).toString();
        File imageFile = Paths.get(dirOut, "chart.png").toFile();
        int width = 800;
        int height = 800;
        rocCurvePlot.saveImageAsPNG(imageFile, width, height);
    }

    private ROC getROC(String file, Pattern delimiter) throws Exception {
        Path dataFile = Paths.get(ResourcesLoaderTest.class.getResource(file).getFile());
        int observedColumn = 1;
        int predictedColumn = 2;
        boolean hasHeader = false;
        List<ObservedPredictedValue> observedPredictedValues = ResourcesLoader.loadData(dataFile, delimiter, observedColumn, predictedColumn, hasHeader);

        return new DeLongROCCurve(observedPredictedValues);
    }

}
