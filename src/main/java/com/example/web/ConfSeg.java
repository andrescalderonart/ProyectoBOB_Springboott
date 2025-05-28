package com.example.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration

public class ConfSeg {

    @Bean
    public UserDetailsService users(){
        UserDetails user = User.withUsername("user")
                .password("{noop}210607")
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password("{noop}123456")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager (user,admin);

    }

}
