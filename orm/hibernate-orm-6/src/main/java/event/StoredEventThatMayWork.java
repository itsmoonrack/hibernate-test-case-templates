package event;

import domain.model.DomainEvent;
import org.hibernate.annotations.CompositeTypeRegistration;
import port.adapter.persistence.hibernate.DomainEventThatMayWorkUserType;

import java.time.Instant;

@CompositeTypeRegistration(embeddableClass = StoredEventThatMayWork.ComponentMapped.class, userType = DomainEventThatMayWorkUserType.class)
public class StoredEventThatMayWork {

    private ComponentMapped event;
    private long eventId;
    private Instant occurredOn;

    public StoredEventThatMayWork(final DomainEvent event) {
        this.event = new ComponentMapped(event, event.getClass().getName());
        this.occurredOn = event.occurredOn();
    }

    public static class ComponentMapped {

        private DomainEvent eventBody;
        private String typeName;

        public ComponentMapped(final DomainEvent event, final String typeName) {
            this.eventBody = event;
            this.typeName = typeName;
        }

        public DomainEvent eventBody() {
            return this.eventBody;
        }

        public String typeName() {
            return this.typeName;
        }
    }

    public DomainEvent event() {
        return this.event.eventBody;
    }

    public long eventId() {
        return this.eventId;
    }

    public Instant occurredOn() {
        return this.occurredOn;
    }

    public String typeName() {
        return this.event.typeName;
    }

    protected StoredEventThatMayWork() {
        // Needed by Hibernate.
    }
}
