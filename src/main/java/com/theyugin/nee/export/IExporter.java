package com.theyugin.nee.export;

import java.sql.SQLException;

public interface IExporter {
    void run() throws SQLException;
    int progress();
    int total();
    String name();
    boolean running();
}
