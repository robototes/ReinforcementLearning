package com.shsrobotics.reinforcementlearning;

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
        
        learner.setLearningRate(0.95);
		learner.setAccuracy(0.85);
		
		for (int i = 0; i < 500; i++) { // 100 learning iterations
			if (Math.random() < 0.7) {
				people++;
			}
			double[] environment = {people};
			State state = learner.getState(environment);
			Action action = learner.requestAction(state);
			double reward = preformAction(action);
			learner.updateQFactors(state, action, reward, 0.0);
		}
		people = 0;			
		learner.setMode(QLearner.Mode.kAct);	
		
		double[] testState = {12};
		double[] testState2 = {13};
		double[] testAction = {1};
		double predictionA = learner.estimateQ(
			learner.getState(testState), 
			learner.getAction(testAction)
			).Q;
		double predictionB = learner.estimateQ(
			learner.getState(testState2), 
			learner.getAction(testAction)
			).Q;
		System.out.println(predictionA);
		System.out.println(predictionB);
		
		while (true) {
			break;
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
			learner.updateQFactors(state, action, reward, 0.0);		
            System.out.println(action.get("Open new line"));
		}
	}
	
	public static double preformAction(Action action) {
		if (Math.round(action.get("Open new line")) == 1) {
		//	System.out.println("Opened a new line when " + people + " people were in line.");
			if (people-- > 10) {
				return 0.8;
			} else {
				return 0;
			}	
		} else {
		//	System.out.println("Did not open a new line when " + people + " people were in line.");
			if (people <= 10) {
				return 0.1;
			} else {
				return 0;
			}
		}
	}
}
