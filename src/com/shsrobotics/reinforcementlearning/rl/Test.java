package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.rl.RLAgent.Action;
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
	
	static double[] environment = {0};
	
	public static void main(String[] args) {
		options.put("Accuracy", 0.85);
		ranges.put("Minimum Action Values", minimumActionValues);
		ranges.put("Maximum Action Values", maximumActionValues);
		ranges.put("Minimum State Values", minimumStateValues);
		ranges.put("Maximum State Values", maximumStateValues);
		ranges.put("Reward Range", rewardRange);
		ModelBasedLearner learner = new ModelBasedLearner(actionNames, stateNames, ranges, options);
		
		learner.setMode(RLAgent.Mode.kLearn);
		
		for (int i = 0; i < 100; i++) {
			if (environment[0] < 20) {
				environment[0]++;
			}
			State state = learner.new State(stateNames, environment);
			Action action = learner.requestAction(state);
			double reward = requestReward(action);
			State newState = learner.new State(stateNames, environment);
			learner.updateSupervisedLearner(state, action, newState, reward);
			learner.plan(newState);
		}
		
		learner.setMode(RLAgent.Mode.kAct);
		State state = learner.new State(stateNames, environment);
		Action action = learner.requestAction(state);
		System.out.println("Opened new line: " + (Math.round(action.get()[0]) == 1));
	}

	private static double requestReward(Action action) {
		boolean opened = (Math.round(action.get()[0]) == 1);
		int people = (int) environment[0];
		if (opened) {
			environment[0] -= 2;
			if (people > 10) {
				return 10;
			} else {
				return -2;
			}
		} else {
			if (people > 10) {
				return -4;
			} else {
				return 0;
			}
		}
	}
}
