package com.theyugin.nee.render;

import codechicken.nei.util.NBTJson;
import com.google.gson.JsonElement;
import com.theyugin.nee.util.StackUtils;
import java.util.Objects;
import lombok.val;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.api.aspects.Aspect;

public class RenderQuery {
    private final RenderType renderType;
    private final String registryName;
    private final Integer metadata;
    private final JsonElement nbt;

    private RenderQuery(RenderType renderType, String registryName, Integer metadata, JsonElement nbt) {
        this.renderType = renderType;
        this.registryName = registryName;
        this.metadata = metadata;
        this.nbt = nbt;
    }

    public static RenderQuery of(ItemStack itemStack) {
        val itemRegistryName = net.minecraft.item.Item.itemRegistry.getNameForObject(itemStack.getItem());
        val nbt = itemStack.hasTagCompound() ? NBTJson.toJsonObject(itemStack.stackTagCompound) : null;
        val metadata = StackUtils.isNotWildcard(itemStack) ? itemStack.getItemDamage() : 0;
        return new RenderQuery(RenderType.ITEM, itemRegistryName, metadata, nbt);
    }

    public static RenderQuery of(FluidStack fluidStack) {
        val registryName = FluidRegistry.getDefaultFluidName(fluidStack.getFluid());
        val nbt = fluidStack.tag != null ? NBTJson.toJsonObject(fluidStack.tag) : null;
        return new RenderQuery(RenderType.FLUID, registryName, 0, nbt);
    }

    public static RenderQuery of(Aspect aspect) {
        return new RenderQuery(RenderType.ASPECT, aspect.getTag(), 0, null);
    }

    public FluidStack getFluidStack() {
        if (renderType == RenderType.FLUID) {
            val fluidStack = new FluidStack(FluidRegistry.getFluid(registryName), 0);
            if (nbt != null) {
                fluidStack.tag = (NBTTagCompound) NBTJson.toNbt(nbt);
            }
            return fluidStack;
        }
        return null;
    }

    public ItemStack getItemStack() {
        if (renderType == RenderType.ITEM) {
            val itemStack = new ItemStack((Item) Item.itemRegistry.getObject(registryName), 1, metadata);
            if (nbt != null) {
                itemStack.stackTagCompound = (NBTTagCompound) NBTJson.toNbt(nbt);
            }
            return itemStack;
        }
        return null;
    }

    public Aspect getAspect() {
        if (renderType == RenderType.ASPECT) {
            return Aspect.getAspect(registryName);
        }
        return null;
    }

    public RenderType renderType() {
        return renderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderQuery that = (RenderQuery) o;
        return renderType == that.renderType
                && registryName.equals(that.registryName)
                && metadata.equals(that.metadata)
                && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, registryName, metadata, nbt);
    }
}
