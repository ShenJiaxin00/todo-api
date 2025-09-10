package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 关闭 CSRF，方便我们用 Postman/curl 测试
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()   // 放行注册和登录接口
                        .anyRequest().permitAll()                      // 其他接口也先放行
                )
                .httpBasic(Customizer.withDefaults());             // 启用基本认证（调试用）
        return http.build();
    }
}
