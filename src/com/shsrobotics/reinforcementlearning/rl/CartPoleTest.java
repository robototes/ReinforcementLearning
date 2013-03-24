package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.rl.RLAgent.Action;
import com.shsrobotics.reinforcementlearning.rl.RLAgent.State;
import java.util.HashMap;
import java.util.Map;

public class CartPoleTest {
	public static final double G = 9.8; // m / s^2
	public static final double L = 1.0; // m
	public static final double M = 10.0; // kg
	
	static double angle = 0.0; // degrees, with 0 being downward.
	static double angularVelocity = 0.0; // m / s
	static double cartVelocity = 0.0; // m / s
	static double power = 0.0; // W
	static double position = 0.0; // m
	
	public static final String[] actionNames = {"Power"};
	public static final String[] stateNames = {"Angle", "Angular velocity"};	
	public static final Map<String, Number> options = new HashMap<>();
	public static final Map<String, double[]> ranges = new HashMap<>();
	public static final double[] minimumActionValues = {-1};
	public static final double[] maximumActionValues = {1};
	public static final double[] minimumStateValues = {-180, -360};
	public static final double[] maximumStateValues = {180, 360};
	public static final double[] rewardRange = {-1, 10};
	
	public static ModelBasedLearner learner;
	
	public static void main(String[] args) {
		options.put("Accuracy", 0.85);
		ranges.put("Minimum Action Values", minimumActionValues);
		ranges.put("Maximum Action Values", maximumActionValues);
		ranges.put("Minimum State Values", minimumStateValues);
		ranges.put("Maximum State Values", maximumStateValues);
		ranges.put("Reward Range", rewardRange);
		
		learner = new ModelBasedLearner(actionNames, stateNames, ranges, options);
		learner.setMode(RLAgent.Mode.kLearn);
		
		for (int i = 0; i < 1000; i++) {
			double[] environment = {angle, angularVelocity};
			State state = learner.new State(stateNames, environment);
			Action action = learner.requestAction(state);
			setPower(action.getActionParameter("Power"));
			stepSimulation();
			double reward = requestReward();
			
			double[] newEnvironment = {angle, angularVelocity};
			State newState = learner.new State(stateNames, environment);
			learner.UCTReset().updateSupervisedLearner(state, action, newState, reward).plan(state);
		}
	}
	
	public static void setPower(double newPower) {
		if (newPower > 1.0) newPower = 1.0;
		if (newPower < -1.0) newPower = -1.0;
		power = newPower;
	}
	
	public static void stepSimulation() {
		double accelerationOnCart = power * cartVelocity / M;
		cartVelocity += 0.01 * accelerationOnCart;
		double positionChange = 0.01 * cartVelocity;
		double angleChange = Math.acos(2 * positionChange / L);
		position += positionChange;
		angle += angleChange;
		angularVelocity += angleChange / 0.01;
	}
	
	public static double requestReward() {
		double roundedAngle = Math.round(angle);
		if (roundedAngle == 180 || roundedAngle == -180) {
			return 10;
		} else {
			return Math.abs(roundedAngle - 180) / 180; // distance from goal
		}
	}
}
