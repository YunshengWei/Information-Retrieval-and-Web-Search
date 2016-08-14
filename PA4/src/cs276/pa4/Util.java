package cs276.pa4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;

public class Util {
  static String[] TFTYPES = { "url", "title", "body", "header", "anchor" };

  /**
   * Load the training data file
   * 
   * @param feature_file_name
   * @return
   * @throws Exception
   */
  public static Map<Query, List<Document>> loadTrainData(
      String feature_file_name) throws Exception {
    Map<Query, List<Document>> result = new HashMap<Query, List<Document>>();

    File feature_file = new File(feature_file_name);
    if (!feature_file.exists()) {
      System.err.println("Invalid feature file name: " + feature_file_name);
      return null;
    }

    BufferedReader reader = new BufferedReader(new FileReader(feature_file));
    String line = null, anchor_text = null;
    Query query = null;
    Document doc = null;
    int numQuery = 0;
    int numDoc = 0;
    while ((line = reader.readLine()) != null) {
      String[] tokens = line.split(":", 2);
      String key = tokens[0].trim();
      String value = tokens[1].trim();

      if (key.equals("query")) {
        query = new Query(value);
        numQuery++;
        result.put(query, new ArrayList<Document>());
      } else if (key.equals("url")) {
        doc = new Document();
        doc.url = new String(value);
        result.get(query).add(doc);
        numDoc++;
      } else if (key.equals("title")) {
        doc.title = new String(value);
      } else if (key.equals("header")) {
        if (doc.headers == null)
          doc.headers = new ArrayList<String>();
        doc.headers.add(value);
      } else if (key.equals("body_hits")) {
        if (doc.body_hits == null)
          doc.body_hits = new HashMap<String, List<Integer>>();
        String[] temp = value.split(" ", 2);
        String term = temp[0].trim();
        List<Integer> positions_int;

        if (!doc.body_hits.containsKey(term)) {
          positions_int = new ArrayList<Integer>();
          doc.body_hits.put(term, positions_int);
        } else
          positions_int = doc.body_hits.get(term);

        String[] positions = temp[1].trim().split(" ");
        for (String position : positions)
          positions_int.add(Integer.parseInt(position));

      } else if (key.equals("body_length"))
        doc.body_length = Integer.parseInt(value);
      else if (key.equals("pagerank"))
        doc.page_rank = Integer.parseInt(value);
      else if (key.equals("anchor_text")) {
        anchor_text = value;
        if (doc.anchors == null)
          doc.anchors = new HashMap<String, Integer>();
      } else if (key.equals("stanford_anchor_count"))
        doc.anchors.put(anchor_text, Integer.parseInt(value));
    }

    reader.close();
    System.err
        .println("# Signal file " + feature_file_name + ": number of queries="
            + numQuery + ", number of documents=" + numDoc);

    return result;
  }

  /**
   * Load the rel data file
   * 
   * @param rel_file_name
   * @return
   * @throws IOException
   */
  public static Map<String, Map<String, Double>> loadRelData(
      String rel_file_name) throws IOException {
    Map<String, Map<String, Double>> result =
        new HashMap<String, Map<String, Double>>();

    File rel_file = new File(rel_file_name);
    if (!rel_file.exists()) {
      System.err.println("Invalid feature file name: " + rel_file_name);
      return null;
    }

    BufferedReader reader = new BufferedReader(new FileReader(rel_file));
    String line = null, query = null, url = null;
    int numQuery = 0;
    int numDoc = 0;
    while ((line = reader.readLine()) != null) {
      String[] tokens = line.split(":", 2);
      String key = tokens[0].trim();
      String value = tokens[1].trim();

      if (key.equals("query")) {
        query = value;
        result.put(query, new HashMap<String, Double>());
        numQuery++;
      } else if (key.equals("url")) {
        String[] tmps = value.split(" ", 2);
        url = tmps[0].trim();
        double score = Double.parseDouble(tmps[1].trim());
        result.get(query).put(url, score);
        numDoc++;
      }
    }
    reader.close();
    System.err.println("# Rel file " + rel_file_name + ": number of queries="
        + numQuery + ", number of documents=" + numDoc);

    return result;
  }

