package com.theyugin.nee.component.service;

import codechicken.nei.util.NBTJson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.theyugin.nee.Config;
import com.theyugin.nee.persistence.general.Fluid;
import com.theyugin.nee.render.StackRenderer;
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
        val nbt = fluidStack.tag != null ? NBTJson.toJson(fluidStack.tag) : "{}";
        val fluid = Fluid.builder()
                .registryName(registryName)
                .displayName(displayName)
                .nbt(nbt)
                .build();
        if (putInCache(fluid)) {
            return fluid;
        }
        byte[] icon;
        if (Config.exportIcons()) {
            icon = StackRenderer.renderIcon(fluidStack);
        } else {
            icon = null;
        }
        insertStmt.setString(1, registryName);
        insertStmt.setString(2, displayName);
        insertStmt.setString(3, nbt);
        insertStmt.setBytes(4, icon);
        insertStmt.executeUpdate();
        return fluid;
    }
}
