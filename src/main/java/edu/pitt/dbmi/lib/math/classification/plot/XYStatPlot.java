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
package edu.pitt.dbmi.lib.math.classification.plot;

import edu.pitt.dbmi.lib.math.classification.plot.Plot;

/**
 *
 * Oct 26, 2011 4:22:53 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public interface XYStatPlot extends Plot {

    public boolean isGridLineVisible();

    public boolean isPlotOutlineVisible();

    public void setTitle(final String title);

    public String getTitle();

    public void setXAxisLabel(final String label);

    public String getXAxisLabel();

    public void setYAxisLabel(final String label);

    public String getYAxisLabel();

    public void setXAxisRange(final double lower, final double upper);

    public void setYAxisRange(final double lower, final double upper);

    public void setGridLineVisible(final boolean visible);

    public void setPlotOutlineVisible(final boolean visible);

}
