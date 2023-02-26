package port.adapter.persistence.hibernate;

import domain.model.DomainEvent;
import event.EventSerializer;
import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class DomainEventUserType implements CompositeUserType {
    @Override
    public String[] getPropertyNames() {
        return new String[] { "eventBody", "typeName" };
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[] {StringType.INSTANCE, StringType.INSTANCE };
    }

    @Override
    public Object getPropertyValue(final Object component, final int property) throws HibernateException {
        switch (property) {
            case 0:
                return EventSerializer.instance().serialize((DomainEvent) component);
            case 1:
                return component.getClass().getName();
        }
        throw new HibernateException("Illegal property index: " + property);
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SharedSessionContractImplementor session, final Object owner) throws HibernateException, SQLException {
        final String eventBody = (String) rs.getObject(names[0]);
        final String typeName = (String) rs.getObject(names[1]);

        final ClassLoaderService classLoaderService = session.getFactory().getServiceRegistry().getService(ClassLoaderService.class);

        return EventSerializer.instance().deserialize(eventBody, classLoaderService.classForName(typeName));
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SharedSessionContractImplementor session) throws HibernateException, SQLException {
        final String serializedEvent = EventSerializer.instance().serialize((DomainEvent) value);
        st.setString(index, serializedEvent);
        st.setString(index + 1, value.getClass().getName());
    }

    @Override
    public void setPropertyValue(final Object component, final int property, final Object value) throws HibernateException {
        // immutable
    }

    @Override
    public Class returnedClass() {
        return DomainEvent.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value; // immutable
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value, final SharedSessionContractImplementor session) throws HibernateException {
        return new String[] { EventSerializer.instance().serialize((DomainEvent) value), value.getClass().getName() };
    }

    @Override
    public Object assemble(final Serializable cached, final SharedSessionContractImplementor session, final Object owner) throws HibernateException {
        final String[] parts = (String[]) cached;

        final ClassLoaderService classLoaderService = session.getFactory().getServiceRegistry().getService(ClassLoaderService.class);

        return EventSerializer.instance().deserialize(parts[0], classLoaderService.classForName(parts[1]));
    }

    @Override
    public Object replace(final Object original, final Object target, final SharedSessionContractImplementor session, final Object owner) throws HibernateException {
        return original;
    }
}
