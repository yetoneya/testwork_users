package main.repo.impl;

import main.domain.User;
import main.dto.UserNameDto;
import main.repo.UserRepository;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
@Primary
public class UserRepositoryImpl  implements UserRepository {

    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);

    private final DataSource dataSource;


    public UserRepositoryImpl(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> gerUsers(int first, int last) {
        List<User> users = new ArrayList<>();
        String query = MessageFormat.format("select * from vk_user where id >= {0} and id <= {1}", first, last);
        try (Connection connection = dataSource.getConnection(); Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"))
                        .setUser_id(rs.getInt("user_id"))
                        .setUser_f_name(rs.getString("user_f_name"))
                        .setUser_l_name(rs.getString("user_l_name"))
                        .setUser_b_date(rs.getString("user_b_date"))
                        .setUser_city(rs.getString("user_city"))
                        .setUser_contacts(rs.getString("user_contacts"));
                users.add(user);
            }
            rs.close();
        } catch (SQLException e) {
            StackTraceElement ste = e.getStackTrace()[0];
            String message = MessageFormat.format("Ошибка {0} в классе {1} методе {2} строке {3}.", e.getMessage(), ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            if (e.getCause() != null) message = message.concat(" Причина: ").concat(e.getCause().getMessage());
            logger.error(message);
            return List.of();
        }
        return users;
    }

    @Override
    public Pair<Integer, Integer> selectIdsInterval() throws SQLException {
        int min = 0;
        int max = 0;
        try (Connection connection = dataSource.getConnection(); Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("select min(id) as min, max(id) as max from vk_user");
            if (rs.next()) {
                min = rs.getInt("min");
                max = rs.getInt("max");
            }
            rs.close();
            return new Pair<>(min, max);
        }
    }

    @Override
    public List<User> gerUsers() {
        return null;
    }

    @Override
    public UserNameDto findNameById(int id) {
        return null;
    }
}
