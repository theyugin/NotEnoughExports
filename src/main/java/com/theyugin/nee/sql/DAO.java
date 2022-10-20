package com.theyugin.nee.sql;

import java.sql.Connection;

public abstract class DAO {
    protected final Connection conn;

    public DAO(Connection conn) {
        this.conn = conn;
    }
}
