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
package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.ObservedPredictedValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * ROC Curve using DeLong's method.
 *
 * Mar 28, 2012 11:33:56 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DeLongROCCurve implements ROC {

    /**
     * Sensitivity, the y-values on the ROC plot.
     */
    private double[] truePositiveRates;

    /**
     * 1 - Specificity, the x-values on the ROC plot.
     */
    private double[] falsePositiveRates;

    private ConfusionMatrix[] confusionMatrices;

    private int numberOfPositives;

    private int numberOfNegatives;

    private double areaUnderRocCurve;

    public DeLongROCCurve(ObservedPredictedValue[] observedPredictedValues) {
        if (observedPredictedValues == null || observedPredictedValues.length == 0) {
            throw new IllegalArgumentException("Observed values and predicted values are required.");
        }

        // copy the array
        observedPredictedValues = Arrays.stream(observedPredictedValues)
                .toArray(ObservedPredictedValue[]::new);

        // sort in descending order
        Arrays.sort(observedPredictedValues, Collections.reverseOrder());

        this.numberOfPositives = (int) Arrays.stream(observedPredictedValues)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 1)
                .count();
        this.numberOfNegatives = (int) Arrays.stream(observedPredictedValues)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 0)
                .count();

        truePositiveRates = new double[observedPredictedValues.length];
        falsePositiveRates = new double[observedPredictedValues.length];

        confusionMatrices = computeConfusionMatrices(observedPredictedValues,
                numberOfPositives, numberOfNegatives,
                truePositiveRates, falsePositiveRates);

        areaUnderRocCurve = computeAreaUnderRocCurve(truePositiveRates, falsePositiveRates);
    }

    private ConfusionMatrix[] computeConfusionMatrices(
            ObservedPredictedValue[] observedPredictedValues,
            int numberOfPositives, int numberOfNegatives,
            double[] truePositiveRates, double[] falsePositiveRates) {
        List<ConfusionMatrix> confusionMatrixList = new LinkedList<>();

        // seperate the values for the positive and negative outcomes
        double[] positivePredictedValues = Arrays.stream(observedPredictedValues)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 1)
                .mapToDouble(obsPredVal -> obsPredVal.getPredictedValue())
                .toArray();
        double[] negativePredictedValues = Arrays.stream(observedPredictedValues)
                .filter(obsPredVal -> obsPredVal.getObservedValue() == 0)
                .mapToDouble(obsPredVal -> obsPredVal.getPredictedValue())
                .toArray();

        int index = 0;
        for (ObservedPredictedValue observedPredictedValue : observedPredictedValues) {
            double z = observedPredictedValue.getPredictedValue();
            double sensitivity = sens(z, positivePredictedValues);
            double specificity = spec(z, negativePredictedValues);

            int truePositive = (int) (sensitivity * numberOfPositives);
            int falseNegative = numberOfPositives - truePositive;
            int trueNegative = (int) (specificity * numberOfNegatives);
            int falsePositive = numberOfNegatives - trueNegative;
            confusionMatrixList.add(new ConfusionMatrix(falseNegative, falsePositive, trueNegative, truePositive, z));

            truePositiveRates[index] = sensitivity;
            falsePositiveRates[index] = 1 - specificity;
            index++;
        }

        return confusionMatrixList.stream()
                .toArray(ConfusionMatrix[]::new);
    }

    private double computeAreaUnderRocCurve(double[] truePositiveRates, double[] falsePositiveRates) {
        double areaUnderCurve = 0;

        double x1, x2, y1, y2;
        x1 = y1 = 0;
        for (int i = 0; i < falsePositiveRates.length; i++) {
            x2 = falsePositiveRates[i];
            y2 = truePositiveRates[i];

            // compute the area using trapezoid method
            double base = Math.abs(x1 - x2);
            double height = (y1 + y2) / 2;
            areaUnderCurve += base * height;

            x1 = x2;
            y1 = y2;
        }

        return areaUnderCurve;
    }

    private final double spec(double z, double[] negativePredictedValues) {
        int sum = 0;
        int n = negativePredictedValues.length;
        for (int i = 0; i < n; i++) {
            if (negativePredictedValues[i] < z) {
                sum++;
            }
        }

        return ((double) sum) / n;
    }

    private final double sens(double z, double[] positivePredictedValues) {
        int sum = 0;
        int m = positivePredictedValues.length;
        for (int i = 0; i < m; i++) {
            if (positivePredictedValues[i] >= z) {
                sum++;
            }
        }

        return ((double) sum) / m;
    }

    @Override
    public double[] getTruePositiveRates() {
        return truePositiveRates;
    }

    @Override
    public double[] getFalsePositiveRates() {
        return falsePositiveRates;
    }

    @Override
    public ConfusionMatrix[] getConfusionMatrices() {
        return confusionMatrices;
    }

    @Override
    public int getNumberOfPositives() {
        return numberOfPositives;
    }

    @Override
    public int getNumberOfNegatives() {
        return numberOfNegatives;
    }

    @Override
    public double getAreaUnderRocCurve() {
        return areaUnderRocCurve;
    }

}
