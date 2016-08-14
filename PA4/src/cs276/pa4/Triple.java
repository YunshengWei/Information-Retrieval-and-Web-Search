package cs276.pa4;

public class Triple<F, S, T> {
  private F first;
  private S second;
  private T third;

  public Triple(F f, S s, T t) {
    this.first = f;
    this.second = s;
    this.third = t;
  }

  public F getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  public T getThird() {
    return third;
  }
}
