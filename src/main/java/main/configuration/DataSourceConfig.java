package main.configuration;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("testdb");
        ds.setUser("testuser");
        ds.setPassword("testpassword");
        ds.setMaxConnections(4);
        return ds;
    }
}
