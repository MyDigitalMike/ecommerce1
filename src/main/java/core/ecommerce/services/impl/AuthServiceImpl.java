package core.ecommerce.services.impl;

import core.ecommerce.dto.AuthResponse;
import core.ecommerce.dto.LoginRequest;
import core.ecommerce.dto.RegisterRequest;
import core.ecommerce.entity.Role;
import core.ecommerce.entity.User;
import core.ecommerce.repository.RoleRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.AuthService;
import core.ecommerce.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthResponse register(RegisterRequest request) {
        var user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rol inválido."));
        user.setRole(role);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, role.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        authenticationManager.authenticate(authToken);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getRole().getName(), user.getEmail());
    }
}
