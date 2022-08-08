package com.zobicfilip.springjwtv2.model;

import lombok.Getter;
import org.springframework.http.MediaType;

public enum MediaTypeWithMetadata {

    /**
     * <a href="http://www.libpng.org/pub/png/spec/1.2/png-1.2.pdf">PNG 1.2 specification</a>
     * <a href="https://garethrees.org/2007/11/14/pngcrush/#:~:text=A%20detailed%20look%20at%20the,PNG%2C%20only%2067%20bytes%20long">Smallest possible PNG</a>.
     */
    PNG("image/png", new int[]{137, 80, 78, 71, 13, 10, 26, 10}, 16, 20, "IHDR", 12, 67, MediaType.IMAGE_PNG, "png");
    @Getter private final String mimeCache;
    @Getter private final int[] fileSignatureBytes;
    @Getter private final int fileSignatureStart;
    @Getter private final int fileSignatureEnd;
    @Getter private final int widthStart;
    @Getter private final int widthEnd;
    @Getter private final int heightStart;
    @Getter private final int heightEnd;
    @Getter private final String contentHeaderName;
    @Getter private final int contentHeaderStart;
    @Getter private final int contentHeaderEnd;
    @Getter private final int minimumRequiredBytes;
    @Getter private final MediaType mediaType;

    @Getter private final String fileExtension;

    public static MediaTypeWithMetadata parseFromMimeCache(String mimeCache) {
        for (MediaTypeWithMetadata img : MediaTypeWithMetadata.values()) {
            if (img.mimeCache.equalsIgnoreCase(mimeCache)) return img;
        }
        throw new IllegalArgumentException(String.format("Cache %s not supported", mimeCache));
    }

    MediaTypeWithMetadata(String mimeCache, int[] fileSignatureBytes, int widthStart, int heightStart, String contentHeaderName, int headerStart, int minimumRequiredBytes, MediaType mediaType, String fileExtension) {
        this.mimeCache = mimeCache;
        this.fileSignatureBytes = fileSignatureBytes;
        this.fileSignatureStart = 0;
        this.fileSignatureEnd = fileSignatureBytes.length;
        this.widthStart = widthStart;
        this.widthEnd = widthStart + 4;
        this.heightStart = heightStart;
        this.heightEnd = heightStart + 4;
        this.contentHeaderStart = headerStart;
        this.contentHeaderEnd = headerStart + contentHeaderName.length();
        this.contentHeaderName = contentHeaderName;
        this.minimumRequiredBytes = minimumRequiredBytes;
        this.mediaType = mediaType;
        this.fileExtension = fileExtension;
    }


}
