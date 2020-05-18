package com.github.ghthou.googleauthenticator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class GoogleAuthenticatorIntegrationApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment env = SpringApplication
                .run(GoogleAuthenticatorIntegrationApplication.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8080");
        log.info("项目信息 http://localhost:{}", port);
        log.info("二维码 http://localhost:{}/qr_code", port);

    }
}
