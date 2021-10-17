package edu.pitt.dbmi.lib.math.classification.roc;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Feb 24, 2011 9:53:07 AM (revised on Oct 17, 2021 10:52:21 AM)
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class AUROC {

    /**
     * The number of observed values of 1.
     */
    private int numOfPositive;

    /**
     * The number of observed values of 0.
     */
    private int numOfNegative;

    private int numOfRocPoints;
    private double areaUnderCurve;

    /**
     * ROC points. points[0] = false positive rate points[1] = true positive
     * rate (PPV)
     */
    private double[][] rocPoints;

    private double[] accuracy;

    private double[] precision;

    private double[] f1score;

    private final ProbabilityValue[] probValues;

    public AUROC(List<ProbabilityValue> probabilityValues) {
        this.probValues = probabilityValues.toArray(new ProbabilityValue[probabilityValues.size()]);

        computeAUC();
    }

    /**
     * Computer Area under ROC curve.
     */
    private void computeAUC() {
        Arrays.sort(probValues);  // sort in ascending order

        numOfPositive = countNumberOfPositive(probValues);
        numOfNegative = probValues.length - numOfPositive;
        numOfRocPoints = computeNumberOfRocPoints(probValues);
        areaUnderCurve = 0.0f;

        rocPoints = new double[numOfRocPoints][2];  // points[0] = false positive rate, points[1] = true positive rate
        accuracy = new double[numOfRocPoints];
        precision = new double[numOfRocPoints];
        f1score = new double[numOfRocPoints];

        // for computing accuracy
        double size = numOfPositive + numOfNegative;

        double falsePositive = 0;  // incorrectly labeled as belonging to the negative class
        double truePositive = 0;  // number of items correctly labeled as belonging to the positive class
        double trueNegative = numOfNegative;
        double falseNegative = numOfPositive;  // items which were not labeled as belonging to the positive class but should have been

        int currentIndex = 0;
        int previousIndex = 0;
        double threshold = -1f;  // negative infinity
        int index = probValues.length - 1;  // start at the end of the array
        double tpPlusFp;
        while (index >= 0) {
            if (probValues[index].getPredictedValue() != threshold) {
                // compute ROC points and make sure we don't divide by zero
                rocPoints[currentIndex][0] = (numOfNegative == 0) ? 0 : falsePositive / numOfNegative;  // x-axis(fp rate)
                rocPoints[currentIndex][1] = (numOfPositive == 0) ? 0 : truePositive / numOfPositive;  // y-axis(tp rate)

                // compute accuracy, precision and f1score
                tpPlusFp = truePositive + falsePositive;
                accuracy[currentIndex] = (truePositive + trueNegative) / size;
                precision[currentIndex] = (tpPlusFp == 0) ? 0 : truePositive / tpPlusFp;
                f1score[currentIndex] = (2 * truePositive) / (numOfPositive + tpPlusFp);

                // compute area under curve
                areaUnderCurve += computeTrapezoidArea(rocPoints[currentIndex][0], rocPoints[previousIndex][0], rocPoints[currentIndex][1], rocPoints[previousIndex][1]);
                previousIndex = currentIndex;

                currentIndex++;
                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 1) {
                truePositive++;
                falseNegative--;
            } else {
                falsePositive++;
                trueNegative--;
            }

            index--;
        }  // end of while-loop

        // compute accuracy, precision and f1score
        tpPlusFp = truePositive + falsePositive;
        accuracy[currentIndex] = (truePositive + trueNegative) / size;
        precision[currentIndex] = (tpPlusFp == 0) ? 0 : truePositive / tpPlusFp;
        f1score[currentIndex] = (2 * truePositive) / (numOfPositive + tpPlusFp);

        // compute ROC points for end point (1, 1) and make sure we don't divide by zero
        rocPoints[currentIndex][0] = (numOfNegative == 0) ? 0 : ((double) falsePositive) / numOfNegative;  // x-axis(fp rate)
        rocPoints[currentIndex][1] = (numOfPositive == 0) ? 0 : ((double) truePositive) / numOfPositive;  // y-axis(tp rate)

        // compute area under curve for end point (1, 1)
        areaUnderCurve += computeTrapezoidArea(rocPoints[currentIndex][0], rocPoints[previousIndex][0], rocPoints[currentIndex][1], rocPoints[previousIndex][1]);
    }

    private double computeTrapezoidArea(double x1, double x2, double y1, double y2) {
        double base = Math.abs(x1 - x2);
        double height = ((double) (y1 + y2)) / 2;

        return base * height;
    }

    /**
     * Calculate the number of points in the ROC curve.
     *
     * @param probValues
     * @return the number of points in the ROC curve.
     */
    private int computeNumberOfRocPoints(ProbabilityValue[] probValues) {
        int numOfPoints = 0;

        double threshold = -1f;
        int index = probValues.length - 1;  // start at the end of the array
        while (index >= 0) {
            double predictedValue = probValues[index].getPredictedValue();
            if (predictedValue != threshold) {
                numOfPoints++;
                threshold = predictedValue;
            }

            index--;
        }

        numOfPoints++;  // include point (1,1);

        return numOfPoints;
    }

    /**
     * Count the number of probability value having the observed value of 1.
     *
     * @param probValues
     * @return
     */
    private int countNumberOfPositive(ProbabilityValue[] probValues) {
        return (int) Arrays.stream(probValues)
                .filter(e -> e.getObservedValue() == 1)
                .count();
    }

    public double[][] getPositiveCasesAboveThresholdPoints() {
        List<CaseThreshold> list = new LinkedList<>();

        int numOfPositiveCases = 0;
        double threshold = 1f;
        int index = probValues.length - 1;  // start at the end of the array
        while (index >= 0) {
            if (probValues[index].getPredictedValue() != threshold) {
                list.add(new CaseThreshold(numOfPositiveCases, threshold));
                threshold = probValues[index].getPredictedValue();
            }
            numOfPositiveCases++;
            index--;
        }
        list.add(new CaseThreshold(numOfPositiveCases, threshold));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (CaseThreshold caseThreshold : list) {
            points[index][0] = caseThreshold.getThreshold();
            points[index][1] = caseThreshold.getNumOfCases();
            index++;
        }

        return points;
    }

    public double[][] getPositivePrecisionRecallPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = 0;  // incorrectly labeled as belonging to the negative class
        double truePositive = 0;  // number of items correctly labeled as belonging to the positive class
        double trueNegative = numOfNegative;
        double falseNegative = numOfPositive;  // items which were not labeled as belonging to the positive class but should have been

        int index = probValues.length - 1;  // start at the end of the array
        double threshold = probValues[index].getPredictedValue();  // set the threshold to the highest value
        double denominator, ppv, recall;
        while (index >= 0) {
            if (probValues[index].getPredictedValue() != threshold) {
                denominator = truePositive + falseNegative;  // the total number of elements that actually belong to the positive class
                recall = (denominator == 0) ? 0 : truePositive / denominator;
                denominator = truePositive + falsePositive;  // number of items correctly labeled as belonging to the positive class
                ppv = (denominator == 0) ? 0 : truePositive / denominator;
                list.add(new Point(recall, ppv));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 1) {
                truePositive++;
                falseNegative--;
            } else {
                falsePositive++;
                trueNegative--;
            }

            index--;
        }

        denominator = truePositive + falseNegative;  // the total number of elements that actually belong to the positive class
        recall = (denominator == 0) ? 0 : truePositive / denominator;
        denominator = truePositive + falsePositive;  // number of items correctly labeled as belonging to the positive class
        ppv = (denominator == 0) ? 0 : truePositive / denominator;
        list.add(new Point(recall, ppv));

        double[][] points = new double[list.size()][2];

        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public double[][] getPPVRecallvsThresholdPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = 0;  // incorrectly labeled as belonging to the negative class
        double truePositive = 0;  // number of items correctly labeled as belonging to the positive class
        double trueNegative = numOfNegative;
        double falseNegative = numOfPositive;  // items which were not labeled as belonging to the positive class but should have been

        int index = probValues.length - 1;  // start at the end of the array
        double threshold = probValues[index].getPredictedValue();  // set the threshold to the highest value
        double denominator;
        while (index >= 0) {
            if (probValues[index].getPredictedValue() != threshold) {
                denominator = truePositive + falseNegative;  // the total number of elements that actually belong to the positive class
                list.add(new Point(threshold, (denominator == 0) ? 0 : truePositive / denominator));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 1) {
                truePositive++;
                falseNegative--;
            } else {
                falsePositive++;
                trueNegative--;
            }

            index--;
        }

        denominator = truePositive + falseNegative;  // the total number of elements that actually belong to the positive class
        list.add(new Point(threshold, (denominator == 0) ? 0 : truePositive / denominator));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public double[][] getNPVRecallvsThresholdPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = numOfNegative;  // items which were not labeled as belonging to the negative class but should have been
        double truePositive = numOfPositive;   // number of items correctly labeled as belonging to the positive class
        double trueNegative = 0;
        double falseNegative = 0;   // incorrectly labeled as belonging to the negative class

        double denominator, yPoint;
        int index = 0;
        double threshold = probValues[index].getPredictedValue();
        while (index < probValues.length) {
            if (probValues[index].getPredictedValue() != threshold) {
                denominator = trueNegative + falsePositive;  // the total number of elements that actually belong to the positive class
                yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
                list.add(new Point(threshold, yPoint));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 0) {
                trueNegative++;
                falsePositive--;
            } else {
                falseNegative++;
                truePositive--;
            }

            index++;
        }

        denominator = trueNegative + falsePositive;  // the total number of elements that actually belong to the positive class
        yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
        list.add(new Point(threshold, yPoint));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public double[][] getPPVvsThresholdPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = 0;  // incorrectly labeled as belonging to the negative class
        double truePositive = 0;  // number of items correctly labeled as belonging to the positive class
        double trueNegative = numOfNegative;
        double falseNegative = numOfPositive;  // items which were not labeled as belonging to the positive class but should have been

        int index = probValues.length - 1;  // start at the end of the array
        double threshold = probValues[index].getPredictedValue();  // set the threshold to the highest value
        double denominator, yPoint;
        while (index >= 0) {
            if (probValues[index].getPredictedValue() != threshold) {
                denominator = truePositive + falsePositive;  // number of items correctly labeled as belonging to the positive class
                yPoint = (denominator == 0) ? 0 : truePositive / denominator;
                list.add(new Point(threshold, yPoint));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 1) {
                truePositive++;
                falseNegative--;
            } else {
                falsePositive++;
                trueNegative--;
            }

            index--;
        }

        denominator = truePositive + falsePositive;  // number of items correctly labeled as belonging to the positive class
        yPoint = (denominator == 0) ? 0 : truePositive / denominator;
        list.add(new Point(threshold, yPoint));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public double[][] getNPVvsThresholdPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = numOfNegative;  // items which were not labeled as belonging to the negative class but should have been
        double truePositive = numOfPositive;   // number of items correctly labeled as belonging to the positive class
        double trueNegative = 0;
        double falseNegative = 0;   // incorrectly labeled as belonging to the negative class

        int index = 0;
        double threshold = probValues[index].getPredictedValue();
        double denominator, yPoint;
        while (index < probValues.length) {
            if (probValues[index].getPredictedValue() != threshold) {
                // TN / (FN + TN)
                denominator = trueNegative + falseNegative;  // number of items correctly labeled as belonging to the positive class
                yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
                list.add(new Point(threshold, yPoint));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 0) {
                trueNegative++;
                falsePositive--;
            } else {
                falseNegative++;
                truePositive--;
            }

            index++;
        }

        denominator = trueNegative + falseNegative;  // number of items correctly labeled as belonging to the positive class
        yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
        list.add(new Point(threshold, yPoint));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public double[][] getNegativePrecisionRecallPoints() {
        List<Point> list = new LinkedList<>();

        double falsePositive = numOfNegative;  // items which were not labeled as belonging to the negative class but should have been
        double truePositive = numOfPositive;   // number of items correctly labeled as belonging to the positive class
        double trueNegative = 0;
        double falseNegative = 0;   // incorrectly labeled as belonging to the negative class

        int index = 0;
        double threshold = probValues[index].getPredictedValue();
        double denominator, xPoint, yPoint;
        while (index < probValues.length) {
            if (probValues[index].getPredictedValue() != threshold) {
                denominator = trueNegative + falsePositive;  // the total number of elements that actually belong to the positive class
                xPoint = (denominator == 0) ? 0 : trueNegative / denominator;
                // TN / (FN + TN)
                denominator = trueNegative + falseNegative;  // number of items correctly labeled as belonging to the positive class
                yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
                list.add(new Point(xPoint, yPoint));

                threshold = probValues[index].getPredictedValue();
            }

            if (probValues[index].getObservedValue() == 0) {
                trueNegative++;
                falsePositive--;
            } else {
                falseNegative++;
                truePositive--;
            }

            index++;
        }

        denominator = trueNegative + falsePositive;  // the total number of elements that actually belong to the positive class
        xPoint = (denominator == 0) ? 0 : trueNegative / denominator;
        // TN / (FN + TN)
        denominator = trueNegative + falseNegative;  // number of items correctly labeled as belonging to the positive class
        yPoint = (denominator == 0) ? 0 : trueNegative / denominator;
        list.add(new Point(xPoint, yPoint));

        double[][] points = new double[list.size()][2];
        index = 0;
        for (Point point : list) {
            points[index][0] = point.getxPoint();
            points[index][1] = point.getyPoint();
            index++;
        }

        return points;
    }

    public int getNumOfPositive() {
        return numOfPositive;
    }

    public int getNumOfNegative() {
        return numOfNegative;
    }

    public int getNumOfRocPoints() {
        return numOfRocPoints;
    }

    public double getAreaUnderCurve() {
        return areaUnderCurve;
    }

    public double[][] getRocPoints() {
        return rocPoints;
    }

    public double[] getAccuracy() {
        return accuracy;
    }

    public double[] getPrecision() {
        return precision;
    }

    public double[] getF1score() {
        return f1score;
    }

    public ProbabilityValue[] getProbValues() {
        return probValues;
    }

    private class Point implements Comparable<Point> {

        private final double xPoint;
        private final double yPoint;

        public Point(double xPoint, double yPoint) {
            this.xPoint = xPoint;
            this.yPoint = yPoint;
        }

        @Override
        public int compareTo(Point point) {
            double otherXPoint = point.getxPoint();

            if (this.xPoint > otherXPoint) {
                return 1;
            } else if (this.xPoint < otherXPoint) {
                return -1;
            } else {
                return 0;
            }
        }

        public double getxPoint() {
            return xPoint;
        }

        public double getyPoint() {
            return yPoint;
        }

    }

    private class CaseThreshold implements Comparable<CaseThreshold> {

        private final int numOfCases;

        private final double threshold;

        public CaseThreshold(int numOfCases, double threshold) {
            this.numOfCases = numOfCases;
            this.threshold = threshold;
        }

        @Override
        public int compareTo(CaseThreshold caseThreshold) {
            double otherThreshold = caseThreshold.getThreshold();

            if (this.threshold > otherThreshold) {
                return 1;
            } else if (this.threshold < otherThreshold) {
                return -1;
            } else {
                return 0;
            }
        }

        public int getNumOfCases() {
            return numOfCases;
        }

        public double getThreshold() {
            return threshold;
        }

    }

}
