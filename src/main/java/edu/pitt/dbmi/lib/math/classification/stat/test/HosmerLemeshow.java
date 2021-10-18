package edu.pitt.dbmi.lib.math.classification.stat.test;

import edu.pitt.dbmi.lib.math.classification.data.ProbabilityValue;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * Hosmer-Lemeshow goodness of fit test. This implementation follows the Stata
 * implementation: https://www.sealedenvelope.com/stata/hl/.
 *
 * Mar 2, 2011 4:44:13 PM (revised on Oct 17, 2021 5:53:30 PM)
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class HosmerLemeshow {

    public static enum DataGroupType {
        DECILE_GROUPS, // ten equally sized groups
        RISK_GROUPS //  groups 0 - 0.1, 0.1 - 0.2, ..., 0.9 - 1
    };

    private final int NUM_OF_INTERVAL = 10;  // break the data into 10 intervals

    private double[][] hlPoints;
    private int[] groups;  // first interval is first group and so on...
    private int[] numDataPerGroup;  // number of data in each group
    private int[] observedPerGroup;  // number of positive in each group
    private double[] predictedPerGroup;  // sum of predictions per group
    private double[] hlChi2PerGroup;
    private double[] ciRangesPerGroup;  // confidence interval ranges

    private int degreeOfFreedom;
    private double pValue;

    private String summary;

    private final ProbabilityValue[] probValues;
    private final DataGroupType groupType;

    public HosmerLemeshow(List<ProbabilityValue> probabilityValues, DataGroupType groupType) {
        this.probValues = probabilityValues.toArray(new ProbabilityValue[probabilityValues.size()]);
        this.groupType = groupType;

        compute();
    }

    private void compute() {
        Arrays.sort(probValues);  // sort in ascending order

        switch (groupType) {
            case DECILE_GROUPS:
                computeUsingDecileGroup();
                break;
            default:
                computeUsingRiskGroup();
                break;
        }

        pValue = computePValue(degreeOfFreedom, hlChi2PerGroup);
    }

    /**
     * Compute by separating the data into ten equally sized groups.
     */
    private void computeUsingDecileGroup() {
        int totalGroup = computeActualHlSummaryPointsInDecialGroup(probValues);
        hlPoints = new double[totalGroup][2];
        groups = new int[totalGroup];
        numDataPerGroup = new int[totalGroup];
        observedPerGroup = new int[totalGroup];
        predictedPerGroup = new double[totalGroup];
        hlChi2PerGroup = new double[totalGroup];
        ciRangesPerGroup = new double[totalGroup];

        int groupIndex = 0;
        int groupNumber = 1;

        double percent = 0.10f;
        int size = probValues.length;
        int index = 0;
        while (index < size) {
            double value = probValues[index].getPredictedValue();

            double threshold;
            int intUpperIndex = (int) (percent * size);
            if (intUpperIndex == 0) {
                threshold = probValues[0].getPredictedValue();
            } else if (intUpperIndex >= size) {
                threshold = probValues[size - 1].getPredictedValue();
            } else {
                threshold = probValues[intUpperIndex].getPredictedValue();
            }

            // sum up all the predictions and tally number of positive in the interval
            double predictionSum = 0;  // sum of predictions within the interval
            int total = 0;  // total number of data within the interval
            int numOfPositive = 0;
            while (value <= threshold) {
                total++;
                predictionSum += probValues[index].getPredictedValue();

                if (probValues[index].getObservedValue() == 1) {
                    numOfPositive++;
                }

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = probValues[index].getPredictedValue();
                } else {
                    break;
                }
            }  // end of inner while-loop

            if (total != 0) {
                double xValue = predictionSum / total;  // average of the predicted values within the interval
                double yValue = ((double) numOfPositive) / total;  // # positive divided by (# positive + # negative) with in the interval

                hlPoints[groupIndex][0] = xValue;
                hlPoints[groupIndex][1] = yValue;

                ciRangesPerGroup[groupIndex] = 1.96 * Math.sqrt((xValue * (1 - xValue)) / total);

                groups[groupIndex] = groupNumber;
                numDataPerGroup[groupIndex] = total;
                observedPerGroup[groupIndex] = numOfPositive;
                predictedPerGroup[groupIndex] = predictionSum;
                hlChi2PerGroup[groupIndex] = Math.pow(numOfPositive - predictionSum, 2) / (predictionSum * (1 - (predictionSum / total)));
                groupIndex++;
            }

            groupNumber++;
            percent += 0.10f;
        }  // end of outter while-loop

        degreeOfFreedom = groups.length - 2;
    }

    /**
     * Compute by separating the data into risk groups 0 - 0.1, 0.1 - 0.2, ...,
     * 0.9 - 1.
     */
    private void computeUsingRiskGroup() {
        int totalGroup = computeActualHlSummaryPointsInRiskGroup(probValues);
        groups = new int[totalGroup];
        hlPoints = new double[totalGroup][2];
        numDataPerGroup = new int[totalGroup];
        observedPerGroup = new int[totalGroup];
        predictedPerGroup = new double[totalGroup];
        hlChi2PerGroup = new double[totalGroup];
        ciRangesPerGroup = new double[totalGroup];

        int groupIndex = 0;
        int groupNumber = 1;

        double threshold = 1.0f / NUM_OF_INTERVAL;
        int size = probValues.length;
        int index = 0;
        while (index < size) {
            double value = probValues[index].getPredictedValue();

            // sum up all the predictions and tally number of positive in the interval
            double predictionSum = 0;  // sum of predictions within the interval
            int total = 0;  // total number of data within the interval
            int numOfPositive = 0;
            while (value < threshold) {
                total++;

                predictionSum += probValues[index].getPredictedValue();

                if (probValues[index].getObservedValue() == 1) {
                    numOfPositive++;
                }

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = probValues[index].getPredictedValue();
                } else {
                    break;
                }
            }

            if (total != 0) {
                double xValue = predictionSum / total;  // average of the predicted values within the interval
                double yValue = ((double) numOfPositive) / total;  // # positive divided by (# positive + # negative) with in the interval

                hlPoints[groupIndex][0] = xValue;
                hlPoints[groupIndex][1] = yValue;

                ciRangesPerGroup[groupIndex] = (double) (1.96 * Math.sqrt((xValue * (1 - xValue)) / total));

                groups[groupIndex] = groupNumber;
                numDataPerGroup[groupIndex] = total;
                observedPerGroup[groupIndex] = numOfPositive;
                predictedPerGroup[groupIndex] = predictionSum;
                hlChi2PerGroup[groupIndex] = (double) Math.pow(numOfPositive - predictionSum, 2) / (predictionSum * (1 - (predictionSum / total)));
                groupIndex++;
            }

            threshold += 1.0f / NUM_OF_INTERVAL;
            groupNumber++;
        }  // end of while loop

        degreeOfFreedom = groups.length - 2;
    }

    private double computePValue(int degreeOfFreedom, double[] hlChi2PerGroup) {
        double pvalue = -1.0;

        if (degreeOfFreedom > 0) {
            ChiSquaredDistribution distribution = new ChiSquaredDistribution(degreeOfFreedom);
            double hlTotal = 0;
            for (int i = 0; i < hlChi2PerGroup.length; i++) {
                hlTotal += hlChi2PerGroup[i];
            }

            if (!Double.isInfinite(hlTotal)) {
                pvalue = (double) (1.0d - distribution.cumulativeProbability(hlTotal));
            }
        }

        return pvalue;
    }

    private int computeActualHlSummaryPointsInDecialGroup(ProbabilityValue[] probValues) {
        int actualHlPoints = 0;

        double percent = 0.10f;
        int size = probValues.length;
        int index = 0;
        while (index < size) {
            double value = probValues[index].getPredictedValue();

            double threshold;
            int intUpperIndex = (int) (percent * size);
            if (intUpperIndex == 0) {
                threshold = probValues[0].getPredictedValue();
            } else if (intUpperIndex >= size) {
                threshold = probValues[size - 1].getPredictedValue();
            } else {
                threshold = probValues[intUpperIndex].getPredictedValue();
            }

            // sum up all the predictions and tally number of positive in the interval
            int numOfData = 0;  // total number of data within the interval
            while (value <= threshold) {
                numOfData++;

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = probValues[index].getPredictedValue();
                } else {
                    break;
                }
            }  // end of inner while-loop

            if (numOfData != 0) {
                actualHlPoints++;
            }
            percent += 0.10f;
        }  // end of outter while-loop

        return actualHlPoints;
    }

    private int computeActualHlSummaryPointsInRiskGroup(ProbabilityValue[] probValues) {
        int actualHlPoints = 0;

        double threshold = 1.0f / NUM_OF_INTERVAL;
        int size = probValues.length;
        int index = 0;
        while (index < size) {
            double value = probValues[index].getPredictedValue();

            boolean hasValue = false;
            while (value < threshold) {
                hasValue = true;

                // check to make sure we have more data to work with before moving on
                index++;
                if (index < size) {
                    value = probValues[index].getPredictedValue();
                } else {
                    break;
                }
            }

            if (hasValue) {
                actualHlPoints++;
            }

            threshold += 1.0f / NUM_OF_INTERVAL;
        }

        return actualHlPoints;
    }

    @Override
    public String toString() {
        if (summary == null) {
            StringBuilder dataBuilder = new StringBuilder("Hosmer-Lemeshow Statistic Summary\n");
            dataBuilder.append("========================================================================\n");
            dataBuilder.append(String.format("%-6s %-8s %-13s %-15s %-8s 95%s CI\n", "Group", "N", "Obs (%)", "Exp (%)", "HL", "%"));
            dataBuilder.append("========================================================================\n");
            int dataSum = 0;
            int observedSum = 0;
            double predictedSum = 0;
            double hlTotal = 0;
            int numOfGroup = groups.length;
            for (int j = 0; j < numOfGroup; j++) {
                dataSum += numDataPerGroup[j];
                observedSum += observedPerGroup[j];
                predictedSum += predictedPerGroup[j];
                hlTotal += hlChi2PerGroup[j];
                dataBuilder.append(String.format("%-6d %-8d %-13s %-15s %-8.2f %s\n",
                        groups[j], numDataPerGroup[j],
                        String.format("%d (%2.1f)", observedPerGroup[j], hlPoints[j][1] * 100),
                        String.format("%3.1f (%2.1f)", predictedPerGroup[j], hlPoints[j][0] * 100),
                        hlChi2PerGroup[j],
                        String.format("(%1.4f, %1.4f)", hlPoints[j][0] - ciRangesPerGroup[j], hlPoints[j][0] + ciRangesPerGroup[j])));
            }
            dataBuilder.append("------------------------------------------------------------------------\n");
            double perObsTotal = ((double) observedSum / dataSum) * 100;
            double perExpTotal = ((double) predictedSum / dataSum) * 100;
            dataBuilder.append(String.format("%-6s %-8d %-13s %-15s %.2f\n",
                    "Total",
                    dataSum,
                    String.format("%d (%-2.1f)", observedSum, perObsTotal),
                    String.format("%-3.1f (%-2.1f)", predictedSum, perExpTotal),
                    hlTotal));
            dataBuilder.append("\n");
            dataBuilder.append(String.format("Total Number of Groups: %d\n", numOfGroup));
            dataBuilder.append(String.format("Hosmer-Lemeshow Chi2(%d): %1.2f\n", numOfGroup, hlTotal));
            dataBuilder.append(String.format("Degree of Freedom: %d\n", degreeOfFreedom));
            dataBuilder.append(String.format("P-Value: %f\n", pValue));
            dataBuilder.append("========================================================================");

            summary = dataBuilder.toString();
        }

        return summary;
    }

    public double[][] getHlPoints() {
        return hlPoints;
    }

    public int[] getGroups() {
        return groups;
    }

    public int[] getNumDataPerGroup() {
        return numDataPerGroup;
    }

    public int[] getObservedPerGroup() {
        return observedPerGroup;
    }

    public double[] getPredictedPerGroup() {
        return predictedPerGroup;
    }

    public double[] getHlChi2PerGroup() {
        return hlChi2PerGroup;
    }

    public double[] getCiRangesPerGroup() {
        return ciRangesPerGroup;
    }

    public int getDegreeOfFreedom() {
        return degreeOfFreedom;
    }

    public double getpValue() {
        return pValue;
    }

    public ProbabilityValue[] getProbValues() {
        return probValues;
    }

    public DataGroupType getGroupType() {
        return groupType;
    }

}
