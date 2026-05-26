package ch.hearc.cafheg.infrastructure.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;


public class Database {

  private static final Logger log = LoggerFactory.getLogger(Database.class);

  private static DataSource dataSource;
  private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

  static Connection activeJDBCConnection() {
    if (connection.get() == null) {
      throw new RuntimeException("Pas de connection JDBC active");
    }
    return connection.get();
  }

  public static <T> T inTransaction(Supplier<T> inTransaction) {
    log.debug("inTransaction#start");
    try {
      log.debug("inTransaction#getConnection");
      connection.set(dataSource.getConnection());
      return inTransaction.get();
    } catch (Exception e) {
      log.error("Erreur lors de l'exécution de la transaction", e);
      throw new RuntimeException(e);
    } finally {
      try {
        log.debug("inTransaction#closeConnection");
        connection.get().close();
      } catch (SQLException e) {
        log.error("Erreur lors de la fermeture de la connexion", e);
        throw new RuntimeException(e);
      }
      log.debug("inTransaction#end");
      connection.remove();
    }
  }

  public static void inTransaction(Runnable inTransaction) {
    inTransaction(() -> {
      inTransaction.run();
      return null;
    });
  }

  DataSource dataSource() {
    return dataSource;
  }

  public void start(String jdbcUrl, String username, String password) {
    log.info("Initializing datasource");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setMaximumPoolSize(20);
    config.setDriverClassName("org.postgresql.Driver");
    dataSource = new HikariDataSource(config);
    log.info("Datasource initialized");
  }
}
