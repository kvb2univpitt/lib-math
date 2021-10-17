package edu.pitt.dbmi.lib.math.io;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * Oct 17, 2021 10:33:33 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class FileUtility {

    private static final Pattern SPACE_DELIM = Pattern.compile("\\s+");

    private FileUtility() {
    }

    public static List<ProbabilityValue> readInProbabilityValues(Path file) throws IOException {
        List<ProbabilityValue> list = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] fields = SPACE_DELIM.split(line.trim());
                if (fields.length == 2) {
                    list.add(new ProbabilityValue(Byte.parseByte(fields[0]), Double.parseDouble(fields[1])));
                }
            }
        }

        return Collections.unmodifiableList(list);
    }

}
