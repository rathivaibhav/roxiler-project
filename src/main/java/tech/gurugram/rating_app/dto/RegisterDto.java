package tech.gurugram.rating_app.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @NoArgsConstructor
public class RegisterDto {
    @NotBlank @Size(min=2, max=60)
    private String name;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min=8, max=16)
    private String password;

    @Size(max=400)
    private String address;
}
