package core.ecommerce.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import core.ecommerce.dto.CreateUserRequest;
import core.ecommerce.dto.UserResponse;
import core.ecommerce.entity.Role;
import core.ecommerce.entity.User;
import core.ecommerce.repository.RoleRepository;
import core.ecommerce.repository.UserRepository;
import core.ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import core.ecommerce.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditServiceImpl auditService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(role);

        userRepository.save(user);

        // Extrae el usuario autenticado (admin que crea)
        String performedBy = SecurityContextHolder.getContext().getAuthentication().getName();

        auditService.log("User", user.getId().toString(), "CREATE", performedBy,
                "Creación del usuario: " + user.getEmail());

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public UserResponse updateUser(Long id, UserResponse userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        userRepository.save(user);
        auditService.log("Usuario actualizado: " + user.getName(), user.getId().toString(), "UPDATE", "SYSTEM",
                "Detalles de la actualización");
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        userRepository.delete(user);
        auditService.log("Usuario eliminado: " + user.getName(), user.getId().toString(), "DELETE", "SYSTEM",
                "Detalles de la eliminación");
    }
}
