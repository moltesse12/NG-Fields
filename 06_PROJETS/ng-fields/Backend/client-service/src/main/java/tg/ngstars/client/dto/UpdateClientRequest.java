package tg.ngstars.client.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
    @NotBlank @Size(max = 200) String companyName,
    @Size(max = 150) String contactName,
    @NotBlank @Email @Size(max = 150) String email,
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Numero de telephone invalide (format E.164 : +22890123456)")
    String phone,
    String address,
    @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {}
