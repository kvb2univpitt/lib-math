package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;
import edu.pitt.dbmi.lib.math.io.FileUtility;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Oct 17, 2021 5:45:12 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DeLongAUCTest {

    /**
     * Test of getStandardError method, of class DeLongAUC.
     */
    @Test
    public void testDeLongAUC() throws IOException {
        Path file = Paths.get("src/test/resources/data/prob_vals.txt");
        List<ProbabilityValue> probValues = FileUtility.readInProbabilityValues(file);

        DeLongAUC deLongAUC = new DeLongAUC(new AUROC(probValues));

        double expResult = 0.013540389479730817;
        double result = deLongAUC.getStandardError();
        Assertions.assertEquals(expResult, result, 0.0);
    }

}
