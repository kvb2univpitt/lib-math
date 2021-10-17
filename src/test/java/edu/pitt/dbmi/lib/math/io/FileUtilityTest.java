package edu.pitt.dbmi.lib.math.io;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * Oct 17, 2021 10:35:55 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class FileUtilityTest {

    /**
     * Test of readInProbabilityValues method, of class FileUtility.
     */
    @Test
    public void testReadInProbabilityValues() throws Exception {
        Path file = Paths.get("src/test/resources/data/prob_vals.txt");
        List<ProbabilityValue> probValues = FileUtility.readInProbabilityValues(file);

        int expResult = 1411;
        int result = probValues.size();
        Assertions.assertEquals(expResult, result);
    }

}
