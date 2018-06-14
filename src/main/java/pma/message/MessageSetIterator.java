package pma.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

/**
 *
 * @author s167501
 */
public class MessageSetIterator implements DataSetIterator {
    
    private final WordVectors wordVectors;
    
    private List<Message> messages;
    private int index = 0;
    private int batchSize;
    private int vectorSize;
    private int truncateLength;
    private boolean train;
    
    private final TokenizerFactory tokenizerFactory;
    
    public MessageSetIterator(List<Message> messages, WordVectors wordVectors, int batchSize, int truncateLength, boolean train) {
        this.messages = messages;
        this.wordVectors = wordVectors;
        this.batchSize = batchSize;
        this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;
        this.truncateLength = truncateLength;
        this.train = train;
        
        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
    }

    @Override
    public DataSet next(int num) {
        if (index >= messages.size()) throw new NoSuchElementException();
        try {
            return nextDataSet(num);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private DataSet nextDataSet(int num) throws IOException {
        List<String> msgs = new ArrayList<>(num);
        boolean[] spam = new boolean[num];
        for (int i = 0; i < num && index < totalExamples(); i++) {
            msgs.add(messages.get(index).getText());
            spam[i] = messages.get(index).isSpam();
            index++;
        }
        
        List<List<String>> allTokens = new ArrayList<>(msgs.size());
        int maxLength = 0;
        for (String s : msgs) {
            List<String> tokens = tokenizerFactory.create(s).getTokens();
            List<String> tokensFiltered = new ArrayList<>();
            for (String t : tokens) {
                if (wordVectors.hasWord(t)) tokensFiltered.add(t);
            }
            allTokens.add(tokensFiltered);
            maxLength = Math.max(maxLength, tokensFiltered.size());
        }
        
        if (maxLength > truncateLength) maxLength = truncateLength;
        
        maxLength = truncateLength;
        
        if (maxLength == 0) {
            System.out.println("MaxLength is 0");
        }
                
        // Create data for training
        INDArray features = Nd4j.create(new int[]{msgs.size(), vectorSize, maxLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{msgs.size(), 2, maxLength}, 'f');
        INDArray featuresMask = Nd4j.zeros(msgs.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(msgs.size(), maxLength);
        
        for (int i = 0; i < msgs.size(); i++) {
            List<String> tokens = allTokens.get(i);
            
            int seqLength = Math.min(tokens.size(), maxLength);
            List<String> subList = tokens.subList(0, seqLength);
            
            if (!subList.isEmpty()) {
                INDArray vectors = wordVectors.getWordVectors(subList).transpose();
            
                features.put(
                    new INDArrayIndex[] {
                        NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.interval(0, seqLength)
                    },
                        vectors);
            
                featuresMask.get(new INDArrayIndex[] {NDArrayIndex.point(i), NDArrayIndex.interval(0, seqLength)}).assign(1);
            }
            
            int idx = (spam[i] ? 0 : 1);
            int lastIdx = Math.min(tokens.size(), maxLength);
            labels.putScalar(new int[]{i, idx, lastIdx-1}, 1.0);
            labelsMask.putScalar(new int[]{i, lastIdx-1}, 1.0);
        }
//        System.out.println("-----------------");
//        System.out.println("features:" + features);
//        System.out.println("labels:" + labels);
//        System.out.println("featuresMask:" + featuresMask);
//        System.out.println("labelsMask:" + labelsMask);
//        System.out.println("-----------------");
        
        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    @Override
    public int totalExamples() {
        return messages.size();
    }

    @Override
    public int inputColumns() {
        return vectorSize;
    }

    @Override
    public int totalOutcomes() {
        return 2;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void reset() {
        index = 0;
    }

    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public int cursor() {
        return index;
    }

    @Override
    public int numExamples() {
        return totalExamples();
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<String> getLabels() {
        return Arrays.asList("spam", "ham");
    }

    @Override
    public boolean hasNext() {
        return index < numExamples();
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }
    
    public INDArray loadFeaturesFromString(String text, int maxLength) {
        List<String> tokens = tokenizerFactory.create(text).getTokens();
        List<String> tokensFiltered = new ArrayList<>();
        for (String t : tokens) {
            if (wordVectors.hasWord(t)) tokensFiltered.add(t);
        }
        int outputLength = Math.max(maxLength, tokensFiltered.size());
        
        INDArray features = Nd4j.create(1, vectorSize, outputLength);
        
        for (int i = 0; i < tokens.size() && i < maxLength; i++) {
            String token = tokens.get(i);
            INDArray vector = wordVectors.getWordVectorMatrix(token);
            features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)}, vector);
        }
        
        return features;
    }
    
}
