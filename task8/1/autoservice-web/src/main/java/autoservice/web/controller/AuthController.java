package autoservice.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import autoservice.web.dto.ErrorResponseDto;
import autoservice.web.dto.LoginRequest;
import autoservice.web.dto.LoginResponse;
import autoservice.web.security.JwtUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Аутентификация: логин возвращает JWT (stateless).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("Укажите логин и пароль", "BAD_REQUEST", 400));
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            List<GrantedAuthority> authorities = auth.getAuthorities().stream().collect(Collectors.toList());
            String token = jwtUtils.generateToken(auth.getName(), authorities);
            String role = authorities.isEmpty() ? "USER" : authorities.get(0).getAuthority().replace("ROLE_", "");
            return ResponseEntity.ok(new LoginResponse(token, auth.getName(), role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(new ErrorResponseDto("Неверный логин или пароль", "UNAUTHORIZED", 401));
        }
    }
}
