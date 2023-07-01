package main.service;

import main.base.UserLoader;
import main.base.ExcelService;
import main.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class UserLoaderImpl implements UserLoader {

    private final DataSource dataSource;

    private final ExcelService excelService;

    @Autowired
    public UserLoaderImpl(DataSource dataSource, ExcelService excelService) {
        this.dataSource = dataSource;
        this.excelService = excelService;
    }

    @Override
    public void getUsers() {

    }

    @Override
    public List<User> getUsersFromDB() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); Statement st = connection.createStatement()) {
            int userCount = selectCount(st);
            ResultSet rs = st.executeQuery("select * from vk_user");
            while (rs.next()) {
                Object id = rs.getLong("user_id");
                System.out.println();
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private int selectCount(Statement st) throws SQLException {
        int total = 0;
        ResultSet rs = st.executeQuery("select count(*) as total from vk_user");
        if (rs.next()) {
            total = rs.getInt("total");
        }
        rs.close();
        return total;
    }

}
