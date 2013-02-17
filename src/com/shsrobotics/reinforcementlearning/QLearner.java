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
    private double defaultQ;
    private double learnerAccuracy;
    
    private double[] minimumStateValues;
    private double[] minimumActionValues;
    private double[] maximumStateValues;
    private double[] maximumActionValues;
    
    private int states;
    private int actions;
    private String[] stateNames;
    private String[] actionNames;
    
    /**
     *  Accepts string array of action values and environment state parameters
     * @param actions
     * @param states
     */
    public QLearner(String[] actions, String[] states) {
        currentMode = Mode.Learn;
        learningRate = 0.2;
        discountFactor = 0.25;
        defaultQ = 0.0;
        learnerAccuracy = 0.9;
        stateNames = states;
        actionNames = actions;
        this.states = states.length;
        this.actions = actions.length;
        minimumStateValues = minimumStateValues = new double[this.states]; // array of mins and maxs for state parameters
        minimumActionValues = maximumActionValues = new double[this.actions]; // array of mins and maxs for action parameters
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
        
        if (currentMode == Mode.Act) { // check modes
            exploreCutoff = 0.0;
        } else if (currentMode == Mode.Watch) {
            throw new Error("Wrong learning mode.");
        }
        
        if (Math.random() < exploreCutoff) { // choose random values
            for (int i = 0; i < actions; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                actionValues[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
            }
        }
        
        return new Action(actionValues);
    }
    
    /**
     * Sets Q Learner mode. <br />
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
    }
    
    /**
     * Sets the Discount Factor. <br />
     * A higher discount factor places a higher value on immediate rewards.
     * @param factor
     */
    public void setDiscountFactor(double factor) {
        discountFactor = factor;
    }
    
    /**
     * Sets the default Q value to use in the Q Estimator Neural Network.
     * @param q
     */
    public void setDefaultQValue(double q) {
        defaultQ = q;
    }
    
    /**
     * Sets the Q maximization algorithm accuracy. <br />
     * A higher value will increase accuracy at the cost of computation time.
     * @param accuracy
     */
    public void setAccuracy(double accuracy) {
        learnerAccuracy = accuracy;
    }
    
    public static class Mode {        
        public static final Mode Watch = new Mode(false, false); // gather data
        public static final Mode Learn = new Mode(true, false); // learn
        public static final Mode Act = new Mode(true, true); // act on learned data
        
        private final boolean allowActionRequests;
        private final boolean chooseBestOption;
        
        private Mode(boolean allowActionRequests, boolean chooseBestOption) {
            this.allowActionRequests = allowActionRequests;
            this.chooseBestOption = allowActionRequests;
        }
    }
    
    public class Action {
        private double[] parameters;
        
        public Action(double[] values) {
            if (values.length != actions) {
                throw new Error("Wrong action parameter length");
            }
            parameters = values;
        }
        
        public double get(String key) {
            return parameters[indexOf(key, actionNames)];
        }
    }
    
    public class State {
        private double[] parameters;
        
        public State(double[] values){
            if (values.length != states) {
                throw new Error("Wrong state parameter length");
            }
            parameters = values;
        }
        
        public double get(String key) {
            return parameters[indexOf(key, actionNames)];
        }
    }
    
    public int indexOf(String value, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            if (value.equals(keys[i])) { // found
                return i;
            }
        }
        return -1; // not found
    }
}