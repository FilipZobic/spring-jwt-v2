package com.zobicfilip.springjwtv2.controller;

import com.zobicfilip.springjwtv2.dto.GoodResponseDTO;
import com.zobicfilip.springjwtv2.dto.UserPaginationDTO;
import com.zobicfilip.springjwtv2.exception.FailedProfilePictureOperationException;
import com.zobicfilip.springjwtv2.model.*;
import com.zobicfilip.springjwtv2.service.FileStorageService;
import com.zobicfilip.springjwtv2.service.UserService;
import com.zobicfilip.springjwtv2.validation.CountryCode;
import com.zobicfilip.springjwtv2.validation.UserExists;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.zobicfilip.springjwtv2.service.FileStorageService.getImageDimension;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final long maximumByteSizeConstraint = 2 * 1024L * 1024L;

    private final FileStorageService fileStorageService;

    private final UserService userService;

    /**
     *
     * @param file only allow png files conversion/compression should be done on client side to reduce load on server, only checks constraints
     * @param userId
     */
    @PreAuthorize("isSelf(#userId) || hasAnyAuthorityCustom('USER_**', 'USER_ALL_PROF_IMG_CRUD')")
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

    @PreAuthorize("hasAnyAuthorityCustom('USER_**', 'USER_ALL_SHARED_R' ,'USER_ALL_PROF_IMG_CRUD') || isSelf(#userId)")
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

    @PreAuthorize("isSelf(#userId) || hasAnyAuthorityCustom('USER_**', 'USER_ALL_PROF_IMG_CRUD')")
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

    @PreAuthorize("hasAnyAuthorityCustom('USER_**', 'USER_ALL_SHARED_R')")
    @GetMapping
    public ResponseEntity<Page<UserPaginationDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @Min (value = 0) @Max (value = 100) @RequestParam(required = false, defaultValue = "50") int size,
            @RequestParam(required = false) String email, //contains
            @RequestParam(required = false) String username, // contains
            @CountryCode (type = CountryCode.Type.ALPHA_2, message = "Country does not exist")@RequestParam(required = false) String countryTag, // match exactly
            @RequestParam(required = true, defaultValue = "ASC") Sort.Direction order,
            @RequestParam (required = false, defaultValue = "") Set<UserAttributes> sortBy
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                order,
                sortBy.stream()
                        .map(a -> a.queryName)
                        .toArray(String[]::new));
        Page<User> userPage = userService.listUsers(pageRequest, username, email, countryTag);
        Page<UserPaginationDTO> response = new PageImpl<>(userPage.getContent().stream().map(a ->
                    UserPaginationDTO.builder()
                            .id(a.getId())
                            .countryTag(a.getCountryTag())
                            .email(a.getEmail())
                            .username(a.getUsername())
                            .dateOfBirth(a.getDateOfBirth())
                            .rolesAndAuthorities(
                                    a.getRoles().stream()
                                            .map(RoleUser::getRole)
                                            .collect(Collectors.toMap(
                                                    Role::getTitle,
                                                    role -> role.getPermissions()
                                                            .stream().map(Permission::getTitle)
                                                            .collect(Collectors.toSet())
                                                    ))
                            )
                            .build()).collect(Collectors.toList()),
                pageRequest, userPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
}
