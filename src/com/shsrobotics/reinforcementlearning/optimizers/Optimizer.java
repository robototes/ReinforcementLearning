package com.shsrobotics.reinforcementlearning.optimizers;

/**
 * Optimize coordinates based on the Nelder-Mead (Simplex) Algorithm.
 * @author Team 2412.
 */
public abstract class Optimizer {
	/**
	 * The number of variables to optimize.
	 * Data dimensions.
	 */
	private final int n; 
	/**
	 * How many times to run the Nelder-Mead algorithm
	 */
	private final int iterations;
	/**
	 * Minimum variable values.
	 * Used to choose random starting points.
	 */
	private final double[] minimums;
	/**
	 * Maximum Variable values.
	 * Used to choose random starting points.
	 */
	private final double[] maximums;
	
	/**
	 * Constant for Reflected point.
	 */
	private double NelderMeadReflectionCoefficient = 1;
	/**
	 * Constant for Expanded point.
	 */
	private double NelderMeadExpansionCoefficient = 2;
	/**
	 * Constant for Contracted point.
	 */
	private double NelderMeadContractionCoefficient = -1 / 2;
	/**
	 * Constant for Reduced point.
	 */
	private double NelderMeadShrinkCoefficient = 1 / 2;
	
	/**
	 * Create an optimizer.
	 * @param dimension the number of variables.
	 * @param iterations how precise to maximize.
	 * @param minimums the minimum domain values.
	 * @param maximums the maximum domain values.
	 */
	public Optimizer(int dimension, int iterations, double[] minimums, double[] maximums) {
		this.n = dimension;
		this.iterations = iterations;
		this.minimums = minimums;
		this.maximums = maximums;
	}
	
	
	
	/**
	 * Maximize the fitness function output for a set of coordinates.
	 * @return the maximized coordinates.
	 */
	public double[] maximize() {
		return optimize(true);
	}
	
	/**
	 * Minimize the fitness function output for a set of coordinates.
	 * @return the minimized coordinates.
	 */
	public double[] minimize() {
		return optimize(false);
	}

	
	/**
	 * Optimize the fitness function output for a set of coordinates.
	 * @param maximize whether to maximize or minimize. Set to true to maximize.
	 * @return the optimized coordinates.
	 */
	private double[] optimize(boolean maximize) {
		double[] toReturn = zeros();
		Point[] vertices = new Point[n + 1]; // simplex vertices

		for (int i = 0; i < vertices.length; i++) {
			double[] input = rands();
			vertices[i] = new Point(input, (double) f(input));
		}			
		vertices = sort(vertices, maximize); // sort according to value

		for (int i = 0; i < iterations; i++) {					
			//calculate CG/centroid of simplex
			double[] centroid = zeros();

			for (int vertex = 0; vertex < vertices.length - 1; vertex++) { // for each vector/coordinate except worst
				double cgSum = 0.0;
				for (int k = 0; k < n; k++) { 
					cgSum += vertices[k].coordinates[vertex];
				}
				centroid[vertex] = cgSum / (vertices.length + 1);
			}			
			Point worst = vertices[n];

			double[] reflectedPoint = new double[n];
			double[] expandedPoint = new double[n];
			double[] contractedPoint = new double[n];
			for (int j = 0; j < n; j++) {
				double difference = (centroid[j] - worst.coordinates[j]);
				reflectedPoint[j] = centroid[j] + NelderMeadReflectionCoefficient * difference;
				expandedPoint[j] = centroid[j] + NelderMeadExpansionCoefficient * difference;
				contractedPoint[j] = centroid[j] + NelderMeadContractionCoefficient * difference;
			}

			double reflectedValue = f(reflectedPoint);
			double expandedValue = f(expandedPoint);
			double contractedValue = f(contractedPoint);
			if (better(vertices[0].value, reflectedValue, maximize) && better(reflectedValue, vertices[n - 1].value, maximize)) { // worst than best but better than second-best
				vertices[n] = new Point(reflectedPoint, f(reflectedPoint)); // replace worst with new reflected point
				continue; // next iteration
			} else if (better(reflectedValue, vertices[0].value, maximize)) { // better than best
				if (expandedValue > reflectedValue) {
					vertices[n] = new Point(expandedPoint, f(expandedPoint)); // replace worst with new expanded point
					continue; // next iteration
				} else {
					vertices[n] = new Point(reflectedPoint, f(reflectedPoint)); // replace worst with new reflected point
					continue; // next iteration
				}
			} else if (better(vertices[n].value, reflectedValue, maximize) && better(contractedValue, worst.value, maximize)) { // worst than second-best
				vertices[n] = new Point(contractedPoint, f(contractedPoint)); // replace worst with new contracted point
				continue; //next iteration
			} else {
				for (int j = 1; j < vertices.length; j++) { // for all but best use reduced point						
					double[] reducedPoint = new double[n];
					for (int k = 0; k < n; k++) {
						double difference = (centroid[k] - worst.coordinates[k]);
						reducedPoint[k] = centroid[k] + NelderMeadShrinkCoefficient * difference;
					}
					vertices[j] = new Point(reducedPoint, f(reducedPoint));
				}
			}							
			vertices = sort(vertices, maximize); // sort according to value
		}
		toReturn = vertices[0].coordinates;
		
		return toReturn;
	}
	
	
	/**
	 * A data point.  Used for optimization
	 */
	private class Point {
		public final double[] coordinates;
		public final double value;
		
