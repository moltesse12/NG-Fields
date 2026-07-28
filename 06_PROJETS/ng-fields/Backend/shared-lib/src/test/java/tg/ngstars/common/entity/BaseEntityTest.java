package tg.ngstars.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BaseEntity")
class BaseEntityTest {

    private static class ConcreteEntity extends BaseEntity {
        ConcreteEntity() {}
        ConcreteEntity(UUID id) { setId(id); }
    }

    @Nested
    @DisplayName("isNew()")
    class IsNew {

        @Test
        @DisplayName("Retourne true si id est null")
        void retourneTrueSiIdNull() {
            var entity = new ConcreteEntity();
            assertTrue(entity.isNew());
        }

        @Test
        @DisplayName("Retourne false si id est defini")
        void retourneFalseSiIdDefini() {
            var entity = new ConcreteEntity(UUID.randomUUID());
            assertFalse(entity.isNew());
        }
    }

    @Nested
    @DisplayName("equals()")
    class Equals {

        @Test
        @DisplayName("Meme instance est egale a elle-meme")
        void memeInstance() {
            var entity = new ConcreteEntity(UUID.randomUUID());
            assertEquals(entity, entity);
        }

        @Test
        @DisplayName("Deux entites avec meme id sont egales")
        void memeId() {
            var id = UUID.randomUUID();
            var e1 = new ConcreteEntity(id);
            var e2 = new ConcreteEntity(id);
            assertEquals(e1, e2);
        }

        @Test
        @DisplayName("Deux entites avec ids differents ne sont pas egales")
        void idsDifferents() {
            var e1 = new ConcreteEntity(UUID.randomUUID());
            var e2 = new ConcreteEntity(UUID.randomUUID());
            assertNotEquals(e1, e2);
        }

        @Test
        @DisplayName("Entite avec id null n'est egale a rien sauf elle-meme")
        void idNull() {
            var e1 = new ConcreteEntity();
            var e2 = new ConcreteEntity();
            assertNotEquals(e1, e2);
        }

        @Test
        @DisplayName("N'est pas egale a null")
        void pasEgaleANull() {
            var entity = new ConcreteEntity(UUID.randomUUID());
            assertNotEquals(null, entity);
        }

        @Test
        @DisplayName("N'est pas egale a un autre type")
        void pasEgaleAutreType() {
            var entity = new ConcreteEntity(UUID.randomUUID());
            assertNotEquals("string", entity);
        }
    }

    @Nested
    @DisplayName("hashCode()")
    class HashCode {

        @Test
        @DisplayName("Utilise la classe comme base du hashcode")
        void utiliseClasse() {
            var e1 = new ConcreteEntity(UUID.randomUUID());
            var e2 = new ConcreteEntity(UUID.randomUUID());
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        @DisplayName("Est coherent avec equals")
        void coherentAvecEquals() {
            var id = UUID.randomUUID();
            var e1 = new ConcreteEntity(id);
            var e2 = new ConcreteEntity(id);
            assertEquals(e1, e2);
            assertEquals(e1.hashCode(), e2.hashCode());
        }
    }

    @Nested
    @DisplayName("Getter/Setter")
    class GetterSetter {

        @Test
        @DisplayName("Set et get id fonctionnent")
        void setGetId() {
            var entity = new ConcreteEntity();
            var id = UUID.randomUUID();
            entity.setId(id);
            assertEquals(id, entity.getId());
        }
    }
}
