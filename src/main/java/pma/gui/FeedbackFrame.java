/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.gui;

import java.awt.BorderLayout;
import static java.awt.Color.green;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import pma.feedback.FeedbackEvaluator;
import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s155538
 */
public class FeedbackFrame extends JFrame implements FeedbackEvaluator {
    JFeedbackList feedbackList;
    
    public FeedbackFrame(){

    }
    
    public void setList(JFeedbackList list) {
        this.feedbackList = list;
    }

    @Override
    public void evaluateFeedback(FeedbackRequest request) {    
        feedbackList.addRequest(request);
        this.setVisible(true);

    }
}
