package com.taller.gestion.facade;

import com.taller.gestion.domain.*;
import com.taller.gestion.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/** Fachada de identidad: desacopla los controladores REST de la persistencia de usuarios y contraseñas. */
@Service
public class AuthFacade {
  private final UserRepository users;
  private final PasswordEncoder passwords;
  public AuthFacade(UserRepository users, PasswordEncoder passwords) { this.users = users; this.passwords = passwords; }

  /** Crea un usuario con contraseña BCrypt si el correo no está ocupado. */
  public ResponseEntity<?> register(String name, String email, String password, Role role) {
    if (users.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "El correo ya está registrado"));
    User user = new User(); user.setName(name); user.setEmail(email.toLowerCase(Locale.ROOT)); user.setPasswordHash(passwords.encode(password)); user.setRoles(Set.of(role == null ? Role.CLIENTE : role)); users.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Usuario creado"));
  }
  /** Genera y persiste un token BCrypt de un solo uso; el envío de correo queda fuera de la fase actual. */
  public void requestRecovery(String email) { users.findByEmail(email.toLowerCase(Locale.ROOT)).ifPresent(user -> { user.setRecoveryToken(passwords.encode(randomToken())); user.setRecoveryExpiresAt(LocalDateTime.now().plusMinutes(15)); users.save(user); }); }
  /** Actualiza la contraseña si encuentra un token válido y elimina dicho token al usarlo. */
  public boolean resetPassword(String token, String newPassword) { return users.findAll().stream().filter(user -> user.getRecoveryToken() != null && user.getRecoveryExpiresAt() != null && user.getRecoveryExpiresAt().isAfter(LocalDateTime.now()) && passwords.matches(token, user.getRecoveryToken())).findFirst().map(user -> { user.setPasswordHash(passwords.encode(newPassword)); user.setRecoveryToken(null); user.setRecoveryExpiresAt(null); users.save(user); return true; }).orElse(false); }
  private String randomToken() { byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
}
