package com.mycompany.myproject.persist.repo;

import com.mycompany.myproject.persist.entity.Profile;
import com.mycompany.myproject.persist.entity.User;

import java.util.List;

public interface UserRepo {

    User findByUserName(String login);

    List<User> findAll();

    void createUser(String userId, Profile profile);
}
