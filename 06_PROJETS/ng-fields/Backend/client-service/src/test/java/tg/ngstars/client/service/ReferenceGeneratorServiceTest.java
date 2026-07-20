package tg.ngstars.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tg.ngstars.client.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ReferenceGeneratorServiceTest {

    @Mock ClientRepository clientRepository;
    ReferenceGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new ReferenceGeneratorService(clientRepository);
    }

    @Test
    void generateNextReference_shouldReturnFormattedReference() {
        when(clientRepository.nextReference()).thenReturn("1");

        var result = service.generateNextReference();

        assertEquals("CLT-0001", result);
        verify(clientRepository).nextReference();
    }

    @Test
    void generateNextReference_largeNumber_shouldPadZeros() {
        when(clientRepository.nextReference()).thenReturn("42");

        var result = service.generateNextReference();

        assertEquals("CLT-0042", result);
    }

    @Test
    void generateNextReference_maxDigits_shouldReturnCorrectFormat() {
        when(clientRepository.nextReference()).thenReturn("9999");

        var result = service.generateNextReference();

        assertEquals("CLT-9999", result);
    }
}
