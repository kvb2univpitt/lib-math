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
package edu.pitt.dbmi.lib.math.classification.calibration;

import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.util.Arrays;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 *
 * Mar 6, 2023 2:20:24 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractHosmerLemeshow implements HosmerLemeshow {

    protected String summary;

    protected int numberOfPredictions;

    /**
     * Actual measured value. Value should be 0 or 1.
     */
    protected int[] observedValues;

    /**
     * Value predicted by classifier. Value should be between 0 and 1,
     * inclusive.
     */
    protected double[] predictedValues;

    /**
     * Contains the group number. The first interval is first group and so on...
     */
    protected int[] groups;

    /**
     * Contains the number of data in each group.
     */
    protected int[] numberOfDataPerGroup;

    /**
     * Contains the sum of actual positive outcomes within each group.
     */
    protected int[] positiveObservedSumPerGroup;

    /**
     * Contains the sum of all the predicted outcomes within each group.
     */
    protected double[] predictedSumPerGroup;

    /**
     * Contains the Hosmer-Lemeshow chi square statistic for each group.
     */
    protected double[] hlChi2PerGroup;

    /**
     * Contains the margin of errors for each group.
     */
    protected double[] marginOfErrorPerGroup;

    /**
     * Contains the average of observed positive outcome for each group
     * (interval);<br>
     * observed_avg = (# of positive observed outcome) / (# of data in the
     * interval)<br>
     * This is the values for the y-axis in the HL plot.
     */
    protected double[] hlObservedValues;

    /**
     * Contains the average of predicted outcomes for each group (interval)<br>
     * predicted_avg = (sum of the predicted outcomes) / (# of data in the
     * interval)<br>
     * This is the values for the x-axis in the HL plot.
     */
    protected double[] hlExpectedValues;

    protected int degreesOfFreedom;

    protected double pValue;

    public AbstractHosmerLemeshow(ObservedPredictedValue[] observedPredictedValues) {
        if (observedPredictedValues == null || observedPredictedValues.length == 0) {
            throw new IllegalArgumentException("Observed values and predicted values are required.");
        }

        // copy the array
        observedPredictedValues = Arrays.stream(observedPredictedValues)
                .toArray(ObservedPredictedValue[]::new);

        // sort in ascending order
        Arrays.sort(observedPredictedValues);

        numberOfPredictions = observedPredictedValues.length;

        // populate observed values and predicted values
        observedValues = new int[numberOfPredictions];
        predictedValues = new double[numberOfPredictions];
        for (int i = 0; i < observedPredictedValues.length; i++) {
            ObservedPredictedValue obsPredVal = observedPredictedValues[i];
            observedValues[i] = obsPredVal.getObservedValue();
            predictedValues[i] = obsPredVal.getPredictedValue();
        }
    }

    @Override
    public String toString() {
        if (summary == null) {
            StringBuilder dataBuilder = new StringBuilder("========================================================================\n");
            dataBuilder.append(String.format("%-6s %-8s %-13s %-15s %-8s 95%s CI\n", "Group", "N", "Obs (%)", "Exp (%)", "HL", "%"));
            dataBuilder.append("========================================================================\n");
            int dataTotal = 0;
            int observedValueTotal = 0;
            double predictedValueTotal = 0;
            double hlTotal = 0;
            int numOfGroup = groups.length;
            for (int j = 0; j < numOfGroup; j++) {
                dataTotal += numberOfDataPerGroup[j];
                observedValueTotal += positiveObservedSumPerGroup[j];
                predictedValueTotal += predictedSumPerGroup[j];
                hlTotal += hlChi2PerGroup[j];
                dataBuilder.append(String.format("%-6d %-8d %-13s %-15s %-8.2f %s\n",
                        groups[j], numberOfDataPerGroup[j],
                        String.format("%d (%2.1f)", positiveObservedSumPerGroup[j], hlObservedValues[j] * 100),
                        String.format("%3.1f (%2.1f)", predictedSumPerGroup[j], hlExpectedValues[j] * 100),
                        hlChi2PerGroup[j],
                        String.format("(%1.4f, %1.4f)", hlExpectedValues[j] - marginOfErrorPerGroup[j], hlExpectedValues[j] + marginOfErrorPerGroup[j])));
            }

            dataBuilder.append("------------------------------------------------------------------------\n");
            double perObsTotal = ((double) observedValueTotal / dataTotal) * 100;
            double perExpTotal = ((double) predictedValueTotal / dataTotal) * 100;
            dataBuilder.append(String.format("%-6s %-8d %-13s %-15s %.2f\n",
                    "Total",
                    dataTotal,
                    String.format("%d (%-2.1f)", observedValueTotal, perObsTotal),
                    String.format("%-3.1f (%-2.1f)", predictedValueTotal, perExpTotal),
                    hlTotal));
            dataBuilder.append("\n");
            dataBuilder.append(String.format("Total Number of Groups: %d\n", numOfGroup));
            dataBuilder.append(String.format("Hosmer-Lemeshow Chi2(%d): %1.2f\n", numOfGroup, hlTotal));
            dataBuilder.append(String.format("Degree of Freedom: %d\n", degreesOfFreedom));
            dataBuilder.append(String.format("P-Value: %f\n", pValue));
            dataBuilder.append("========================================================================");

            summary = dataBuilder.toString();
        }

        return summary;
    }

    protected double computePValue(int degreesOfFreedom, double[] hlChi2PerGroup) {
        double pvalue = -1.0;

        if (degreesOfFreedom > 0) {
            ChiSquaredDistribution distribution = new ChiSquaredDistribution(degreesOfFreedom);

            double hlTotal = 0;
            for (int i = 0; i < hlChi2PerGroup.length; i++) {
                hlTotal += hlChi2PerGroup[i];
            }

            if (!Double.isInfinite(hlTotal)) {
                pvalue = 1.0 - distribution.cumulativeProbability(hlTotal);
            }
        }

        return pvalue;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public int getNumberOfPredictions() {
        return numberOfPredictions;
    }

    @Override
    public int[] getObservedValues() {
        return observedValues;
    }

    @Override
    public double[] getPredictedValues() {
        return predictedValues;
    }

    @Override
    public int[] getGroups() {
        return groups;
    }

    @Override
    public int[] getNumberOfDataPerGroup() {
        return numberOfDataPerGroup;
    }

    @Override
    public int[] getPositiveObservedSumPerGroup() {
        return positiveObservedSumPerGroup;
    }

    @Override
    public double[] getPredictedSumPerGroup() {
        return predictedSumPerGroup;
    }

    @Override
    public double[] getHlChi2PerGroup() {
        return hlChi2PerGroup;
    }

    @Override
    public double[] getMarginOfErrorPerGroup() {
        return marginOfErrorPerGroup;
    }

    @Override
    public double[] getHlObservedValues() {
        return hlObservedValues;
    }

    @Override
    public double[] getHlExpectedValues() {
        return hlExpectedValues;
    }

    @Override
    public int getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    @Override
    public double getPValue() {
        return pValue;
    }

}
