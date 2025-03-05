package com.sanedge.modularexample.room.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.sanedge.modularexample.room.service.FolderService;

@Service
public class FolderServiceImpl implements FolderService {

    public String createFolder(String name) {
        String roomPath = "static/room";

        System.out.println("Room path: " + name);

        File roomDir = new File(roomPath);

        if (!roomDir.exists()) {
            if (!roomDir.mkdirs()) {
                System.err.println("Failed to create directory: " + roomPath);
                return null;
            }
        }

        String path = roomPath + File.separator + name;
        File folder = new File(path);

        if (folder.exists()) {
            return path;
        }

        if (!folder.mkdirs()) {
            System.err.println("Failed to create directory: " + path);
            return null;
        }

        System.out.println("Directory created: " + path);

        return path;
    }

    public void deleteFolder(String folder) {
        String path = "static/room/" + File.separator + folder;

        File folderToDelete = new File(path);

        if (!folderToDelete.exists()) {
            System.err.println("Directory does not exist: " + path);
            return;
        }

        if (!deleteRecursive(folderToDelete)) {
            System.err.println("Failed to delete directory: " + path);
        }
    }

    private static boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();

            if (contents != null) {

                for (File f : contents) {
                    if (!deleteRecursive(f)) {
                        return false;
                    }
                }
            }
        }

        return file.delete();
    }
}