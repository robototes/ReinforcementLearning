package com.shsrobotics.reinforcementlearning;

/**
 * <h1>Reinforcement Learning Q Learner</h1>
 * Learns how to act based on rewards.
 * @author Team 2412
 */
public class QLearner {
    private Mode currentMode;
    
    private double learningRate;
	private double discountFactor;
    private double learnerAccuracy;
    
    public double[] minimumStateValues;
    public double[] minimumActionValues;
    public double[] maximumStateValues;
    public double[] maximumActionValues;
    
    private int states;
    private int actions;
    private String[] stateNames;
    private String[] actionNames;
    
    private ErsatzEstimator qEstimator;
	
	private int iterations;
	
	private double NelderMeadReflectionCoefficient = 1;
	private double NelderMeadExpansionCoefficient = 2;
	private double NelderMeadContractionCoefficient = -1 / 2;
	private double NelderMeadShrinkCoefficient = 1 / 2;
    
    /**
     *  Accepts string array of action values and environment state parameters
     * @param actions
     * @param states
     */
    public QLearner(String[] actions, String[] states) {
        currentMode = Mode.kLearn;
        learningRate = 0.2;
		discountFactor = 0.4;
        learnerAccuracy = 0.9;
        stateNames = states;
        actionNames = actions;
        this.states = states.length;
        this.actions = actions.length; 
		
		iterations = 1;
		
        // assume 0 to 1 for ranges
        minimumStateValues = fill(0.0, this.states); // array of minimums for state parameters
        maximumStateValues = fill(1.0, this.states); // array of maximums for state parameters
        minimumActionValues = fill(0.0, this.actions); // array of minimums for action parameters
        maximumActionValues = fill(1.0, this.actions); // array of maximums for action parameters
        
        //Create a neural network
        qEstimator = new ErsatzEstimator(this.states + this.actions, (int) Math.ceil(Math.sqrt(this.states)), 1, 0.5, 0.2);
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
		qEstimator.setIterations(2);
    }
    
    /**
     * Request action given current state. <br />
     * For use in Learn and Act modes.
     * @param state
     * @return
     */
    public Action requestAction(State state) {
        double exploreCutoff = learningRate;        
        double[] actionValues = new double[actions];
        
        if (currentMode.chooseBestOption) { // check modes
            exploreCutoff = 0.0;
        }
        if (!currentMode.allowActionRequests) {
            throw new Error("Wrong learning mode.");
        }
        
        if (Math.random() < exploreCutoff) { // choose random values
            actionValues = rands();
        } else {
			int accuracyIterations = (int) Math.ceil(1 / (1 - learnerAccuracy));
			Point[] vertices = new Point[actions + 1]; // simplex vertices
			
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = new Point(rands(), state);
			}			
			vertices = sort(vertices); // sort according to value
			
			for (int i = 0; i < accuracyIterations; i++) {					
				//calculate CG/centroid of simplex
				double[] centroid = zeros(actions);
				
				for (int vertex = 0; vertex < vertices.length - 1; vertex++) { // for each vector/coordinate except worst
					double cgSum = 0.0;
					for (int k = 0; k < actions; k++) { 
						cgSum += vertices[vertex].coordinates[k];
					}
					centroid[vertex] = cgSum / (vertices.length + 1);
				}			
				Point worst = vertices[actions];
				
				double[] reflectedPoint = new double[actions];
				double[] expandedPoint = new double[actions];
				double[] contractedPoint = new double[actions];
				for (int j = 0; j < actions; j++) {
					double difference = (centroid[j] - worst.coordinates[j]);
					reflectedPoint[j] = centroid[j] + NelderMeadReflectionCoefficient * difference;
					expandedPoint[j] = centroid[j] + NelderMeadExpansionCoefficient * difference;
					contractedPoint[j] = centroid[j] + NelderMeadContractionCoefficient * difference;
				}
				
				double reflectedValue = estimateQ(state, new Action(reflectedPoint)).Q;
				double expandedValue = estimateQ(state, new Action(expandedPoint)).Q;
				double contractedValue = estimateQ(state, new Action(contractedPoint)).Q;
				if (vertices[0].value >= reflectedValue && reflectedValue > vertices[actions - 1].value) { // worst than best but better than second-best
					vertices[actions] = new Point(reflectedPoint, state); // replace worst with new reflected point
					continue; // next iteration
				} else if (reflectedValue > vertices[0].value) { // better than best
					if (expandedValue > reflectedValue) {
						vertices[actions] = new Point(expandedPoint, state); // replace worst with new expanded point
						continue; // next iteration
					} else {
						vertices[actions] = new Point(reflectedPoint, state); // replace worst with new reflected point
						continue; // next iteration
					}
				} else if (reflectedValue <= vertices[actions].value && contractedValue > worst.value) { // worst than second-best
					vertices[actions] = new Point(contractedPoint, state); // replace worst with new contracted point
					continue; //next iteration
				} else {
					for (int j = 1; j < vertices.length; j++) { // for all but best use reduced point						
						double[] reducedPoint = new double[actions];
						for (int k = 0; k < actions; k++) {
							double difference = (centroid[k] - worst.coordinates[k]);
							reducedPoint[k] = centroid[k] + NelderMeadShrinkCoefficient * difference;
						}
						vertices[j] = new Point(reducedPoint, state);
					}
				}							
				vertices = sort(vertices); // sort according to value
			}
			actionValues = vertices[0].coordinates;
        }
        
