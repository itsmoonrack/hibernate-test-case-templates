package port.adapter.persistence.hibernate;

import domain.model.DomainEvent;
import event.EventSerializer;
import event.StoredEventMapped;
import event.StoredEventThatMayWork;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

public class DomainEventThatMayWorkUserType implements CompositeUserType<StoredEventMapped> {

    public static class DomainEventMapper {
        String eventBody;
        String typeName;
    }

    @Override
    public Object getPropertyValue(final StoredEventMapped component, final int property) throws HibernateException {
        // alphabetical
        return switch (property) {
            case 0 -> EventSerializer.instance().serialize(component.eventBody());
            case 1 -> component.typeName();
            default -> null;
        };
    }

    @Override
    public StoredEventMapped instantiate(final ValueAccess values, final SessionFactoryImplementor sessionFactory) {
        // alphabetical
        final String eventBody = values.getValue(0, String.class);
        final String typeName = values.getValue(1, String.class);

        final ClassLoaderService classLoaderService = sessionFactory.getServiceRegistry().getService(ClassLoaderService.class);

        return new StoredEventMapped(
                EventSerializer.instance().deserialize(eventBody, classLoaderService.classForName(typeName)),
                typeName);
    }

    @Override
    public Class<?> embeddable() {
        // Why it is not used as a "projection embeddable" ?
        // I thought this would be a virtual component for mapping only, but it seems we need a real component.
        return DomainEventMapper.class;
    }

    @Override
    public Class<StoredEventMapped> returnedClass() {
        return StoredEventMapped.class;
    }

    @Override
    public boolean equals(final StoredEventMapped x, final StoredEventMapped y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final StoredEventMapped x) {
        return Objects.hashCode(x);
    }

    @Override
    public StoredEventMapped deepCopy(final StoredEventMapped value) {
        return value; // immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final StoredEventMapped value) {
        return new String[] { EventSerializer.instance().serialize(value.eventBody()), value.typeName() };
    }

    @Override
    @SuppressWarnings("unchecked")
    public StoredEventMapped assemble(final Serializable cached, final Object owner) {
        final String[] parts = (String[]) cached;
        try {
            return new StoredEventMapped(
                    EventSerializer.instance().deserialize(parts[0], (Class<? extends DomainEvent>) Class.forName(parts[1])),
                    parts[1]);
        } catch (ClassNotFoundException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public StoredEventMapped replace(final StoredEventMapped detached, final StoredEventMapped managed, final Object owner) {
        return detached;
    }
}
