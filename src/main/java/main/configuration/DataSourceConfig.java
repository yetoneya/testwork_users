package main.configuration;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${db.user}")
    private String USER;

    @Value("${db.password}")
    private String PASSWORD;

    @Value("${server.name}")
    private String SERVER_NAME;

    @Value("${db.name}")
    private String DATABASE_NAME;

    @Value("${pool.size}")
    private int POOL_SIZE = 2;


    @Bean
    public DataSource dataSource() {
        PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setServerName(SERVER_NAME);
        ds.setDatabaseName(DATABASE_NAME);
        ds.setUser(USER);
        ds.setPassword(PASSWORD);
        ds.setMaxConnections(POOL_SIZE);
        return ds;
    }
}
