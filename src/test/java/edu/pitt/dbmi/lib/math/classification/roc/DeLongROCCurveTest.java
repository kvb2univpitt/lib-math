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
package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoader;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoaderTest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * Mar 7, 2023 1:46:45 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DeLongROCCurveTest {

    private static DeLongROCCurve roc;

    @BeforeAll
    public static void setUpClass() throws IOException {
        Path file = Paths.get(ResourcesLoaderTest.class.getResource("/data/obs_pred_vals.csv").getFile());
        Pattern delimiter = Delimiters.COMMA;
        int observedColumn = 1;
        int predictedColumn = 2;
        boolean hasHeader = false;
        List<ObservedPredictedValue> observedPredictedValues = ResourcesLoader.loadData(file, delimiter, observedColumn, predictedColumn, hasHeader);

        roc = new DeLongROCCurve(observedPredictedValues);
    }

    /**
     * Test of getTruePositiveRates method, of class DeLongROCCurve.
     */
    @Test
    public void testGetTruePositiveRates() {
        int expResult = 1411;
        int result = roc.getTruePositiveRates().length;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getFalsePositiveRates method, of class DeLongROCCurve.
     */
    @Test
    public void testGetFalsePositiveRates() {
        int expResult = 1411;
        int result = roc.getFalsePositiveRates().length;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getConfusionMatrices method, of class DeLongROCCurve.
     */
    @Test
    public void testGetConfusionMatrices() {
        int expResult = 1411;
        int result = roc.getConfusionMatrices().length;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getNumberOfPositives method, of class DeLongROCCurve.
     */
    @Test
    public void testGetNumberOfPositives() {
        int expResult = 861;
        int result = roc.getNumberOfPositives();

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getNumberOfNegatives method, of class DeLongROCCurve.
     */
    @Test
    public void testGetNumberOfNegatives() {
        int expResult = 550;
        int result = roc.getNumberOfNegatives();

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getAreaUnderRocCurve method, of class DeLongROCCurve.
     */
    @Test
    public void testGetAreaUnderRocCurve() {
        double expResult = 0.7227832330271358;
        double result = roc.getAreaUnderRocCurve();

        Assertions.assertEquals(expResult, result);
    }

}
