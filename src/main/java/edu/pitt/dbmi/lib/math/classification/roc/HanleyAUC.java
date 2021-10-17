package edu.pitt.dbmi.lib.math.classification.roc;

/**
 *
 * Mar 2, 2011 3:32:58 (revised on Oct 17, 2021 5:48:49 PM)
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HanleyAUC {

    /**
     * Standard error (Hanely's method)
     */
    private final double standardError;

    /**
     * confidenceInterval[0] = lower CI interval. confidenceInterval[1] = upper
     * CI interval
     */
    private final double[] confidenceInterval;

    public HanleyAUC(AUROC auroc) {
        this.standardError = computeStandardError(auroc);
        this.confidenceInterval = getConfidenceInterval(auroc, this.standardError);
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
        ci[0] = (double) (areaUnderCurve - marginOfError);
        ci[1] = (double) (areaUnderCurve + marginOfError);

        return ci;
    }

    /**
     * Hanley's Method
     *
     * @param numOfPositive is the number of positive cases
     * @param numOfNegative is the number of negative cases
     * @param auc is the area under the curve
     * @return standard error
     */
    private double computeStandardError(AUROC auroc) {
        int numOfPositive = auroc.getNumOfPositive();
        int numOfNegative = auroc.getNumOfNegative();
        double areaUnderCurve = auroc.getAreaUnderCurve();

        double aucSq = areaUnderCurve * areaUnderCurve;
        double Q1 = areaUnderCurve / (2 - areaUnderCurve);
        double Q2 = (2 * aucSq) / (1 + areaUnderCurve);

        return Math.sqrt((areaUnderCurve * (1 - areaUnderCurve) + (numOfPositive - 1) * (Q1 - aucSq) + (numOfNegative - 1) * (Q2 - aucSq)) / (numOfPositive * numOfNegative));
    }

    public double getStandardError() {
        return standardError;
    }

    public double[] getConfidenceInterval() {
        return confidenceInterval;
    }

}
