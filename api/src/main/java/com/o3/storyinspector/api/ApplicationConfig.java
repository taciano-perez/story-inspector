package com.o3.storyinspector.api;

import com.o3.storyinspector.api.util.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Properties;

@Configuration
@EnableWebSecurity
@EnableAsync
@EnableTransactionManagement
public class ApplicationConfig extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_USER_ID = "108700212624021084744";

    final static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final String PROFILE_DEV = "dev";
    private static final String PROFILE_GCP_COMPENGINE = "gcp-compengine";
    private static final String PROFILE_GCP_APPENGINE = "gcp-appengine";
    private static final String PROFILE_GCP_CLOUDRUN = "gcp-cloudrun";

    @Value("${spring.profiles.active}")
    private String activeSpringProfile;

    @Value("${spring.mail.host}")
    private String mailServerHost;

    @Value("${spring.mail.port}")
    private Integer mailServerPort;

    @Value("${spring.mail.username}")
    private String mailServerUsername;

    @Value("${spring.mail.password}")
    private String mailServerPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mailServerAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mailServerStartTls;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http.cors()
                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
        http.authorizeRequests()
                .anyRequest()
                .permitAll()
                .and().csrf().disable();
        if (PROFILE_DEV.equals(activeSpringProfile)) {
            logger.warn("Using dev security configuration");
            ApiUtils.setForceHttps(false);
        } else if (PROFILE_GCP_APPENGINE.equals(activeSpringProfile)) {
            logger.info("Using gcp-appengine security configuration");
            ApiUtils.setForceHttps(true);
        } else if (PROFILE_GCP_CLOUDRUN.equals(activeSpringProfile)) {
            logger.info("Using gcp-cloudrun security configuration");
            ApiUtils.setForceHttps(true);
        } else {
            logger.info("Using gcp-compengine production security configuration");
            http.requiresChannel()
                    .anyRequest()
                    .requiresSecure();
            ApiUtils.setForceHttps(false);
        }
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailServerHost);
        mailSender.setPort(mailServerPort);

        mailSender.setUsername(mailServerUsername);
        mailSender.setPassword(mailServerPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailServerAuth);
        props.put("mail.smtp.starttls.enable", mailServerStartTls);
        props.put("mail.debug", "true");

        return mailSender;
    }

}
