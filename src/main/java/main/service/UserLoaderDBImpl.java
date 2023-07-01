package main.service;

import main.base.UserLoaderDB;
import main.domain.User;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@PropertySource("classpath:application.properties")
public class UserLoaderDBImpl implements UserLoaderDB {

    private static final Logger logger = LogManager.getLogger(UserLoaderDBImpl.class);

    private final DataSource dataSource;

    @Value("${pool.size}")
    private int poolSize = 2;

    @Autowired
    public UserLoaderDBImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> getUsersFromDB() {
        try {
            List<User> users = new ArrayList<>();
            Pair<Integer, Integer> idsInterval = selectIdsInterval();
            List<Task> tasks = createTaskList(idsInterval);
            List<Future<List<User>>> resultList;
            ExecutorService service = Executors.newFixedThreadPool(poolSize);
            try {
                resultList = service.invokeAll(tasks);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                return List.of();
            } finally {
                service.shutdown();
            }
            for (int i = 0; i < resultList.size(); i++) {
                Future<List<User>> future = resultList.get(i);
                try {
                    List<User> userList = future.get();
                    users.addAll(userList);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                    return List.of();
                }
            }
            return users;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return List.of();
        }
    }

    private Pair<Integer, Integer> selectIdsInterval() throws SQLException {
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

    private List<Task> createTaskList(Pair<Integer, Integer> idsInterval) {
        List<Pair<Integer, Integer>> parts = getParts(idsInterval);
        List<Task> tasks = new ArrayList<>();
        for (Pair<Integer, Integer> pair : parts) {
            tasks.add(new Task(pair.getFirst(), pair.getSecond()));
        }
        return tasks;
    }


    private List<Pair<Integer, Integer>> getParts(Pair<Integer, Integer> idsInterval) {
        int count = idsInterval.getSecond() - idsInterval.getFirst() + 1;
        int diff = count / poolSize;
        List<Pair<Integer, Integer>> limits = new ArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            if (i == poolSize - 1) {
                limits.add(new Pair<>(idsInterval.getFirst() + diff * i, idsInterval.getSecond()));
            } else {
                limits.add(new Pair<>(idsInterval.getFirst() + diff * i, idsInterval.getFirst() + diff * (i + 1) - 1));
            }
        }
        return limits;
    }

    private class Task implements Callable<List<User>> {

        private final Integer first;
        private final Integer last;

        public Task(Integer first, Integer last) {
            this.first = first;
            this.last = last;
        }

        @Override
        public List<User> call() {
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
    }

}
