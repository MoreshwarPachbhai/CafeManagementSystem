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

@SuppressWarnings("unused")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final StaffUserDetailsService staffUserDetailsService;

    public SecurityConfig(StaffUserDetailsService staffUserDetailsService) {
        this.staffUserDetailsService = staffUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .userDetailsService(staffUserDetailsService) // ✅ VERY IMPORTANT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/CLUB SANDWICH 1.png",
                    "/reset_password",
                    "/api/**" 
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
    .loginPage("/login")
    .loginProcessingUrl("/login-process") // 🔥 CHANGE HERE
    .defaultSuccessUrl("/menu_dashboard", true)
    .failureUrl("/login?error=true")
    .permitAll()
)


            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

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
