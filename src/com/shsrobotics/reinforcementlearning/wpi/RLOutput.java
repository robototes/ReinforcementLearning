package com.shsrobotics.reinforcementlearning.wpi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link RLRobot} hardware or software output.
 * @author Team 2412.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RLOutput {
	/**
	 * @return the name of the method that is called to set values.
	 */
	String set();
	/**
	 * @return the minimum output value.
	 */
	double minimum();
	/**
	 * @return the maximum output value.
	 */
	double maximum();
}
