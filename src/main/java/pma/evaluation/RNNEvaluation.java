package pma.evaluation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import pma.layer.Storable;
import pma.layer.Trainable;
import pma.message.Message;
import pma.message.MessageSetIterator;

/**
 *
 * @author s167501
 */
public class RNNEvaluation extends Evaluation implements Trainable, Storable {
    
    private MultiLayerNetwork net;
    private Word2Vec vec = null;
    
    
    private int batchSize = 200;     //Number of examples in each minibatch
    private int vectorSize = 300;   //Size of the word vectors. 300 in the Google News model
    private int nEpochs = 20;        //Number of epochs (full passes of training data) to train on
    private int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this
    private final int seed = 0;     //Seed for reproducibility
    
    public static final String WORD_VECTORS_PATH = "C:/Users/s167501/Documents/GoogleNewsVectors/GoogleNews-vectors-negative300.bin.gz";
    
    
    public RNNEvaluation() {
        constructNetwork();
    }
    
    private void constructNetwork() {
        Nd4j.getMemoryManager().setAutoGcWindow(6000);
        
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam())
                .l2(1e-5)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(1.0)
                .trainingWorkspaceMode(WorkspaceMode.SEPARATE).inferenceWorkspaceMode(WorkspaceMode.SEPARATE)   //https://deeplearning4j.org/workspaces
                .list()
                .layer(0, new LSTM.Builder().nIn(vectorSize).nOut(256)
                .activation(Activation.TANH).build())
                .layer(1, new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
                .lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
                .pretrain(false).backprop(true).build();
        net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Override
    public double[] evaluate(List<Message> messages) {
        if (vec == null) {
            throw new IllegalStateException("Cannot evaluate since Word2Vec is null");
        }
        
        double[] results = new double[messages.size()];
        
        MessageSetIterator input = new MessageSetIterator(messages, vec, batchSize, truncateReviewsToLength, false);

        INDArray networkOutput = net.output(input);
        
        //System.out.println(networkOutput);

        for (int i = 0; i < messages.size(); i++) {
            //INDArray features = input.loadFeaturesFromString(messages.get(i).getText(), truncateReviewsToLength);
            //INDArray out = net.output(features);
            
            //int timeSeriesLength = out.size(2);
            //INDArray probabilitiesAtLastWord = out.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));
            //double pSpam = probabilitiesAtLastWord.getDouble(0);
            //double pHam = probabilitiesAtLastWord.getDouble(1);
            
            
            
            INDArray row = networkOutput.getRow(i);
            //System.out.println(row);
            double pSpam = row.getRow(0).sumNumber().doubleValue();
            double pHam = row.getRow(1).sumNumber().doubleValue();
            //System.out.println("P(spam): " + pSpam);
            //System.out.println("P(ham): " + pHam);
            
            double prob;
            if (pSpam > pHam) {
                prob = 1 - pSpam;
            } else {
                prob = pHam;
            }
            
            results[i] = prob;
        }
        
        return results;
    }

    @Override
    public void train(List<Message> messages) {
        if (vec == null) {
            String data = "";
            for (int i = 0; i < messages.size(); i++) {
                if (data.length() > 0) {
                    data += "\n" + messages.get(i).getText();
                } else {
                    data += messages.get(i).getText();
                }
            }
            
            InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            //SentenceIterator iter = new BasicLineIterator(in);
            FileSentenceIterator iter = new FileSentenceIterator(new File("input/warpeace.txt"));
            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());
            
            vec = new Word2Vec.Builder()
                .minWordFrequency(2)
                .layerSize(vectorSize)
                .seed(42)
                .windowSize(5)
                .epochs(5)
                .elementsLearningAlgorithm(new SkipGram<VocabWord>())
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
            
            
            vec.fit();
        }
        
        DataSetIterator train = new MessageSetIterator(messages, vec, batchSize, truncateReviewsToLength, true);
        DataSetIterator test = new MessageSetIterator(messages, vec, batchSize, truncateReviewsToLength, false);
        
        for (int i = 0; i < nEpochs; i++) {
            net.fit(train);
            train.reset();
            System.out.println("Epoch " + i + " complete. Starting evaluation:");
            
            org.deeplearning4j.eval.Evaluation evaluation = net.evaluate(test);
            test.reset();
            System.out.println(evaluation.stats());
        }
    }
    
    @Override
    public void save(String path, String name) {
        if (vec == null) {
            throw new IllegalStateException("Cannot save since Word2Vec is null");
        }
        
        if (net == null) {
            throw new IllegalStateException("Cannot save since net is null");
        }
        
        // Save word vector
        WordVectorSerializer.writeWord2VecModel(vec, "pa-network-storage/" + name + ".word2vec.txt");
        
        try {
            // Save RNN
            ModelSerializer.writeModel(net, "pa-network-storage/" + name + ".rnn.txt", true);
        } catch (IOException ex) {
            Logger.getLogger(RNNEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void load(String path, String name) {
        // Load word vector
        vec = WordVectorSerializer.readWord2VecModel("pa-network-storage/" + name + ".word2vec.txt");
        
        try {
            // Load RNN
            net = ModelSerializer.restoreMultiLayerNetwork("pa-network-storage/" + name + ".rnn.txt", true);
        } catch (IOException ex) {
            Logger.getLogger(RNNEvaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
