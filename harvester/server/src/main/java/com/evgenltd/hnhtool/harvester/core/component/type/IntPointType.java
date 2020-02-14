package com.evgenltd.hnhtool.harvester.core.component.type;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 14-02-2020 18:18
 */
public class IntPointType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.INTEGER, Types.INTEGER};
    }

    @Override
    public Class<IntPoint> returnedClass() {
        return IntPoint.class;
    }

    @Override
    public boolean equals(final Object left, final Object right) throws HibernateException {
        return Objects.equals(left, right);
    }

    @Override
    public int hashCode(final Object o) throws HibernateException {
        return Objects.hashCode(o);
    }

    @Override
    public Object nullSafeGet(
            final ResultSet resultSet,
            final String[] names,
            final SharedSessionContractImplementor session,
            final Object owner
    ) throws HibernateException, SQLException {
        final int x = resultSet.getInt(names[0]);
        if (resultSet.wasNull()) {
            return new IntPoint();
        }

        final int y = resultSet.getInt(names[1]);
        if (resultSet.wasNull()) {
            return new IntPoint();
        }

        return new IntPoint(x, y);
    }

    @Override
    public void nullSafeSet(
            final PreparedStatement statement,
            final Object object,
            final int index,
            final SharedSessionContractImplementor session
    ) throws HibernateException, SQLException {
        if (object == null) {
            statement.setNull(index, Types.INTEGER);
            statement.setNull(index + 1, Types.INTEGER);
        } else {
            final IntPoint point = (IntPoint) object;
            statement.setInt(index, point.getX());
            statement.setInt(index + 1, point.getY());
        }
    }

    @Override
    public Object deepCopy(final Object object) throws HibernateException {
        final IntPoint point = (IntPoint) object;
        return new IntPoint(point.getY(), point.getY());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object object) throws HibernateException {
        final IntPoint point = (IntPoint) object;
        return point.asString();
    }

    @Override
    public Object assemble(final Serializable serializable, final Object owner) throws HibernateException {
        final String cached = (String) serializable;
        return IntPoint.valueOf(cached);
    }

    @Override
    public Object replace(final Object left, final Object right, final Object owner) throws HibernateException {
        return left;
    }
}
