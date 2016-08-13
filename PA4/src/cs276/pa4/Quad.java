package cs276.pa4;

public class Quad<F, S, T, FO> {
  private F first;
  private S second;
  private T third;
  private FO fourth;

  public Quad(F f, S s, T t, FO fo) {
    this.first = f;
    this.second = s;
    this.third = t;
    this.fourth = fo;
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

  public FO getFourth() {
    return fourth;
  }
}
