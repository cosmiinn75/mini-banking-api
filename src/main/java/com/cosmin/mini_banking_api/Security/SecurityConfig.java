package com.cosmin.mini_banking_api.Security;

import com.cosmin.mini_banking_api.Enum.Permissions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    public SecurityConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // AUTH
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // ACCOUNTS
                        .requestMatchers(HttpMethod.POST, "/api/accounts")
                        .hasAuthority(Permissions.MAKE_ACCOUNTS.name())

                        .requestMatchers(HttpMethod.GET, "/api/accounts")
                        .hasAuthority(Permissions.READ_ACCOUNTS.name())

                        .requestMatchers(HttpMethod.GET, "/api/accounts/{account_number}")
                        .hasAuthority(Permissions.READ_ACCOUNTS.name())

                        // TRANSACTIONS
                        .requestMatchers(HttpMethod.GET, "/api/accounts/{account_number}/transactions")
                        .hasAuthority(Permissions.READ_TRANSACTIONS.name())

                        .requestMatchers(HttpMethod.POST, "/api/accounts/{account_number}/deposit")
                        .hasAuthority(Permissions.MAKE_TRANSACTIONS.name())

                        .requestMatchers(HttpMethod.POST, "/api/accounts/{account_number}/withdraw")
                        .hasAuthority(Permissions.MAKE_TRANSACTIONS.name())

                        .requestMatchers(HttpMethod.POST, "/api/accounts/{account_number}/transfer")
                        .hasAuthority(Permissions.MAKE_TRANSACTIONS.name())


                        .requestMatchers(HttpMethod.GET,"/api/admin/users")
                        .hasAuthority(Permissions.READ_ALL_USERS.name())

                        .requestMatchers(HttpMethod.GET, "/api/admin/accounts")
                        .hasAuthority(Permissions.READ_ALL_USERS_ACCOUNTS.name())

                        .requestMatchers(HttpMethod.PUT, "/api/admin/users/{userId}/role")
                        .hasAuthority(Permissions.UPDATE_ROLE.name())


                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}