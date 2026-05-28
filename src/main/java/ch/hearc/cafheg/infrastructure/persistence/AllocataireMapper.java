package ch.hearc.cafheg.infrastructure.persistence;

import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.domain.allocations.NoAVS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocataireMapper extends Mapper {

  private static final Logger log = LoggerFactory.getLogger(AllocataireMapper.class);

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_FIND_WHERE_NO_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_HAS_VERSEMENTS = "SELECT COUNT(*) FROM VERSEMENTS V JOIN ALLOCATAIRES A ON A.NUMERO=V.FK_ALLOCATAIRES WHERE A.NO_AVS=?";
  private static final String QUERY_DELETE_BY_NO_AVS = "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";

  public List<Allocataire> findAll(String likeNom) {
    log.debug("findAll() likeNom={}", likeNom);
    Connection connection = activeJDBCConnection();
    String query = likeNom == null ? QUERY_FIND_ALL : QUERY_FIND_WHERE_NOM_LIKE;
    log.debug("SQL: {}", query);
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      if (likeNom != null) {
        ps.setString(1, likeNom + "%");
      }
      List<Allocataire> allocataires = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          log.trace("ResultSet#next - mapping allocataire");
          allocataires.add(new Allocataire(new NoAVS(rs.getString(3)), rs.getString(2), rs.getString(1)));
        }
      }
      log.debug("Allocataires trouvés : {}", allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche des allocataires", e);
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    log.debug("findById() id={}", id);
    Connection connection = activeJDBCConnection();
    log.debug("SQL: {}", QUERY_FIND_WHERE_NUMERO);
    try (PreparedStatement ps = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        log.trace("ResultSet#next - mapping allocataire");
        if (rs.next()) {
          return new Allocataire(new NoAVS(rs.getString(1)), rs.getString(2), rs.getString(3));
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'allocataire id={}", id, e);
      throw new RuntimeException(e);
    }
  }

  public Allocataire findByNoAVS(String noAVS) {
    log.debug("findByNoAVS() noAVS={}", noAVS);
    Connection connection = activeJDBCConnection();
    try (PreparedStatement ps = connection.prepareStatement(QUERY_FIND_WHERE_NO_AVS)) {
      ps.setString(1, noAVS);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Allocataire(new NoAVS(rs.getString(1)), rs.getString(2), rs.getString(3));
        }
        return null;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public boolean hasVersements(String noAVS) {
    log.debug("hasVersements() noAVS={}", noAVS);
    Connection connection = activeJDBCConnection();
    try (PreparedStatement ps = connection.prepareStatement(QUERY_HAS_VERSEMENTS)) {
      ps.setString(1, noAVS);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getInt(1) > 0;
      }
    } catch (SQLException e) {
      log.error("Erreur lors de la vérification des versements pour noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public void deleteByNoAVS(String noAVS) {
    log.debug("deleteByNoAVS() noAVS={}", noAVS);
    Connection connection = activeJDBCConnection();
    try (PreparedStatement ps = connection.prepareStatement(QUERY_DELETE_BY_NO_AVS)) {
      ps.setString(1, noAVS);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.error("Erreur lors de la suppression de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public void updateAllocataire(String noAVS, String nom, String prenom) {
    log.debug("updateAllocataire() noAVS={}", noAVS);
    Connection connection = activeJDBCConnection();
    try (PreparedStatement ps = connection.prepareStatement(QUERY_UPDATE)) {
      ps.setString(1, nom);
      ps.setString(2, prenom);
      ps.setString(3, noAVS);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.error("Erreur lors de la mise à jour de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }
}
