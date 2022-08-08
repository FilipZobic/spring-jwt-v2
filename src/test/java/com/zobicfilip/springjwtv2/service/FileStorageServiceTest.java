package com.zobicfilip.springjwtv2.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static com.zobicfilip.springjwtv2.integration.util.TestingUtil.createDummyImage;

class FileStorageServiceTest {

    private static Stream<Arguments> createImageFormats() {
        return Stream.of(
                Arguments.of(
                      400, 400
                ),Arguments.of(
                    800, 100
                )
        );
    }

    @ParameterizedTest()
    @MethodSource("createImageFormats")
    public void testImage(int width, int height) throws IOException {
        BufferedImage dummyImage = createDummyImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);

        byte[] bytes = baos.toByteArray();
        Dimension dimension = FileStorageService.getImageDimension(bytes, FileStorageService.ImageType.PNG);

        Assertions.assertEquals(width, dimension.getWidth());
        Assertions.assertEquals(height, dimension.getHeight());
    }
}