package core.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "El correo no es válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
