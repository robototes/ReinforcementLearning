package com.shsrobotics.reinforcementlearning;

import com.shsrobotics.reinforcementlearning.util.DataPoint;

/**
 * <h1>Q Estimator</h1>
 * <h2>Estimates Q Values using a neural network for use in a {@link QLearner}</h2>
 * Different from a normal neural network because we learn from estimates of correct Q-Values, not guaranteed accurate values.
 */
public class NeuralNetworkQEstimator {
	
    private final int hiddenLayers;
    private final int numberOfOutputs;
    private final int numberOfInputs;
    private final int outputLayer;    
    
    private double learningRate;
	private double momentum;

    private int[] sizes;
    private double[][] biases;
    private double[][][] weights;
    private double[][] outputs;
    private double[][] deltas;
    private double[][][] changes;
    private double[][] errors;
	
	private DataPoint[] data;	
	private int shortTermMemory;
	private int iterations;

    /**
     * <h1>Q-Value Estimator</h1>
     * @param inputs the number of input parameters
     * @param outputs the number of output parameters
     * @param hiddenLayers the number of hidden layers
     * @param learningRate the learning rate
     */
    public NeuralNetworkQEstimator(int inputs, int hiddenLayers, int outputs, double learningRate) {
        this.numberOfInputs = inputs;
        this.numberOfOutputs = outputs;
        this.hiddenLayers = hiddenLayers;		
        this.learningRate = hiddenLayers;
        this.outputLayer = 1 + this.hiddenLayers;
		
		iterations = 20;
		shortTermMemory = 50000; // store last n values
		data = new DataPoint[shortTermMemory];

		initialize();	
    }
	
	/**
     * <h1>Q-Value Estimator</h1>
     * @param inputs the number of input parameters.
     * @param outputs the number of output parameters.
     * @param hiddenLayers the number of hidden layers.
     * @param learningRate the learning rate.
	 * @param momentum the adjustment momentum.
     */
    public NeuralNetworkQEstimator(int inputs, int hiddenLayers, int outputs, double learningRate, double momentum) {
        this.numberOfInputs = inputs;
        this.numberOfOutputs = outputs;
        this.hiddenLayers = hiddenLayers;		
        this.learningRate = hiddenLayers;
		this.momentum = momentum;
        this.outputLayer = 1 + this.hiddenLayers;
		
		iterations = 20;
		shortTermMemory = 50000; // store last n values
		data = new DataPoint[shortTermMemory];

        initialize();
    }
	
	private void initialize() {
		int totalSize = this.outputLayer + 1;
        this.sizes = new int[totalSize];
        this.outputs = new double[totalSize][];
        this.biases = new double[totalSize][];
        this.deltas = new double[totalSize][];
        this.errors = new double[totalSize][];
        this.weights = new double[totalSize][][];
        this.changes = new double[totalSize][][];		
		
		LayerType currentLayer;
		int hiddenLayerNodes = (int) (1.5 * numberOfInputs);
        for (int layer = 0; layer <= this.outputLayer; layer++) {
			currentLayer = getLayer(layer);
			if (currentLayer == LayerType.inputLayer) {
				this.sizes[layer] = this.numberOfInputs;
			} else if (currentLayer == LayerType.hiddenLayer) {
				this.sizes[layer] = hiddenLayerNodes;
			} else if (currentLayer == LayerType.outputLayer) {
				this.sizes[layer] = this.numberOfOutputs;
			}
		}
		
		for (int layer = 0; layer <= this.outputLayer; layer++) {
			currentLayer = getLayer(layer);			
			int size = this.sizes[layer];
			
            this.outputs[layer] = zeros(size);
			this.deltas[layer] = zeros(size);
			this.errors[layer] = zeros(size);
			
			if (currentLayer != LayerType.inputLayer) {				
				this.biases[layer] = rands(size, currentLayer);
				this.weights[layer] = new double[size][];
				this.changes[layer] = new double[size][];
				
				for (int node = 0; node < this.sizes[layer]; node++) {
					int previousSize = this.sizes[layer - 1];
					this.weights[layer][node] = rands(previousSize, currentLayer);				
					this.changes[layer][node] = zeros(previousSize);				
				}
			}
        }
	}
    
    /**
     * Set the learning rate.
     * @param rate the learning rate.
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
    }
	
	/**
	 * Set the number of training iterations.
	 * @param iterations the number of iterations.
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	/**
	 * Set how long the qEstimator uses new values to train.
	 * @param memory the number values to store.
	 */
	public void setShortTermMemory(int memory) {
		shortTermMemory = memory;
		DataPoint[] temp = slice(data, 0, shortTermMemory);
		data = new DataPoint[shortTermMemory];
		data = temp;
	}

    /**
     * Add a data point to the set and train the network on it.
     * @param data the data point to be added.
     */
    public void addDataPoint(DataPoint newData) {
        if (newData.getInputs().length != numberOfInputs) {
            throw new Error("Incorrect number of inputs.");
        }
        if (newData.getOutputs().length != numberOfOutputs) {
            throw new Error("Incorrect number of outputs.");
        }
		data = shift(data, newData);
    }

