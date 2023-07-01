package main.domain;

import java.time.LocalDate;
import java.util.Objects;

public class User {

    private int id;
    private int user_id;
    private String user_f_name;
    private String user_l_name;
    private LocalDate user_b_date;
    private String user_city;
    private String user_contacts;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_f_name() {
        return user_f_name;
    }

    public String getUser_l_name() {
        return user_l_name;
    }

    public LocalDate getUser_b_date() {
        return user_b_date;
    }

    public String getUser_city() {
        return user_city;
    }

    public String getUser_contacts() {
        return user_contacts;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public User setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public User setUser_f_name(String user_f_name) {
        this.user_f_name = user_f_name;
        return this;
    }

    public User setUser_l_name(String user_l_name) {
        this.user_l_name = user_l_name;
        return this;
    }

    public User setUser_b_date(LocalDate user_b_date) {
        this.user_b_date = user_b_date;
        return this;
    }

    public User setUser_city(String user_city) {
        this.user_city = user_city;
        return this;
    }

    public User setUser_contacts(String user_contacts) {
        this.user_contacts = user_contacts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return user_id == user.user_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id);
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_f_name='" + user_f_name + '\'' +
                ", user_l_name='" + user_l_name + '\'' +
                ", user_b_date=" + user_b_date +
                ", user_city='" + user_city + '\'' +
                ", user_contacts=" + user_contacts +
                '}';
    }
}
