package com.rev.app.repository;

import com.rev.app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
}
