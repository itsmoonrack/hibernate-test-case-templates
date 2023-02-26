package event;

import domain.model.DomainEvent;
import org.hibernate.annotations.CompositeTypeRegistration;
import port.adapter.persistence.hibernate.DomainEventThatMayWorkUserType;

import java.time.Instant;

public class StoredEventThatMayWork {

    private StoredEventMapped event;
    private long eventId;
    private Instant occurredOn;

    public StoredEventThatMayWork(final DomainEvent event) {
        this.event = new StoredEventMapped(event, event.getClass().getName());
        this.occurredOn = event.occurredOn();
    }

    public DomainEvent event() {
        return this.event.eventBody();
    }

    public long eventId() {
        return this.eventId;
    }

    public Instant occurredOn() {
        return this.occurredOn;
    }

    public String typeName() {
        return this.event.typeName();
    }

    protected StoredEventThatMayWork() {
        // Needed by Hibernate.
    }
}
