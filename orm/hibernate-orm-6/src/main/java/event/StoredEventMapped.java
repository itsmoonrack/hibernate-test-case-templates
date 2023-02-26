package event;

import domain.model.DomainEvent;
import jakarta.persistence.Embeddable;

@Embeddable
public class StoredEventMapped {

    private DomainEvent eventBody;
    private String typeName;

    public StoredEventMapped(final DomainEvent event, final String typeName) {
        this.eventBody = event;
        this.typeName = typeName;
    }

    public DomainEvent eventBody() {
        return this.eventBody;
    }

    public String typeName() {
        return this.typeName;
    }

    protected StoredEventMapped() {
        // Needed by Hibernate.
    }
}
