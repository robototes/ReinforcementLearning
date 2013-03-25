package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.rl.RLAgent.Action;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.State;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MountainCarTest {
	public static final String[] actionNames = {"Power"};
	public static final String[] stateNames = {"Velocity", "Position"};	
	public static final Map<String, Number> options = new HashMap<>();
	public static final Map<String, double[]> ranges = new HashMap<>();
	public static final double[] minimumActionValues = {-1};
	public static final double[] maximumActionValues = {1};
	public static final double[] minimumStateValues = {-10, -5};
	public static final double[] maximumStateValues = {10, 1};
	public static final double[] rewardRange = {-1, 1};
	
	public ModelBasedLearner learner;
	
	double V = 0;
	double X = -0.5;
	
	double r = 0;
	
	boolean done = false;
	Integer count = 0;
	
	Map<Integer, double[]> history = new HashMap<>();
		
	public static void main(String[] args) {
		options.put("Accuracy", 0.9);
		options.put("Number of Bins", 100);
		ranges.put("Minimum Action Values", minimumActionValues);
		ranges.put("Maximum Action Values", maximumActionValues);
		ranges.put("Minimum State Values", minimumStateValues);
		ranges.put("Maximum State Values", maximumStateValues);
		ranges.put("Reward Range", rewardRange);
		
		new MountainCarTest().train();
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
		
		{
			double[] environment = {0.001, 0.599};
			State state = learner.new State(stateNames, environment);
			double[] actionValues = {1};
			double[] newEnvironment = {0, 0.6};
			State newState = learner.new State(stateNames, newEnvironment);
			Action action = learner.new Action(actionNames, actionValues);
			double reward = 1;
			learner.updateSupervisedLearner(state, action, newState, reward);
		}
		
		learner.setMode(RLAgent.Mode.kLearn);
		
		for (int i = 0; i < 5; i++) {
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
				
				if (count % 100 == 0) {
					System.out.println(count);
					generateCSV();
				}
			}
			System.out.println("DONE");
			Toolkit.getDefaultToolkit().beep();
			generateCSV();
			
			r = 0;
			V = 0;
			X = -0.5;
			done = false;
		}		
		
		learner.setMode(RLAgent.Mode.kAct);
		
		return this;
	}
	
	public void step(double P) {
		double[] state = {V, X, r};
		history.put(count++, state);
		
		V += P * 0.01 - cos(3 * X) * 0.005;
		X += V;
	}
	
	public double requestReward() {
		if (X < 0.6) {
			r += -1;
			return -1;
		} else {
			done = true;
			return 0;
		}
	}
	
	private double cos(double v) {
		return Math.cos(v);
	}	
	
	private void generateCSV()
	{
		try {
			try (FileWriter writer = new FileWriter("C:\\Users\\Cory McCartan\\Documents\\test.csv")) {
				Iterator it = history.keySet().iterator();
				while (it.hasNext()) {		
					Integer ct = (Integer) it.next();
					double[] state = history.get(ct);
					writer.append(Integer.toString(ct));
					writer.append(",");
					writer.append(Double.toString(state[0]));
					writer.append(",");
					writer.append(Double.toString(state[1]));
					writer.append(",");
					writer.append(Double.toString(state[2]));
					writer.append("\n");
				}
				
				writer.flush();
			}
		 } catch(IOException e) {
			 System.out.println("Please close any process that is using this file.");
		 }
    }
}
