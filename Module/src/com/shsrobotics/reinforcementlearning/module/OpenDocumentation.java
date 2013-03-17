/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shsrobotics.reinforcementlearning.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
    category = "Base IDE",
id = "com.shsrobotics.reinforcementlearning.module.OpenDocumentation")
@ActionRegistration(
    iconBase = "com/shsrobotics/reinforcementlearning/module/images/icon.png",
	displayName = "#CTL_OpenDocumentation",
	popupText="Reinforcment Learning Help")
@ActionReference(path = "Toolbars/File", position = 500)
@Messages("CTL_OpenDocumentation=Reinforcement Learning")
public class OpenDocumentation implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		HelpTopComponent component = new HelpTopComponent();
		component.setVisible(true);
		component.open();
		component.requestActive();
	}
}
