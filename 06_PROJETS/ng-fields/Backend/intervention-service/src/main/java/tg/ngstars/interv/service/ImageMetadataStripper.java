package tg.ngstars.interv.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public final class ImageMetadataStripper {

    private static final Logger log = LoggerFactory.getLogger(ImageMetadataStripper.class);

    private ImageMetadataStripper() {}

    public static MultipartFile stripMetadata(MultipartFile original) throws IOException {
        String contentType = original.getContentType();
        if (contentType == null) return original;

        String formatName = switch (contentType.toLowerCase()) {
            case "image/jpeg" -> "jpeg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> null;
        };

        if (formatName == null) return original;

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(original.getBytes()));
            if (image == null) {
                log.warn("Impossible de lire l'image pour strip metadata: {}", original.getOriginalFilename());
                return original;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, formatName, baos);

            byte[] stripped = baos.toByteArray();
            log.info("Metadata strippée: {} (avant={} octets, après={} octets)",
                    original.getOriginalFilename(), original.getSize(), stripped.length);

            return new StrippedMultipartFile(original, stripped, contentType);
        } catch (Exception e) {
            log.warn("Échec strip metadata pour {}: {}. Upload original conservé.",
                    original.getOriginalFilename(), e.getMessage());
            return original;
        }
    }

    private record StrippedMultipartFile(
            MultipartFile delegate,
            byte[] content,
            String contentType) implements MultipartFile {

        @Override
        public String getName() { return delegate.getName(); }

        @Override
        public String getOriginalFilename() { return delegate.getOriginalFilename(); }

        @Override
        public String getContentType() { return contentType; }

        @Override
        public boolean isEmpty() { return content.length == 0; }

        @Override
        public long getSize() { return content.length; }

        @Override
        public byte[] getBytes() { return content; }

        @Override
        public ByteArrayInputStream getInputStream() { return new ByteArrayInputStream(content); }

        @Override
        public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}
