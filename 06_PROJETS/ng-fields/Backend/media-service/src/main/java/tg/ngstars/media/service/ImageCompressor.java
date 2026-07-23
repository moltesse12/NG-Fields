package tg.ngstars.media.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tg.ngstars.media.config.MediaProperties;

@Component
public class ImageCompressor {

    private static final Logger log = LoggerFactory.getLogger(ImageCompressor.class);

    private final MediaProperties properties;

    public ImageCompressor(MediaProperties properties) {
        this.properties = properties;
    }

    public MultipartFile compressIfNeeded(MultipartFile file) throws IOException {
        if (file.getSize() < 500_000) return file;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return file;
        if ("image/gif".equals(contentType)) return file;

        String formatName = switch (contentType.toLowerCase()) {
            case "image/jpeg" -> "jpeg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> null;
        };

        if (formatName == null) return file;

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            if (image == null) return file;

            if ("jpeg".equals(formatName) || "webp".equals(formatName)) {
                return compressJpeg(image, file, formatName);
            }

            return file;
        } catch (Exception e) {
            log.warn("Échec compression image {}: {}", file.getOriginalFilename(), e.getMessage());
            return file;
        }
    }

    private MultipartFile compressJpeg(BufferedImage image, MultipartFile original, String formatName) throws IOException {
        var quality = properties.imageCompressionQuality() / 100.0f;
        var baos = new ByteArrayOutputStream();

        var writers = ImageIO.getImageWritersByFormatName(formatName);
        if (!writers.hasNext()) return original;

        var writer = writers.next();
        var param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }

        byte[] compressed = baos.toByteArray();
        if (compressed.length >= original.getSize()) return original;

        log.info("Image compressée: {} (avant={} octets, après={} octets, qualité={})",
                original.getOriginalFilename(), original.getSize(), compressed.length,
                properties.imageCompressionQuality());

        return new CompressedMultipartFile(original, compressed, original.getContentType());
    }

    private record CompressedMultipartFile(
            MultipartFile delegate,
            byte[] content,
            String contentType) implements MultipartFile {

        @Override public String getName() { return delegate.getName(); }
        @Override public String getOriginalFilename() { return delegate.getOriginalFilename(); }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public ByteArrayInputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(java.io.File dest) throws IOException { java.nio.file.Files.write(dest.toPath(), content); }
    }
}
