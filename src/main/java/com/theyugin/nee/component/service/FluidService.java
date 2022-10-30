package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.persistence.general.Fluid;
import com.theyugin.nee.render.RenderQuery;
import com.theyugin.nee.render.RenderState;
import com.theyugin.nee.render.RenderType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import lombok.val;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@Singleton
public class FluidService {
    private final PreparedStatement insertStmt;
    private final Set<Fluid> fluidCache = new HashSet<>();

    @Inject
    public FluidService(@NonNull Connection conn) throws SQLException {
        insertStmt = conn.prepareStatement(
                "insert or ignore into fluid (registry_name, display_name, icon) values (?, ?, ?)");
    }

    public Fluid processFluidStack(FluidStack fluidStack) throws SQLException {
        val registryName = FluidRegistry.getDefaultFluidName(fluidStack.getFluid());
        val displayName = fluidStack.getLocalizedName();
        val fluid = Fluid.builder()
                .registryName(registryName)
                .displayName(displayName)
                .build();
        if (fluidCache.contains(fluid)) {
            return fluid;
        }
        fluidCache.add(fluid);
        RenderState.queueRender(new RenderQuery(RenderType.FLUID, registryName));
        val icon = RenderState.getFluidRenderResult();
        insertStmt.setString(1, registryName);
        insertStmt.setString(2, displayName);
        insertStmt.setBytes(3, icon);
        insertStmt.executeUpdate();
        return fluid;
    }
}
