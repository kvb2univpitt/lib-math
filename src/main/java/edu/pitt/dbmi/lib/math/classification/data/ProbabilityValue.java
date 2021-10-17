package edu.pitt.dbmi.lib.math.classification.data;

/**
 *
 * Feb 24, 2011 9:38:07 AM (revised on Oct 17, 2021 10:25:40 AM)
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class ProbabilityValue implements Comparable<ProbabilityValue> {

    /**
     * Actual measured value. Value should be 0 or 1
     */
    private final byte observedValue;

    /**
     * Value predicted by classifier. Probability value between 0 and 1,
     * inclusive.
     */
    private final double predictedValue;

    public ProbabilityValue(byte observedValue, double predictedValue) {
        this.observedValue = observedValue;
        this.predictedValue = predictedValue;
    }

    @Override
    public int compareTo(ProbabilityValue otherProbabilityValue) {
        double otherPredictedValue = otherProbabilityValue.getPredictedValue();

        if (predictedValue > otherPredictedValue) {
            return 1;
        } else if (predictedValue < otherPredictedValue) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ProbabilityValue{observedValue=%d, predictedValue=%f}",
                observedValue, predictedValue);
    }

    public byte getObservedValue() {
        return observedValue;
    }

    public double getPredictedValue() {
        return predictedValue;
    }

}
