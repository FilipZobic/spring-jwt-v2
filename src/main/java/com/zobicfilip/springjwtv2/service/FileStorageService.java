package com.zobicfilip.springjwtv2.service;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

public interface FileStorageService {

    boolean saveFile(byte[] content, String type, String name);
    byte[] getFile(String name) throws FileNotFoundException;
    boolean deleteFile(String name) throws NoSuchFileException;
}
