package main.service;

import main.domain.User;

import java.util.List;

public interface UserLoaderDB {

    List<User> getUsersFromDB();
}
