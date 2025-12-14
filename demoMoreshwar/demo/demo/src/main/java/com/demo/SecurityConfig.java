package com.demo;

import com.demo.service.StaffUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final StaffUserDetailsService staffUserDetailsService;

    public SecurityConfig(StaffUserDetailsService staffUserDetailsService) {
        this.staffUserDetailsService = staffUserDetailsService;
    }

 @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(staffUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .authenticationProvider(authProvider) // 🔥 THIS IS THE KEY
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/login",
                "/css/**",
                "/js/**",
                "/images/**",
                "/CLUB SANDWICH 1.png",
                "/api/**",
                "/reset_password",
                "/reset-password",
                "/api/auth/reset-password"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/menu_dashboard", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

    return http.build();
}


    // 🔥 THIS WAS MISSING — MOST IMPORTANT
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // SAME encoder (no change)
    @Bean
    public PasswordEncoder passwordEncoder() {
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
