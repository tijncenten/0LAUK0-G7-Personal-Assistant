/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pma;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pma.contact.Contact;
import pma.message.Message;

/**
 *
 * @author s167501
 */
public class PersonalMessagingAssistantTest {
    
    public PersonalMessagingAssistantTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class PersonalMessagingAssistant.
     */
    @Test
    public void testProcess() {
        Contact[] contacts = new Contact[] {
            new Contact("0612345678", "John"),
            new Contact("0612345678", "Jane"),
            new Contact("0612345678", "Henk"),
            new Contact("0612345678", "Jan")
        };
        
        System.out.println("process");
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("test", 0, contacts[0], true));
        messages.add(new Message("spam", 1, contacts[0], true));
        messages.add(new Message("this is spam", 2, contacts[1], true));
        messages.add(new Message("real message", 3, contacts[3], true));
        messages.add(new Message("real good message", 4, contacts[2], true));
        messages.add(new Message("is this good?", 5, contacts[0], false));
        messages.add(new Message("hello! shall we go to the beach?", 6, contacts[2], true));
        messages.add(new Message("Are you attending the lecture?", 10, contacts[0], false));
        messages.add(new Message("Yes, I am!", 11, contacts[1], false));
        messages.add(new Message("Yes, I am too!", 12, contacts[2], false));
        messages.add(new Message("No, i am on holiday", 14, contacts[3], false));
        messages.add(new Message("when will you be back?", 16, contacts[0], false));
        messages.add(new Message("I will be back tomorrow", 19, contacts[3], false));
        messages.add(new Message("any of you know the answer to question 5?", 21, contacts[1], false));
        messages.add(new Message("???", 30, contacts[1], true));
        PersonalMessagingAssistant instance = new PersonalMessagingAssistant();
        //instance.train(messages);
        instance.load("", "test2");
        instance.process(messages);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
