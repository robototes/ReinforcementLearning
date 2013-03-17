package com.shsrobotics.reinforcementlearning.wpi;

import com.shsrobotics.reinforcementlearning.architecture.Architecture;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.communication.FRCControl;

/**
 * A robot that runs Team 2412's Reinforcement Learning architecture.
 * <p/>
 * The robot learns over time how to act, by receiving rewards for actions it takes.  The algorithm attempts to maximize
 * reward over the long run by choosing actions now.
 * <p?
 * <b>IMPORTANT NOTE</b><br />
 * Test mode runs the robot in RL mode, as does autonomous mode.
 * 
 * @author Team 2412.
 */
public abstract class RLRobot extends RobotBase {

	private RobotMode currentMode;
	
	/**
	 * Create an {@link RL Robot}. 
	 */
	public RLRobot() {
		currentMode = RLRobot.RobotMode.kDisabled;
	}
	
	/**
     * Robot-wide initialization code should go here.
     * <p/>
     * Users should override this method for default Robot-wide initialization which will
     * be called once when the robot is first powered on.
     */
	public void robotInit() {
		System.out.println("=======================================");
        System.out.println("=============CODE COMPILED=============");
        System.out.println("=======================================");
		System.out.println();
		System.out.println();
	}
	
	/**
     * Initialization code for disabled mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot is in {@link RobotMode#kDisabled} mode.
     */
    public void disabledInit() {
        System.out.println("=======================================");
        System.out.println("============ROBOT  DISABLED============");
        System.out.println("=======================================");
	}
	
	/**
     * Code to run while the robot is disabled should go here.
     *
     * This code which will be called continuously while the robot is in {@link RobotMode#kDisabled} mode.
     */
    public void disabledPeriodic() { };
	
    /**
     * Initialization code for tele-operated mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters {@link RobotMode#kTeleOperated} mode.
     */
    public void teleoperatedInit() {
        System.out.println("====Tele-operated code initialized.====");
    }
	
	/**
     * Code for tele-operated mode should go here.
     *
     * This code which will be called continuously while the robot is in {@link RobotMode#kTeleOperated} mode.
     */
    public abstract void teleoperatedPeriodic();
	
	/**
     * Initialization code for learner mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters {@link RobotMode#kReinforcementLearning} mode or {@link RobotMode#kThink} mode.
     */
	public void learnerInit() {
		System.out.println("=========RL code initialized.=========");
	}
	
	public void startCompetition() {
		robotInit();		
		while (true) {
			if (isDisabled()) {
				if (currentMode == RobotMode.kDisabled) {
					FRCControl.observeUserProgramDisabled();
					disabledPeriodic();
				} else {
					Architecture.stop();
					currentMode = RobotMode.kDisabled;
					disabledInit();
				}
			} else if (isAutonomous()) {
				if (currentMode == RobotMode.kReinforcementLearning) {
					FRCControl.observeUserProgramAutonomous();
				} else {
					currentMode = RobotMode.kReinforcementLearning;
					learnerInit();
					Architecture.start();
				}
			} else if (isTest()) {
				if (currentMode == RobotMode.kReinforcementLearning) {
					FRCControl.observeUserProgramTest();
				} else {
					currentMode = RobotMode.kReinforcementLearning;
					learnerInit();
					Architecture.start();
				}
			} else {
				if (currentMode == RobotMode.kTeleOperated) {
					FRCControl.observeUserProgramTeleop();
					teleoperatedPeriodic();
				} else {
					currentMode = RobotMode.kReinforcementLearning;
					teleoperatedInit();
				}
			}
		}
	}
	
	/**
	 * The mode of the robot.
	 */
	public static class RobotMode {
		
		/**
		 * Use RL algorithm.
		 */
		public static final RobotMode kReinforcementLearning = new RobotMode(true, true);
		/**
		 * Let the user drive.
		 */
		public static final RobotMode kTeleOperated = new RobotMode(false, true);
		/**
		 * Disabled.
		 */
		public static final RobotMode kDisabled = new RobotMode(false, false);
		
		
		/**
		 * Use the RL learner.
		 */
		private boolean useLearner;
		/**
		 * Allow motor use.
		 */
		private boolean motorUse;
		
		/**
		 * Enumeration constructor.
		 * @param useLearner see {@link #useLearner}
		 * @param motorUse see {@link #motorUse}
		 */
		private RobotMode(boolean useLearner, boolean motorUse) {
			this.useLearner = useLearner;
			this.motorUse = motorUse;
		}
	}
}
