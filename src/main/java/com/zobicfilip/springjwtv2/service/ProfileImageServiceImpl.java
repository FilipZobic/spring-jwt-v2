package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.MediaTypeWithMetadata;
import com.zobicfilip.springjwtv2.model.ProfileImageConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class ProfileImageServiceImpl implements ProfileImageService {

    private final FileStorageService fileStorageService;
    private final Set<Integer> allowedWidths;
    private final Set<Integer> allowedHeights;

    private final long maxSize;

    public ProfileImageServiceImpl(ProfileImageConfiguration configuration, FileStorageService fileStorageService) {
        this.allowedWidths = configuration.allowedWidths();
        this.allowedHeights = configuration.allowedHeights();
        this.maxSize = configuration.maxSize() * 1024L;
        this.fileStorageService = fileStorageService;
    }
    private boolean validateSize(final byte[] bytes)  {
        assert bytes != null;
        if (maxSize < bytes.length) {
            log.warn("Profile picture violates size constraint");
        };
        return true;
    }
    private boolean validateFileFormat(final byte[] bytes, final MediaTypeWithMetadata mediaType) {
        assert bytes != null;
        assert mediaType != null;
        if (mediaType.getMinimumRequiredBytes() > bytes.length) {
            log.warn("Does not have minimum required bytes");
            return false;
        }

        byte[] rawSignature = ArrayUtils.subarray(bytes, mediaType.getFileSignatureStart(), mediaType.getFileSignatureEnd());
        for (int i=0; i<rawSignature.length; i++) {
            if ((rawSignature[i]& 0xff) != mediaType.getFileSignatureBytes()[i]) {
                log.warn("Image file type signature mismatch expected: {} actual: {}",
                        Arrays.toString(mediaType.getFileSignatureBytes()),
                        Arrays.toString(rawSignature));
                return false;
            }
        }

        String fileHeaderName = new String(ArrayUtils.subarray(bytes, mediaType.getContentHeaderStart(), mediaType.getContentHeaderEnd()));
        if (!fileHeaderName.equalsIgnoreCase(mediaType.getContentHeaderName())) {
            log.warn("Mismatch header names expected: {} actual: {}", mediaType.getContentHeaderName(), fileHeaderName);
            return false;
        }
        return true;
    }
    private boolean validateDimensions(final Dimension dimension) {
        assert dimension != null;
        if (allowedWidths.contains(dimension.width) && allowedHeights.contains(dimension.height)) {
            return true;
        }
        log.warn("Dimension constraint violated {}x{}", dimension.width, dimension.height);
        return false;
    }
    private Dimension getImageDimension(final byte[] bytes, final MediaTypeWithMetadata mediaType) {
        assert bytes != null;
        assert mediaType != null;
        assert this.validateFileFormat(bytes, mediaType); // asserts currently only in private methods

        int width = getIntFromBytes(bytes, mediaType.getWidthStart(), mediaType.getWidthEnd());
        int height = getIntFromBytes(bytes, mediaType.getHeightStart(), mediaType.getHeightEnd());

        return new Dimension(width,height);
    }

    private int getIntFromBytes(final byte[] bytes, int from, int to) {
        assert bytes != null;
        return ByteBuffer.wrap(
                ArrayUtils.subarray(bytes, from, to)
        ).getInt();
    }

    private String createFileName(UUID userId, MediaTypeWithMetadata mediaType) {
        assert userId != null;
        assert mediaType != null;
        return new StringBuilder(userId.toString())
                .append(".")
                .append(mediaType.getFileExtension()).toString();
    }

    @Override
    public boolean deleteProfilePicture(UUID userId, MediaTypeWithMetadata mediaType) throws NoSuchFileException {
        return this.fileStorageService.deleteFile(createFileName(userId, mediaType));
    }

    @Override
    public byte[] getProfilePicture(UUID userId, MediaTypeWithMetadata mediaType) throws FileNotFoundException {
        String fileName = createFileName(userId, mediaType);
        log.info("Downloading image: {}", fileName);
        return this.fileStorageService.getFile(fileName);
    }

    @Override
    public boolean saveProfilePicture(byte[] content, UUID userId, MediaTypeWithMetadata mediaType) {
        if (!validate(content, mediaType)) {
            return false;
        }
        log.info("Image passed all checks");
        return this.fileStorageService.saveFile(content, mediaType.getFileExtension(), userId.toString());
    }

    @Override
    public boolean validate(byte[] bytes, final MediaTypeWithMetadata mediaType) {
        return validateSize(bytes) && validateFileFormat(bytes, mediaType) && validateDimensions(getImageDimension(bytes, mediaType));
    }

    @Override
    public String getImageType(byte[] bytes) throws IOException {
        throw new UnsupportedOperationException();
    }
}
