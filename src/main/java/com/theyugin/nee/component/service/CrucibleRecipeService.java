package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;

public class CrucibleRecipeService {
    private final PreparedStatement crucibleInsertStmt;

    @Inject
    @SneakyThrows
    public CrucibleRecipeService(Connection conn) {
        crucibleInsertStmt = conn.prepareStatement("insert into crucibleRecipe values (?, ?)");
    }
}
