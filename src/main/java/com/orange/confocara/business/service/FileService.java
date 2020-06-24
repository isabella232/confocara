/*
 * Software Name: ConfOCARA
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2020 Orange
 * SPDX-License-Identifier: MPL-2.0
 *
 * This software is distributed under the Mozilla Public License v. 2.0,
 * the text of which is available at http://mozilla.org/MPL/2.0/ or
 * see the "license.txt" file for more details.
 *
 */

package com.orange.confocara.business.service;

import com.orange.confocara.connector.persistence.model.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FileService {

    public static final String ROOT = "upload-dir";

    public static final String ERR_UPLOAD_MESSAGE = "err_upload_message";
    public static final String MESSAGE_FILE_ALREADY_ADDED = "message.file_already_added";
    public static final String ICON_IS_PUBLISHED = "iconIsPublished";

    private static final String MESSAGE_FILE_ALREADY_PUBLISHED = "message.file_already_published";
    private static final String MESSAGE_UNEXPECTED_ERROR = "message.unexpected.error";

    public enum UploadStatus {
        OK(""),
        NOK(MESSAGE_UNEXPECTED_ERROR),
        ALREADY_EXISTS(MESSAGE_FILE_ALREADY_ADDED),
        ALREADY_PUBLISHED(MESSAGE_FILE_ALREADY_PUBLISHED);

        private String message;

        UploadStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public String getFileExtension(String fileName) {
        String extension = "";

        if (fileName != null) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i + 1);
            }
        }

        return extension;
    }

    /**
     * test whether icon is an image file
     *
     * @param icon the file to test
     * @return true if icon is an image, false otherwise
     */
    public boolean isImageFile(MultipartFile icon) {
        if (!icon.isEmpty()) {
            try (InputStream input = icon.getInputStream()) {
                return input != null && ImageIO.read(input) != null;
            } catch (Exception e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Icon empty  ", e);
                return false;
            }
        }

        return false;
    }

    /**
     * Upload icon : add image to ROOT
     *
     * @param image the image to save
     * @return UploadStatus
     */
    public UploadStatus uploadIcon(MultipartFile image, Image imageObject) {
        if (!image.isEmpty()) {
            String originalFilename = imageObject.getFileNameWithExtension();
            try {
                Files.copy(image.getInputStream(), Paths.get(ROOT, originalFilename));
            } catch (IOException | RuntimeException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "uploadIcon", e);
                return UploadStatus.NOK;
            }
        }

        return UploadStatus.OK;
    }

    /**
     * delete the icon from ROOT
     *
     * @param iconName the icon name
     */
    public boolean deleteIcon(String iconName) {
        try {
            final Path path = Paths.get(ROOT, iconName);
            File file = new File(path.toUri());
            return file.delete();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "deleteIcon", e);
        }

        return false;
    }

    /**
     * The files stored in database should not contain spaces
     * The following method replaces spaces with "-"
     *
     * @param filename the original filename
     * @return the valid filename
     */
    public String formatOriginalFilename(String filename) {
        return filename.replaceAll("\\s", "-");
    }
}
