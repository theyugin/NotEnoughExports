package com.theyugin.nee.component.export;

public interface IExporter {
    void run();

    int progress();

    int total();

    String name();

    boolean running();
}
