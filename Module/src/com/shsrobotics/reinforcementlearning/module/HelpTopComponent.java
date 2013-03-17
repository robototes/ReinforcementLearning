/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shsrobotics.reinforcementlearning.module;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd = "-//com.shsrobotics.reinforcementlearning.module//Help//EN",
autostore = false)
@TopComponent.Description(
    preferredID = "HelpTopComponent",
iconBase = "com/shsrobotics/reinforcementlearning/module/images/icon.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.shsrobotics.reinforcementlearning.module.HelpTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_HelpAction",
preferredID = "HelpTopComponent")
@Messages({
	"CTL_HelpAction=Help",
	"CTL_HelpTopComponent=Help Window",
	"HINT_HelpTopComponent=This is a Help window"
})
public final class HelpTopComponent extends TopComponent {

	public HelpTopComponent() {
		initComponents();
		setName(Bundle.CTL_HelpTopComponent());
		setToolTipText(Bundle.HINT_HelpTopComponent());
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        header = new javax.swing.JLabel();
        tabContainer = new javax.swing.JTabbedPane();
        javadocTab = new javax.swing.JTabbedPane();
        conceptsTab = new javax.swing.JTabbedPane();

        header.setBackground(new java.awt.Color(255, 38, 38));
        header.setFont(new java.awt.Font("CMU Serif", 0, 32)); // NOI18N
        header.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shsrobotics/reinforcementlearning/module/images/iconRawSmall.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(header, org.openide.util.NbBundle.getMessage(HelpTopComponent.class, "HelpTopComponent.header.text")); // NOI18N
        header.setToolTipText(org.openide.util.NbBundle.getMessage(HelpTopComponent.class, "HelpTopComponent.header.toolTipText")); // NOI18N

        tabContainer.setBackground(new java.awt.Color(229, 229, 229));

        javadocTab.setFont(new java.awt.Font("CMU Serif", 0, 12)); // NOI18N
        tabContainer.addTab(org.openide.util.NbBundle.getMessage(HelpTopComponent.class, "HelpTopComponent.javadocTab.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/com/shsrobotics/reinforcementlearning/module/images/helpIcon.png")), javadocTab); // NOI18N

        conceptsTab.setFocusable(false);
        conceptsTab.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        tabContainer.addTab(org.openide.util.NbBundle.getMessage(HelpTopComponent.class, "HelpTopComponent.conceptsTab.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/com/shsrobotics/reinforcementlearning/module/images/icon.png")), conceptsTab); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabContainer, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(header, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(header)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
        );

        tabContainer.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(HelpTopComponent.class, "HelpTopComponent.tabContainer.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane conceptsTab;
    private javax.swing.JLabel header;
    private javax.swing.JTabbedPane javadocTab;
    private javax.swing.JTabbedPane tabContainer;
    // End of variables declaration//GEN-END:variables
	@Override
	public void componentOpened() {
		// TODO add custom code on component opening
	}

	@Override
	public void componentClosed() {
		// TODO add custom code on component closing
	}

	void writeProperties(java.util.Properties p) {
		// better to version settings since initial version as advocated at
		// http://wiki.apidesign.org/wiki/PropertyFiles
		p.setProperty("version", "1.0");
		// TODO store your settings
	}

	void readProperties(java.util.Properties p) {
		String version = p.getProperty("version");
		// TODO read your settings according to their version
	}
}
