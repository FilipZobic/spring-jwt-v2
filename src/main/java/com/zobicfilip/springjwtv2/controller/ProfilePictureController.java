package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.GoodResponseDTO;
import com.zobicfilip.springjwtv2.exception.FailedProfilePictureOperationException;
import com.zobicfilip.springjwtv2.model.MediaTypeWithMetadata;
import com.zobicfilip.springjwtv2.service.ProfileImageService;
import com.zobicfilip.springjwtv2.validation.UserExists;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ProfilePictureController {

    private final ProfileImageService profileImageService;

    /**
     *
     * @param file only allow png files conversion/compression should be done on client side to reduce load on server, only checks constraints
     * @param userId
     */
    @PreAuthorize("isSelf(#userId) || hasAnyAuthorityCustom('USER_**', 'USER_ALL_PROF_IMG_CRUD')")
    @PatchMapping(path = "/api/users/{userId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoodResponseDTO> profilePictureUpload(@RequestParam("image") MultipartFile file,
                                                                @UserExists @PathVariable UUID userId, HttpServletRequest request) throws FailedProfilePictureOperationException {
        log.info("Profile image received");
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.warn("Failed reading image");
            throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Invalid image");
        };
        boolean result = profileImageService.saveProfilePicture(bytes, userId, MediaTypeWithMetadata.PNG);
        if (result) {
            return ResponseEntity
                    .created(URI.create(request.getRequestURI().replace("upload","download")))
                    .body(new GoodResponseDTO("Image saved successfully", 201));
        }
        throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Failed saving image");
    }

    @PreAuthorize("hasAnyAuthorityCustom('USER_**', 'USER_ALL_SHARED_R' ,'USER_ALL_PROF_IMG_CRUD') || isSelf(#userId)")
    @GetMapping(path = "/api/users/{userId}/image/download",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> profilePictureDownload(@UserExists @PathVariable UUID userId) throws FailedProfilePictureOperationException {
        try {
            return ResponseEntity.ok(
                    profileImageService.getProfilePicture(userId, MediaTypeWithMetadata.PNG));
        } catch (FileNotFoundException e) {
            throw new FailedProfilePictureOperationException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }

    @PreAuthorize("isSelf(#userId) || hasAnyAuthorityCustom('USER_**', 'USER_ALL_PROF_IMG_CRUD')")
    @DeleteMapping(path = "/api/users/{userId}/image/delete")
    public ResponseEntity<GoodResponseDTO> profilePictureDelete(@UserExists @PathVariable UUID userId) throws FailedProfilePictureOperationException {
        try {
            profileImageService.deleteProfilePicture(userId, MediaTypeWithMetadata.PNG);
            return ResponseEntity.ok(new GoodResponseDTO("Image deleted successfully", 200));
        } catch (NoSuchFileException e) {
            throw new FailedProfilePictureOperationException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }
}
