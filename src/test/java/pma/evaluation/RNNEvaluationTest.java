package pma.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pma.chatparsers.MessageParser;
import pma.contact.Contact;
import pma.evaluation.function.EvaluationFunction;
import pma.evaluation.function.MaxEvaluationFunction;
import pma.feedback.AutoFeedbackEvaluator;
import pma.feedback.FeedbackModule;
import pma.layer.LayerNetwork;
import pma.layer.OutputLayer;
import pma.message.Message;
import pma.preferences.UserPreferences;

/**
 *
 * @author s167501
 */
public class RNNEvaluationTest {
    
    public RNNEvaluationTest() {
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
     * Test of evaluate method, of class RNNEvaluation.
     */
    @Test
    public void testEvaluate() {
        Contact[] contacts = new Contact[] {
            new Contact("0612345678", "John"),
            new Contact("0612345678", "Jane"),
            new Contact("0612345678", "Henk"),
            new Contact("0612345678", "Jan")
        };
        
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
        
        
        LayerNetwork network = new LayerNetwork();
        network.setPrefs(new UserPreferences());
        FeedbackModule feedbackModule = new FeedbackModule();
        feedbackModule.setFeedbackEvaluator(new AutoFeedbackEvaluator());
        network.setFeedbackModule(feedbackModule);
        
        EvaluationFunction evalFunc = new MaxEvaluationFunction();
        EvaluationLayer evalLayer = new EvaluationLayer(evalFunc);
        RNNEvaluation evaluation = new RNNEvaluation();
        evalLayer.addEvaluation(evaluation);
        network.addLayer(evalLayer);
        OutputLayer output = new OutputLayer();
        network.addLayer(output);
        
        network.build();
        
        network.load("", "rnn-test-emre");
        
        //network.train(messages);
        MessageParser mp = new MessageParser();
        
        
        //network.process(messages);
        
        try {
            //network.train(mp.parse(new File("chats/emre-es.txt")));
            
            System.out.println("Starting processing");
            network.process(mp.parse(new File("chats/emre-es.txt")));
            
            //network.save("", "rnn-test-emre");
        } catch (ParseException ex) {
            Logger.getLogger(RNNEvaluationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RNNEvaluationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<Message> outputMessages = network.getOutputLayer().getOutput();
        for (Message m : outputMessages) {
            System.out.println(m);
        }
        
        pma.PersonalMessagingAssistant.calculateAccuracy(outputMessages);
    }
    
}
