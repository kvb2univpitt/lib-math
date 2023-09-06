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
package edu.pitt.dbmi.lib.math.classification.calibration.plot;

import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshow;
import edu.pitt.dbmi.lib.math.classification.calibration.HosmerLemeshowRiskGroup;
import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.plot.PlotColors;
import edu.pitt.dbmi.lib.math.classification.plot.PlotShapes;
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
 * Mar 7, 2023 6:11:09 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowPlotTest {

    @TempDir
    public static Path tempDir;

    /**
     * Test of saveImageAsPNG method, of class HosmerLemeshowPlot.
     */
    @Test
    public void testSaveImageAsPNG() throws Exception {
        Path dataFile = Paths.get(ResourcesLoaderTest.class.getResource("/data/obs_pred_vals.csv").getFile());
        Pattern delimiter = Delimiters.COMMA;
        int observedColumn = 1;
        int predictedColumn = 2;
        boolean hasHeader = false;
        List<ObservedPredictedValue> observedPredictedValues = ResourcesLoader.loadData(dataFile, delimiter, observedColumn, predictedColumn, hasHeader);

        HosmerLemeshow hl = new HosmerLemeshowRiskGroup(observedPredictedValues);

        String title = "Hosmer Lemeshow Chart";
        String name = "rfci-bootstrap";
        HosmerLemeshowPlot plot = new HosmerLemeshowPlot(title);
        plot.addDataSeries(hl, name, name, PlotColors.BLUE, PlotShapes.CIRCLE_SHAPE, true);

        String dirOut = Files.createDirectory(Paths.get(tempDir.toString(), "plot")).toString();
        File imageFile = Paths.get(dirOut, "chart.png").toFile();
        int width = 800;
        int height = 800;
        plot.saveImageAsPNG(imageFile, width, height);
    }

}
