package com.taller.gestion.repository;
import com.taller.gestion.domain.User; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long>{ Optional<User> findByEmail(String email); Optional<User> findByRecoveryToken(String token); }
