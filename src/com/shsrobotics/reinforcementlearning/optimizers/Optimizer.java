package com.shsrobotics.reinforcementlearning.optimizers;

/**
 * Optimize coordinates based on either the Nelder-Mead (Simplex) Algorithm
 * or a default pattern search algorithm.
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
	 * Step size for Pattern Search algorithm.
	 * Defaults to ten percent of average variable range.
	 */
	private double PatternSearchStep;
	
	/**
	 * Create an optimizer.
	 * @param dimension the number of variables.
	 * @param iterations how precise to maximize.
	 * @param minimums the minimum domain values.
	 * @param maximums the maximum domain values.
	 */
	public Optimizer(int iterations, double[] minimums, double[] maximums) {
		this.n = minimums.length;
		this.iterations = iterations;
		this.minimums = minimums;
		this.maximums = maximums;
		
		//find average step size
		double sum = 0;
		for (int variable = 0; variable < n; variable++) {
			sum += 0.1 * (maximums[variable] - minimums[variable]); // ten percent of the range
		}
		PatternSearchStep = sum / n;
	}
	
	
	
	/**
	 * Maximize the fitness function output for a set of coordinates.
	 * @return the maximized coordinates.
	 */
	public double[] maximize() {
		if (n >= 4) { // cutoff for when one algorithm is better
			return simplexOptimize(true);
		} else {
			return psOptimize(true);
		}
	}
	
	/**
	 * Minimize the fitness function output for a set of coordinates.
	 * @return the minimized coordinates.
	 */
	public double[] minimize() {
		if (n >= 4) { // cutoff for when one algorithm is better
			return simplexOptimize(false);
		} else {
			return psOptimize(false);
		}
	}

	
	private double[] psOptimize(boolean maximize) {
		/*
		 * Pattern vertices.  Center is stored in 0, Left(k) is stored in k + 1,
		 * and Right(k) is stored in k + n.
		 */
		Point[] vertices = new Point[2 * n + 1]; 
		int length = vertices.length;
		
		// center point placed randomly
		double[] center = rands();
		for (int vertex = 0; vertex < length; vertex++) {
			if (vertex == 0) {
				vertices[vertex] = new Point(center, f(center));
			} else {
				vertices[vertex] = new Point(center, 0.0); // saves computation time, as these are recalculated immediately
			}
		}
		
		// each variable
		for (int k = 0; k < n; k++) {
			int leftPoint = k + 1;
			int rightPoint = k + 1 + n;
			vertices[leftPoint].increment(k, -PatternSearchStep);
			vertices[rightPoint].increment(k, PatternSearchStep);	
		}
		
		for (int i = 0; i < iterations; i++) {
			double best = vertices[0].value; // best value
			int bestIndex = -1; // index of best value
			for (int vertex = 1; vertex < length; vertex++) {
				if (better(vertices[vertex].value, best, maximize)) {
					best = vertices[vertex].value;
					bestIndex = vertex;
				}
			}
			if (bestIndex == -1) { // center was best
				// scale pattern
				PatternSearchStep /= 2; // halve search size.
				for (int k = 0; k < n; k++) {
					int leftPoint = k + 1;
					int rightPoint = k + 1 + n;
					vertices[leftPoint].increment(k, PatternSearchStep);
					vertices[rightPoint].increment(k, -PatternSearchStep);
				}
			} else {
				// move pattern
				double[] changes = new double[n];
				for (int k = 0; k < n; k++) {
					changes[k] = vertices[bestIndex].coordinates[k]
							- vertices[0].coordinates[k];
				}
				for (int vertex = 0; vertex < length; vertex++) {
					for (int k = 0; k < n; k++) {
						vertices[vertex].increment(k, changes[k]);			
					}
				}
			}
		}
		
		return vertices[0].coordinates;
	}
	
	/**
	 * Optimize the fitness function output for a set of coordinates
	 * using the Nelder-Mead algorithm.
	 * @param maximize whether to maximize or minimize. Set to true to maximize.
	 * @return the optimized coordinates.
	 */
	private double[] simplexOptimize(boolean maximize) {
		double[] toReturn = zeros(n);
		Point[] vertices = new Point[n + 1]; // simplex vertices

		for (int i = 0; i < vertices.length; i++) {
			double[] input = rands();
			vertices[i] = new Point(input, f(input));
		}			
		vertices = sort(vertices, maximize); // sort according to value

		for (int i = 0; i < iterations; i++) {					
			//calculate CG/centroid of simplex
			double[] centroid = zeros(n);

			for (int variable = 0; variable < n; variable++) { // each parameter
				double cgSum = 0.0;
				for (int k = 0; k <= n; k++) { 
					cgSum += vertices[k].coordinates[variable];
				}
				centroid[variable] = cgSum / (n + 2);
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
	 * A data point.  Used for optimization.
	 */
	public class Point {
		/**
		 * The data coordinates.
		 */
		public double[] coordinates;
		/**
		 * The data dependent variable (output).
		 */
		public double value;
		
		/**
		 * Create a point.
		 * @param coordinates the action coordinates.
		 * @param value the Q-Value from the coordinates.
		 */
		public Point(double[] coordinates, double value)	{
			this.coordinates = coordinates;
			this.value = value;
		}
		
		/**
		 * Update the {@code value} variable.
		 */
		public void update() {
			this.value = f(this.coordinates);
		}
		
		/**
		 * Increment a coordinate by a specified amount.
		 * @param k the coordinate variable to change.
		 * @param amount the amount to increment by.
		 */
		public void increment(int k, double amount) {
			double[] newCoordinates = this.coordinates.clone();
			newCoordinates[k] += amount;
			this.coordinates = newCoordinates.clone();
			update();
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
	 * @param size the number of zeros.
     * @return The array.
     */
    private double[] zeros(int size) {
        double[] toReturn = new double[size];
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