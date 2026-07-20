package tg.ngstars.client.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.client.model.Contact;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByClientIdAndActiveTrue(UUID clientId);
    void deleteByClientId(UUID clientId);
}
