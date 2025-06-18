package core.ecommerce.services;

import core.ecommerce.dto.CreateUserRequest;
import core.ecommerce.dto.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse createUser(CreateUserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserResponse user);

    void deleteUser(Long id);
}
