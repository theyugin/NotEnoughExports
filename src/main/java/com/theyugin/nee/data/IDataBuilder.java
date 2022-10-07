package com.theyugin.nee.data;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDataBuilder<T> {
    T save(Connection conn) throws SQLException;
}
