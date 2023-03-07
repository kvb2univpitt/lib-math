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

import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * Mar 6, 2023 1:39:09 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class ResourcesLoader {

    private ResourcesLoader() {
    }

    public static ObservedPredictedValue[] loadObservedPredictedValues(Path file, Pattern delimiter) throws IOException {
        List<ObservedPredictedValue> values = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();

                // skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                String[] fields = delimiter.split(line.trim());
                if (fields.length == 2) {
                    values.add(new ObservedPredictedValue(
                            Integer.parseInt(fields[0]),
                            Double.parseDouble(fields[1])));
                }
            }
        }

        return values.stream().toArray(ObservedPredictedValue[]::new);
    }

}
