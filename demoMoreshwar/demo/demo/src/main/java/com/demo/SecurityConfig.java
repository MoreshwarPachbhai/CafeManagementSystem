package com.demo;

import com.demo.service.StaffUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 🔹 Inject your DB UserDetailsService
    private final StaffUserDetailsService staffUserDetailsService;

    public SecurityConfig(StaffUserDetailsService staffUserDetailsService) {
        this.staffUserDetailsService = staffUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .userDetailsService(staffUserDetailsService) // ✅ THIS IS THE KEY LINE
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/login-check",
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
                .loginProcessingUrl("/login-check")
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
            )
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // 🔹 SAME plain-text encoder (unchanged)
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
