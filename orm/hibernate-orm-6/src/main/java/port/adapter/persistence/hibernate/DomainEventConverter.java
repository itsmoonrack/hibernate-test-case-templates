package port.adapter.persistence.hibernate;

import domain.model.DomainEvent;
import jakarta.persistence.AttributeConverter;

public class DomainEventConverter implements AttributeConverter<DomainEvent, String> {

    @Override
    public String convertToDatabaseColumn(final DomainEvent attribute) {
        return null;
    }

    @Override
    public DomainEvent convertToEntityAttribute(final String dbData) {
        return null;
    }
}
