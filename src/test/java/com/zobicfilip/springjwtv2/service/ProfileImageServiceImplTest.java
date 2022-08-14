package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.model.MediaTypeWithMetadata;
import com.zobicfilip.springjwtv2.model.ProfileImageConfiguration;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.zobicfilip.springjwtv2.integration.util.TestingUtil.createDummyImage;

class ProfileImageServiceImplTest {

    private FileStorageService fileStorageService;

    private ProfileImageService profileImageService;

    private static Stream<Arguments> createImageFormats() {
    return Stream.of(
            Arguments.of(
                  400, 399
            ),Arguments.of(
                800, 100
            ),Arguments.of(
                    399, 400
            ),Arguments.of(
                    100, 100
            ),Arguments.of(
                    399, 399
            )
        );
    }

    private static Stream<Arguments> integerValues() {
        return Stream.of(
                Arguments.of(
                        Integer.MAX_VALUE
                ),Arguments.of(
                        Integer.MIN_VALUE
                ),Arguments.of(
                        0
                ),Arguments.of(
                        1
                ),Arguments.of(
                        58302
                ),Arguments.of(
                        -58302
                ),Arguments.of(
                        -1
                )
        );
    }

    private static Stream<Arguments> createImageFormatsIllegalArguments() {
        return Stream.of(
                Arguments.of(
                        -1, -1
                ),Arguments.of(
                        0, 0
                )
        );
    }

    private static Stream<Arguments> test() {
        return Stream.of(
                Arguments.of(
                        "png", true
                ),
                Arguments.of(
                        "jpg", false
                )
        );
    }

    @BeforeEach
    void setUp() {
        ProfileImageConfiguration profileServiceConfig = new ProfileImageConfiguration(Set.of(400), Set.of(400), 2048);
        fileStorageService = Mockito.mock(FileStorageService.class);
        profileImageService = new ProfileImageServiceImpl(profileServiceConfig, fileStorageService);

    }

