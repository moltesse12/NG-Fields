package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateEquipmentRequest(
    @Size(max = 100) String brand,
    @Size(max = 100) String model,
    @Size(max = 100) String serial,
    @Size(max = 200) String location,
    @Size(max = 2000) String problemDescription,
    @Size(max = 50) String openprojectTicketId,
    @Size(max = 500) String openprojectTicketUrl
) {}
