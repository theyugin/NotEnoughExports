package com.theyugin.nee.config;

import java.io.File;

import com.theyugin.nee.Tags;
import lombok.val;
import net.minecraftforge.common.config.Configuration;

public class Config {
    static final Configuration configuration = new Configuration(new File("config", Tags.MODID + ".cfg"));

    static class Categories {
        public static final String general = "general";
    }

    static void saveConfiguration() {
        configuration.save();
    }

    public static void synchronizeConfiguration() {
        configuration.load();
        // have to get properties after configuration load to have them associated with our Configuration instance
        for (val exportOption : ExportConfigOption.values()) {
            exportOption.refresh();
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
