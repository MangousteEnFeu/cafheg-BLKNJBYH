package ch.hearc.cafheg;

import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.domain.allocations.AllocationService;
import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.Database;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class MyTestsIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1");

    private static AllocataireMapper allocataireMapper;
    private static AllocationService allocationService;

    @BeforeAll
    static void setup() {
        new Database().start(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();

        allocataireMapper = new AllocataireMapper();
        allocationService = new AllocationService(allocataireMapper, null);
    }

    @BeforeEach
    void loadTestData() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            // Nettoyage idempotent des données de test précédentes
            conn.prepareStatement(
                    "DELETE FROM allocataires WHERE no_avs IN ('756.0000.0001.01','756.0000.0002.02')"
            ).executeUpdate();
        }

        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            IDatabaseConnection dbConn = new DatabaseConnection(conn, "public");
            dbConn.getConfig().setProperty(
                    DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());

            IDataSet dataset = new FlatXmlDataSetBuilder()
                    .build(MyTestsIT.class.getResourceAsStream("/allocataires-dataset.xml"));
            DatabaseOperation.INSERT.execute(dbConn, dataset);
        }
    }

    @Test
    void simpleTest() {
        assertThat(1).isEqualTo(1);
    }

    @Test
    void deleteAllocataire_supprimeBienDeLaBase() {
        String noAVS = "756.0000.0001.01";

        Database.inTransaction(() -> allocationService.deleteAllocataire(noAVS));

        Allocataire result = Database.inTransaction(() -> allocataireMapper.findByNoAVS(noAVS));
        assertThat(result).isNull();
    }

    @Test
    void updateAllocataire_modifieBienDansLaBase() {
        String noAVS = "756.0000.0002.02";

        Database.inTransaction(() -> allocationService.updateAllocataire(noAVS, "NouveauNom", "NouveauPrenom"));

        Allocataire result = Database.inTransaction(() -> allocataireMapper.findByNoAVS(noAVS));
        assertThat(result.getNom()).isEqualTo("NouveauNom");
        assertThat(result.getPrenom()).isEqualTo("NouveauPrenom");
    }
}
