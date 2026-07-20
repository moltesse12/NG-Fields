package tg.ngstars.interv.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateBillingRequest(
    boolean billable,
    @Positive BigDecimal billingAmount,
    @Size(max = 1000) String billingNotes
) {}
