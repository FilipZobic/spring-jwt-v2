package com.zobicfilip.springjwtv2;

import com.zobicfilip.springjwtv2.model.ProfileImageConfiguration;
import com.zobicfilip.springjwtv2.model.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({SecurityProperties.class, ProfileImageConfiguration.class})
public class SpringJwtV2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringJwtV2Application.class, args);
    }

}
