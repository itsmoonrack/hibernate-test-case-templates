package port.adapter.persistence.hibernate;

import domain.model.DomainEvent;
import event.EventSerializer;
import event.StoredEventThatMayWork;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

public class DomainEventThatMayWorkUserType implements CompositeUserType<StoredEventThatMayWork.ComponentMapped> {

    public static class DomainEventMapper {
        String eventBody;
        String typeName;
    }

    @Override
    public Object getPropertyValue(final StoredEventThatMayWork.ComponentMapped component, final int property) throws HibernateException {
        // alphabetical
        return switch (property) {
            case 0 -> EventSerializer.instance().serialize(component.eventBody());
            case 1 -> component.typeName();
            default -> null;
        };
    }

    @Override
    public StoredEventThatMayWork.ComponentMapped instantiate(final ValueAccess values, final SessionFactoryImplementor sessionFactory) {
        // alphabetical
        final String eventBody = values.getValue(0, String.class);
        final String typeName = values.getValue(1, String.class);

        final ClassLoaderService classLoaderService = sessionFactory.getServiceRegistry().getService(ClassLoaderService.class);

        return new StoredEventThatMayWork.ComponentMapped(
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
    public Class<StoredEventThatMayWork.ComponentMapped> returnedClass() {
        return StoredEventThatMayWork.ComponentMapped.class;
    }

    @Override
    public boolean equals(final StoredEventThatMayWork.ComponentMapped x, final StoredEventThatMayWork.ComponentMapped y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final StoredEventThatMayWork.ComponentMapped x) {
        return Objects.hashCode(x);
    }

    @Override
    public StoredEventThatMayWork.ComponentMapped deepCopy(final StoredEventThatMayWork.ComponentMapped value) {
        return value; // immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final StoredEventThatMayWork.ComponentMapped value) {
        return new String[] { EventSerializer.instance().serialize(value.eventBody()), value.typeName() };
    }

    @Override
    @SuppressWarnings("unchecked")
    public StoredEventThatMayWork.ComponentMapped assemble(final Serializable cached, final Object owner) {
        final String[] parts = (String[]) cached;
        try {
            return new StoredEventThatMayWork.ComponentMapped(
                    EventSerializer.instance().deserialize(parts[0], (Class<? extends DomainEvent>) Class.forName(parts[1])),
                    parts[1]);
        } catch (ClassNotFoundException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public StoredEventThatMayWork.ComponentMapped replace(final StoredEventThatMayWork.ComponentMapped detached, final StoredEventThatMayWork.ComponentMapped managed, final Object owner) {
        return detached;
    }
}
