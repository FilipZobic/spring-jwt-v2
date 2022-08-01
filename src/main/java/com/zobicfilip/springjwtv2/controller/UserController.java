package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

import static com.zobicfilip.springjwtv2.service.FileStorageService.getImageDimension;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final long maximumByteSizeConstraint = 2 * 1024L * 1024L;

    private final FileStorageService fileStorageService;

    /**
     *
     * @param file only allow png files conversion should be done on client side to reduce load on server
     * @param userId
     */
//    @PreAuthorize("isSelfUpdate(userId) || hasAnyAuthority('**', 'USER_**', 'USER_ALL_P_IMG')")
    @PatchMapping(path = "{userId}/image/upload",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> profilePictureUpload(@RequestParam("image") MultipartFile file,
                                               @PathVariable UUID userId) {
        log.info("Attempting profile image upload");

        // size check
        if (file.getSize() > maximumByteSizeConstraint) {
            log.warn("Image too large {}/{}", file.getSize(), maximumByteSizeConstraint);
            return ResponseEntity.badRequest().build();
        }

        // format & dimension check
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
            Dimension dimension = getImageDimension(bytes, FileStorageService.ImageType.PNG);
            if (dimension.width != 400 || dimension.height != 400) {
                log.warn("Image not in correct format expected: {}x{} actual: {}x{}",400, 400, dimension.width, dimension.height);
                return ResponseEntity.badRequest().build();
            }
        } catch (IOException e) {
            log.warn("Failed reading image");
            return ResponseEntity.badRequest().build();
        }
        fileStorageService.saveFile(bytes, FileStorageService.ImageType.PNG.toString(), userId.toString());
        return ResponseEntity.ok().build();
    }
}
