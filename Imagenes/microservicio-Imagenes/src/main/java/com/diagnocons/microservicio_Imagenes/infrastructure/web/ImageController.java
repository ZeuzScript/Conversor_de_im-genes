package com.diagnocons.microservicio_Imagenes.infrastructure.web;

import com.diagnocons.microservicio_Imagenes.application.service.ImageProcessingService;
import com.diagnocons.microservicio_Imagenes.domain.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageProcessingService imageProcessingService;

    public ImageController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        logger.info("Solicitud de carga de imagen recibida.");

        try {
            if (file.isEmpty()) {
                logger.warn("No se subió ningún archivo.");
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            Image image = imageProcessingService.processAndSaveImage(file);
            logger.info("Imagen procesada y subida con éxito.");

            return ResponseEntity.ok("Image uploaded successfully: " + image.getPath());
        } catch (Exception e) {
            logger.error("Error al procesar la imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }
}