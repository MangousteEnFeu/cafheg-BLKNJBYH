package ch.hearc.cafheg.domain.allocations;

import java.util.Objects;

public class NoAVS {

  private final String value;

  public NoAVS(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NoAVS)) return false;
    NoAVS noAVS = (NoAVS) o;
    return Objects.equals(getValue(), noAVS.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
