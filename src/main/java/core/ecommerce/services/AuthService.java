package core.ecommerce.services;

import core.ecommerce.dto.AuthResponse;
import core.ecommerce.dto.LoginRequest;
import core.ecommerce.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
