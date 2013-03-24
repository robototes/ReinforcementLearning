package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.rl.RLAgent.Action;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.State;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MountainCarTest {
	public static final String[] actionNames = {"Power"};
	public static final String[] stateNames = {"Velocity", "Position"};	
	public static final Map<String, Number> options = new HashMap<>();
	public static final Map<String, double[]> ranges = new HashMap<>();
	public static final double[] minimumActionValues = {-1};
	public static final double[] maximumActionValues = {1};
	public static final double[] minimumStateValues = {-2, -2};
	public static final double[] maximumStateValues = {2, 2};
	public static final double[] rewardRange = {-1, 1};
	
	public ModelBasedLearner learner;
	
	double V = 0;
	double X = -0.5;
	
	boolean done = false;
	Integer count = 0;
	
	Map<Integer, double[]> history = new HashMap<>();
		
	public static void main(String[] args) {
		options.put("Accuracy", 0.9);
		options.put("Number of Bins", 250);
		ranges.put("Minimum Action Values", minimumActionValues);
		ranges.put("Maximum Action Values", maximumActionValues);
		ranges.put("Minimum State Values", minimumStateValues);
		ranges.put("Maximum State Values", maximumStateValues);
		ranges.put("Reward Range", rewardRange);
		
		new MountainCarTest().train().generateCSV();
	}
	
	public MountainCarTest run() {
		while(!done) {
			double[] environment = {V, X};
			State state = learner.new State(stateNames, environment);
			double[] actionValues = learner.requestAction(state).get();
			step(actionValues[0]);

			double[] newEnvironment = {V, X};
			State newState = learner.new State(stateNames, newEnvironment);
			Action action = learner.new Action(actionNames, actionValues);
			double reward = requestReward();
			learner.updateSupervisedLearner(state, action, newState, reward).plan(state);
		}
		
		return this;
	}
	
	public MountainCarTest train() {
		learner = new ModelBasedLearner(actionNames, stateNames, ranges, options);
		learner.setMode(RLAgent.Mode.kLearn);
		
		for (int i = 0; i < 1; i++) {
			while(!done) {
				double[] environment = {V, X};
				State state = learner.new State(stateNames, environment);
				double[] actionValues = learner.requestAction(state).get();
				step(actionValues[0]);

				double[] newEnvironment = {V, X};
				State newState = learner.new State(stateNames, newEnvironment);
				Action action = learner.new Action(actionNames, actionValues);
				double reward = requestReward();
				learner.updateSupervisedLearner(state, action, newState, reward).plan(state);
			}
		}		
		
		learner.setMode(RLAgent.Mode.kAct);
		
		return this;
	}
	
	public void step(double P) {
		double[] state = {V, X};
		history.put(count++, state);
		
		V = V + P * 0.001 + cos(3 * X) * -0.0025;
		P += V;
	}
	
	public double requestReward() {
		if (X < 0.6) {
			return -1;
		} else {
			done = true;
			return 1;
		}
	}
	
	private double cos(double v) {
		return Math.cos(v);
	}	
	
	private void generateCSV()
	{
		try {
			try (FileWriter writer = new FileWriter("C:\\users\\Cory\\Documents\\test.csv")) {
				int length = history.size();
				for (int i = 0; i < length; i++) {
					writer.append(Double.toString(V));
					writer.append(",");
					writer.append(Double.toString(X));
					writer.append("\n");
				}

				writer.flush();
			}
		 } catch(IOException e) {
			  e.printStackTrace();
		 } 
    }
}
