package main.service;

import main.base.ExcelService;
import main.base.UserFacade;
import main.base.UserLoaderDB;
import main.base.UserLoaderNet;
import main.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class UserFacadeImpl implements UserFacade {

    private final ExcelService exelService;
    private final UserLoaderDB userLoaderDB;
    private final UserLoaderNet userLoaderNet;

    @Autowired
    public UserFacadeImpl(ExcelService exelService, UserLoaderDB userLoaderDB, UserLoaderNet userLoaderNet) {
        this.exelService = exelService;
        this.userLoaderDB = userLoaderDB;
        this.userLoaderNet = userLoaderNet;
    }

    @Override
    public void createUserData() throws IOException {
        if (userLoaderNet.getUserData()) {
            List<User> users = userLoaderDB.getUsersFromDB();
            exelService.createExel(users);
        }
    }
}
