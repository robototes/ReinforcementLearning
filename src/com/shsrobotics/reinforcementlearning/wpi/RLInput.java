package com.shsrobotics.reinforcementlearning.wpi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link RLRobot} hardware or software input.
 * @author Team 2412.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RLInput {
	/**
	 * @return the name of the method that is called to get values.
	 */
	String get();
	/**
	 * @return the minimum input value.
	 */
	double minimum();
	/**
	 * @return the maximum input value.
	 */
	double maximum();
}
