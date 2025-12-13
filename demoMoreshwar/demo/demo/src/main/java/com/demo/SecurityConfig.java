package com.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",         // --- FIX: Now matches your PageController ---
                    "/login-check",   
                    "/css/**",        
                    "/js/**",         
                    "/images/**",
                    "/CLUB SANDWICH 1.png" ,// --- FIX: Added your image ---
                    "/api/**",
                    "/reset_password",      // The HTML page
                    "/reset-password",     // Alternative URL if you use that
                    "/api/auth/reset-password"  
                ).permitAll()           // --- These are all public ---
                .anyRequest().authenticated() // --- ALL other pages require login ---
            )
            .formLogin(form -> form
                .loginPage("/login")           // --- FIX: Now points to your new @GetMapping ---
                .loginProcessingUrl("/login-check") 
                .defaultSuccessUrl("/menu_dashboard", true) 
                .failureUrl("/login?error=true") // --- FIX: Matches the /login path ---
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true") // --- FIX: Matches the /login path ---
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .httpBasic(basic -> basic.disable()); 
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is the "No Operation" Password Encoder for plain-text passwords.
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}

