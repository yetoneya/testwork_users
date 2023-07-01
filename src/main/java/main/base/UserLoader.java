package main.base;

import main.domain.User;

import java.util.List;

public interface UserLoader {

    void getUsers();

    List<User> getUsersFromDB();



}
