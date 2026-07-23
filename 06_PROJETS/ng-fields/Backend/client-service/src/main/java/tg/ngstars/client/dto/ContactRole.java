package tg.ngstars.client.dto;

public enum ContactRole {
    DECISION_MAKER,
    TECHNICAL,
    ADMINISTRATIVE,
    PURCHASER,
    FINANCIAL,
    LEGAL,
    OTHER;

    public static ContactRole fromString(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role invalide : " + value + ". Valeurs acceptees : DECISION_MAKER, TECHNICAL, ADMINISTRATIVE, PURCHASER, FINANCIAL, LEGAL, OTHER");
        }
    }
}
