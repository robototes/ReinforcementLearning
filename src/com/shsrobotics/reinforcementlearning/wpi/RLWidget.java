package com.shsrobotics.reinforcementlearning.wpi;

import com.shsrobotics.reinforcementlearning.wpi.RLRobot.RobotMode;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Smart Dashboard widget for Reinforcement Learning Agent.
 * <p/>
 * For FRC SmartDashboard.
 * @author Team 2412.
 */
public class RLWidget extends Widget {
	/**
	 * {@link RobotMode}
	 */
	private RobotMode mode = RobotMode.kDisabled;
	
	@Override
	public void setValue(Object o) {
		this.mode = (RobotMode) o;
		repaint();
	}

	@Override
	public void init() {
		this.setPreferredSize(new Dimension(500, 300));
		repaint();
	}

	@Override
	public void propertyChanged(Property prprt) {
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = this.getSize();
		if (mode == RobotMode.kDisabled) {
			g.setColor(Color.RED);
		} else if (mode == RobotMode.kReinforcementLearning) {
			g.setColor(Color.GREEN);
		} else if (mode == RobotMode.kTeleOperated) {
			g.setColor(Color.YELLOW);
		}
		g.fillRect(0, 0, size.width, size.height);
		
		g.setColor(Color.BLACK);
		g.setFont(Font.getFont("Courrier New"));
		g.drawString(mode.getName(), 20, 20);
	}
	
}
