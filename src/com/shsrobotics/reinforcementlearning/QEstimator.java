package com.shsrobotics.reinforcementlearning;

public class QEstimator {
	
	private final int hiddenLayers;
	private final int learningRate;
	private final int momentum;
	private final int numberOfOutputs;
	private final int numberOfInputs;
	private final int outputLayer;
	
	private int[] sizes;
	private double[][] biases;
	private double[][][] weights;
	private double[][] outputs;
	private double[][] deltas;
	private double[][][] changes;
	private double[][] errors;
	
	/**
	 * <h1>Q-Value Estimator</h1>
	 * @param inputs the number of input parameters
	 * @param outputs the number of output parameters
	 * @param hiddenLayers the number of hidden layers
	 * @param learningRate the learning rate
	 * @param momentum momentum to prevent local maximization
	 */
	public QEstimator(int inputs, int hiddenLayers, int outputs, double learningRate, double momentum) {
		this.numberOfInputs = inputs;
		this.numberOfOutputs = outputs;
		this.hiddenLayers = hiddenLayers;		
		this.learningRate = hiddenLayers;		
		this.momentum = hiddenLayers;	
		this.outputLayer = 1 + this.hiddenLayers;
		
		this.sizes = new int[this.outputLayer + 1];
		this.outputs = new double[this.outputLayer + 1][];
		this.biases = new double[this.outputLayer + 1][];
		this.deltas = new double[this.outputLayer + 1][];
		this.errors = new double[this.outputLayer + 1][];
		this.weights = new double[this.outputLayer][][];
		this.changes = new double[this.outputLayer][][];
		for (int layer = 0; layer <= this.outputLayer; layer++) {
			if (layer == this.outputLayer) {
				this.outputs[layer] = zeros(this.numberOfOutputs);
				this.biases[layer] = rands(this.numberOfOutputs);
				this.deltas[layer] = zeros(this.numberOfOutputs);
				this.errors[layer] = zeros(this.numberOfOutputs);
				this.sizes[layer] = this.numberOfOutputs;	
			} else {				
				this.outputs[layer] = zeros(this.numberOfInputs);
				this.biases[layer] = rands(this.numberOfInputs);
				this.deltas[layer] = zeros(this.numberOfInputs);
				this.errors[layer] = zeros(this.numberOfInputs);
				this.sizes[layer] = this.numberOfInputs;					
				this.weights[layer] = new double[this.numberOfInputs][];
				this.changes[layer] = new double[this.numberOfInputs][];		
			}
		}
		for (int layer = 0; layer < this.outputLayer; layer++) {
			for (int node = 0; node < this.sizes[layer]; node++) {
				this.weights[layer][node] = rands(this.sizes[layer + 1]);				
				this.changes[layer][node] = rands(this.sizes[layer + 1]);				
			}
		}		
	}
	
	public void addDataPoint(double[] input, double[]output) {
		if (input.length != numberOfInputs) {
			throw new Error("Incorrect number of inputs.");
		}
		if (output.length != numberOfOutputs) {
			throw new Error("Incorrect number of outputs.");
		}
		runInput(input);
		calculateDeltas(output);
		adjustWeights();
	}
	
	public void train(DataPoint[] data) {
		for (int i = 0; i < 20000; i++) {
			for (int j = 0; j < data.length; j++) {
				addDataPoint(data[j].input, data[j].output);
			}
		}
	}

	public double[] runInput(double[] input) {
		outputs[0] = input;
		for (int layer = 1; layer <= this.outputLayer; layer++) { // for every layer
			for (int node = 0; node < this.sizes[layer]; node++) { // for every node
				double currentWeights[] = this.weights[layer - 1][node];
				double sum = this.biases[layer][node];
				for (int k = 0; k < currentWeights.length; k++) { // for every connection
					sum += currentWeights[k] * input[k];
				}
				outputs[layer][node] = 1 / (1 + Math.exp(-sum)); // logistic output
			}
			input = outputs[layer];
		}
		return outputs[outputLayer];
	}

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
						error += currentDeltas[k] * weights[layer][node][k];
					}
				}
				errors[layer][node] = error; // save error
				deltas[layer][node] = error * output * (1 - output); // save deltas
			}
		}
	}

	private void adjustWeights() {
		for (int layer = 1; layer <= this.outputLayer; layer++) { // for every layer			
			double[] incoming = outputs[layer - 1];	
			for (int node = 0; node < this.sizes[layer]; node++) { // for every node
				double currentDelta = deltas[layer][node]; // the next layers deltas (we are working backwards)
				for (int k = 0; k < incoming.length; k++) { // for each incoming connection
					double currentChange = this.changes[layer - 1][node][k];
					currentChange = (learningRate * currentDelta * incoming[k]) + (this.momentum * currentChange);
					
					changes[layer - 1][node][k] = currentChange;
					weights[layer - 1][node][k] += currentChange;
				}
				biases[layer][node] += learningRate * currentDelta;
			}
		}
	}
	
	private double randomWeight() {
		return Math.random() * 0.4 - 0.2;
	}
	
	private double[] zeros(int size) {
		double[] toReturn = new double[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = 0;
		}
		return toReturn;
	}
	
	private double[] rands(int size) {
		double[] toReturn = new double[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = randomWeight();
		}
		return toReturn;
	}
}
