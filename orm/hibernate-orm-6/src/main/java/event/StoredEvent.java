package event;

import domain.model.DomainEvent;

import java.time.Instant;

public class StoredEvent {

    private DomainEvent event;
    private long eventId;
    private Instant occurredOn;
    private String typeName;

    public StoredEvent(final DomainEvent event) {
        this.event = event;
        this.occurredOn = event.occurredOn();
        this.typeName = event.getClass().getName();
    }

    public DomainEvent event() {
        return this.event;
    }

    public long eventId() {
        return this.eventId;
    }

    public Instant occurredOn() {
        return this.occurredOn;
    }

    public String typeName() {
        return this.typeName;
    }

    protected StoredEvent() {
        // Needed by Hibernate.
    }
}
