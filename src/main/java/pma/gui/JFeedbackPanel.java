/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s155538
 */
public class JFeedbackPanel extends JPanel {
    
    public JFeedbackPanel(FeedbackRequest request, JFeedbackList list) {
        JLabel label1 = new JLabel("testing");
        label1.setText(request.getSubTitle());
        this.add(label1);
        
        String[] options = request.getOptions();
        JPanel panel1 = new JPanel();
        for(String option : options){
            JButton button1 = new JButton(option);
            panel1.add(button1);
            final JFeedbackPanel panel = this;
            button1.addActionListener(new ActionListener() { 
                @Override
                public void actionPerformed(ActionEvent ae) {
                    request.setFeedback(option);
                    list.removeRequest(panel);
                }
            } );
            
        }
        this.add(panel1);
    }
}
