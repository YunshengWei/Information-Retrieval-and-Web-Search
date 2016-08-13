package cs276.pa4;

import weka.core.Instances;
import java.util.Map;

/**
 * A sample class to store the result
 */
public class TestFeatures {
  /* Test features */
  Instances features;

  /*
   * Associate query-doc pair to its index within FEATURES instances {query ->
   * {doc -> index}}
   * 
   * For example, you can get the feature for a pair of (query, url) using:
   * features.get(index_map.get(query).get(url));
   */
  Map<String, Map<String, Integer>> index_map;

  TestFeatures(Instances features, Map<String, Map<String, Integer>> indexMap) {
    this.features = features;
    this.index_map = indexMap;
  }
}
