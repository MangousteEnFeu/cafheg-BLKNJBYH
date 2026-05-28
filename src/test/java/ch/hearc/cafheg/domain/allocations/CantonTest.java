package ch.hearc.cafheg.domain.allocations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CantonTest {

  @Test
  void fromValue_GivenFR_ShouldBeFR() {
    assertThat(Canton.fromValue("FR")).isEqualTo(Canton.FR);
  }

  @Test
  void fromValue_GivenUnknownValue_ShouldThrow() {
    assertThrows(IllegalArgumentException.class, () -> Canton.fromValue("MM"));
  }

}