    @Test
    void deleteProfilePicture_deleteSuccessfullyWhenPictureExists() throws NoSuchFileException {
        UUID userId = UUID.randomUUID();
        String fileName = userId.toString()+".png";
        Mockito.when(fileStorageService.deleteFile(fileName)).thenReturn(true);
        boolean result = profileImageService.deleteProfilePicture(userId, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(result);
    }

    @Test
    void deleteProfilePicture_throwException_whenPictureDoesNotExist() throws NoSuchFileException {
        UUID userId = UUID.randomUUID();
        String fileName = userId.toString()+".png";
        Mockito.when(fileStorageService.deleteFile(fileName)).thenThrow(new NoSuchFileException("File not found"));
        Assertions.assertThrowsExactly(NoSuchFileException.class, () -> profileImageService.deleteProfilePicture(userId, MediaTypeWithMetadata.PNG));
    }

    @Test
    void getProfilePicture_returnSameFile_whenFileExists() throws IOException {
        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);

        byte[] bytes = baos.toByteArray();

        UUID userId = UUID.randomUUID();
        String fileName = userId.toString() +".png";
        Mockito.when(fileStorageService.getFile(fileName)).thenReturn(bytes);
        byte[] result = profileImageService.getProfilePicture(userId, MediaTypeWithMetadata.PNG);

        Assertions.assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(bytes), new ByteArrayInputStream(result)));
    }

    @Test
    void getProfilePicture_throwException_whenFileNotFound() throws IOException {
        UUID userId = UUID.randomUUID();
        String fileName = userId.toString()+".png";
        Mockito.when(fileStorageService.getFile(fileName)).thenThrow(new FileNotFoundException());
        Assertions.assertThrowsExactly(FileNotFoundException.class,() -> profileImageService.getProfilePicture(userId,MediaTypeWithMetadata.PNG));
    }

    @Test
    void saveProfilePicture_saveProfilePicture_whenImageFitsConstraints() throws IOException {
        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        UUID userId = UUID.randomUUID();
        String fileName = userId.toString();

        Mockito.when(fileStorageService.saveFile(bytes, MediaTypeWithMetadata.PNG.getFileExtension(), fileName)).thenReturn(true);
        boolean result = profileImageService.saveProfilePicture(bytes, userId, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("createImageFormats")
    void saveProfilePicture_dontSave_whenImageDoesNotFitFormatConstraints(int width, int height) throws IOException {
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        UUID userId = UUID.randomUUID();
        String fileName = userId.toString();

        Mockito.when(fileStorageService.saveFile(bytes, MediaTypeWithMetadata.PNG.getFileExtension(), fileName))
                .thenReturn(true);
        boolean result = profileImageService.saveProfilePicture(bytes, userId, MediaTypeWithMetadata.PNG);
        Assertions.assertFalse(result);
    }

    @Test
    void saveProfilePicture_dontSave_whenImageDoesNotFitFileTypeConstraints() throws IOException {
        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", baos);
        byte[] bytes = baos.toByteArray();

        UUID userId = UUID.randomUUID();
        String fileName = userId.toString();

        Mockito.when(fileStorageService.saveFile(bytes, MediaTypeWithMetadata.PNG.getFileExtension(), fileName))
                .thenReturn(true);
        boolean result = profileImageService.saveProfilePicture(bytes, userId, MediaTypeWithMetadata.PNG);
        Assertions.assertFalse(result);

    }

    @Test
    void validate_willPassAllChecks_whenConstraintsCorrect() throws  IOException {

        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        boolean result = profileImageService.validate(bytes, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(result);
    }

    @Test
    void validate_willThrowException_withoutMetadata() throws  IOException {
        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();
        Assertions.assertThrowsExactly(UnsupportedOperationException.class, () -> profileImageService.validate(bytes));
    }

    @Test
    void getImageType_operationNotSupported() throws NoSuchMethodException {
        byte[] bytes = new byte[10];
        Assertions.assertThrowsExactly(UnsupportedOperationException.class, () -> profileImageService.getImageType(bytes));
    }

    @Test
    public void validateSize() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        Method validateSize = ProfileImageServiceImpl.class.getDeclaredMethod("validateSize", byte[].class);
        validateSize.setAccessible(true);

        int width, height;
        width = height = 100;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();
        Object returnValue = validateSize.invoke(profileImageService, bytes);
        if (!(returnValue instanceof Boolean polyReturnValue)) throw new RuntimeException(); // This is allowed and variable will have entire method scope
        Assertions.assertTrue(polyReturnValue);
        validateSize.setAccessible(false);
    }

    @ParameterizedTest()
    @MethodSource("test")
    public void validateFileFormat_willMatchWillPassVariable_whenFormatPassed(String imageType, boolean willPass) throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        Method validateFileFormat = ProfileImageServiceImpl.class.getDeclaredMethod("validateFileFormat", byte[].class, MediaTypeWithMetadata.class);
        validateFileFormat.setAccessible(true);

        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, imageType, baos);
        byte[] bytes = baos.toByteArray();

        Object returnValue = validateFileFormat.invoke(profileImageService, bytes, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(returnValue instanceof Boolean);
        Boolean polyReturnValue = (Boolean) returnValue;
        Assertions.assertEquals(willPass, polyReturnValue);
        validateFileFormat.setAccessible(false);
    }

    @ParameterizedTest
    @MethodSource("createImageFormats")
    public void validateDimensions_returnFalse_whenPassingUnsupportedFormats(int width, int height) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method validateDimensions = ProfileImageServiceImpl.class.getDeclaredMethod("validateDimensions", Dimension.class);
        validateDimensions.setAccessible(true);
        Object returnValue = validateDimensions.invoke(profileImageService, new Dimension(width, height));
        Assertions.assertTrue(returnValue instanceof Boolean);
        Boolean polyReturnValue = (Boolean) returnValue;
        Assertions.assertFalse(polyReturnValue);
        validateDimensions.setAccessible(false);
    }

    @Test
    public void getImageDimension() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
        Method getImageDimension = ProfileImageServiceImpl.class.getDeclaredMethod("getImageDimension", byte[].class, MediaTypeWithMetadata.class);
        getImageDimension.setAccessible(true);

        int width, height;
        width = height = 400;
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        Object returnValue = getImageDimension.invoke(profileImageService, bytes, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(returnValue instanceof Dimension);
        Dimension polymorphismsReturnValue = (Dimension) returnValue;
        Assertions.assertEquals(width, polymorphismsReturnValue.width);
        Assertions.assertEquals(height, polymorphismsReturnValue.height);
    } // needs to be same format

    @ParameterizedTest()
    @MethodSource("integerValues")
    public void getIntFromBytes_willReturnExpectedInt_whenPassedByteEquivalentValue(int expected) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getIntFromBytes = ProfileImageServiceImpl.class.getDeclaredMethod("getIntFromBytes", byte[].class, int.class, int.class);
        getIntFromBytes.setAccessible(true);
        byte[] expectedBytes = ByteBuffer.allocate(4).putInt(expected).array();
        Object returnValue = getIntFromBytes.invoke(profileImageService, expectedBytes, 0, expectedBytes.length);
        Assertions.assertTrue(returnValue instanceof Integer);
        Integer polymorphismsReturnValue = (Integer) returnValue;
        Assertions.assertEquals(expected, polymorphismsReturnValue);
    }// TODO create one with bigger byte array or violating length

    @Test
    public void createFileName_fileNameCreated_whenPassedArgumentsNotNull() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method createFileName = ProfileImageServiceImpl.class.getDeclaredMethod("createFileName", UUID.class, MediaTypeWithMetadata.class);
        createFileName.setAccessible(true);
        UUID userId = UUID.randomUUID();
        Object methodReturnValue = createFileName.invoke(profileImageService, userId, MediaTypeWithMetadata.PNG);
        Assertions.assertTrue(methodReturnValue instanceof String);
        String polymorphesMethodReturnValue = (String) methodReturnValue;
        Assertions.assertEquals(userId.toString() + ".png", polymorphesMethodReturnValue);
        createFileName.setAccessible(false);
    }
}