package com.rev.app.repository;

import com.rev.app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {

    @Override
    <S extends Notification> S save(S entity);

    @Override
    Optional<Notification> findById(Long id);

    @Override
    List<Notification> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(Notification entity);

    List<Notification> findByUserId(Long userId);
}
