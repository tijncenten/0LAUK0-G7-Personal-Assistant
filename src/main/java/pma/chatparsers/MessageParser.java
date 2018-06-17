/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma.chatparsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pma.contact.Contact;
import pma.message.Message;
/**
 *
 * @author Emre Aydogan
 * 
 * This class retrieves the text file from the GUI and parses it to a form that 
 * is readable for the PA
 */
public class MessageParser {
    
    public List<Message> parse(File f) throws ParseException, FileNotFoundException {
        String conversationText = stringRetriever(f);
        return this.parse(conversationText);
    }
    
    public List<Message> parse(InputStream is) throws ParseException {
        Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
        return this.parse(s.hasNext() ? s.next() : "");
    }
    
    private List<Message> parse(String input) throws ParseException {
        List<Message> messages = new ArrayList<>();
        Map<String, Contact> contactMap = new HashMap<>();
        
        final int flags = Pattern.DOTALL;
        // English language regex: (\\d{1,2}\\/\\d{1,2}\\/\\d{1,2},\\s\\d{2}:\\d{2})\\s-\\s([^:]+): (.+?)(?=(\\d{1,2}\\/\\d{1,2}\\/\\d{1,2},\\s\\d{2}:\\d{2}\\s-\\s[^:]*:|$))
        // General language regex: (\d{1,2}(?>\/|-)\d{1,2}(?>\/|-)\d{1,2},?\s\d{2}:\d{2})\s-\s([^:]+): (.+?)?(?=(\d{1,2}(?>\/|-)\d{1,2}(?>\/|-)\d{1,2},?\s\d{2}:\d{2}\s-\s[^:]*:|$))
        // General language regex: (with 2 or 4 year digits)
            // (\d{1,2}(?>\/|-)\d{1,2}(?>\/|-)(?:\d{2}|\d{4}),?\s\d{2}:\d{2})\s-\s([^:]+): (.+?)?(?=(\d{1,2}(?>\/|-)\d{1,2}(?>\/|-)(?:\d{2}|\d{4}),?\s\d{2}:\d{2}\s-\s[^:]*:|$))
        Pattern p = Pattern.compile("(\\d{1,2}(?>\\/|-)\\d{1,2}(?>\\/|-)(?:\\d{2}|\\d{4}),?\\s\\d{2}:\\d{2})\\s-\\s([^:]+): (.+?)?(?=(\\d{1,2}(?>\\/|-)\\d{1,2}(?>\\/|-)(?:\\d{2}|\\d{4}),?\\s\\d{2}:\\d{2}\\s-\\s[^:]*:|$))", flags);
        Matcher matcher = p.matcher(input);
        while (matcher.find()) {
            String date = matcher.group(1);
            String sender = matcher.group(2);
            String text = matcher.group(3);
            text = text.replace("\n", "").replace("\r", "");
            
            Boolean spam = null;
            
            if (text.length() > 1 && text.charAt(text.length()-2) == '|') {
                spam = text.charAt(text.length()-1) != '1';
                text = text.substring(0, text.length()-2);
            }
            
            long t = dateToLongConverter(date);

            Contact c = contactMap.get(sender);
            if (c == null) {
                c = new Contact(sender, sender); //TODO: Check if sender is number or name
                contactMap.put(sender, c);
            }

            if (spam != null) {
                messages.add(new Message(text, t, c, spam));
            } else {
                messages.add(new Message(text, t, c));
            }
            
        }
        
        return messages;
    }
    
    private String stringRetriever(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file, "UTF-8");
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        
        return text;
    }
    
    private long dateToLongConverter(String date) throws ParseException {
        DateFormat formatter;
        if (date.contains("/")) {
            if (date.matches(".*/.*/\\d{2}")) {
                // United States English
                formatter = new SimpleDateFormat("MM/dd/yy, HH:mm");
            } else {
                // United Kingdom English
                formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
            }
        } else if (date.contains("-")) {
            // Nederlands
            formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
        } else {
            throw new IllegalStateException("Date format not supported (" + date + ")");
        }
        Date d = formatter.parse(date);
        long t = d.getTime();
        
        return t;
    }
    
    private void arraylistMaker(long t, String[] sp) {
//        time.add(t);
//        name.add(sp[0].trim());
//        message.add(sp[1].trim());
        
        /*
        System.out.println("time: " + time.toString());
        System.out.println("name: " + name.toString());
        System.out.println("message: " + message.toString());
        */
    }

}