		/**
		 * Create a point.
		 * @param coordinates the action coordinates.
		 * @param value the Q-Value from the coordinates.
		 */
		public Point(double[] coordinates, double value)	{
			this.coordinates = coordinates;
			this.value = value;
		}
	}

	/**
	 * Sort an array.
	 * @param array the array.
	 * @param descending whether to put larger values first (maximization) or
	 * smaller values first (minimization). Set to true to maximize.
	 * @return the sorted result.
	 */
	private Point[] sort(Point[] array, boolean descending) {
		int length = array.length; // to save array lookups
		double best; // best value so far
		int bestIndex = -1;	// index of best value so far	
		boolean[] usedIndices = new boolean[array.length];
        Point[] toReturn = new Point[length];
        for (int i = 0; i < length; i++) { // final array
			if (descending) {
				best = Double.NEGATIVE_INFINITY;
			} else {
				best = Double.POSITIVE_INFINITY;
			}
			
			for (int j = 0; j < length; j++) { // input array
				if (usedIndices[j]) continue;
				if (better(array[j].value, best, descending)) {
					bestIndex = j;
					best = array[j].value;
				}			
			}
			usedIndices[bestIndex] = true;
			toReturn[i] = array[bestIndex];
        }
        return toReturn;
    }	

	/**
	 * Find which double is better.
	 * Whether or not a variable is better is determined by 
	 * the maximize parameter.
	 * @param a the first value to test.
	 * @param b the second value to test.
	 * @param maximize whether or not to test if {@code a > b}.  If false, tests for
	 * {@code a < b}.	
	 * @return True if {@code a} is better than {@code b}.
	 */
	private boolean better(double a, double b, boolean maximize) {
		if (maximize) {
			return a > b;
		} else {
			return a < b;
		}
	}
	
	
	/**
     * Fill an array with random values.
     * @return The array.
     */
    private double[] rands() {
        double[] toReturn = new double[n];
        for (int i = 0; i < n; i++) {
			double range = maximums[i] - minimums[i];
			toReturn[i] = Math.random() * range + minimums[i]; // generate random number in range
		}
        return toReturn;
    }
	
	/**
     * Fill an array with zeros
     * @return The array.
     */
    private double[] zeros() {
        double[] toReturn = new double[n];
        for (int i = 0; i < n; i++) {
                toReturn[i] = 0;
        }
        return toReturn;
    }
	
	
	/**
	 * Run the fitness function.
	 * @param input the input values (domain).
	 * @return the result (range).
	 */
	public abstract double f(double[] input);
}