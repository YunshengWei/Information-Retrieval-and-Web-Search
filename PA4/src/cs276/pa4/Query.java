package cs276.pa4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class Query implements Comparable<Query> {
  String query;
  List<String> queryWords; /* Words with no duplicates and all lower case */

  public Query(String query) {
    this.query = new String(query);
    String[] wordsArray = query.toLowerCase().split(" ");
    // Use LinkedHashSet to remove duplicates
    wordsArray = (new LinkedHashSet<String>(Arrays.asList(wordsArray)))
        .toArray(new String[0]);
    queryWords = new ArrayList<String>(Arrays.asList(wordsArray));
  }

  @Override
  public int compareTo(Query arg0) {
    return this.query.compareTo(arg0.query);
  }

  @Override
  public String toString() {
    return query;
  }
}