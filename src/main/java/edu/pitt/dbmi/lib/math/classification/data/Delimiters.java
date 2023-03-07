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
package edu.pitt.dbmi.lib.math.classification.data;

import java.util.regex.Pattern;

/**
 *
 * Mar 6, 2023 1:42:12 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class Delimiters {

    public static final Pattern SPACE = Pattern.compile("\\s");

    public static final Pattern SPACES = Pattern.compile("\\s+");

    public static final Pattern COMMA = Pattern.compile(",");

    public static final Pattern TAB = Pattern.compile("\t");

    private Delimiters() {
    }

}
