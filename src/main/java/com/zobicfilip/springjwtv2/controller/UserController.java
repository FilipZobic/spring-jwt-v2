package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.GoodResponseDTO;
import com.zobicfilip.springjwtv2.exception.FailedProfilePictureOperationException;
import com.zobicfilip.springjwtv2.service.FileStorageService;
import com.zobicfilip.springjwtv2.validation.UserExists;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.util.UUID;

import static com.zobicfilip.springjwtv2.service.FileStorageService.getImageDimension;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final long maximumByteSizeConstraint = 2 * 1024L * 1024L;

    private final FileStorageService fileStorageService;

    /**
     *
     * @param file only allow png files conversion/compression should be done on client side to reduce load on server
     * @param userId
     */
//    @PreAuthorize("isSelfUpdate(userId) || hasAnyAuthority('**', 'USER_**', 'USER_ALL_P_IMG')")
    @PatchMapping(path = "{userId}/image/upload",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoodResponseDTO> profilePictureUpload(@RequestParam("image") MultipartFile file,
                                                                @UserExists @PathVariable UUID userId, HttpServletRequest request) throws FailedProfilePictureOperationException {
        log.info("Profile image received");

        // size check
        if (file.getSize() > maximumByteSizeConstraint) {
            log.warn("Image too large {}/{}", file.getSize(), maximumByteSizeConstraint);
            throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Invalid image");
        }

        // format & dimension check
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
            Dimension dimension = getImageDimension(bytes, FileStorageService.ImageType.PNG);
            if (dimension.width != 400 || dimension.height != 400) {
                log.warn("Image not in correct format expected: {}x{} actual: {}x{}",400, 400, dimension.width, dimension.height);
                throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Invalid image");
            }
        } catch (IOException e) {
            log.warn("Failed reading image");
            throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Invalid image");
        }
        log.info("Image passed all checks");
        boolean result = fileStorageService.saveFile(bytes, FileStorageService.ImageType.PNG.toString(), userId.toString());
        if (result) {
            return ResponseEntity
                    .created(URI.create(request.getRequestURI().replace("upload","download")))
                    .body(new GoodResponseDTO("Image saved successfully", 201));
        }
        throw new FailedProfilePictureOperationException(HttpStatus.BAD_REQUEST, "Failed saving image");
    }

//    @PreAuthorize()
    @GetMapping(path = "{userId}/image/download",
    produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> profilePictureDownload(@UserExists @PathVariable UUID userId) throws FailedProfilePictureOperationException {
        String fileName = userId.toString() + ".png";
        try {
            log.info("Downloading image: {}", fileName);
            return ResponseEntity.ok(
                    fileStorageService.getFile(fileName)
            );
        } catch (FileNotFoundException  e) {
            throw new FailedProfilePictureOperationException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }

    //    @PreAuthorize()
    @DeleteMapping(path = "{userId}/image/delete")
    public ResponseEntity<GoodResponseDTO> profilePictureDelete(@UserExists @PathVariable UUID userId) throws FailedProfilePictureOperationException {
        String fileName = userId.toString() + ".png";
        try {
            fileStorageService.deleteFile(fileName);
            return ResponseEntity.ok(new GoodResponseDTO("Image deleted successfully", 200));
        } catch (NoSuchFileException e) {
            throw new FailedProfilePictureOperationException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }
}
