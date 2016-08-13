package cs276.pa4;

import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * An abstract class that is extended by PairwiseLearner and PointWiseLearner
 *
 */
public abstract class Learner {
  public static boolean isLinearKernel = false;

  /* Construct training features matrix */
  public abstract Instances extractTrainFeatures(String train_data_file,
      String train_rel_file, Map<String, Double> idfs);

  /* Train the model */
  public abstract Classifier training(Instances dataset);

  /* Construct testing features matrix */
  public abstract TestFeatures extractTestFeatures(String test_data_file,
      Map<String, Double> idfs);

  /* Test the model, return ranked queries */
  public abstract Map<String, List<String>> testing(TestFeatures tf,
      Classifier model);
}
