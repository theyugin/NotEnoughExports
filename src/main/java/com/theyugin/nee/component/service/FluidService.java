package com.theyugin.nee.component.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.Config;
import com.theyugin.nee.persistence.general.Fluid;
import com.theyugin.nee.render.RenderQuery;
import com.theyugin.nee.render.RenderState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

@Singleton
public class FluidService extends AbstractCacheableService<Fluid> {
    private final PreparedStatement insertStmt;

    @Inject
    @SneakyThrows
    public FluidService(@NonNull Connection conn) {
        insertStmt = conn.prepareStatement(
                "insert or ignore into fluid (registry_name, display_name, nbt, icon) values (?, ?, ?, ?)");
    }

    @SneakyThrows
    public Fluid processFluidStack(FluidStack fluidStack) {
        val registryName = FluidRegistry.getDefaultFluidName(fluidStack.getFluid());
        val displayName = fluidStack.getLocalizedName();
        val fluid = Fluid.builder()
                .registryName(registryName)
                .displayName(displayName)
                .build();
        new FluidStack(fluidStack, 1);
        if (putInCache(fluid)) {
            return fluid;
        }
        byte[] icon;
        if (Config.exportIcons()) {
            RenderState.queueRender(RenderQuery.of(fluidStack));
            icon = RenderState.getFluidRenderResult();
        } else {
            icon = null;
        }
        insertStmt.setString(1, registryName);
        insertStmt.setString(2, displayName);
        insertStmt.setBytes(3, icon);
        insertStmt.executeUpdate();
        return fluid;
    }
}
