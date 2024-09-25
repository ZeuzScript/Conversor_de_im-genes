package com.diagnocons.microservicio_Imagenes.infrastructure.persistence;

import com.diagnocons.microservicio_Imagenes.domain.model.Image;
import com.diagnocons.microservicio_Imagenes.domain.ports.ImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresImageRepository extends JpaRepository<Image, Long>, ImageRepository {
}
