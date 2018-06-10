package pma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import pma.PersonalMessagingAssistant.EvalResult;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class JMessageList extends JPanel {
    
    private JPanel mainList;
    
    private List<JMessagePanel> messagePanels = new ArrayList<>();
    
    public JMessageList() {
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
    
    public void addMessage(Message m) {
        JMessagePanel messagePanel = new JMessagePanel(m);
        messagePanels.add(messagePanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainList.add(messagePanel, gbc);
        
        validate();
        repaint();
    }
    
    public void clear() {
        mainList.removeAll();
        messagePanels = new ArrayList<>();
    }
    
    public void updateList() {
        validate();
        repaint();
    }
    
    public void setResults(EvalResult[] results) {
        if (results.length != messagePanels.size()) {
            throw new IllegalStateException("size not equal");
        }
        
        for (int i = 0; i < results.length; i++) {
            messagePanels.get(i).getMessage().setResult(results[i]);
        }
        
        updateList();
    }
    
}
