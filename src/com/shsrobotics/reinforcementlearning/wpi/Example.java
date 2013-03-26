package com.shsrobotics.reinforcementlearning.wpi;

import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class Example extends RLRobot {
	
	@RLOutput(set = "set", minimum = -1, maximum = 1)
	public Jaguar frontLeftJaguar = new Jaguar(1);
	
	@RLOutput(set = "set", minimum = -1, maximum = 1)
	public Jaguar frontRightJaguar = new Jaguar(2);
	
	@RLOutput(set = "set", minimum = -1, maximum = 1)
	public Jaguar rearLeftJaguar = new Jaguar(3);
	
	@RLOutput(set = "set", minimum = -1, maximum = 1)
	public Jaguar rearRightJaguar = new Jaguar(4);
	
	public RobotDrive robotDrive = new RobotDrive(frontLeftJaguar, rearLeftJaguar, frontRightJaguar, rearRightJaguar);
	
	public Joystick joystick = new Joystick(1);
	
	public void teleoperatedPeriodic() {
		robotDrive.mecanumDrive_Cartesian(joystick.getX(), joystick.getY(), joystick.getZ(), 0.0);
	}
	
}
