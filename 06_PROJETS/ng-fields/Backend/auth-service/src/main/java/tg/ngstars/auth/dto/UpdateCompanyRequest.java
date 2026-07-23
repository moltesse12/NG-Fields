package tg.ngstars.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
    @Size(max = 200) String name,
    @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    String address,
    @Size(max = 150) String contactName,
    @Size(max = 30) String contactPhone
) {}
