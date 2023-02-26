package port.adapter.persistence.hibernate;

import domain.model.DomainEvent;
import event.EventSerializer;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

public class DomainEventUserType implements CompositeUserType<DomainEvent> {

    public static class DomainEventMapper {
        String eventBody;
        String typeName;
    }

    @Override
    public Object getPropertyValue(final DomainEvent component, final int property) throws HibernateException {
        // alphabetical
        return switch (property) {
            case 0 -> EventSerializer.instance().serialize(component);
            case 1 -> component.getClass().getName();
            default -> null;
        };
    }

    @Override
    public DomainEvent instantiate(final ValueAccess values, final SessionFactoryImplementor sessionFactory) {
        // alphabetical
        final String eventBody = values.getValue(0, String.class);
        final String typeName = values.getValue(1, String.class);

        final ClassLoaderService classLoaderService = sessionFactory.getServiceRegistry().getService(ClassLoaderService.class);

        return EventSerializer.instance().deserialize(eventBody, classLoaderService.classForName(typeName));
    }

    @Override
    public Class<?> embeddable() {
        // Why it is not used as a "projection embeddable" ?
        // I thought this would be a virtual component for mapping only, but it seems we need a real component.
        return DomainEventMapper.class;
    }

    @Override
    public Class<DomainEvent> returnedClass() {
        return DomainEvent.class;
    }

    @Override
    public boolean equals(final DomainEvent x, final DomainEvent y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final DomainEvent x) {
        return Objects.hashCode(x);
    }

    @Override
    public DomainEvent deepCopy(final DomainEvent value) {
        return value; // immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final DomainEvent value) {
        return new String[] { EventSerializer.instance().serialize(value), value.getClass().getName() };
    }

    @Override
    @SuppressWarnings("unchecked")
    public DomainEvent assemble(final Serializable cached, final Object owner) {
        final String[] parts = (String[]) cached;
        try {
            return EventSerializer.instance().deserialize(parts[0], (Class<? extends DomainEvent>) Class.forName(parts[1]));
        } catch (ClassNotFoundException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public DomainEvent replace(final DomainEvent detached, final DomainEvent managed, final Object owner) {
        return detached;
    }
}
