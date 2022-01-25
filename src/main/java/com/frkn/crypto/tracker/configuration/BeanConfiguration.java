package com.frkn.crypto.tracker.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@Configuration
public class BeanConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


}
