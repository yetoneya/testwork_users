package main.repo;

import main.domain.User;
import main.dto.UserNameDto;
import org.apache.commons.math3.util.Pair;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {

    List<User> gerUsers();

    UserNameDto findNameById(int id);

    List<User> gerUsers(int first, int last);

    Pair<Integer, Integer> selectIdsInterval() throws SQLException;
}
