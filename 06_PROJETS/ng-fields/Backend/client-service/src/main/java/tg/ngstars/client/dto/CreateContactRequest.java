package tg.ngstars.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateContactRequest(
    @NotBlank @Size(max = 150) String fullName,
    @Email @Size(max = 150) String email,
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Numero de telephone invalide (format E.164 : +22890123456)")
    String phone,
    @Size(max = 50) String role
) {}
