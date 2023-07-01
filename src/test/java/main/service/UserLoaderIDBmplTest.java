package main.service;

import main.domain.User;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserLoaderIDBmplTest {

    @Test
    public void shouldGetUsers() {
        List<User> users = new UserLoaderDBImpl(getDatSource()).getUsersFromDB();
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
