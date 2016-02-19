package com.mycompany.myproject.social;

import com.mycompany.myproject.persist.entity.Profile;
import com.mycompany.myproject.persist.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountConnectionSignUpService implements ConnectionSignUp {

    private static final Logger LOG = LoggerFactory.getLogger(AccountConnectionSignUpService.class);

    @Autowired
    private UserRepo usersRepo;

    @Override
    public String execute(Connection<?> connection) {
        Profile profile = new Profile();
        BeanUtils.copyProperties(connection.fetchUserProfile(), profile);
        String userId = UUID.randomUUID().toString();
        profile.setImageUrl(connection.getImageUrl());
        LOG.debug("Created user-id: " + userId);
        usersRepo.createUser(userId, profile);
        return userId;
    }
}