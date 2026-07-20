package tg.ngstars.interv.dto;

public record SignatureResponse(
    String message,
    String url
) {
    public static SignatureResponse created(String message, String url) {
        return new SignatureResponse(message, url);
    }
}
