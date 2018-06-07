/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
/**
 *
 * @author Emre Aydogan
 * 
 * This class retrieves the text file from the GUI and parses it to a form that 
 * is readable for the PA
 */
public class MessageParser {
    File file;
    BufferedReader br = null;
    ArrayList<Long> time = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> message = new ArrayList<>();
    
    // retrieve the text file from the GUI
    public MessageParser(File f) {
        this.file = f;
    }
    
    public void parse() throws ParseException, FileNotFoundException {
        // do the actual parsing      
        String text = stringRetriever(file);
        
        try {
            Reader inputMessage = new StringReader(text);
            br = new BufferedReader(inputMessage);

            String line = br.readLine();
            /*
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            */
            while (line != null) {
                String[] dateAndTime = line.split("[?<=,?<=-]");
                String[] nameAndMessage = dateAndTime[2].split("[:]", 2);
                long t = dateToLongConverter(dateAndTime);
                arraylistMaker(t, nameAndMessage);

                line = br.readLine();
            }
        } catch (IOException e) {
        }
    }
    
    public String stringRetriever(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();
        
        return text;
    }
    
    public long dateToLongConverter(String[] fp) throws ParseException {
        String year = fp[0];
        String time = fp[1];
        String completeDate = year + time;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d = formatter.parse(completeDate);
        long t = d.getTime();
        
        return t;
    }
    
    public void arraylistMaker(long t, String[] sp) {
        time.add(t);
        name.add(sp[0].trim());
        message.add(sp[1].trim());
        
        /*
        System.out.println("time: " + time.toString());
        System.out.println("name: " + name.toString());
        System.out.println("message: " + message.toString());
        */
    }

}
