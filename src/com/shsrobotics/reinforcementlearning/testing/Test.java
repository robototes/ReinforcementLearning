package com.shsrobotics.reinforcementlearning.testing;

import com.shsrobotics.reinforcementlearning.QLearner;
import com.shsrobotics.reinforcementlearning.QLearner.Action;
import com.shsrobotics.reinforcementlearning.QLearner.State;
import java.util.Scanner;

public class Test {	
	static int people = 0;
	
	public static void main(String[] args) {	
		System.out.println("		START");
		Scanner scanner = new Scanner(System.in);
		
		String[] actions = {"Open new line"};
		String[] states = {"Number of customers in line"};
		QLearner learner = new QLearner(actions, states);
		learner.setMode(QLearner.Mode.kLearn);
		double[] minAction = {0.0};
		double[] maxAction = {1.0};
		double[] minState = {0.0};
		double[] maxState = {20.0};
		learner.minimumActionValues = minAction;
		learner.maximumActionValues = maxAction;
		learner.minimumStateValues = minState;
		learner.maximumStateValues = maxState;
        
        learner.setLearningRate(1.0);
		learner.setAccuracy(0.9);
		
		for (int i = 0; i < 50; i++) { // 100 learning iterations
			if (Math.random() < 0.7) {
				people++;
			}
			double[] environment = {people};
			State state = learner.getState(environment);
			Action action = learner.requestAction(state);
			double reward = preformAction(action);
			double[] newEnvironment = {people};
			State newState = learner.getState(newEnvironment);
			learner.updateQFactors(state, action, newState, reward);
		}
		people = 0;			
		learner.setMode(QLearner.Mode.kAct);	
		
		while (true) {
			System.out.println(people + " customers.  Press A to add customer, R to remove customer, E to exit, and Enter to simulate.");
			String input = scanner.nextLine().toLowerCase();
			switch (input) {
				case "a":
					people++;
					break;
				case "r":
					people--;
					break;
				case "e":
					return;
			}
			double[] environment = {people};
			State state = learner.getState(environment);			
			Action action = learner.requestAction(state);			
			double reward = preformAction(action);				
			double[] newEnvironment = {people};
			State newState = learner.getState(newEnvironment);
			learner.updateQFactors(state, action, newState, reward);		
            System.out.println(action.get("Open new line"));
		}
	}
	
	public static double preformAction(Action action) {
		if (Math.round(action.get("Open new line")) == 1) {
			System.out.println("Opened a new line when " + people + " people were in line.");
			if (people-- > 10) {
				return 10;
			} else {
				return -2;
			}	
		} else {
			System.out.println("Did not open a new line when " + people + " people were in line.");
			if (people <= 10) {
				return 0;
			} else {
				return -4;
			}
		}
	}
}
