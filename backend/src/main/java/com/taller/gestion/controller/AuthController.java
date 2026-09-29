package com.taller.gestion.controller;

import com.taller.gestion.domain.Role;
import com.taller.gestion.facade.AuthFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/** Adaptador REST de autenticación; delega persistencia y reglas de identidad a AuthFacade. */
@RestController @RequestMapping("/api/auth") @CrossOrigin(origins="*")
public class AuthController {
  private final AuthFacade auth;
  public AuthController(AuthFacade auth) { this.auth = auth; }
  /** Alta inicial de usuario. */
  @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody Registration r) { return auth.register(r.name(), r.email(), r.password(), r.role()); }
  /** Solicita recuperación sin revelar si el correo existe. */
  @PostMapping("/recovery") public Map<String, String> requestRecovery(@Valid @RequestBody RecoveryRequest r) { auth.requestRecovery(r.email()); return Map.of("message", "Si el correo existe, recibirás instrucciones para recuperar la cuenta."); }
  /** Restablece contraseña con token no vencido. */
  @PostMapping("/reset-password") public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPassword r) { return auth.resetPassword(r.token(), r.newPassword()) ? ResponseEntity.ok(Map.of("message", "Contraseña actualizada")) : ResponseEntity.badRequest().body(Map.of("message", "Token inválido o vencido")); }
  /** Devuelve usuario y roles de la sesión Basic para que Vue aplique el control de la vista. */
  @GetMapping("/me") public Map<String, Object> me(Authentication authentication) { return Map.of("email", authentication.getName(), "roles", authentication.getAuthorities().stream().map(x -> x.getAuthority().replace("ROLE_", "")).toList()); }
  public record Registration(@NotBlank String name, @Email String email, @Size(min=12,max=72) String password, Role role) {}
  public record RecoveryRequest(@Email String email) {}
  public record ResetPassword(@NotBlank String token, @Size(min=12,max=72) String newPassword) {}
}
