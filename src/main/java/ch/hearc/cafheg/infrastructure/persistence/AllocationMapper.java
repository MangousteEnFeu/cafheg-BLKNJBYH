package ch.hearc.cafheg.infrastructure.persistence;

import ch.hearc.cafheg.domain.allocations.Allocation;
import ch.hearc.cafheg.domain.allocations.Canton;
import ch.hearc.cafheg.domain.common.Montant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocationMapper extends Mapper {

  private static final Logger log = LoggerFactory.getLogger(AllocationMapper.class);

  private static final String QUERY_FIND_ALL = "SELECT MONTANT, CANTON, DEBUT, FIN FROM ALLOCATIONS";

  public List<Allocation> findAll() {
    log.debug("Recherche de toutes les allocations");
    Connection connection = activeJDBCConnection();
    log.debug("SQL: {}", QUERY_FIND_ALL);
    try (PreparedStatement ps = connection.prepareStatement(QUERY_FIND_ALL);
         ResultSet rs = ps.executeQuery()) {
      List<Allocation> allocations = new ArrayList<>();
      while (rs.next()) {
        log.trace("ResultSet#next - mapping allocation");
        allocations.add(new Allocation(
            new Montant(rs.getBigDecimal(1)),
            Canton.fromValue(rs.getString(2)),
            rs.getDate(3).toLocalDate(),
            rs.getDate(4) != null ? rs.getDate(4).toLocalDate() : null));
      }
      log.debug("Allocations trouvées : {}", allocations.size());
      return allocations;
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche des allocations", e);
      throw new RuntimeException(e);
    }
  }
}
