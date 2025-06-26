package micro.service.mail_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import micro.service.mail_service.entity.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    Optional<EmailTemplate> findByCodeAndIsActiveTrue(String code);
}
