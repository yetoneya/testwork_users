package main.repo.impl.integration;

import main.domain.User;
import main.repo.impl.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryImplTest {

    @Test
    public void shouldGetUsers() throws SQLException {
        List<User> users = new UserRepositoryImpl(getDatSource()).gerUsers();
        assertNotNull(users);
    }

    private DataSource getDatSource() {
        PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("testdb");
        ds.setUser("testuser");
        ds.setPassword("testpassword");
        ds.setMaxConnections(2);
        return ds;
    }
}
