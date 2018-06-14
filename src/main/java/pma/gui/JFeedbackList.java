/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pma.feedback.request.FeedbackRequest;

/**
 *
 * @author s155538
 */
public class JFeedbackList extends JPanel {
    
    private JPanel mainList;
    
    private List<FeedbackRequest> requestList = new ArrayList<>();
    
    public JFeedbackList () {
        setLayout(new BorderLayout());
        
        mainList = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        mainList.add(new JPanel(), gbc);
        
        JScrollPane scrollPane = new JScrollPane(mainList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }
    
    public void addRequest(FeedbackRequest r) {
        final FeedbackRequest feedbackRequest = r;
        
        requestList.add(r);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainList.add(new JFeedbackPanel(r, this), gbc);
        
        validate();
        repaint();
    }
    
    public void removeRequest(JFeedbackPanel panel) {
        mainList.remove(panel);
        validate();
        repaint();
    }
}
