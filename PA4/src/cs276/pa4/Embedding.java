package cs276.pa4;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.ejml.simple.SimpleMatrix;

/**
 * Class that can help use embeddings as features. Might be helpful for
 * extra-credit
 */
public class Embedding {
  private Map<String, SimpleMatrix> wordVectors;
  private int embeddingSize;

  static final String START_WORD = "*START*";
  static final String END_WORD = "*END*";
  static final String UNKNOWN_WORD = "*UNK*";

  public Embedding(String wordVectorFile, boolean isGoogleEmbedding) {
    this.wordVectors = new HashMap<String, SimpleMatrix>();
    System.err.println(
        "# Loading embedding ...\n  word vector file = " + wordVectorFile);

    try {
      BufferedReader br = null;
      if (wordVectorFile.endsWith(".gz")) {
        br = new BufferedReader(new InputStreamReader(
            new GZIPInputStream(new FileInputStream(wordVectorFile))));
      } else {
        br = new BufferedReader(new FileReader(wordVectorFile));
      }
      if (isGoogleEmbedding) {
        br.readLine();
      } // skip the header line
      loadWordVectors(br);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method reads a file of raw word vectors, with a given expected size,
   * and returns a map of word to vector. <br>
   * The file should be in the format ....<br>
   * <code>WORD X1 X2 X3 ...</code> <br>
   * If vectors in the file are smaller than expectedSize, an exception is
   * thrown. If vectors are larger, the vectors are truncated and a warning is
   * printed.
   * 
   * @throws IOException
   * @throws NumberFormatException
   */
  private void loadWordVectors(BufferedReader br) throws IOException {
    int dimOfWords = 0;
    boolean warned = false;

    int numWords = 0;
    String line = null;
    while ((line = br.readLine()) != null) {
      String[] lineSplit = line.split("\\s+");
      String word = lineSplit[0];

      // check for unknown token
      if (word.equals("UNKNOWN") || word.equals("UUUNKKK") || word.equals("UNK")
          || word.equals("*UNKNOWN*") || word.equals("<unk>")) {
        word = UNKNOWN_WORD;
      }
      // check for start token
      if (word.equals("<s>")) {
        word = START_WORD;
      }
      // check for end token
      if (word.equals("</s>")) {
        word = START_WORD;
      }

      dimOfWords = lineSplit.length - 1;
      if (embeddingSize <= 0) {
        embeddingSize = dimOfWords;
        System.err.println("  detected embedding size = " + dimOfWords);
      }
      // the first entry is the word itself
      // the other entries will all be entries in the word vector
      if (dimOfWords > embeddingSize) {
        if (!warned) {
          warned = true;
          System.err.println(
              "WARNING: Dimensionality of numHid parameter and word vectors do not match, deleting word vector dimensions to fit!");
        }
        dimOfWords = embeddingSize;
      } else if (dimOfWords < embeddingSize) {
        br.close();
        throw new RuntimeException(
            "Word vectors file has dimension too small for requested numHid of "
                + embeddingSize);
      }
      double vec[][] = new double[dimOfWords][1];
      for (int i = 1; i <= dimOfWords; i++) {
        vec[i - 1][0] = Double.parseDouble(lineSplit[i]);
      }
      SimpleMatrix vector = new SimpleMatrix(vec);
      wordVectors.put(word, vector);

      numWords++;
    }
    br.close();
    System.err.println("  num words = " + numWords);
  }

  /**
   * Check if a word has vectors in the wordVector matrix
   * 
   * @param word
   * @return true if the word has a vector representation
   */
  public boolean hasWord(String word) {
    return wordVectors.containsKey(word);
  }

  /**
   * Return the size of wordVectors
   * 
   * @return size of the wordVector matrix
   */
  public int size() {
    return wordVectors.size();
  }

  /**
   * Collection of the values of wordVectors
   * 
   * @return values associated with the wordVectors
   */
  public Collection<SimpleMatrix> values() {
    return wordVectors.values();
  }

  /**
   * Get set of all the keys stored in WordVectors
   * 
   * @return A set of all the keys in wordVectors
   */
  public Set<String> keySet() {
    return wordVectors.keySet();
  }

  public Set<Entry<String, SimpleMatrix>> entrySet() {
    return wordVectors.entrySet();
  }

  /**
   * Returns the vector representation of the word
   * 
   * @param word:
   *          String for which we want the vector representation
   * @return Vector representation of the word
   */
  public SimpleMatrix get(String word) {
    if (wordVectors.containsKey(word)) {
      return wordVectors.get(word);
    } else {
      return wordVectors.get(UNKNOWN_WORD);
    }
  }

  /**
   * Get vector representation of the start-word of the embedding
   * 
   * @return Vector representation of the start word
   */
  public SimpleMatrix getStartWordVector() {
    return wordVectors.get(START_WORD);
  }

  /**
   * Get vector representation of the end-word of the embedding
   * 
   * @return Vector representation of the ending word
   */
  public SimpleMatrix getEndWordVector() {
    return wordVectors.get(END_WORD);
  }

  /**
   * Get vector representation of the unknown word of the embedding
   * 
   * @return Vector representation for words that are not present in wordVector
   *         matrix
   */
  public SimpleMatrix getUnknownWordVector() {
    return wordVectors.get(UNKNOWN_WORD);
  }

  /**
   * Return a set of all the words stored in the wordVector matrix
   * 
   * @return
   */
  public Set<String> words() {
    return wordVectors.keySet();
  }

  public int getEmbeddingSize() {
    return embeddingSize;
  }

  /**
   * A simple example showing how to use this class to import wordVectors
   * 
   * @param args
   * @throws Exception
   */

  public static void main(String[] args) throws Exception {
    String wordVectorFile = "data/wordVectors.txt.gz";
    Embedding embedding = new Embedding(wordVectorFile, false);
    for (String word : embedding.words()) {
      System.err.println(word + "\t" + embedding.get(word).transpose());
    }
  }
}
