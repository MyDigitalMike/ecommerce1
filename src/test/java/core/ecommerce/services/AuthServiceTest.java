package core.ecommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import core.ecommerce.dto.AuthResponse;
import core.ecommerce.dto.RegisterRequest;
import core.ecommerce.entity.Role;
import core.ecommerce.entity.User;
import core.ecommerce.repository.RoleRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.impl.AuthServiceImpl;
import core.ecommerce.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtService;

    @Test
    void testRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest("Mike", "test@example.com", "123456", "CLIENT");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(new Role(1L, "CLIENT")));
        when(passwordEncoder.encode("123456")).thenReturn("hashedPwd");
        when(jwtService.generateToken(any(User.class))).thenReturn("mockedJwt");

        AuthResponse response = authService.register(request);

        assertEquals("mockedJwt", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterFailsIfUserExists() {
        RegisterRequest request = new RegisterRequest("Mike", "existing@example.com", "123456", "CLIENT");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
