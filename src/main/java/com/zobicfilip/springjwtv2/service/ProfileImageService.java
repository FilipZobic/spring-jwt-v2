package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.MediaTypeWithMetadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.UUID;

public interface ProfileImageService {

    default boolean validate(byte[] bytes) {
        try {
            String typeString = getImageType(bytes);
            return validate(bytes, MediaTypeWithMetadata.parseFromMimeCache(typeString));
        } catch (IOException e) {
            return false;
        }
    }

    boolean validate(byte[] bytes, MediaTypeWithMetadata mediaType);

    String getImageType(byte[] bytes) throws IOException;

    boolean deleteProfilePicture(UUID userId, MediaTypeWithMetadata mediaType) throws NoSuchFileException;

    byte[] getProfilePicture(UUID userId, MediaTypeWithMetadata mediaType) throws FileNotFoundException;

    boolean saveProfilePicture(byte[] content, UUID userId, MediaTypeWithMetadata mediaType);
}
