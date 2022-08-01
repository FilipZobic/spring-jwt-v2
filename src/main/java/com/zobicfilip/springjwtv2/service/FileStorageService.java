package com.zobicfilip.springjwtv2.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public abstract class FileStorageService {

    public enum ImageType {
        PNG
    }

    @Getter
    @Builder
    @AllArgsConstructor
    protected static class FileImageMetadata {
        private final int[] fileSignatureBytes;
        private final ImageType type;
        private final Pair<Integer, Integer> fileSignatureLocation;
        private final Pair<Integer, Integer> widthLocation;
        private final Pair<Integer, Integer> heightLocation;
        private final String contentHeaderName;
        private final Pair<Integer, Integer> contentHeaderLocation;
        private final int minimumRequiredBytes;
    }

    protected static final Map<ImageType, FileImageMetadata> IMAGE_IMAGE_BYTES_MAP = Map.of(
            ImageType.PNG, FileImageMetadata.builder()
                    .contentHeaderName("IHDR")
                    .minimumRequiredBytes(25)
                    .fileSignatureBytes(new int[]{137, 80, 78, 71, 13, 10, 26, 10})
                    .type(ImageType.PNG)
                    .fileSignatureLocation(Pair.of(0, 8))
                    .widthLocation(Pair.of(16, 20))
                    .heightLocation(Pair.of(20, 24))
                    .contentHeaderLocation(Pair.of(12,16))
                .build()
    );


    /**
     *
     * @param bytes image bytes
     * @return Dimension of image
     * @throws IOException if image not one of supported files (png)
     */
    public static Dimension getImageDimension(byte[] bytes, ImageType type) throws IOException {
        FileImageMetadata metadata = IMAGE_IMAGE_BYTES_MAP.get(type);
        if (bytes == null || metadata.getMinimumRequiredBytes() > bytes.length) {
            log.warn("Invalid file");
            throw new IOException();
        }
        // Check file type bytes
        Pair<Integer, Integer> sigLoc = metadata.getFileSignatureLocation();
        byte[] rawSignature = ArrayUtils.subarray(bytes, sigLoc.getLeft(), sigLoc.getRight());
        int[] expectedSignature = metadata.getFileSignatureBytes();
        for (int i=0; i<rawSignature.length; i++) {
            if ((rawSignature[i]& 0xff) != expectedSignature[i]) {
                log.warn("Image file type signature mismatch expected: {} actual: {}",
                        Arrays.toString(expectedSignature),
                        Arrays.toString(rawSignature));
                throw new IOException();
            }
        }
        // Check header signature text
        Pair<Integer, Integer> headLoc = metadata.getContentHeaderLocation();
        String fileHeaderName = new String(ArrayUtils.subarray(bytes, headLoc.getLeft(), headLoc.getRight()));
        if (!fileHeaderName.equalsIgnoreCase(metadata.getContentHeaderName())) {
            log.warn("Mismatch header names expected: {} actual: {}", metadata.getContentHeaderName(), fileHeaderName);
            throw new IOException();
        }

        Pair<Integer, Integer> widthLoc = metadata.getWidthLocation();
        int width = ByteBuffer.wrap(
                ArrayUtils.subarray(bytes, widthLoc.getLeft(), widthLoc.getRight())
        ).getInt();

        Pair<Integer, Integer> heightLoc = metadata.getHeightLocation();
        int height = ByteBuffer.wrap(
                ArrayUtils.subarray(bytes, heightLoc.getLeft(), heightLoc.getRight())
        ).getInt();
        return new Dimension(width, height);
    }

    public abstract boolean saveFile(byte[] content, String type, String name);
    public abstract byte[] loadFile(String name);
    public abstract boolean getFile(boolean name);
}
