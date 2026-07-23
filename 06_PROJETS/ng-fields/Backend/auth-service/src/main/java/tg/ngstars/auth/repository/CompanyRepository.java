package tg.ngstars.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByActiveTrue();

    boolean existsByName(String name);
}
