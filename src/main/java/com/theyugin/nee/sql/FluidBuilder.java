package com.theyugin.nee.sql;

import com.theyugin.nee.data.Fluid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FluidBuilder implements IDataBuilder<Fluid> {
    private String unlocalizedName;
    private String localizedName;

    public FluidBuilder setUnlocalizedName(String name) {
        unlocalizedName = name;
        return this;
    }

    public FluidBuilder setLocalizedName(String name) {
        localizedName = name;
        return this;
    }

    @Override
    public Fluid save(Connection conn) throws SQLException {
        PreparedStatement stmt =
                conn.prepareStatement("insert or ignore into fluid (unlocalizedName, localizedName) values (?, ?)");
        stmt.setString(1, unlocalizedName);
        stmt.setString(2, localizedName);
        stmt.executeUpdate();
        return new Fluid(unlocalizedName, localizedName);
    }
}
