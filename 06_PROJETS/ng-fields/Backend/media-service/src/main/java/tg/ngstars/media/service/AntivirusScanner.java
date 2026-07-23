package tg.ngstars.media.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tg.ngstars.media.config.MediaProperties;

@Component
public class AntivirusScanner {

    private static final Logger log = LoggerFactory.getLogger(AntivirusScanner.class);

    private final MediaProperties properties;

    public AntivirusScanner(MediaProperties properties) {
        this.properties = properties;
    }

    public void scan(Path file) throws IOException {
        if (!properties.antivirusEnabled()) {
            log.debug("Antivirus désactivé, scan ignoré pour {}", file.getFileName());
            return;
        }

        var host = properties.clamavHost();
        var port = properties.clamavPort();

        log.debug("Scan antivirus via ClamAV {}:{} pour {}", host, port, file.getFileName());

        try (var socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            out.write("zINSTREAM\0".getBytes());
            out.flush();

            var bytes = java.nio.file.Files.readAllBytes(file);
            out.write(intToBytes(bytes.length));
            out.write(bytes);
            out.write(intToBytes(0));
            out.flush();

            var response = new String(in.readAllBytes());
            if (response.contains("FOUND")) {
                throw new SecurityException(
                        "Fichier contaminé détecté par ClamAV: " + file.getFileName() + " — " + response.trim());
            }

            log.info("Scan antivirus OK: {}", file.getFileName());
        } catch (java.net.ConnectException e) {
            log.warn("ClamAV non disponible ({}:{}): {}. Scan ignoré.", host, port, e.getMessage());
        } catch (SecurityException e) {
            throw e;
        } catch (IOException e) {
            log.warn("Erreur communication ClamAV: {}. Scan ignoré.", e.getMessage());
        }
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }
}
