package com.diagnocons.microservicio_Imagenes.domain.ports;

import com.diagnocons.microservicio_Imagenes.domain.model.Image;

public interface ImageRepository {
    Image save(Image image);
}
