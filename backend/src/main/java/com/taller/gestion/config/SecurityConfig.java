package com.taller.gestion.config;
import com.taller.gestion.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Configuración de identidad HTTP Basic y autorización por rol de la fase inicial. */
@Configuration @EnableWebSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean UserDetailsService users(UserRepository users) { return email -> { var user = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email)); String[] roles = user.getRoles().stream().map(role -> "ROLE_" + role.name()).toArray(String[]::new); return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPasswordHash()).authorities(roles).build(); }; }
  @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()).cors(cors -> {}).authorizeHttpRequests(requests -> requests
      .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/recovery", "/api/auth/reset-password").permitAll()
      .requestMatchers("/actuator/health").permitAll()
      .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
      .requestMatchers("/api/clients/**").hasAnyRole("ADMINISTRADOR", "SECRETARIA", "RECEPCIONISTA")
      .requestMatchers("/api/orders/**", "/api/parts/**").hasAnyRole("ADMINISTRADOR", "SECRETARIA", "RECEPCIONISTA", "MECANICO")
      .anyRequest().authenticated()).httpBasic(basic -> {}).build();
  }
}
