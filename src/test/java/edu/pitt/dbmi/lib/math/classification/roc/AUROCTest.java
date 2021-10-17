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
 * Oct 17, 2021 11:05:17 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class AUROCTest {

    @Test
    public void testAUROC() throws IOException {
        Path file = Paths.get("src/test/resources/data/prob_vals.txt");
        List<ProbabilityValue> probValues = FileUtility.readInProbabilityValues(file);

        AUROC auroc = new AUROC(probValues);

        double expResult = 0.7227832330271359;
        double result = auroc.getAreaUnderCurve();
        Assertions.assertEquals(expResult, result, 0.0);
    }

}
