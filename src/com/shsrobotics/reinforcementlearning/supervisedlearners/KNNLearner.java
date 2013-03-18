package com.shsrobotics.reinforcementlearning.supervisedlearners;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.ArrayList;
import sun.nio.cs.ext.ISCII91;

/**
 * Regression K-Nearest-Neighbor algorithm.
 */
public class KNNLearner extends SupervisedLearner {

	/**
	 * The {@link #k} nearest neighbors to base the decision off of.
	 */
	private int k;

	/**
	 * How many input variables.
	 */
	private int dimensions;

	/**
	 * Create a KNN Learner.
	 * <p/>
	 * @param minimums minimum input values.
	 * @param maximums maximum input values.
	 */
	public KNNLearner(double[] minimums, double[] maximums) {
		super(minimums, maximums);
		this.dimensions = minimums.length;
		this.k = (int) Math.floor(Math.sqrt(this.data.size()));
		if (k < 3) {
			k = 3;
		}
	}

	/**
	 * The new k nearest neighbors to use.
	 * <p/>
	 * @param k the new k
	 */
	public void setK(int k) {
		this.k = k;
	}

	@Override
	public void update(DataPoint dataPoint) {
		data.add(dataPoint);
	}

	@Override
	public double query(DataPoint input) {
		int length = data.size();
		if (length == 0) {
			throw new Error("No data.");
		}

		ArrayList<OutputDouble> distance = new ArrayList<>(); // sorted distances by best
		double[] inputValues = input.getInputs();
		for (int i = 0; i < length; i++) { // each data point
			double sum = 0.0;
			double[] dataPoint = data.get(i).getInputs();
			for (int j = 0; j < dimensions; j++) {
				sum += Math.pow(inputValues[j] - dataPoint[j], 2);
			}
			sum = Math.sqrt(sum);
			int size = distance.size();
			if (size == 0) {
				distance.add(new OutputDouble(sum, data.get(i).getOutput()));
				continue;
			}
			for (int j = 0; j <= size; j++) {
				if (j == size) {
					distance.add(new OutputDouble(sum, data.get(i).getOutput()));
					break;
				}
				if (sum <= distance.get(j).distance) { // better
					distance.add(j, new OutputDouble(sum, data.get(i).getOutput()));
					break;
				}
			}
		}

		distance.subList(0, k).toArray(); // get k nearest neighbors

		double numerator = 0.0;
		double denominator = 0.0;
		double best = distance.get(0).distance;
		for (int i = 0; i < k; i++) {
			OutputDouble d = distance.get(i);
			double weight = Math.exp(1 - Math.pow(best / d.distance, 2));
			numerator += d.output * weight;
			denominator += weight;
		}
		
		double output = numerator / denominator;
		int i = 0;
		while (Double.isNaN(output)) {
			output = distance.get(i++).output;
		}
		return output;
	}

	/**
	 * Labeled distances.
	 */
	public class OutputDouble {

		/**
		 * Distance to query point.
		 */
		public double distance;

		/**
		 * Output value.
		 */
		public double output;

		/**
		 * Create a labeled distance.
		 * <p/>
		 * @param distance the distance.
		 * @param output the output value.
		 */
		public OutputDouble(double distance, double output) {
			this.distance = distance;
			this.output = output;
		}
	}

}
