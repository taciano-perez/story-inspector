package com.o3.storyinspector.api;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Properties;

@Configuration
@EnableWebSecurity
@EnableAsync
public class ApplicationConfig extends WebSecurityConfigurerAdapter {

    public static final String ADMIN_USER_ID = "108700212624021084744";

    final static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final String PROFILE_DEV = "dev";

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
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests()
                .anyRequest()
                .permitAll();
        
        if (PROFILE_DEV.equals(activeSpringProfile)) {
            logger.warn("Using dev security configuration - CSRF disabled for development");
            http.csrf().disable();
        } else {
            logger.info("Using production security configuration - CSRF enabled");
            http.csrf()
                .ignoringAntMatchers("/h2-console/**"); // Allow H2 console in dev
            http.requiresChannel()
                    .anyRequest()
                    .requiresSecure();
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        if (PROFILE_DEV.equals(activeSpringProfile)) {
            // Development: Allow localhost origins
            configuration.addAllowedOrigin("http://localhost:3000");
            configuration.addAllowedOrigin("http://localhost:8080");
            configuration.addAllowedOrigin("http://localhost:8081");
            logger.warn("CORS: Development mode - allowing localhost origins only");
        } else {
            // Production: Restrict to specific domains
            configuration.addAllowedOrigin("https://story-inspector-web.herokuapp.com");
            configuration.addAllowedOrigin("https://storyinspector.com");
            logger.info("CORS: Production mode - allowing specific domains only");
        }
        
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
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
