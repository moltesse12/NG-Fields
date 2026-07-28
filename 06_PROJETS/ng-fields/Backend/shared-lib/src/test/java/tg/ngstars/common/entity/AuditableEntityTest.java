package tg.ngstars.common.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditableEntity")
class AuditableEntityTest {

    private static class ConcreteAuditableEntity extends AuditableEntity {
        ConcreteAuditableEntity() {}
        ConcreteAuditableEntity(UUID id) { setId(id); }

        public void callOnCreate() throws Exception {
            Method method = AuditableEntity.class.getDeclaredMethod("onCreate");
            method.setAccessible(true);
            method.invoke(this);
        }

        public void callOnUpdate() throws Exception {
            Method method = AuditableEntity.class.getDeclaredMethod("onUpdate");
            method.setAccessible(true);
            method.invoke(this);
        }
    }

    @Nested
    @DisplayName("@PrePersist - onCreate()")
    class OnCreate {

        @Test
        @DisplayName("Definit createdAt et updatedAt a la creation")
        void definitCreatedAtEtUpdatedAt() throws Exception {
            var entity = new ConcreteAuditableEntity();
            var avant = OffsetDateTime.now();

            entity.callOnCreate();

            assertNotNull(entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            assertFalse(entity.getCreatedAt().isBefore(avant));
            assertEquals(entity.getCreatedAt(), entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("@PreUpdate - onUpdate()")
    class OnUpdate {

        @Test
        @DisplayName("Met a jour updatedAt sans toucher a createdAt")
        void metAJourUpdatedAt() throws Exception {
            var entity = new ConcreteAuditableEntity();
            entity.callOnCreate();
            var createdAt = entity.getCreatedAt();

            Thread.sleep(10);
            entity.callOnUpdate();

            assertEquals(createdAt, entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            assertTrue(entity.getUpdatedAt().isAfter(createdAt) || entity.getUpdatedAt().isEqual(createdAt));
        }
    }

    @Nested
    @DisplayName("Getter/Setter")
    class GetterSetter {

        @Test
        @DisplayName("Set et get createdBy et updatedBy")
        void setGetCreatedByUpdatedBy() {
            var entity = new ConcreteAuditableEntity();
            entity.setCreatedBy("user-1");
            entity.setUpdatedBy("user-2");

            assertEquals("user-1", entity.getCreatedBy());
            assertEquals("user-2", entity.getUpdatedBy());
        }
    }
}
