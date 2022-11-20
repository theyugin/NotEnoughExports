package com.theyugin.nee.export.exporter;

import com.theyugin.nee.NotEnoughExports;

public abstract class AbstractExporter {
    int progress = 0;

    public abstract void run();

    abstract int total();

    abstract String name();

    void logProgress() {
        if (progress % 1000 == 0) {
            NotEnoughExports.info(String.format("Exporting %s: %d/%d", name(), progress, total()));
        }
    }
}
