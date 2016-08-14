package cs276.pa4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * Implements Pairwise learner that can be used to train SVM
 *
 */
public class PairwiseLearner extends Learner {
  private LibSVM model;

  public PairwiseLearner(boolean isLinearKernel) {
    try {
      model = new LibSVM();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (isLinearKernel) {
      model.setKernelType(
          new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
    }
  }

  public PairwiseLearner(double C, double gamma, boolean isLinearKernel) {
    try {
      model = new LibSVM();
    } catch (Exception e) {
      e.printStackTrace();
    }

    model.setCost(C);
    model.setGamma(gamma); // only matter for RBF kernel
    if (isLinearKernel) {
      model.setKernelType(
          new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));
    }
  }

  @Override
  public Instances extractTrainFeatures(String train_data_file,
      String train_rel_file, Map<String, Double> idfs) throws IOException {
    List<String> classLabels = Arrays.asList(new String[] { "+1", "-1" });
    Attribute classLabelAttribute = new Attribute("class_label", classLabels);
    Triple<Instances, Map<String, Map<String, Integer>>, ArrayList<Attribute>> triple =
        Learner.extractFeatures(train_data_file, idfs, classLabelAttribute);

    Instances instances = triple.getFirst();
    Map<String, Map<String, Integer>> indexMap = triple.getSecond();
    ArrayList<Attribute> attributes = triple.getThird();
    Map<String, Map<String, Double>> rels = Util.loadRelData(train_rel_file);

    Instances dataset = new Instances("train_dataset", attributes, 0);
    dataset.setClassIndex(instances.numAttributes() - 1);

    for (Map.Entry<String, Map<String, Double>> e1 : rels.entrySet()) {
      String query = e1.getKey();
      List<String> urls = new ArrayList<>(e1.getValue().keySet());
      for (int i = 0; i < urls.size() - 1; i++) {
        for (int j = i + 1; j < urls.size(); j++) {
          String url1 = urls.get(i);
          String url2 = urls.get(j);
          double diffScore = e1.getValue().get(url1) - e1.getValue().get(url2);
          if (diffScore != 0) {
            // distribute training examples equally into classes
            Instance inst;
            String classLabel;
            if (Math.random() > 0.5) {
              inst = PairwiseLearner.diffInstance(
                  instances.get(indexMap.get(query).get(url1)),
                  instances.get(indexMap.get(query).get(url2)));
              classLabel = diffScore > 0 ? "+1" : "-1";
            } else {
              inst = PairwiseLearner.diffInstance(
                  instances.get(indexMap.get(query).get(url2)),
                  instances.get(indexMap.get(query).get(url1)));
              classLabel = diffScore > 0 ? "-1" : "+1";
            }
            inst.setDataset(dataset);
            inst.setClassValue(classLabel);
            dataset.add(inst);
          }
        }
      }
    }

    return dataset;
  }

  @Override
  public Classifier training(Instances dataset) throws Exception {
    model.buildClassifier(dataset);
    return model;
  }

  @Override
  public TestFeatures extractTestFeatures(String test_data_file,
      Map<String, Double> idfs) {
    List<String> classLabels = Arrays.asList(new String[] { "+1", "-1" });
    Attribute classLabelAttribute = new Attribute("class_label", classLabels);
    Triple<Instances, Map<String, Map<String, Integer>>, ArrayList<Attribute>> triple =
        Learner.extractFeatures(test_data_file, idfs, classLabelAttribute);

    Instances instances = triple.getFirst();
    Map<String, Map<String, Integer>> indexMap = triple.getSecond();

    instances.setRelationName("test_dataset");

    return new TestFeatures(instances, indexMap);
  }

  private static Instance diffInstance(Instance inst1, Instance inst2) {
    double[] feature1 = inst1.toDoubleArray();
    double[] feature2 = inst2.toDoubleArray();
    double[] diff = new double[feature1.length];
    for (int i = 0; i < diff.length; i++) {
      diff[i] = feature1[i] - feature2[i];
    }
    Instance inst = new DenseInstance(1.0, diff);
    return inst;
  }

  @Override
  public Map<String, List<String>> testing(TestFeatures tf, Classifier model)
      throws Exception {
    Map<String, List<String>> ranked = new HashMap<>();
    for (Map.Entry<String, Map<String, Integer>> e : tf.index_map.entrySet()) {
      String query = e.getKey();
      List<String> rankedUrls = new ArrayList<>();
      Map<String, Instance> features = new HashMap<>();

      for (Map.Entry<String, Integer> urlEntry : e.getValue().entrySet()) {
        String url = urlEntry.getKey();
        Instance feature = tf.features.get(urlEntry.getValue());
        rankedUrls.add(url);
        features.put(url, feature);
      }

      rankedUrls.sort(new Comparator<String>() {
        @Override
        public int compare(String url1, String url2) {
          Instance inst = PairwiseLearner.diffInstance(features.get(url1),
              features.get(url2));
          inst.setDataset(tf.features);

          double y = 0;
          try {
            y = model.classifyInstance(inst);
          } catch (Exception e) {
            e.printStackTrace();
            // Do we have better way to handle the exceptions here?
            System.exit(-1);
          }
          return y == 0 ? -1 : 1;
        }
      });

      ranked.put(query, rankedUrls);
    }

    return ranked;
  }

}
