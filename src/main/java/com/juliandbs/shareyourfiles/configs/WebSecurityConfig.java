package com.juliandbs.shareyourfiles.configs;

import com.juliandbs.shareyourfiles.persistence.token.filter.RequestAuthenticationFilter;
import com.juliandbs.shareyourfiles.persistence.token.filter.TokenAuthenticationFilter;
import com.juliandbs.shareyourfiles.persistence.token.handler.LogoutHandler;
import com.juliandbs.shareyourfiles.persistence.token.service.TokenService;
import com.juliandbs.shareyourfiles.security.UserDetailsServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

/**
 * This class set the Spring Security configuration.
 * @author JulianDbs
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String STYLE_CSS_PATH = "/styles/style.css";
    private static final String CUSTOM_FONT_PATH = "/fonts/Orbitron/Orbitron-Regular.ttf";
    private static final String FAV_ICON_PATH = "/icons/favicon.png";
    private static final String HOME_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTRATION_PATH = "/registration";

    private final UserDetailsServiceImpl userDetailsService;

    private final TokenService tokenService;

    public WebSecurityConfig(final UserDetailsServiceImpl userDetailsService, final TokenService tokenService) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(this.tokenService, this.userDetailsService);
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return new LogoutHandler(this.tokenService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.cors().and().csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( requests -> requests
                    .requestMatchers(HOME_PATH, LOGIN_PATH, REGISTRATION_PATH, STYLE_CSS_PATH, CUSTOM_FONT_PATH, FAV_ICON_PATH).permitAll()
                    .anyRequest().authenticated()
                )
                .addFilterBefore(new RequestAuthenticationFilter(authenticationManager, this.tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .formLogin( form -> form.loginPage("/login")
                                .usernameParameter("email")
                                .defaultSuccessUrl("/desktop", true).permitAll())
                .authenticationManager(authenticationManager)
                .logout( logout -> logout
                        .addLogoutHandler(logoutHandler())
                        .logoutSuccessUrl("/")
                );
        return http.build();
    }

}
