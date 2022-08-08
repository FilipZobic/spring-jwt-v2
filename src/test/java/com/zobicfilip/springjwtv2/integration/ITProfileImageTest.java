package com.zobicfilip.springjwtv2.integration;

import com.zobicfilip.springjwtv2.dto.AuthSignUpDTO;
import com.zobicfilip.springjwtv2.dto.TokensCreatedResponseDTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.zobicfilip.springjwtv2.integration.util.TestingUtil.createDummyImage;

public class ITProfileImageTest extends BaseRest {
    protected static final String UPLOAD_URL = "/api/users/{userId}/image/upload";

    @Test
    public void crudProfileImage_consistentDataOnSameUser_whenUploadThenDownloadThenDeleteThenDownloadAttempt() throws Exception {
        // Phase UPLOAD
        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos); // try with jpg

        byte[] bytes = baos.toByteArray();

        var multipart = new LinkedMultiValueMap<>();
        multipart.add("image", new ByteArrayResourceExtension(bytes, UUID.randomUUID().toString()));
//        multipart.add("image",new org.springframework.core.io.ClassPathResource("image.png")); from Resources if image present

        AuthSignUpDTO user = this.user;
        TokensCreatedResponseDTO tokenDto = loginUser(user.getEmail(), user.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.put("Authorization", List.of(tokenDto.getAccessToken()));

        String userId = getIDFromAccessToken(tokenDto.getAccessToken()).toString();

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(this.rootUri + UPLOAD_URL, HttpMethod.PATCH, new HttpEntity<>(multipart, httpHeaders), String.class, userId);

        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Phase DOWNLOAD
        URI downloadUri = responseEntity.getHeaders().getLocation();
        Assertions.assertNotNull(downloadUri);
        ResponseEntity<byte[]> downloadResponseEntity = testRestTemplate.exchange(this.rootUri + downloadUri.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<byte[]>() {});
        Assertions.assertEquals(HttpStatus.OK, downloadResponseEntity.getStatusCode());
        Assertions.assertNotNull(downloadResponseEntity.getBody());
        Assertions.assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(bytes), new ByteArrayInputStream(downloadResponseEntity.getBody())));

        // Phase DELETE & DOWNLOAD
        ResponseEntity<String> deleteResponse = testRestTemplate.exchange(this.rootUri + downloadUri.toString().replace("download", "delete"), HttpMethod.DELETE, new HttpEntity<>(httpHeaders), String.class);
        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ResponseEntity<byte[]> downloadResponseEntityNotFound = testRestTemplate.exchange(this.rootUri + downloadUri.toString(), HttpMethod.GET, new HttpEntity<>(httpHeaders), new ParameterizedTypeReference<byte[]>() {});
        Assertions.assertEquals(HttpStatus.NOT_FOUND, downloadResponseEntityNotFound.getStatusCode());
    }
    @Test
    public void crudProfileImage_consistentDataIntegrityAndAuthorityControlAcrossMultipleUsersForTheSameResource_whenAdminUploadsImageForPrimaryUserThenPrimaryUserDownloadsThenSecondaryUserDeleteAttemptThenDownloadThenModeratorDeleteAndAdminDownloadAttempt() throws Exception {
        TokensCreatedResponseDTO adminTokens = loginUser(admin.getEmail(), admin.getPassword());
        HttpHeaders adminAuth = new HttpHeaders();
        adminAuth.put("Authorization", List.of(adminTokens.getAccessToken()));

        TokensCreatedResponseDTO mainUserTokens = loginUser(user.getEmail(), user.getPassword());
        HttpHeaders mainUserAuth = new HttpHeaders();
        mainUserAuth.put("Authorization", List.of(mainUserTokens.getAccessToken()));

        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos); // try with jpg

        byte[] bytes = baos.toByteArray();

        var multipart = new LinkedMultiValueMap<>();
        multipart.add("image", new ByteArrayResourceExtension(bytes, UUID.randomUUID().toString()));
        String mainUserId = getIDFromAccessToken(mainUserTokens.getAccessToken()).toString();

        ResponseEntity<String> adminMainUserProfilePictureResponseEntity = testRestTemplate.exchange(this.rootUri + UPLOAD_URL, HttpMethod.PATCH, new HttpEntity<>(multipart, adminAuth), String.class, mainUserId);
        Assertions.assertEquals(HttpStatus.CREATED, adminMainUserProfilePictureResponseEntity.getStatusCode());

        URI downloadUri = adminMainUserProfilePictureResponseEntity.getHeaders().getLocation();
        ResponseEntity<byte[]> mainUserDownloadSelfImage = testRestTemplate.exchange(this.rootUri + downloadUri.toString(), HttpMethod.GET, new HttpEntity<>(mainUserAuth), new ParameterizedTypeReference<byte[]>() {});
        Assertions.assertEquals(HttpStatus.OK, mainUserDownloadSelfImage.getStatusCode());
        Assertions.assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(bytes), new ByteArrayInputStream(mainUserDownloadSelfImage.getBody())));

        AuthSignUpDTO authSignUpDTO = insertUserInDB("user2", "ROLE_USER");
        TokensCreatedResponseDTO secondaryUserTokens = loginUser(authSignUpDTO.getEmail(), authSignUpDTO.getPassword());
        HttpHeaders secondaryUserAuth = new HttpHeaders();
        secondaryUserAuth.put("Authorization", List.of(secondaryUserTokens.getAccessToken()));
        ResponseEntity<String> secondaryUserForbiddenResponse = testRestTemplate.exchange(this.rootUri + downloadUri.toString().replace("download", "delete"), HttpMethod.DELETE, new HttpEntity<>(secondaryUserAuth), String.class);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, secondaryUserForbiddenResponse.getStatusCode());
        ResponseEntity<byte[]> secondaryUserDownloadMainUserImage = testRestTemplate.exchange(this.rootUri + downloadUri.toString(), HttpMethod.GET, new HttpEntity<>(secondaryUserAuth), new ParameterizedTypeReference<byte[]>() {});
        Assertions.assertEquals(HttpStatus.OK, secondaryUserDownloadMainUserImage.getStatusCode());
        Assertions.assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(bytes), new ByteArrayInputStream(secondaryUserDownloadMainUserImage.getBody())));
        userRepository.deleteByUsername(authSignUpDTO.getUsername());

        TokensCreatedResponseDTO moderatorTokens = loginUser(moderator.getEmail(), moderator.getPassword());
        HttpHeaders moderatorAuth = new HttpHeaders();
        moderatorAuth.put("Authorization", List.of(moderatorTokens.getAccessToken()));
        ResponseEntity<String> moderatorDeleteSuccessfully = testRestTemplate.exchange(this.rootUri + downloadUri.toString().replace("download", "delete"), HttpMethod.DELETE, new HttpEntity<>(moderatorAuth), String.class);
        Assertions.assertEquals(HttpStatus.OK, moderatorDeleteSuccessfully.getStatusCode());

        ResponseEntity<byte[]> adminDownloadDeletedImage = testRestTemplate.exchange(this.rootUri + downloadUri.toString(), HttpMethod.GET, new HttpEntity<>(adminAuth), new ParameterizedTypeReference<byte[]>() {});
        Assertions.assertEquals(HttpStatus.NOT_FOUND, adminDownloadDeletedImage.getStatusCode());
    }

    /**
     * Going through the code we found sending a file with multipart requirs that class implements Resource interface
     * ClassPathResource --> AbstractFileResolvingResource --> AbstractResource <-- ByteArrayResource, InMemoryResource || ByteArrayResource <-- TransformedResource
     * Initially InMemoryResource and ByteArrayResource did not work (REST API missing form value (image) the file which was strange since org.springframework.core.io.ClassPathResource worked without issues
     * Investigating brought that InMemoryResource and ByteArrayResource method getFilename returned null while ClassPathResource returned an actual name null caused null ptr exception which was handled in the mapper (so it was just ignoring that field)
     * Hence the solution was to extend ByteArrayResource & add override the getFilename method to actually return a string
     */
    private static class ByteArrayResourceExtension extends ByteArrayResource {
        private final String fileName;

        public ByteArrayResourceExtension(byte[] byteArray, String fileName) {
            super(byteArray);
            this.fileName = fileName;
        }

        @Override
        public String getFilename() {
            return fileName;
        }
    }
}
