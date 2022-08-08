package com.zobicfilip.springjwtv2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class FileStorageServiceSystemImpl implements FileStorageService {

    private final String profileImagesPath;

    public FileStorageServiceSystemImpl() throws IOException {
        profileImagesPath = System.getProperty("user.home") + "/.profileImages";
        Path path = Path.of(profileImagesPath);
        boolean isDirAndExists = Files.isDirectory(path);
        if (!isDirAndExists) {
            try {
                Files.delete(path);
            } catch (NoSuchFileException ignored) {}
            log.info("Directory has been created dir: {}", profileImagesPath);
            Files.createDirectory(path);
        } else {
            log.info("Directory already exists dir: {}", profileImagesPath);
        }
    }

    @Override
    public boolean saveFile(byte[] content, String type, String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.append('.');
        sb.append(type.toLowerCase());

        Path saveTo = Path.of(
                profileImagesPath,
                "/",
                sb.toString()
        );
        try {
            if (Files.exists(saveTo)) {
                log.info("Overwriting image");
            } else {
                log.info("Writing image");
            }
            Files.write(saveTo, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Successfully wrote image");
            return true;
        } catch (IOException e) {
            log.error("Error occurred on saving image msg: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public byte[] getFile(String nameWithExtension) throws FileNotFoundException {
        log.info("Reading image on filesystem");
        try {
            Path path = Path.of(profileImagesPath,  nameWithExtension);
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                log.info("Successfully read image on filesystem");
                return bytes;
            }
            throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            log.warn("Image not found {}", nameWithExtension);
            throw e;
        } catch (IOException e) {
            log.error("Error occurred on reading image msg: {}", e.getMessage(), e);
        }
        throw new FileNotFoundException();
    }

    @Override
    public boolean deleteFile(String nameWithExtension) throws NoSuchFileException {
        log.info("Deleting image on filesystem");
        Path path = Path.of(profileImagesPath, nameWithExtension);
        try {
            Files.delete(path);
            log.info("Image was deleted successfully");
            return true;
        } catch (NoSuchFileException e) {
            log.warn("Image not found {}", nameWithExtension);
            throw e;
        } catch (IOException e) {
            log.error("Error occurred on deleting image msg: {}", e.getMessage(), e);
        }
        return false;
    }
}
