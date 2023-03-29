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
package edu.pitt.dbmi.lib.math.classification.calibration;

import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
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
 * Mar 6, 2023 3:10:52 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowRiskGroupTest {

    private static HosmerLemeshowRiskGroup hlstat;

    @BeforeAll
    public static void setUpClass() throws IOException {
        Path file = Paths.get(ResourcesLoaderTest.class.getResource("/data/obs_pred_vals.txt").getFile());
        Pattern delimiter = Delimiters.COMMA;
        ObservedPredictedValue[] observedPredictedValues = ResourcesLoader.loadObservedPredictedValues(file, delimiter);

        hlstat = new HosmerLemeshowRiskGroup(observedPredictedValues);
    }

    /**
     * Test of toString method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testToString() {
        Assertions.assertNotNull(hlstat.toString());
    }

    /**
     * Test of getSummary method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetSummary() {
        Assertions.assertNotNull(hlstat.toString());
    }

    /**
     * Test of getNumberOfPredictions method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetNumberOfPredictions() {
        int result = hlstat.getNumberOfPredictions();
        int expResult = 1411;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getObservedValues method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetObservedValues() {
        int result = hlstat.getObservedValues().length;
        int expResult = 1411;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getPredictedValues method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetPredictedValues() {
        int result = hlstat.getPredictedValues().length;
        int expResult = 1411;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getGroups method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetGroups() {
        int result = hlstat.getGroups().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getNumberOfDataPerGroup method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetNumberOfDataPerGroup() {
        int result = hlstat.getNumberOfDataPerGroup().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getPositiveObservedSumPerGroup method, of class
     * AbstractHosmerLemeshow.
     */
    @Test
    public void testGetPositiveObservedSumPerGroup() {
        int result = hlstat.getPositiveObservedSumPerGroup().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getPredictedSumPerGroup method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetPredictedSumPerGroup() {
        int result = hlstat.getPredictedSumPerGroup().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getHlChi2PerGroup method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetHlChi2PerGroup() {
        int result = hlstat.getHlChi2PerGroup().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getMarginOfErrorPerGroup method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetMarginOfErrorPerGroup() {
        int result = hlstat.getMarginOfErrorPerGroup().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getHlObservedValues method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetHlObservedValues() {
        int result = hlstat.getHlObservedValues().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getHlExpectedValues method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetHlExpectedValues() {
        int result = hlstat.getHlExpectedValues().length;
        int expResult = 9;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getDegreesOfFreedom method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetDegreesOfFreedom() {
        int result = hlstat.getDegreesOfFreedom();
        int expResult = 7;

        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getPValue method, of class AbstractHosmerLemeshow.
     */
    @Test
    public void testGetPValue() {
        double result = hlstat.getPValue();
        double expResult = 0;

        Assertions.assertEquals(expResult, result);
    }

}
