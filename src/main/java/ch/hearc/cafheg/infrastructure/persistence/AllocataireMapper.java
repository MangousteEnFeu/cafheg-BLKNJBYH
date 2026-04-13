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

  public List<Allocataire> findAll(String likeNom) {
    log.debug("findAll() likeNom={}", likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        log.debug("SQL: {}", QUERY_FIND_ALL);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_ALL);
      } else {
        log.debug("SQL: {}", QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      List<Allocataire> allocataires = new ArrayList<>();
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          log.trace("ResultSet#next - mapping allocataire");
          allocataires
              .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2),
                  resultSet.getString(1)));
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
    try {
      log.debug("SQL: {}", QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      log.trace("ResultSet#next - mapping allocataire");
      resultSet.next();
      return new Allocataire(new NoAVS(resultSet.getString(1)),
          resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'allocataire id={}", id, e);
      throw new RuntimeException(e);
    }
  }

    public Allocataire findByNoAVS(String noAVS) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?");
      preparedStatement.setString(1, noAVS);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return new Allocataire(new NoAVS(resultSet.getString(1)),
            resultSet.getString(2), resultSet.getString(3));
      }
      return null;
    } catch (SQLException e) {
      log.error("Erreur lors de la recherche de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public boolean hasVersements(String noAVS) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "SELECT COUNT(*) FROM VERSEMENTS V JOIN ALLOCATAIRES A ON A.NUMERO=V.FK_ALLOCATAIRES WHERE A.NO_AVS=?");
      preparedStatement.setString(1, noAVS);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return resultSet.getInt(1) > 0;
    } catch (SQLException e) {
      log.error("Erreur lors de la vérification des versements pour noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public void deleteByNoAVS(String noAVS) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?");
      preparedStatement.setString(1, noAVS);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error("Erreur lors de la suppression de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

  public void updateAllocataire(String noAVS, String nom, String prenom) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(
          "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?");
      preparedStatement.setString(1, nom);
      preparedStatement.setString(2, prenom);
      preparedStatement.setString(3, noAVS);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      log.error("Erreur lors de la mise à jour de l'allocataire noAVS={}", noAVS, e);
      throw new RuntimeException(e);
    }
  }

}
