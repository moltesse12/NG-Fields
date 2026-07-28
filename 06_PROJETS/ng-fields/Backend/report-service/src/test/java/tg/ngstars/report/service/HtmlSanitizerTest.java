package tg.ngstars.report.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HtmlSanitizer")
class HtmlSanitizerTest {

    @Nested
    @DisplayName("sanitize(html)")
    class Sanitize {

        @Test
        @DisplayName("Supprime les balises script")
        void supprimeBalisesScript() {
            var html = "<p>Texte</p><script>alert('xss')</script>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les balises script self-closing")
        void supprimeBalisesScriptSelfClosing() {
            var html = "<p>Texte</p><script src='evil.js'/>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les balises iframe")
        void supprimeBalisesIframe() {
            var html = "<p>Texte</p><iframe src='evil.com'></iframe>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les balises object")
        void supprimeBalisesObject() {
            var html = "<p>Texte</p><object data='evil.swf'></object>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les balises embed")
        void supprimeBalisesEmbed() {
            var html = "<p>Texte</p><embed src='evil.swf'/>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les balises form")
        void supprimeBalisesForm() {
            var html = "<p>Texte</p><form action='evil.com'><input/></form>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les event handlers inline")
        void supprimeEventHandlers() {
            var html = "<img src='photo.jpg' onerror='alert(1)'>";
            assertEquals("<img src='photo.jpg'>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime le prefixe javascript: des URLs")
        void supprimePrefixeJavascript() {
            var html = "<a href='javascript:alert(1)'>Lien</a>";
            assertEquals("<a href='alert(1)'>Lien</a>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime le prefixe vbscript: des URLs")
        void supprimePrefixeVbscript() {
            var html = "<a href='vbscript:MsgBox(1)'>Lien</a>";
            assertEquals("<a href='MsgBox(1)'>Lien</a>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les data: URLs avec script")
        void supprimeDataUrlsScript() {
            var html = "<a href='data:text/script,alert(1)'>Lien</a>";
            assertEquals("<a href=',alert(1)'>Lien</a>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Supprime les expressions CSS")
        void supprimeExpressionsCSS() {
            var html = "<div style='background: expression(alert(1))'>Texte</div>";
            assertEquals("<div style='background: alert(1))'>Texte</div>", HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Conserve le HTML safe")
        void conserveHTMLSafe() {
            var html = "<h1>Titre</h1><p>Paragraphe avec <strong>gras</strong></p>";
            assertEquals(html, HtmlSanitizer.sanitize(html));
        }

        @Test
        @DisplayName("Retourne null si input null")
        void retourneNullSiNull() {
            assertNull(HtmlSanitizer.sanitize(null));
        }

        @Test
        @DisplayName("Retourne vide si input vide")
        void retourneVideSiVide() {
            assertEquals("", HtmlSanitizer.sanitize(""));
        }

        @Test
        @DisplayName("Retourne trimmed si input avec espaces")
        void retourneTrimmedSiEspaces() {
            assertEquals("Texte", HtmlSanitizer.sanitize("  Texte  "));
        }

        @Test
        @DisplayName("Gere les balises script en majuscules")
        void gereBalisesScriptMajuscules() {
            var html = "<p>Texte</p><SCRIPT>alert('xss')</SCRIPT>";
            assertEquals("<p>Texte</p>", HtmlSanitizer.sanitize(html));
        }
    }

    @Nested
    @DisplayName("sanitizePlainText(text)")
    class SanitizePlainText {

        @Test
        @DisplayName("Encode les balises < et >")
        void encodeBalises() {
            assertEquals("&lt;p&gt;Test&lt;/p&gt;", HtmlSanitizer.sanitizePlainText("<p>Test</p>"));
        }

        @Test
        @DisplayName("Retourne null si input null")
        void retourneNullSiNull() {
            assertNull(HtmlSanitizer.sanitizePlainText(null));
        }

        @Test
        @DisplayName("Retourne vide si input vide")
        void retourneVideSiVide() {
            assertEquals("", HtmlSanitizer.sanitizePlainText(""));
        }

        @Test
        @DisplayName("Texte sans balise est inchangé")
        void texteSansBaliseInchange() {
            assertEquals("Bonjour le monde", HtmlSanitizer.sanitizePlainText("Bonjour le monde"));
        }

        @Test
        @DisplayName("Trimme les espaces")
        void trimmeEspaces() {
            assertEquals("&lt;b&gt;Test&lt;/b&gt;", HtmlSanitizer.sanitizePlainText("  <b>Test</b>  "));
        }
    }
}
