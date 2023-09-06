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
package edu.pitt.dbmi.lib.math.classification.utils;

import edu.pitt.dbmi.lib.math.classification.data.Delimiters;
import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Mar 6, 2023 1:51:41 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ResourcesLoaderTest {

    /**
     * Test of loadObservedPredictedValues method, of class ResourcesLoader.
     */
    @Test
    public void testLoadObservedPredictedValues() throws Exception {
        Path file = Paths.get(ResourcesLoaderTest.class.getResource("/data/obs_pred_vals.csv").getFile());
        Pattern delimiter = Delimiters.COMMA;
        int observedColumn = 1;
        int predictedColumn = 2;
        boolean hasHeader = false;
        List<ObservedPredictedValue> observedPredictedValues = ResourcesLoader.loadData(file, delimiter, observedColumn, predictedColumn, hasHeader);

        int result = observedPredictedValues.size();
        int expResult = 1411;
        Assertions.assertEquals(expResult, result);
    }

}
