package com.mycompany.myproject.social;

import com.mycompany.myproject.persist.entity.Authority;
import com.mycompany.myproject.persist.entity.User;
import com.mycompany.myproject.persist.repo.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class AccountConnectionSignUpService implements ConnectionSignUp {

    private static final Logger LOG = LoggerFactory.getLogger(AccountConnectionSignUpService.class);

    @Autowired
    private UserRepo usersRepo;

    @Override
    public String execute(Connection<?> connection) {

        UserProfile profile = connection.fetchUserProfile();

        String login = profile.getEmail() == null? profile.getUsername(): profile.getEmail();

        User user = usersRepo.findByLogin(login);

        if(user == null) {
            user = new User();
        }

        user.setLogin(login);
        user.setFirstName(profile.getFirstName());
        user.setFamilyName(profile.getLastName());
        user.setEmail(profile.getEmail());
        user.setPassword(RandomStringUtils.randomAlphanumeric(6));
        user.setEnabled(true);
        user.setImageUrl(connection.getImageUrl());
        Authority authority = new Authority();
        authority.setId(1L);
        user.getAuthorities().add(authority);

        usersRepo.save(user);
        return user.getLogin();
    }
}