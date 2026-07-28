package tg.ngstars.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginatedResponse")
class PaginatedResponseTest {

    @Nested
    @DisplayName("of(content, page, size, totalElements)")
    class Of {

        @Test
        @DisplayName("Calcule totalPages correctement")
        void calculeTotalPagesCorrectement() {
            var response = PaginatedResponse.of(List.of("a", "b", "c"), 0, 10, 25);

            assertEquals(List.of("a", "b", "c"), response.content());
            assertEquals(0, response.page());
            assertEquals(10, response.size());
            assertEquals(25, response.totalElements());
            assertEquals(3, response.totalPages());
        }

        @Test
        @DisplayName("Calcule totalPages avec reste")
        void calculeTotalPagesAvecReste() {
            var response = PaginatedResponse.of(List.of("a"), 1, 5, 11);

            assertEquals(3, response.totalPages());
        }

        @Test
        @DisplayName("TotalPages est 0 quand totalElements est 0")
        void totalPagesZero() {
            var response = PaginatedResponse.of(List.of(), 0, 10, 0);

            assertEquals(0, response.totalPages());
        }

        @Test
        @DisplayName("Taille de page exacte donne le bon nombre de pages")
        void tailleExacte() {
            var response = PaginatedResponse.of(List.of(), 0, 5, 10);

            assertEquals(2, response.totalPages());
        }

        @Test
        @DisplayName("Lance exception si size <= 0")
        void lanceExceptionSiSizeZero() {
            assertThrows(IllegalArgumentException.class,
                () -> PaginatedResponse.of(List.of(), 0, 0, 10));
        }

        @Test
        @DisplayName("Lance exception si size negatif")
        void lanceExceptionSiSizeNegatif() {
            assertThrows(IllegalArgumentException.class,
                () -> PaginatedResponse.of(List.of(), 0, -1, 10));
        }
    }
}
