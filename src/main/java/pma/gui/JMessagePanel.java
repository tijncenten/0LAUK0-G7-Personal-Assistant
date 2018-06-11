/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import static pma.PersonalMessagingAssistant.EvalResult.high;
import static pma.PersonalMessagingAssistant.EvalResult.low;
import static pma.PersonalMessagingAssistant.EvalResult.medium;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class JMessagePanel extends JPanel {
    
    private Message message;
    
    private Color normalColor = new Color(255, 255, 255);
    private Color highColor = new Color(128, 255, 128);
    private Color mediumColor = new Color(255, 255, 128);
    private Color lowColor = new Color(255, 128, 128);
    
    private JPanel threadIndicator = new JPanel();
    private JLabel threadLabel = new JLabel();
    private JPanel spamIndicator = new JPanel();
    
    public JMessagePanel(Message m) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.message = m;
       // this.setLayout(new BorderLayout());
        
        this.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        
        //threadIndicator.setLayout(new BorderLayout());
        threadIndicator.setSize(20, 25);
        threadIndicator.add(threadLabel);
        this.add(threadIndicator);
        
        //spamIndicator.setLayout(new BorderLayout());
        spamIndicator.setSize(25, 25);
        this.add(spamIndicator);
        
        Date d = new Date(m.getTimestamp());
        DateFormat f = new SimpleDateFormat("dd-MM-yy HH:mm");
        this.add(new JLabel("<html>" + f.format(d) + " - " + m.getSender() + ": " + escape(m.getText()) + "</html>"));
        
        repaint();
    }
    
    public Message getMessage() {
        return this.message;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        threadLabel.setText("" + message.getThreadIndex());
        
        
        Color spamColor = normalColor;
        if (message.isSpam() != null) {
            if (message.isSpam()) {
                spamColor = lowColor;
            } else {
                spamColor = highColor;
            }
        }
        spamIndicator.setBackground(spamColor);

        Color background = normalColor;
        if (message.hasResult()) {
            switch (message.getResult()) {
            case high:
                background = highColor;
                break;
            case medium:
                background = mediumColor;
                break;
            case low:
                background = lowColor;
                break;
            }
        }
        
        this.setBackground(background);
    }
    
    private String escape(String s) {
        return s.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
    }
    
}
