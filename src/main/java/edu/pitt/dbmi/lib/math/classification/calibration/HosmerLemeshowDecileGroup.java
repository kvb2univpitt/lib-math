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

/**
 * Compute Hosmer-Lemeshow using decile grouping, split the dataset into 10
 * equal parts.
 *
 * Mar 30, 2012 11:43:49 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshowDecileGroup extends AbstractHosmerLemeshow {

    public HosmerLemeshowDecileGroup(ObservedPredictedValue[] observedPredictedValues) {
        super(observedPredictedValues);
    }

    @Override
    protected void computePlotPoints() {
        int groupIndex = 0;
        int groupNumber = 1;

        double percent = 0.10;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // compute the threshold
            double threshold;
            int intUpperIndex = (int) (percent * size);
            if (intUpperIndex >= size) {
                threshold = predictedValues[size - 1];
            } else {
                threshold = predictedValues[intUpperIndex];
            }

            // for each interval, sum up all the predictions and tally number of positive
            double predictedValueSum = 0.0;  // sum of predicted values within the interval
            int numOfData = 0;  // total number of data within the interval
            int numOfPosObserVal = 0;
            double value = predictedValues[index];
            while (value <= threshold) {
                numOfData++;
                predictedValueSum += predictedValues[index];

                if (observedValues[index] == 1) {
                    numOfPosObserVal++;
                }

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = predictedValues[index];
                } else {
                    break;
                }
            }

            if (numOfData != 0) {
                double xValue = predictedValueSum / numOfData;  // average of the predicted values within the interval
                double yValue = ((double) numOfPosObserVal) / numOfData;  // # positive divided by (# positive + # negative) with in the interval

                hlExpectedValues[groupIndex] = xValue;
                hlObservedValues[groupIndex] = yValue;
                groups[groupIndex] = groupNumber;
                numberOfDataPerGroup[groupIndex] = numOfData;
                positiveObservedSumPerGroup[groupIndex] = numOfPosObserVal;
                predictedSumPerGroup[groupIndex] = predictedValueSum;
                marginOfErrorPerGroup[groupIndex] = CRITICAL_VALUE * Math.sqrt((xValue * (1 - xValue)) / numOfData);
                hlChi2PerGroup[groupIndex] = Math.pow(numOfPosObserVal - predictedValueSum, 2) / (predictedValueSum * (1 - (predictedValueSum / numOfData)));

                groupIndex++;
            }

            groupNumber++;
            percent += 0.10;
        }
    }

    /**
     * Split into 10 equal groups and count how many of groups has at least one
     * member.
     *
     * @return number of groups with at least one member
     */
    @Override
    protected int computeTotalNumberOfGroups() {
        int totalGroup = 0;

        double percent = 0.10;
        int size = predictedValues.length;
        int index = 0;
        while (index < size) {
            // compute the threshold
            double threshold;
            int intUpperIndex = (int) (percent * size);
            if (intUpperIndex >= size) {
                threshold = predictedValues[size - 1];
            } else {
                threshold = predictedValues[intUpperIndex];
            }

            // for each interval, make sure we have data in each group (interval)
            boolean hasDataInInterval = false;
            double value = predictedValues[index];
            while (value <= threshold) {
                hasDataInInterval = true;

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = predictedValues[index];
                } else {
                    break;
                }
            }

            // count only if there's data in the group
            if (hasDataInInterval) {
                totalGroup++;
            }

            percent += 0.10;
        }

        return totalGroup;
    }

}
