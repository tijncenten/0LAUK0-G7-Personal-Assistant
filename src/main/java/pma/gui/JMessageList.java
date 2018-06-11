package pma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import pma.PersonalMessagingAssistant.EvalResult;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class JMessageList extends JPanel {
    
    private JPanel mainList;
    
    private boolean hideSpam = true;
    
    private List<JMessagePanel> messagePanels = new ArrayList<>();
    
    private JMessagePanel selectedPanel = null;
    
    private MessageSelectedListener messageSelectedListener;
    
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
        final JMessagePanel messagePanel = new JMessagePanel(m);
        messagePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (selectedPanel != null) {
                    selectedPanel.setBorder(messagePanel.getBorder());
                }
                selectedPanel = messagePanel;
                messagePanel.setBorder(new LineBorder(Color.BLUE, 1));
                messageSelectedListener.MessageSelected(messagePanel.getMessage());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        messagePanels.add(messagePanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainList.add(messagePanel, gbc);
        
        validate();
        repaint();
    }
    
    public void setMessageSelectedListener(MessageSelectedListener l) {
        this.messageSelectedListener = l;
    }
    
    public void clear() {
        mainList.removeAll();
        messagePanels = new ArrayList<>();
    }
    
    public void updateList() {
        updateMessageVisibility();
        validate();
        repaint();
    }
    
    public void setHideSpam(boolean value) {
        this.hideSpam = value;
        this.updateMessageVisibility();
    }
    
    private void updateMessageVisibility() {
        for (JMessagePanel mp : messagePanels) {
            if (mp.getMessage().getResult() == EvalResult.low) {
                mp.setVisible(!this.hideSpam);
            }
        }
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
    
    public void setThreads(int[] threadIndices) {
        if (threadIndices.length != messagePanels.size()) {
            throw new IllegalStateException("size not equal");
        }
        
        for (int i = 0; i < threadIndices.length; i++) {
            messagePanels.get(i).getMessage().setThreadIndex(threadIndices[i]);
        }
    }
}

interface MessageSelectedListener {
    public void MessageSelected(Message m);
}
