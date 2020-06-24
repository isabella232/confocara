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

package com.orange.confocara.presentation.view.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.orange.confocara.business.service.FileService;
import com.orange.confocara.business.service.ImageService;
import com.orange.confocara.connector.persistence.model.Image;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class FileUploadController {
    private static final String MESSAGE = "message";
    private static final String FAILED_TO_UPLOAD = "Failed to upload";
    private final ResourceLoader resourceLoader;

    @Autowired
    private ImageService imageService;

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/testupload")
    public String provideUploadInfo(Model model) throws IOException {

        Path root = Paths.get(FileService.ROOT);

        List files;
        try (Stream<Path> stream = Files.walk(root)) {
            files = stream
                .filter(path -> !path.equals(Paths.get(FileService.ROOT)))
                .map(path -> Paths.get(FileService.ROOT).relativize(path))
                .map(path -> linkTo(methodOn(FileUploadController.class).getFile(path.toString())).withRel(path.toString()))
                .collect(Collectors.toList());
        }
        model.addAttribute("files", files);

        return "uploadForm";
    }

    /**
     * Vulnérabilité identifiée par Sonar :
     * A file is opened to read its content. The filename comes from an input parameter. If
     * an unfiltered parameter is passed to this file API, files from an arbitrary filesystem
     * location could be read.
     *
     * This rule identifies potential path traversal vulnerabilities. In many cases, the
     * constructed file path cannot be controlled by the user. If that is the case, the
     * reported instance is a false positive.
     *
     * Pour corriger, remplacer:
     * File file = new File("resources/images/", image); //Weak point
     *
     * par la valeur suivante:
     * File file = new File("resources/images/", FilenameUtils.getName(image)); //Fix
     */
    @RequestMapping(method = RequestMethod.GET, value = "/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity getFile(@PathVariable String filename) {

        try {
            Resource resource = resourceLoader.getResource("file:" + Paths.get(FileService.ROOT, filename).toString()); // Weak point
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            log.error("Could not retrieve file " + filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images/publish/{filename:.+}")
    @ResponseBody
    public ResponseEntity getPublishedFile(@PathVariable String filename) {

        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(FileService.ROOT, filename).toString()));
        } catch (Exception e) {
            log.error("Could not retrieve file " + filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This WS enables to import images inside upload-dir.
     * Warning : the object linked with the image must already exist. The image will be saved with the UUID given in Image object
     * If no Image object has the same name as the imported file, then the image will not be imported
     *
     * @param files              the list of files to import
     * @param redirectAttributes the redirectAttributes
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/uploadPicto")
    public String handleFileUpload(@RequestParam("files") List<MultipartFile> files,
                                   RedirectAttributes redirectAttributes) {

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = file.getOriginalFilename().replaceAll("\\s", "-");
                    Image imageFromDbWithSameName = imageService.findByImageName(fileName);

                    if (imageFromDbWithSameName != null) {
                        Files.copy(file.getInputStream(), Paths.get(FileService.ROOT, imageFromDbWithSameName.getFileNameWithExtension()));
                    }
                } catch (IOException | RuntimeException e) {
                    log.error("ErrorMessage=Could not upload file", e);
                    redirectAttributes.addFlashAttribute(MESSAGE, FAILED_TO_UPLOAD + file.getOriginalFilename() + " => " + e.getMessage());
                }
            } else {
                redirectAttributes.addFlashAttribute(MESSAGE, FAILED_TO_UPLOAD + file.getOriginalFilename() + " because it was empty");
            }
        }

        return "redirect:/testupload";
    }

}