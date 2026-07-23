package tg.ngstars.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.client.dto.ClientResponse;
import tg.ngstars.client.dto.ContactDto;
import tg.ngstars.client.dto.ContactRole;
import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.CreateContactRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.client.model.Client;
import tg.ngstars.client.model.Contact;
import tg.ngstars.client.repository.ClientRepository;
import tg.ngstars.client.repository.ContactRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ReferenceGeneratorService referenceGeneratorService;
    private final ContactRepository contactRepository;
    private final InterventionSyncClient interventionSyncClient;

    public ClientService(ClientRepository clientRepository, ReferenceGeneratorService referenceGeneratorService,
            ContactRepository contactRepository, InterventionSyncClient interventionSyncClient) {
        this.clientRepository = clientRepository;
        this.referenceGeneratorService = referenceGeneratorService;
        this.contactRepository = contactRepository;
        this.interventionSyncClient = interventionSyncClient;
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request, String createdBy) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("Un client avec l'email '" + request.email() + "' existe deja");
        }

        if (clientRepository.existsByCompanyNameIgnoreCase(request.companyName())) {
            throw new ConflictException("Un client avec le nom '" + request.companyName() + "' existe deja");
        }

        String reference = referenceGeneratorService.generateNextReference();

        var client = Client.builder()
                .reference(reference)
                .companyName(request.companyName())
                .contactName(request.contactName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .createdBy(createdBy)
                .build();

        var saved = clientRepository.save(client);
        log.info("Fiche client creee : {} (ref={})", request.companyName(), saved.getReference());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> listClients(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("companyName").ascending());
        return clientRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClient(UUID id) {
        return clientRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));
    }

    @Transactional
    public ClientResponse updateClient(UUID id, UpdateClientRequest request) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));

        if (!client.getEmail().equals(request.email()) && clientRepository.existsByEmail(request.email())) {
            throw new ConflictException("L'email '" + request.email() + "' est deja utilise");
        }

        if (!client.getCompanyName().equalsIgnoreCase(request.companyName())
                && clientRepository.existsByCompanyNameIgnoreCase(request.companyName())) {
            throw new ConflictException("Le nom '" + request.companyName() + "' est deja utilise");
        }

        client.setCompanyName(request.companyName());
        client.setContactName(request.contactName());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        client.setLatitude(request.latitude());
        client.setLongitude(request.longitude());

        var saved = clientRepository.save(client);
        interventionSyncClient.syncClientData(id, request.companyName(), request.email(),
                request.phone(), request.address());
        return toResponse(saved);
    }

    @Transactional
    public void deactivateClient(UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));
        client.setActive(false);
        clientRepository.save(client);
        log.info("Client desactive : {}", client.getCompanyName());
    }

    @Transactional
    public ClientResponse reactivateClient(UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));
        if (Boolean.TRUE.equals(client.getActive())) {
            return toResponse(client);
        }
        client.setActive(true);
        var saved = clientRepository.save(client);
        log.info("Client reactive : {}", client.getCompanyName());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> searchClients(String query, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("companyName").ascending());
        return clientRepository.search(query, pageable).map(this::toResponse);
    }

    @Transactional
    public ContactDto addContact(UUID clientId, CreateContactRequest request) {
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + clientId));

        var contactRole = ContactRole.fromString(request.role());

        var contact = Contact.builder()
                .client(client)
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .role(contactRole != null ? contactRole.name() : null)
                .build();
        var saved = contactRepository.save(contact);
        log.info("Contact ajoute : {} pour client {}", request.fullName(), client.getReference());
        return toContactDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ContactDto> getContacts(UUID clientId) {
        if (!clientRepository.existsById(clientId))
            throw new NotFoundException("Client introuvable : id=" + clientId);
        return contactRepository.findByClientIdAndActiveTrue(clientId).stream()
                .map(this::toContactDto)
                .toList();
    }

    @Transactional
    public void removeContact(UUID clientId, UUID contactId) {
        var contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new NotFoundException("Contact introuvable : id=" + contactId));
        if (!contact.getClient().getId().equals(clientId))
            throw new NotFoundException("Contact introuvable pour ce client");
        contact.setActive(false);
        contactRepository.save(contact);
        log.info("Contact desactive : {}", contactId);
    }

    private ClientResponse toResponse(Client c) {
        List<ContactDto> contactDtos = c.getContacts() != null
                ? c.getContacts().stream().filter(Contact::getActive).map(this::toContactDto).toList()
                : List.of();
        return new ClientResponse(c.getId(), c.getReference(), c.getCompanyName(),
                c.getContactName(), c.getEmail(), c.getPhone(), c.getAddress(),
                c.getLatitude(), c.getLongitude(), c.getActive(), c.getCreatedAt(), contactDtos);
    }

    private ContactDto toContactDto(Contact contact) {
        return new ContactDto(contact.getId(), contact.getFullName(),
                contact.getEmail(), contact.getPhone(), contact.getRole());
    }
}
