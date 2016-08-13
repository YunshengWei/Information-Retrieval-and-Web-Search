package cs276.pa4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
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
      String train_rel_file, Map<String, Double> idfs) {

    /*
     * @TODO: Below is a piece of sample code to show you the basic approach to
     * construct a Instances object, replace with your implementation.
     */

    Instances dataset = null;

    /* Build attributes list */
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    attributes.add(new Attribute("url_w"));
    attributes.add(new Attribute("title_w"));
    attributes.add(new Attribute("body_w"));
    attributes.add(new Attribute("header_w"));
    attributes.add(new Attribute("anchor_w"));
    attributes.add(new Attribute("relevance_score"));
    dataset = new Instances("train_dataset", attributes, 0);

    /* Add data */
    double[] instance = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };
    Instance inst = new DenseInstance(1.0, instance);
    dataset.add(inst);

    /* Set last attribute as target */
    dataset.setClassIndex(dataset.numAttributes() - 1);

    return dataset;
  }

  @Override
  public Classifier training(Instances dataset) {
    /*
     * @TODO: Your code here
     */
    return null;
  }

  @Override
  public TestFeatures extractTestFeatures(String test_data_file,
      Map<String, Double> idfs) {
    /*
     * @TODO: Your code here
     */
    return null;
  }

  @Override
  public Map<String, List<String>> testing(TestFeatures tf, Classifier model) {
    /*
     * @TODO: Your code here
     */
    return null;
  }

}
