package cs276.pa4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * An abstract class that is extended by PairwiseLearner and PointWiseLearner
 *
 */
public abstract class Learner {
  public static boolean isLinearKernel = false;

  /* Construct training features matrix */
  public abstract Instances extractTrainFeatures(String train_data_file,
      String train_rel_file, Map<String, Double> idfs) throws IOException;

  /* Train the model */
  public abstract Classifier training(Instances dataset) throws Exception;

  /* Construct testing features matrix */
  public abstract TestFeatures extractTestFeatures(String test_data_file,
      Map<String, Double> idfs);

  /* Test the model, return ranked queries */
  public abstract Map<String, List<String>> testing(TestFeatures tf,
      Classifier model) throws Exception;

  protected static Triple<Instances, Map<String, Map<String, Integer>>, ArrayList<Attribute>> extractFeatures(
      String data_file, Map<String, Double> idfs, Attribute classAttribute) {
    Quad<Instances, List<Pair<Query, Document>>, ArrayList<Attribute>, Map<Integer, List<Integer>>> quad =
        Util.loadSignalFile(data_file, idfs);
    Instances instances = quad.getFirst();
    List<Pair<Query, Document>> queryDocList = quad.getSecond();
    ArrayList<Attribute> attributes = quad.getThird();

    instances.insertAttributeAt(classAttribute, instances.numAttributes());
    attributes.add(classAttribute);
    instances.setClassIndex(instances.numAttributes() - 1);

    Map<String, Map<String, Integer>> indexMap = new HashMap<>();
    for (int i = 0; i < queryDocList.size(); ++i) {
      Pair<Query, Document> pair = queryDocList.get(i);
      String query = pair.getFirst().query;
      String url = pair.getSecond().url;
      if (!indexMap.containsKey(query)) {
        Map<String, Integer> map = new HashMap<>();
        map.put(url, i);
        indexMap.put(query, map);
      } else {
        indexMap.get(query).put(url, i);
      }
    }

    return new Triple<>(instances, indexMap, attributes);
  }
}