    /**
     * Train the network on a series of data points.
     * @param data the data points to train with.
     */
    public void train(DataPoint[] newData) {
        shortTermMemory = newData.length;
		data = new DataPoint[shortTermMemory];
		data = newData;
		train();
    }
	
	/**
     * Train the network on stored data points.
     */
    public void train() {
        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < data.length; j++) {			
                if (data[j] == null) { // if not enough data has been entered
                    continue;
                }
                runInput(data[j].getInputs());
                calculateDeltas(data[j].getOutputs());                
                adjustWeights();                
            }
        }
    }

    /**
     * Make a prediction.
     * @param nextInput the input  array.
     * @return an array representing the output.
     */
    public double[] runInput(double[] input) {
		double[] nextInput = input;
        outputs[0] = input;
        for (int layer = 1; layer <= this.outputLayer; layer++) { // for every hidden layer
            for (int node = 0; node < this.sizes[layer]; node++) { // for every node
                double currentWeights[] = this.weights[layer][node];
				
                double sum = this.biases[layer][node];
                for (int k = 0; k < currentWeights.length; k++) { // for every connection
                    sum += currentWeights[k] * nextInput[k];
                }
                outputs[layer][node] = 1 / (1 + Math.exp(-sum)); // logistic output 
            }
            nextInput = outputs[layer];
        }
        return outputs[outputLayer];
    }

    /**
     * Calculate the error gradient across the network.
     * @param target the array of output values expected.
     */
    private void calculateDeltas(double[] target) {
        for (int layer = this.outputLayer; layer >= 0; layer--) { // every layer, backwards
            for (int node = 0; node < this.sizes[layer]; node++) { // every node
                double output = this.outputs[layer][node]; // output from node

                double error = 0;
                if (layer == this.outputLayer) {
                    error = target[node] - output; // final output error
                }
                else {
                    double[] currentDeltas = deltas[layer + 1]; // the next layers deltas (we are working backwards)
                    for (int k = 0; k < currentDeltas.length; k++) { // every connection
                        error += currentDeltas[k] * weights[layer + 1][k][node];
                    }
                }
                errors[layer][node] = error; // save error
                deltas[layer][node] = error * output * (1 - output); // save deltas
            }
        }
    }

    /**
     * Adjust the weights of the network, based on the error gradient.
     */
    private void adjustWeights() {
        for (int layer = 1; layer <= this.outputLayer; layer++) { // for every layer			
            double[] incoming = outputs[layer - 1];	
            for (int node = 0; node < this.sizes[layer]; node++) { // for every node
                double currentDelta = deltas[layer][node]; // the next layers deltas (we are working backwards)
                for (int k = 0; k < incoming.length; k++) { // for each incoming connection
                    double currentChange = this.changes[layer][node][k];
                    currentChange = (learningRate * currentDelta * incoming[k]) + momentum * currentChange;
                    changes[layer][node][k] = currentChange;
                    weights[layer][node][k] += currentChange;
                }
                biases[layer][node] += learningRate * currentDelta;
            }
        }
    }

    /**
     * Generate a random weight
     * @return The random weight.
     */
    private double randomWeight(LayerType layer) {
		double base = 0.5 * Math.random() - 0.25; // default for hidden and output layers
		if (layer == LayerType.inputLayer) {
			base /= 2; // allows for non-scaled inputs to be accepted into network more easily.
		}	
		return base;
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

    /**
     * Fill an array with random weights
     * @param size the size of the array
     * @return The array.
     */
    private double[] rands(int size, LayerType layer) {
        double[] toReturn = new double[size];
        for (int i = 0; i < size; i++) {
                toReturn[i] = randomWeight(layer);
        }
        return toReturn;
    }
	
	private DataPoint[] slice(DataPoint[] array, int a, int b) {
		DataPoint[] toReturn = new DataPoint[b - a];
		for (int i = 0; i < b - a; i++) {
			toReturn[i] = array[a + i];
		}
		return toReturn;
	}
	
	private DataPoint[] shift(DataPoint[] array, DataPoint toAdd) {
		DataPoint[] toReturn = new DataPoint[shortTermMemory];
		for (int i = 1; i < shortTermMemory; i++) {
			toReturn[i] = array[i-1];
		}
		toReturn[0] = toAdd;
		return toReturn;
	}

	private static class LayerType {
		private int type; // allows use of == to check types.
		
		public static final LayerType inputLayer = new LayerType(1);
		public static final LayerType hiddenLayer = new LayerType(2);
		public static final LayerType outputLayer = new LayerType(3);
		
		private LayerType(int type) {
			this.type = type;
		}
	}
	
	private LayerType getLayer(int layer) {
		LayerType toReturn = LayerType.hiddenLayer;
		if (layer == 0) {
			toReturn = LayerType.inputLayer;
		} else if (layer == this.outputLayer) {
			toReturn = LayerType.outputLayer;
		}
		return toReturn;
	}
}