package com.mycompany.myproject.config;


import com.mycompany.myproject.social.AccountConnectionSignUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import javax.sql.DataSource;

@Configuration
@EnableSocial
@PropertySource("classpath:myapp.properties")
@ComponentScan(basePackages = {"com.mycompany.myproject.social"})
public class SocialConfig implements SocialConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SocialConfig.class);

    public SocialConfig() {
        super();
        logger.info("loading SocialConfig ................................................ ");

        logger.info(System.getProperty("test"));
    }

    @Autowired
    private AccountConnectionSignUpService accountConnectionSignUpService;

    @Autowired
    private DataSource dataSource;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfc, Environment env) {
        cfc.addConnectionFactory(new LinkedInConnectionFactory(
                env.getProperty("spring.social.linkedin.appId"),
                env.getProperty("spring.social.linkedin.appSecret")));
        cfc.addConnectionFactory(new GitHubConnectionFactory(
                env.getProperty("spring.social.github.appId"),
                env.getProperty("spring.social.github.appSecret")));
        cfc.addConnectionFactory(new TwitterConnectionFactory(
                env.getProperty("spring.social.twitter.appId"),
                env.getProperty("spring.social.twitter.appSecret")));
        GoogleConnectionFactory gcf = new GoogleConnectionFactory(
                env.getProperty("spring.social.google.appId"),
                env.getProperty("spring.social.google.appSecret"));
        gcf.setScope("email");
        cfc.addConnectionFactory(gcf);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator cfl) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, cfl, Encryptors.noOpText());
        repository.setConnectionSignUp(accountConnectionSignUpService);
        return repository;
    }
}