  /**
   * Load Signal files. Might be helpful in implementing extractTrainFeatures
   * and extractTestFeatures methods
   * 
   * @param train_data_file
   * @param idfs
   * @return
   */
  public static Quad<Instances, List<Pair<Query, Document>>, ArrayList<Attribute>, Map<Integer, List<Integer>>> loadSignalFile(
      String train_data_file, Map<String, Double> idfs) {

    /* Initial feature vectors */
    Instances X = null;

    /* Build X and Y matrices */
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    attributes.add(new Attribute("url_w"));
    attributes.add(new Attribute("title_w"));
    attributes.add(new Attribute("body_w"));
    attributes.add(new Attribute("header_w"));
    attributes.add(new Attribute("anchor_w"));
    X = new Instances("train_dataset", attributes, 0);
    int numAttributes = X.numAttributes();

    /* Map to record which doc belong to which query: query -> [list of doc] */
    Map<Integer, List<Integer>> index_map =
        new HashMap<Integer, List<Integer>>();
    int query_counter = 0, doc_counter = 0;
    List<Pair<Query, Document>> queryDocList =
        new ArrayList<Pair<Query, Document>>();
    try {
      Map<Query, List<Document>> data_map = Util.loadTrainData(train_data_file);

      Feature feature = new Feature(idfs);

      /* Add data */
      for (Query query : data_map.keySet()) {
        index_map.put(query_counter, new ArrayList<Integer>());

        for (Document doc : data_map.get(query)) {
          index_map.get(query_counter).add(doc_counter);
          doc_counter++;
          double[] features = feature.extractFeatureVector(doc, query);
          double[] instance = new double[numAttributes];
          for (int i = 0; i < features.length; ++i) {
            instance[i] = features[i];
          }
          Instance inst = new DenseInstance(1.0, instance);
          X.add(inst);

          queryDocList.add(new Pair<Query, Document>(query, doc));
        }
        query_counter++;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* Conduct standardization on X */
    Standardize filter = new Standardize();
    // Normalize filter = new Normalize(); filter.setScale(2.0);
    // filter.setTranslation(-1.0); // scale values to [-1, 1]
    Instances new_X = null;
    try {
      filter.setInputFormat(X);
      new_X = Filter.useFilter(X, filter);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new Quad<Instances, List<Pair<Query, Document>>, ArrayList<Attribute>, Map<Integer, List<Integer>>>(
        new_X, queryDocList, attributes, index_map);
  }

  /**
   * Load document-frequencies. Could also be used to load the idfs file
   * supplied with PA4 data.
   * 
   * @param idfFile
   * @return
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Double> loadDFs(String idfFile) {
    Map<String, Double> termDocCount = null;
    try {
      FileInputStream fis = new FileInputStream(idfFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      termDocCount = (HashMap<String, Double>) ois.readObject();
      ois.close();
      fis.close();
    } catch (IOException | ClassNotFoundException ioe) {
      ioe.printStackTrace();
      return null;
    }
    return termDocCount;
  }

  /**
   * Extract various kinds of term frequencies eg. (url, title, body, header,
   * and anchor)
   * 
   * @param d
   * @param q
   * @return
   */
  public static Map<String, Map<String, Double>> getDocTermFreqs(Document d,
      Query q) {
    String url = d.url;

    // map from tf type -> queryWord -> score
    Map<String, Map<String, Double>> tfs =
        new HashMap<String, Map<String, Double>>();

    //////////////////// Initialization/////////////////////

    // initialize tfs
    for (String type : Util.TFTYPES)
      tfs.put(type, new HashMap<String, Double>());

    // initialize tfs for querywords
    for (String queryWord : q.queryWords)
      for (String tfType : tfs.keySet())
        tfs.get(tfType).put(queryWord, 0.0);

    // tokenize url
    ArrayList<String> urlTokens = Parser.parseUrlString(url);

    // tokenize title
    List<String> titleTokens = Parser.parseTitle(d.title);

    // tokenize headers if exist
    List<String> headerTokens = Parser.parseHeaders(d.headers);

    // tokenize anchors if exist
    Map<String, Integer> anchorCountMap = Parser.parseAnchors(d.anchors);

    // tokenize body_hits if exists
    Map<String, Integer> bodyCountMap = Parser.parseBody(d.body_hits);

    ////////////////////////////////////////////////////////

    ////////// handle counts//////

    // loop through query terms increasing relevant tfs
    for (String queryWord : q.queryWords) {
      // url
      for (String urlToken : urlTokens) {

        if (queryWord.equals(urlToken)) {
          double oldVal = tfs.get("url").get(queryWord);
          tfs.get("url").put(queryWord, oldVal + 1);
        }
      }

      // title
      for (String titleToken : titleTokens) {
        if (queryWord.equals(titleToken)) {
          double oldVal = tfs.get("title").get(queryWord);
          tfs.get("title").put(queryWord, oldVal + 1);
        }
      }

      // headers --if size is 0, just skip over
      for (String headerToken : headerTokens) {
        if (queryWord.equals(headerToken)) {
          double oldVal = tfs.get("header").get(queryWord);
          tfs.get("header").put(queryWord, oldVal + 1);
        }
      }

      // anchors --if none, skip over
      for (String anchorToken : anchorCountMap.keySet()) {
        if (queryWord.equals(anchorToken)) {
          double oldVal = tfs.get("anchor").get(queryWord);
          tfs.get("anchor").put(queryWord,
              oldVal + anchorCountMap.get(anchorToken));
        }
      }

      // body_hits--if none, skip over
      for (String bodyHit : bodyCountMap.keySet()) {
        if (queryWord.equals(bodyHit)) {
          double oldVal = tfs.get("body").get(queryWord);
          tfs.get("body").put(queryWord, oldVal + bodyCountMap.get(bodyHit));
        }
      }

    }
    return tfs;
  }

  public static void main(String[] args) {
    try {
      System.out.print(loadRelData(args[0]));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
