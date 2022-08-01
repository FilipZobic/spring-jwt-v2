package com.zobicfilip.springjwtv2.service;

import com.zobicfilip.springjwtv2.exception.UnsupportedFileException;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public abstract class FileStorageService {

    public enum ImageType {
        PNG
    }

    // if we know header byte position locations we will be able just to get those needed bytes
    // Pair.of(start, length) ints for PNG,JPEG,JPG and just select those
    protected static final int[] PNG_FIRST_EIGHT_BYTES = new int[]{137, 80, 78, 71, 13, 10, 26, 10};

    protected static final Map<ImageType, int[]> IMAGE_MAP = Map.of(ImageType.PNG, PNG_FIRST_EIGHT_BYTES);

    /**
     *
     * @param bytes image bytes
     * @return Dimension of image
     * @throws IOException if image not one of supported files (png)
     */
    public static Dimension getImageDimension(byte[] bytes, ImageType type) throws IOException {
        InputStream is = new ByteArrayInputStream(bytes);

        int[] imageByte = IMAGE_MAP.get(type);

        int index = 0;
        while (index < imageByte.length) {
            int nextByte = is.read();
            if (nextByte != imageByte[index]) {
                log.warn("Image mime cache is not {}({}) index: {} value: {}", type.toString(), Arrays.toString(imageByte), index, nextByte);
                throw new UnsupportedFileException();
            }
            index++;
        }

//        int IHDRLengthSection = ByteBuffer.wrap(is.readNBytes(4)).getInt();
        is.skip(4);
        index = 0;
        char[] blockType = new char[4];
        while (index < 4) {
            blockType[index] = (char) is.read();
            index++;
        }
        if (!new String(blockType).equalsIgnoreCase("IHDR")) {
            throw new UnsupportedFileException();
        }
        // data
        int width = ByteBuffer.wrap(is.readNBytes(4)).getInt();
        int height = ByteBuffer.wrap(is.readNBytes(4)).getInt();
        is.close();
        return new Dimension(width, height);
    }
}
