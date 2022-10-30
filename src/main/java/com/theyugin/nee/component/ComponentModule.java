package com.theyugin.nee.component;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.theyugin.nee.util.NEEUtils;
import java.sql.Connection;
import java.sql.SQLException;

public class ComponentModule extends AbstractModule {
    @Provides
    static Connection getConnection() throws SQLException {
        return NEEUtils.createConnection();
    }

    @Override
    protected void configure() {}
}
