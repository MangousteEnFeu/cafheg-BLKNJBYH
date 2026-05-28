package ch.hearc.cafheg.domain.allocations;

import java.util.stream.Stream;

public enum Canton {
  NE, BE, FR, GE, SH,  // Romand et français
  AG, AI, AR, BL, BS,  // Nord-ouest
  GL, GR, JU, LU,      // Divers
  OW, SG, SO, SZ,      // Est et centre
  TG, TI, UR, VD, VS,  // Sud et ouest
  ZG, ZH;              // Zurich et Zoug

  public static Canton fromValue(String value) {
    return Stream.of(Canton.values())
        .filter(c -> c.name().equals(value))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Canton inconnu : " + value));
  }
}
