package com.diagnocons.microservicio_Imagenes.infrastructure.storege;

import com.diagnocons.microservicio_Imagenes.domain.ports.ImageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class LocalImageStorage implements ImageStorage {

    @Value("${image.storage.path:images}")
    private String storagePath;

    @Override
    public Path saveImage(InputStream inputStream, String fileName) throws Exception {
        Path path = Paths.get(storagePath, fileName);
        Files.createDirectories(path.getParent());
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        return path;
    }
}
