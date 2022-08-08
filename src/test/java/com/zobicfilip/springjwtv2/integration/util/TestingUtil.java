package com.zobicfilip.springjwtv2.integration.util;

import java.awt.image.BufferedImage;

public class TestingUtil {
    public static BufferedImage createDummyImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // generating values less than 256
                int a = (int)(Math.random()*256);
                int r = (int)(Math.random()*256);
                int g = (int)(Math.random()*256);
                int b = (int)(Math.random()*256);

                //pixel
                int p = (a<<24) | (r<<16) | (g<<8) | b;

                img.setRGB(x, y, p);
            }
        }

        return img;
    }
}