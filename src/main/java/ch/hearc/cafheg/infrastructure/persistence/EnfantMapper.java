package ch.hearc.cafheg.infrastructure.persistence;

import ch.hearc.cafheg.domain.allocations.NoAVS;
import ch.hearc.cafheg.domain.versements.Enfant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnfantMapper extends Mapper {

  private static final Logger log = LoggerFactory.getLogger(EnfantMapper.class);

  private static final String QUERY_FIND_ENFANT_BY_ID = "SELECT NO_AVS, NOM, PRENOM FROM ENFANTS WHERE NUMERO=?";

  public Enfant findById(long id) {
    log.debug("Recherche d'un enfant par son id {}", id);
    Connection connection = activeJDBCConnection();
    try (PreparedStatement ps = connection.prepareStatement(QUERY_FIND_ENFANT_BY_ID)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        log.trace("ResultSet#next - mapping enfant");
        if (rs.next()) {
          return new Enfant(new NoAVS(rs.getString(1)), rs.getString(2), rs.getString(3));
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'enfant id={}", id, e);
      throw new RuntimeException(e);
    }
  }
}
