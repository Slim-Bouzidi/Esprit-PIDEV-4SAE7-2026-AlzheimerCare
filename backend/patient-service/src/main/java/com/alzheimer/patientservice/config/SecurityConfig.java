package com.alzheimer.patientservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/patients/me").authenticated()
                .antMatchers(HttpMethod.POST, "/api/patients/me").authenticated()
                .antMatchers(HttpMethod.POST, "/api/patients").hasRole("admin")
                .antMatchers(HttpMethod.PUT, "/api/patients/**").hasRole("admin")
                .antMatchers(HttpMethod.DELETE, "/api/patients/**").hasRole("admin")
                .antMatchers(HttpMethod.GET, "/api/patients/**").hasRole("admin")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }
}
