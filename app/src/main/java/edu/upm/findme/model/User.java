package edu.upm.findme.model;

import java.util.List;

public class User {
    int id;
    String name;
    String phoneNumber;

    public User(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    static public User getById(List<User> users, int id) {
        for (User u : users)
            if (u.getId() == id)
                return u;

        return null;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getId() {
        return id;
    }
}
