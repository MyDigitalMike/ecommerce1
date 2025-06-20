package core.ecommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import core.ecommerce.dto.CreateUserRequest;
import core.ecommerce.dto.UserResponse;
import core.ecommerce.entity.Role;
import core.ecommerce.entity.User;
import core.ecommerce.exception.ResourceNotFoundException;
import core.ecommerce.repository.RoleRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.impl.AuditServiceImpl;
import core.ecommerce.services.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private AuditServiceImpl auditService;

    @InjectMocks
    private UserServiceImpl userService;

    private Role role;
    private User user;

    @BeforeEach
    void setup() {
        role = new Role(1L, "CLIENT");
        user = new User(1L, "Mike", "1231231", "mike@correo.com", role);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Mike", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(1L);

        assertEquals("Mike", result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowIfNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void createUser_shouldSaveUser() {
        CreateUserRequest request = new CreateUserRequest("Mike", "Mike@correo.com", "CLIENT");
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken("adminUser", null)));

        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(role));
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Mike");
        savedUser.setEmail("Mike@correo.com");
        savedUser.setRole(role);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserResponse result = userService.createUser(request);

        assertEquals("Mike", result.getName()); // Se devuelve el user simulado
        verify(userRepository).save(any(User.class));
        verify(auditService).log(eq("User"), any(), eq("CREATE"), any(), contains("Creación"));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        UserResponse request = new UserResponse(1L, "Mike Actualizado", "mike@correo.com", role);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User savedUser = new User();
        savedUser.setId(1L); // <-- simula la DB
        savedUser.setName("Ana");
        savedUser.setEmail("ana@correo.com");
        savedUser.setRole(role);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        UserResponse result = userService.updateUser(1L, request);

        assertEquals("Mike Actualizado", result.getName());
        verify(auditService).log(contains("actualizado"), eq("1"), eq("UPDATE"), eq("SYSTEM"), any());
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
        verify(auditService).log(contains("eliminado"), eq("1"), eq("DELETE"), eq("SYSTEM"), any());
    }
}
