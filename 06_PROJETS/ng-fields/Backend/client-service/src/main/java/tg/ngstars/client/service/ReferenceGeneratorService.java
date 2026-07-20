package tg.ngstars.client.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.client.repository.ClientRepository;

@Service
public class ReferenceGeneratorService {
    
    private final ClientRepository clientRepository;

    public ReferenceGeneratorService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public String generateNextReference() {
        // Utilisation de la séquence native pour éviter les conflits concurrents
        String nextVal = clientRepository.nextReference();
        return "CLT-" + String.format("%04d", Long.parseLong(nextVal));
    }
}