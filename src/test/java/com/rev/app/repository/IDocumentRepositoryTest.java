package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.Document;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class IDocumentRepositoryTest {

    @Autowired
    private IDocumentRepository documentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserId_ReturnsDocuments() {
        User user = new User();
        user.setEmail("doc@test.com");
        user.setFullName("Doc User");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        user = entityManager.persistAndFlush(user);

        Document doc = new Document();
        doc.setUser(user);
        doc.setFileName("file.txt");
        doc.setFileType("text/plain");
        doc.setDocumentType("ID");
        doc.setData(new byte[]{1, 2, 3});
        doc.setVerified(false);
        entityManager.persistAndFlush(doc);

        List<Document> found = documentRepository.findByUserId(user.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
