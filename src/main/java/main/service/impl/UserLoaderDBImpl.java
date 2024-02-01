package main.service.impl;

import main.repo.UserRepository;
import main.service.UserLoaderDB;
import main.domain.User;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
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

    @Value("${pool.size}")
    private final int poolSize = 2;

    private final UserRepository userRepository;

    @Autowired
    public UserLoaderDBImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsersFromDB() {
        try {
            List<User> users = new ArrayList<>();
            Pair<Integer, Integer> idsInterval = userRepository.selectIdsInterval();
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
            return userRepository.gerUsers(first, last);
        }
    }
}
