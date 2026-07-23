package tg.ngstars.report.service;

import java.util.regex.Pattern;

public final class HtmlSanitizer {

    private static final Pattern SCRIPT_TAG = Pattern.compile(
            "<script\\b[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern SCRIPT_TAG_SELF = Pattern.compile(
            "<script\\b[^>]*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_HANDLER = Pattern.compile(
            "\\s+on\\w+\\s*=\\s*(?:\"[^\"]*\"|'[^']*'|\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_URL = Pattern.compile(
            "javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_URL = Pattern.compile(
            "vbscript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA_URL_SCRIPT = Pattern.compile(
            "data\\s*:[^,]*script", Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_EXPRESSION = Pattern.compile(
            "expression\\s*\\(", Pattern.CASE_INSENSITIVE);
    private static final Pattern IFRAME_TAG = Pattern.compile(
            "<iframe\\b[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern IFRAME_TAG_SELF = Pattern.compile(
            "<iframe\\b[^>]*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern OBJECT_TAG = Pattern.compile(
            "<object\\b[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EMBED_TAG = Pattern.compile(
            "<embed\\b[^>]*/?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern FORM_TAG = Pattern.compile(
            "<form\\b[^>]*>.*?</form>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private HtmlSanitizer() {}

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) return html;

        var clean = html;
        clean = SCRIPT_TAG.matcher(clean).replaceAll("");
        clean = SCRIPT_TAG_SELF.matcher(clean).replaceAll("");
        clean = IFRAME_TAG.matcher(clean).replaceAll("");
        clean = IFRAME_TAG_SELF.matcher(clean).replaceAll("");
        clean = OBJECT_TAG.matcher(clean).replaceAll("");
        clean = EMBED_TAG.matcher(clean).replaceAll("");
        clean = FORM_TAG.matcher(clean).replaceAll("");
        clean = EVENT_HANDLER.matcher(clean).replaceAll("");
        clean = JAVASCRIPT_URL.matcher(clean).replaceAll("");
        clean = VBSCRIPT_URL.matcher(clean).replaceAll("");
        clean = DATA_URL_SCRIPT.matcher(clean).replaceAll("");
        clean = STYLE_EXPRESSION.matcher(clean).replaceAll("");

        return clean.trim();
    }

    public static String sanitizePlainText(String text) {
        if (text == null || text.isBlank()) return text;
        return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;").trim();
    }
}
