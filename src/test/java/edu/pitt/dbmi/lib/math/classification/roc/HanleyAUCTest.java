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
 * Oct 17, 2021 5:50:11 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HanleyAUCTest {

    /**
     * Test of getStandardError method, of class HanleyAUC.
     */
    @Test
    public void testHanleyAUC() throws IOException {
        Path file = Paths.get("src/test/resources/data/prob_vals.txt");
        List<ProbabilityValue> probValues = FileUtility.readInProbabilityValues(file);

        HanleyAUC hanleyAUC = new HanleyAUC(new AUROC(probValues));
        double expResult = 0.013298849428474419;
        double result = hanleyAUC.getStandardError();
        Assertions.assertEquals(expResult, result, 0.0);
    }

}
