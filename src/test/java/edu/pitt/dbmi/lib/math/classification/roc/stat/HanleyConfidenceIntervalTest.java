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
package edu.pitt.dbmi.lib.math.classification.roc.stat;

import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import edu.pitt.dbmi.lib.math.classification.roc.DeLongROCCurve;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoader;
import edu.pitt.dbmi.lib.math.classification.utils.ResourcesLoaderTest;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * Mar 7, 2023 4:02:24 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HanleyConfidenceIntervalTest {

    private static ConfidenceInterval confidenceInterval;

    @BeforeAll
    public static void setUpClass() throws IOException {
        Path file = Paths.get(ResourcesLoaderTest.class.getResource("/data/obs_pred_vals.csv").getFile());
        Pattern delimiter = Delimiters.COMMA;
        ObservedPredictedValue[] observedPredictedValues = ResourcesLoader.loadObservedPredictedValues(file, delimiter);
        DeLongROCCurve deLongROCCurve = new DeLongROCCurve(observedPredictedValues);

        confidenceInterval = new HanleyConfidenceInterval(deLongROCCurve);
    }

    /**
     * Test of getStandardError method, of class ConfidenceInterval.
     */
    @Test
    public void testGetStandardError() {
        double expResult = 0.013298849428474424;
        double result = confidenceInterval.getStandardError();

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getLowerConfidenceInterval method, of class ConfidenceInterval.
     */
    @Test
    public void testGetLowerConfidenceInterval() {
        double expResult = 0.6967174881473259;
        double result = confidenceInterval.getLowerConfidenceInterval();

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getUpperConfidenceInterval method, of class ConfidenceInterval.
     */
    @Test
    public void testGetUpperConfidenceInterval() {
        double expResult = 0.7488489779069456;
        double result = confidenceInterval.getUpperConfidenceInterval();

        Assertions.assertEquals(expResult, result);
    }

}
