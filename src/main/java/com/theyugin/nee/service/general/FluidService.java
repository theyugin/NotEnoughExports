package com.theyugin.nee.service.general;

import codechicken.nei.util.NBTJson;
import com.theyugin.nee.config.ExportConfigOption;
import com.theyugin.nee.data.general.Fluid;
import com.theyugin.nee.render.StackRenderer;
import com.theyugin.nee.service.AbstractCacheableService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidService extends AbstractCacheableService<Fluid> {
    private final PreparedStatement insertStmt;

    @SneakyThrows
    public FluidService(Connection conn) {
        insertStmt = conn.prepareStatement(
                "insert or ignore into fluid (registry_name, display_name, nbt, icon) values (?, ?, ?, ?)");
    }

    @SneakyThrows
    public Fluid processFluidStack(FluidStack fluidStack) {
        val registryName = FluidRegistry.getDefaultFluidName(fluidStack.getFluid());
        val displayName = fluidStack.getLocalizedName();
        val nbt = fluidStack.tag != null ? NBTJson.toJson(fluidStack.tag) : "{}";
        val fluid = new Fluid(registryName, displayName, nbt);
        if (putInCache(fluid)) {
            return fluid;
        }
        byte[] icon;
        if (ExportConfigOption.ICONS.get()) {
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
