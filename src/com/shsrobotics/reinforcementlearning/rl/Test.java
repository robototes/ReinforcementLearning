package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.rl.ModelBasedLearner.Prediction;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.Action;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.Mode;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.State;
import java.util.HashMap;
import java.util.Map;

public class Test {
	public static final String[] actionNames = {"Open new line"};
	public static final String[] stateNames = {"Number of people in line"};	
	public static final Map<String, Number> options = new HashMap<>();
	public static final Map<String, double[]> ranges = new HashMap<>();
	public static final double[] minimumActionValues = {0};
	public static final double[] maximumActionValues = {1};
	public static final double[] minimumStateValues = {0};
	public static final double[] maximumStateValues = {20};
	public static final double[] rewardRange = {-4, 10};
	
	public ModelBasedLearner learner;
	
	double[] environment = {0};
	
	public static void main(String[] args) {
		options.put("Accuracy", 0.85);
		options.put("Exploration Rate", 1.0);
		options.put("Number of Bins", 20);
		ranges.put("Minimum Action Values", minimumActionValues);
		ranges.put("Maximum Action Values", maximumActionValues);
		ranges.put("Minimum State Values", minimumStateValues);
		ranges.put("Maximum State Values", maximumStateValues);
		ranges.put("Reward Range", rewardRange);
		
		new Test().test();
	}
	
	public void run() {
		train();
		
		learner.setMode(RLAgent.Mode.kAct);
		environment[0] = 07;
		for (int i = 0; i < 10; i++) {
			State state = learner.new State(stateNames, environment);
			double[] actionValues = learner.requestAction(state).get();
			actionValues[0] = Math.round(actionValues[0]);
			Action action = learner.new Action(actionNames, actionValues);
			Prediction queryModel = learner.queryModel(state, action);
			
			System.out.println("Actual Reward: " + requestReward(action));
			System.out.println("Predicted Reward: " + queryModel.reward);
			System.out.println("Predicted New People: " + queryModel.state.get()[0]);
			System.out.println();
			
			environment[0]++;
		}
	}
	
	public void test() {
		train();
		
		double[] actionValues = {1.0};
		double[] stateValues = {10};
		Prediction query = learner.queryModel(learner.new State(stateNames, stateValues), 
			learner.new Action(actionNames, actionValues));
		
		System.out.println("R: " + query.reward);
		System.out.println("S: " + query.state.get()[0]);
		System.out.println();
	}
	
	public void train() {
		learner = new ModelBasedLearner(actionNames, stateNames, ranges, options);
		learner.setMode(RLAgent.Mode.kLearn);
		
		for (int i = 0; i < 200; i++) {
			environment[0]++;
			checkEnvironment();
			State state = learner.new State(stateNames, environment);
			double[] actionValues = learner.requestAction(state).get();
			actionValues[0] = Math.round(actionValues[0]);
			Action action = learner.new Action(actionNames, actionValues);
			double reward = requestReward(action);
			State newState = learner.new State(stateNames, environment);
			learner.updateSupervisedLearner(state, action, newState, reward).plan(state);
		}
	}

	private double requestReward(Action action) {
		boolean opened = (Math.round(action.get()[0]) == 1);
		int people = (int) environment[0];
		if (opened) {
			if (learner.getMode() == Mode.kAct) System.out.println("Opened a new line with " + people + " people in line.");		
			environment[0] -= 2;
			checkEnvironment();
			if (people > 10) {
				return 10;
			} else {
				return -2;
			}
		} else {
			if (learner.getMode() == Mode.kAct) System.out.println("Did not open a new line with " + people + " people in line.");		
			if (people > 10) {
				return -4;
			} else {
				return 0;
			}
		}
	}
	
	private void checkEnvironment() {
		if (environment[0] > 20) {
			environment[0] = 20;
		}
		if (environment[0] < 0) {
			environment[0] = 0;
		}
	}
}