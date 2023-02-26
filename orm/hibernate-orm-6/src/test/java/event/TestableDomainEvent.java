package event;

import domain.model.DomainEvent;

import java.time.Instant;

public class TestableDomainEvent implements DomainEvent {

    private int eventVersion;
    private long id;
    private String name;
    private Instant occurredOn;

    public TestableDomainEvent(long id, String name) {
        this.eventVersion = 1;
        this.id = id;
        this.name = name;
        this.occurredOn = Instant.now();
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }

    private TestableDomainEvent() {
        // Needed by Jackson.
    }
}
