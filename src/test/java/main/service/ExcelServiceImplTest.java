package main.service;

import main.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class ExcelServiceImplTest {

    @Test
    public void shouldCreateExcelFile() {
        String res = new ExcelServiceImpl().createExel(listUsers());
        Assertions.assertNotNull(res);
    }

    private List<User> listUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            User user = new User();
            user = user.setUser_id(i)
                    .setUser_f_name("f_name" + i)
                    .setUser_l_name("l_name" + i)
                    .setUser_b_date("1.9")
                    .setUser_city("Moscow")
                    .setUser_contacts("email");
            users.add(user);
        }
        return users;
    }
}
