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

        var user = User.withUsername("user@example.com")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin@example.com")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        System.out.println(">>> SECURITY CONFIG LOADED <<<");

        return new InMemoryUserDetailsManager(user, admin);
        
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔥 THE MOST IMPORTANT PART
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(cs -> cs.disable())
            .authorizeHttpRequests(auth -> auth
                    // booking endpoint requires authenticated USER
                    .requestMatchers("/api/booking/flight/**").hasRole("USER")
                    .requestMatchers("/api/booking/history/**").hasRole("USER")
                    .requestMatchers("/api/booking/ticket/**").permitAll()
                    .requestMatchers("/api/booking/cancel/**").hasRole("USER")
                    // everything else → authenticated
                    .anyRequest().authenticated()
            )
            .httpBasic();

        return http.build();
    }
}
