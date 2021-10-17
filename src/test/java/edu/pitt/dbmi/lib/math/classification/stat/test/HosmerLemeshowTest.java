package edu.pitt.dbmi.lib.math.classification.stat.test;

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
 * Oct 17, 2021 5:54:51 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowTest {

    /**
     * Test of toString method, of class HosmerLemeshow.
     */
    @Test
    public void testHosmerLemeshow() throws IOException {
        Path file = Paths.get("src/test/resources/data/prob_vals.txt");
        List<ProbabilityValue> probValues = FileUtility.readInProbabilityValues(file);

        HosmerLemeshow hl = new HosmerLemeshow(probValues, HosmerLemeshow.DataGroupType.RISK_GROUPS);
        double expResult = 0.0;
        double result = hl.getpValue();
        Assertions.assertEquals(expResult, result, 0.0);
    }

}
