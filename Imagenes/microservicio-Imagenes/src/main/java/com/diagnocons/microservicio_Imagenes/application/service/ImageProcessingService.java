package com.diagnocons.microservicio_Imagenes.application.service;


import com.diagnocons.microservicio_Imagenes.domain.model.Image;
import com.diagnocons.microservicio_Imagenes.domain.ports.ImageRepository;
import com.diagnocons.microservicio_Imagenes.domain.ports.ImageStorage;
import io.github.mojtabaJ.cwebp.CWebp;
import io.github.mojtabaJ.cwebp.WebpConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingService.class);

    private final ImageRepository imageRepository;
    private final ImageStorage imageStorage;

    // Lista de formatos de imagen soportados para la conversión
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "jpeg", "png", "tiff");

    public ImageProcessingService(ImageRepository imageRepository, ImageStorage imageStorage) {
        this.imageRepository = imageRepository;
        this.imageStorage = imageStorage;
    }

    public Image processAndSaveImage(MultipartFile file) throws Exception {
        logger.info("Iniciando procesamiento de imagen: {}", file.getOriginalFilename());

        // Obtener el nombre del archivo y reemplazar espacios por guiones bajos o eliminarlos
        String originalName = sanitizeFileName(file.getOriginalFilename());
        String extension = getFileExtension(originalName).toLowerCase();

        // Validar que el archivo tiene un formato soportado
        if (!SUPPORTED_FORMATS.contains(extension)) {
            logger.error("Formato de imagen no soportado: {}", extension);
            throw new IllegalArgumentException("Formato de imagen no soportado: " + extension);
        }

        // Guardar la imagen original temporalmente para la conversión
        Path tempPath = imageStorage.saveImage(file.getInputStream(), originalName);

        // Generar el nombre del archivo WebP y definir la ruta de salida
        String outputFileName = tempPath.toString().replaceAll("\\.[^.]+$", ".webp");
        File outputFile = new File(outputFileName);

        try {
            // Convertir la imagen a WebP usando WebpConverter
            WebpConverter.imageFileToWebpFile(tempPath.toFile(), String.valueOf(outputFile), 80); // 80 es la calidad de la imagen
            logger.info("Imagen convertida y guardada como WebP: {}", outputFileName);

            // Verificar si el archivo de salida está vacío después de la conversión
            if (outputFile.length() == 0) {
                logger.error("La conversión falló: el archivo WebP está vacío.");
                throw new Exception("La conversión a WebP falló, el archivo resultante está vacío.");
            }

            // Eliminar la imagen temporal original después de la conversión
            if (!tempPath.toFile().delete()) {
                logger.warn("No se pudo eliminar el archivo temporal: {}", tempPath.toFile().getAbsolutePath());
            }

        } catch (IOException e) {
            logger.error("Error al convertir la imagen a WebP: {}", e.getMessage(), e);
            throw new Exception("Error al convertir la imagen a WebP", e);
        }

        // Guardar los detalles de la imagen WebP en la base de datos
        Image savedImage = new Image();
        savedImage.setOriginalName(originalName);
        savedImage.setPath(outputFileName);
        savedImage.setFormat("webp");
        savedImage = imageRepository.save(savedImage);
        logger.info("Imagen WebP guardada y registrada en la base de datos: {}", savedImage.getPath());

        return savedImage;
    }

    // Método para obtener la extensión del archivo
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return ""; // En caso de que no tenga extensión
        }
    }

    // Método para sanitizar el nombre del archivo reemplazando espacios y caracteres problemáticos
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "archivo_sin_nombre";
        }
        // Reemplazar espacios y caracteres especiales por guiones bajos o eliminarlos
        return fileName.trim().replaceAll("[\\s]+", "_").replaceAll("[^a-zA-Z0-9._-]", "");
    }
}