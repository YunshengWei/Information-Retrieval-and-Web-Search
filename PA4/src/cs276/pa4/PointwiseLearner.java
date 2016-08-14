package cs276.pa4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Implements point-wise learner that can be used to implement logistic
 * regression
 *
 */
public class PointwiseLearner extends Learner {

  @Override
  public Instances extractTrainFeatures(String train_data_file,
      String train_rel_file, Map<String, Double> idfs) throws IOException {

    /* Add data */
    Quad<Instances, List<Pair<Query, Document>>, ArrayList<Attribute>, Map<Integer, List<Integer>>> quad =
        Util.loadSignalFile(train_data_file, idfs);
    Map<String, Map<String, Double>> rels = Util.loadRelData(train_rel_file);

    Instances dataset = quad.getFirst();
    dataset.insertAttributeAt(new Attribute("relevance_score"),
        dataset.numAttributes());

    List<Pair<Query, Document>> queryDocList = quad.getSecond();
    for (int i = 0; i < dataset.size(); i++) {
      Instance instance = dataset.get(i);
      Pair<Query, Document> pair = queryDocList.get(i);
      String query = pair.getFirst().query;
      String url = pair.getSecond().url;
      double relScore = rels.get(query).get(url);
      instance.setValue(dataset.numAttributes() - 1, relScore);
    }

    /* Set last attribute as target */
    dataset.setClassIndex(dataset.numAttributes() - 1);

    return dataset;
  }

  @Override
  public Classifier training(Instances dataset) throws Exception {
    LinearRegression model = new LinearRegression();
    model.buildClassifier(dataset);

    return model;
  }

  @Override
  public TestFeatures extractTestFeatures(String test_data_file,
      Map<String, Double> idfs) {
    Triple<Instances, Map<String, Map<String, Integer>>, ArrayList<Attribute>> triple =
        Learner.extractFeatures(test_data_file, idfs,
            new Attribute("relevance_score"));
    Instances instances = triple.getFirst();
    Map<String, Map<String, Integer>> indexMap = triple.getSecond();

    instances.setRelationName("test_dataset");

    return new TestFeatures(instances, indexMap);
  }

  @Override
  public Map<String, List<String>> testing(TestFeatures tf, Classifier model)
      throws Exception {
    Map<String, List<String>> ranked = new HashMap<>();
    for (Map.Entry<String, Map<String, Integer>> e : tf.index_map.entrySet()) {
      String query = e.getKey();
      List<String> rankedUrls = new ArrayList<>();
      // url -> relevance_score
      Map<String, Double> relScores = new HashMap<>();

      for (Map.Entry<String, Integer> urlEntry : e.getValue().entrySet()) {
        String url = urlEntry.getKey();
        Instance feature = tf.features.get(urlEntry.getValue());
        rankedUrls.add(url);
        relScores.put(url, model.classifyInstance(feature));
      }

      rankedUrls.sort(new Comparator<String>() {
        @Override
        public int compare(String url1, String url2) {
          double score1 = relScores.get(url1);
          double score2 = relScores.get(url2);
          return score1 > score2 ? -1 : (score1 == score2 ? 0 : 1);
        }
      });
      ranked.put(query, rankedUrls);
    }

    return ranked;
  }
}
