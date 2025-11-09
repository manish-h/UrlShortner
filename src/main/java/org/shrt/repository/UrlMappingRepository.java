package org.shrt.repository;

import org.shrt.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {

    Optional<UrlMapping> findByLongUrlHash(String longUrlHash);
}
