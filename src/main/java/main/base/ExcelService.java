package main.base;

import main.domain.User;

import java.util.List;

public interface ExcelService {

    String createExel(List<User> users);
}
