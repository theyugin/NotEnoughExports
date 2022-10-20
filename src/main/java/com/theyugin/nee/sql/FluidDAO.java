package com.theyugin.nee.sql;

import com.theyugin.nee.data.Fluid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FluidDAO extends DAO {
    public FluidDAO(Connection conn) {
        super(conn);
    }

    public Fluid create(String unlocalizedName, String localizedName, byte[] icon) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "insert or ignore into fluid (unlocalizedName, localizedName, icon) values (?, ?, ?)");
        stmt.setString(1, unlocalizedName);
        stmt.setString(2, localizedName);
        stmt.setBytes(3, icon);
        stmt.executeUpdate();
        return new Fluid(unlocalizedName, localizedName);
    }
}
