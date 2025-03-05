package com.sanedge.modularexample.room.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sanedge.modularexample.room.service.FileService;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String createFileImage(MultipartFile file, String filepath) {
        try {
            Path destinationPath = Path.of(filepath);

            if (Files.exists(destinationPath)) {

                System.err.println("File already exists");
                return null;
            }

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            return filepath;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteFileImage(String filepath) {

        File fileToDelete = new File(filepath);

        if (!fileToDelete.exists()) {
            System.err.println("File does not exist");
            return;
        }

        if (!fileToDelete.delete()) {
            System.err.println("Failed to delete file");
            return;
        }
    }

}