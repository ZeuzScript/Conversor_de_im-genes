package com.diagnocons.microservicio_Imagenes.domain.ports;

import java.io.InputStream;
import java.nio.file.Path;

public interface ImageStorage {
    Path saveImage(InputStream inputStream, String fileName) throws Exception;
}
