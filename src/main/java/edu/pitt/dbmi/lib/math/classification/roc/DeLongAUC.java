package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;

/**
 *
 * Feb 24, 2011 12:01:01 PM (revised on Oct 17, 2021 5:43:13 PM)
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DeLongAUC {

    /**
     * Standard error (DeLong's method)
     */
    private final double standardError;

    /**
     * confidenceInterval[0] = lower CI interval. confidenceInterval[1] = upper
     * CI interval
     */
    private final double[] confidenceInterval;

    public DeLongAUC(AUROC auroc) {
        this.standardError = computeStandardError(auroc);
        this.confidenceInterval = getConfidenceInterval(auroc, standardError);
    }

    /**
     * Compute confidence interval (CI). CI[0] = lower CI interval. CI[1] =
     * upper CI interval
     *
     * @return confidence interval
     */
    private double[] getConfidenceInterval(AUROC auroc, double standardError) {
        double[] ci = new double[2];
        double marginOfError = 1.96 * standardError;
        double areaUnderCurve = auroc.getAreaUnderCurve();

        ci[0] = areaUnderCurve - marginOfError;
        ci[1] = areaUnderCurve + marginOfError;

        return ci;
    }

    private double computeStandardError(AUROC auroc) {
        ProbabilityValue[] probValues = auroc.getProbValues();

        double[] abNormData = new double[auroc.getNumOfPositive()];
        double[] normData = new double[auroc.getNumOfNegative()];
        double auc = auroc.getAreaUnderCurve();
        int posIndex = 0;
        int negIndex = 0;
        for (ProbabilityValue probValue : probValues) {
            if (probValue.getObservedValue() == 1) {
                abNormData[posIndex++] = probValue.getPredictedValue();
            } else {
                normData[negIndex++] = probValue.getPredictedValue();
            }
        }

        return Math.sqrt((sNorm(normData, abNormData, auc) / normData.length) + (sAbnorm(normData, abNormData, auc) / abNormData.length));
    }

    private static double indicator(double Xi, double Yi) {
        if (Yi < Xi) {
            return 1.0D;
        } else if (Yi == Xi) {
            return 0.5D;
        } else {
            return 0.0D;
        }
    }

    private static double vNorm(double Yj, double[] abNormData) {
        double result = 0;

        for (double Xi : abNormData) {
            result += indicator(Xi, Yj);
        }

        return result / abNormData.length;
    }

    private static double vAbnorm(double Xi, double[] normData) {
        double result = 0;

        for (double Yj : normData) {
            result += indicator(Xi, Yj);
        }

        return result / normData.length;
    }

    private static double sAbnorm(double[] normData, double[] abNormData, double auc) {
        double result = 0;

        for (double Xi : abNormData) {
            result += Math.pow(vAbnorm(Xi, normData) - auc, 2);
        }

        return result / (abNormData.length - 1);
    }

    private static double sNorm(double[] normData, double[] abNormData, double auc) {
        double result = 0;

        for (double Yj : normData) {
            result += Math.pow(vNorm(Yj, abNormData) - auc, 2);
        }

        return result / (normData.length - 1);
    }

    public double getStandardError() {
        return standardError;
    }

    public double[] getConfidenceInterval() {
        return confidenceInterval;
    }

}
