package com.mycompany.myproject.persist.repo;

import com.mycompany.myproject.persist.entity.Profile;
import com.mycompany.myproject.persist.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserRepoImpl implements UserRepo {

    private static final Logger logger = LoggerFactory.getLogger(UserRepoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @PostConstruct
    public void init() {
//        org.hsqldb.util.DatabaseManagerSwing.main(new String[]{"--url", "jdbc:hsqldb:mem:myDb", "--noexit"});
    }

    private static final String QUERY_USERS =
            " SELECT up.userId, up.email, up.firstName, up.lastName, up.name,  up.username, up.imageUrl, au.authority" +
                    " FROM UserProfile up " +
                    " LEFT OUTER JOIN UserConnection uc ON uc.userId = up.userId " +
                    " LEFT OUTER JOIN authorities au ON au.username = up.userId ";

    private static final String QUERY_USER_BY_USERID = QUERY_USERS + " WHERE up.userId = ? ";

    @Override
    public User findByUserName(String username) {
        logger.debug("query {}; params {}", QUERY_USER_BY_USERID, username);
        List<User> results = jdbcTemplate.query(QUERY_USER_BY_USERID, new Object[]{username}, new UserResultSetExtractor());
        return DataAccessUtils.requiredSingleResult(results);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(QUERY_USERS, new UserResultSetExtractor());
    }


    @Override
    public void createUser(String userId, Profile profile) {
        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)", userId, RandomStringUtils.randomAlphanumeric(8));
        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)", userId, "user");
        jdbcTemplate.update("INSERT into userProfile(userId, email, firstName, lastName, name, username, imageUrl) values(?,?,?,?,?,?,?)",
                userId,
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getName(),
                profile.getUsername(),
                profile.getImageUrl());
    }


    protected class UserResultSetExtractor implements ResultSetExtractor<List<User>> {

        private HashMap<String, User> users = new HashMap<>();

        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            while (rs.next()) {
                String userId = rs.getString("userId");
                User user = users.get(userId);
                if (user == null) {
                    user = mapRow(rs);
                    users.put(userId, user);
                }
                user.getAuthorities().add(rs.getString("authority"));
            }
            return new ArrayList<>(users.values());
        }

        private User mapRow(ResultSet rs) throws SQLException {
            User user = new User();
            user.setId(rs.getString("userId"));
            user.setFirstName(rs.getString("firstName"));
            user.setFamilyName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            user.setImageUrl(rs.getString("imageUrl"));
            return user;
        }


    }


}
