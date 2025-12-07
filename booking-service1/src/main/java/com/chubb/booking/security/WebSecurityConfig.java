package com.chubb.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        // For simplicity username==email in these demo users
        var user = User.withUsername("user@example.com")
                .password(encoder.encode("userpass"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin@example.com")
                .password(encoder.encode("adminpass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/booking/flight/**").hasRole("USER")
                    .requestMatchers("/api/booking/ticket/**").permitAll()
                    .requestMatchers("/api/booking/history/**").authenticated()
                    .requestMatchers("/api/booking/cancel/**").authenticated()
                    .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
}
