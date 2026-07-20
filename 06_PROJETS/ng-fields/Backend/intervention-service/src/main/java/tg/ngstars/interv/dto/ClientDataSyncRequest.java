package tg.ngstars.interv.dto;

import java.util.UUID;

public record ClientDataSyncRequest(
    UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress
) {}
