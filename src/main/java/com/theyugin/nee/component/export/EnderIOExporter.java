package com.theyugin.nee.component.export;

import com.theyugin.nee.util.StackUtils;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.OreDictionaryRecipeInput;
import crazypants.enderio.machine.vat.VatRecipeManager;
import lombok.val;

public class EnderIOExporter implements IExporter {
    private int progress = 0;
    private int total = 0;
    private boolean running = false;

    @Override
    public int progress() {
        return 0;
    }

    @Override
    public int total() {
        return 0;
    }

    @Override
    public String name() {
        return "EnderIO recipes";
    }

    @Override
    public boolean running() {
        return running;
    }

    private void processRecipe(IRecipe recipe, String type) {
        for (val input : recipe.getInputs()) {
            if (input instanceof OreDictionaryRecipeInput) {
                String oreName;
                try {
                    val field = OreDictionaryRecipeInput.class.getDeclaredField("oreId");
                    field.setAccessible(true);
                    oreName = (String) field.get(input);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    oreName = StackUtils.getOreDictValue(input.getEquivelentInputs());
                }
            }
        }
    }

    @Override
    public void run() {
        for (IRecipe recipe : VatRecipeManager.getInstance().getRecipes()) {
            processRecipe(recipe, "vat");
        }
    }
}
