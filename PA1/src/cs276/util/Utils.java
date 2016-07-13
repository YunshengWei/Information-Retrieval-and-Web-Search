package cs276.util;

import java.util.ArrayList;
import java.util.List;

public class Utils {
  public static int[] integerListToIntArray(List<Integer> list) {
    int[] array = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      array[i] = list.get(i);
    }
    return array;
  }

  public static List<Integer> intArrayToIntegerList(int[] array) {
    List<Integer> list = new ArrayList<>(array.length);
    for (int i : array) {
      list.add(i);
    }
    return list;
  }
}