        return new Action(actionValues);
    }
	
	/**
	 * Estimate a Q-Value.
	 * @param state the state parameters.
	 * @param action the action parameters.
	 * @return the estimated Q-Value.
	 */
	public Q estimateQ(State state, Action action) {
		return new Q(qEstimator.runInput(join(state.getRaw(), action.getRaw())));
	}
	
	/**
	 * Estimate multiple Q-Value.
	 * @param state the state parameters.
	 * @param action the action parameters.
	 * @return an array of estimated Q-Values.
	 */
	private double[] estimateQs(State state, double[][] action) {
		double[] toReturn = new double[action.length];
		for (int i = 0; i < action.length; i++) {
			toReturn[i] = qEstimator.runInput(join(state.getRaw(), action[i]))[0];
		}
		return toReturn;
	}
	
	/**
	 * Update the {@link ErsatzEstimator} Q values.
	 * @param state the old state.
	 * @param action the action taken.
	 * @param reward the reward gained.
	 * @param newState the new state transitioned to.
	 */
	public void updateQFactors(State state, Action action, State newState, double reward) {
		double aK = getPrimaryLearningRate();
		double estimatedQ = estimateQ(state, action).Q;
		double q = (1 - aK) * estimatedQ + aK * (reward + discountFactor 
			* estimateQ(newState, requestAction(newState)).Q - estimatedQ);	
		
		double[] output = {reward};
		qEstimator.addDataPoint(new DataPoint(join(state.getRaw(), action.getRaw()), output));
        qEstimator.train();
		iterations++;
	}
    
    /**
     * Sets {@link QLearner} mode. <br />
     * <ul>
     *      <li>Watch: Gather data, but don't act.</li>
     *      <li>Learn: Explore environment, act on data, but don't always choose best choice.</li>
     *      <li>Act: Act on data, choose best choice.</li>
     * </ul>
     * @param mode
     */
    public void setMode(Mode mode) {
        currentMode = mode;
    }
    
    /**
     * Sets Learning rate. <br />
     * A higher learning rate will mean more choices are chosen randomly.
     * @param rate
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
        qEstimator.setLearningRate(learningRate);
    }
    
    
    /**
     * Sets the Q maximization algorithm accuracy. <br />
     * A higher value will increase accuracy at the cost of computation time.
     * @param accuracy
     */
    public void setAccuracy(double accuracy) {
        learnerAccuracy = accuracy;		
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
    }
		
    
    /**
     * A mode that the {@link QLearner} can operate in
     */
    public static class Mode { 
        
        /**
         * Update Q-Values but don't act on them.
         */
        public static final Mode kWatch = new Mode(false, false); // gather data
        
        /**
         * Explore the environment, updating Q-Values
         */
        public static final Mode kLearn = new Mode(true, false); // learn
        
        /**
         * Choose the best action to act on instead of exploring, updating Q-Values
         */
        public static final Mode kAct = new Mode(true, true); // act on learned data
        
        private final boolean allowActionRequests;
        private final boolean chooseBestOption;
        
        private Mode(boolean allowActionRequests, boolean chooseBestOption) {
            this.allowActionRequests = allowActionRequests;
            this.chooseBestOption = chooseBestOption;
        }
    }
    
	
	
    /**
     * Actions available, represented by key-value pairs.
     */
    public class Action {
        private double[] parameters;
        
        /**
         * Create an action.
         * @param values a double array of values.
         */
        public Action(double[] values) {
            if (values.length != actions) {
                throw new Error("Wrong action parameter length");
            }
            parameters = values;
        }
        
        /**
         * Returns value from key
         * @param key the string key.
         * @return The value associated with the key.
         */
        public double get(String key) {
			int index = indexOf(key, actionNames);
			if (index == -1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return parameters[index];
			}
        }
		
		/**
		 * Get the raw data.
		 * @return the values.
		 */
		public double[] getRaw() {
			return parameters;
		}
    }
    
    /**
     * An environment state, represented by key-value pairs.
     */
    public class State {
        private double[] parameters;
        
        /**
         * Create a state.
         * @param values a double array of values.
         */
        public State(double[] values){
            if (values.length != states) {
                throw new Error("Wrong state parameter length");
            }
            parameters = values;
        }
        
        /**
         * Returns value from key
         * @param key the string key.
         * @return The value associated with the key.
         */
        public double get(String key) {
           int index = indexOf(key, stateNames);
			if (index == -1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return parameters[index];
			}
        }
		
		/**
		 * Get the raw data.
		 * @return the values.
		 */
		public double[] getRaw() {
			return parameters;
		}
    }
    
	/**
	 * Holds Q-Values
	 */
	public class Q {
		public double Q;
		
		protected Q(double[] estimatorOutput) {
			Q = estimatorOutput[0];
		}
	}
	
	/**
	 * A data point.  Used for optimization
	 */
	private final class Point {
		public double[] coordinates;
		public double value;
		
		/**
		 * Create a point.
		 * @param coordinates the action coordinates.
		 * @param state the Q-Value from the coordinates.
		 */
		public Point(double[] coordinates, State state)	{
			this.coordinates = coordinates;
			value = estimateQ(state, new Action(coordinates)).Q;
		}
	}
	
    /**
     * Finds the  value from an array of string keys
     * @param value the value to look for.
     * @param keys the array of string keys.
     */
    private int indexOf(String value, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            if (value.equals(keys[i])) { // found
                return i;
            }
        }
        return -1; // not found
    }
    
	
    /**
     * Fill double array with value
     * @param value the value to fill the array with.
     * @param length the length of the array.
     * @return
     */
    private double[] fill(double value, int length) {
        double[] toReturn = new double[length];
        for (int i = 0; i < length; i++) {
            toReturn[i] = value;
        }
        return toReturn;
    }
	
	
	/**
	 * Learning rate that decays over time
	 * @return the learning rate.
	 */
	private double getPrimaryLearningRate() {
		return (Math.log10(iterations) / iterations);
	}	
	
	
	/**
	 * Join two arrays
	 * @param a
	 * @param b
	 * @return 
	 */
	private DataPoint[] join(DataPoint[] a, DataPoint[] b) {
		int length = a.length + b.length;
		DataPoint[] toReturn = new DataPoint[length];
		for (int i = 0; i < length; i++) {
			if (i > a.length - 1) {
				toReturn[i] = b[i - a.length];
			} else {
				toReturn[i] = a[i];
			}
		}
		return toReturn;
	}
	
	/**
	 * Join two arrays
	 * @param a
	 * @param b
	 * @return 
	 */
	private double[] join(double[] a, double[] b) {
		int length = a.length + b.length;
		double[] toReturn = new double[length];
		for (int i = 0; i < length; i++) {
			if (i > a.length - 1) {
				toReturn[i] = b[i - a.length];
			} else {
				toReturn[i] = a[i];
			}
		}
		return toReturn;
	}
    
    /**
	 * Sort an array.
	 * @param a the array.
	 * @return the sorted result.
	 */
	private Point[] sort(Point[] a) {
		double maximum = Double.NEGATIVE_INFINITY;
		int minIndex = -1;		
		boolean[] usedIndices = new boolean[a.length];
        Point[] toReturn = new Point[a.length];
        for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				if (usedIndices[j] == true) continue;
				if (a[j].value > maximum) {
					minIndex = j;
					maximum = a[j].value;
					usedIndices[j] = true;
				}			
			}
			toReturn[i] = a[minIndex];
        }
        return toReturn;
    }	
	
	
	/**
	 * Create a state.
	 * @param arg the constructor arguments.
	 * @return The state.
	 */
	public State getState(double[] arg) {
		return new State(arg);
	}
	
	/**
	 * Create an action.
	 * @param arg the constructor arguments.
	 * @return The action.
	 */
	public Action getAction(double[] arg) {
		return new Action(arg);
	}
		
	/**
     * Fill an array with random action values
     * @param size the size of the array
     * @return The array.
     */
    private double[] rands() {
        double[] toReturn = new double[this.actions];
        for (int i = 0; i < this.actions; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                toReturn[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
        }
        return toReturn;
    }
	
	/**
     * Fill an array with zeros
     * @param size the size of the array
     * @return The array.
     */
    private double[] zeros(int size) {
        double[] toReturn = new double[size];
        for (int i = 0; i < size; i++) {
                toReturn[i] = 0;
        }
        return toReturn;
    }
}