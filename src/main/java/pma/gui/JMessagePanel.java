/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class JMessagePanel extends JPanel {
    
    private Message message;
    
    public JMessagePanel(Message m) {
        super();
        this.message = m;
        initComponents();
    }
    
    private void initComponents() {
        this.add(new JLabel(message.getSender().toString()));
        this.add(new JLabel(message.getText()));
    }
    
}
