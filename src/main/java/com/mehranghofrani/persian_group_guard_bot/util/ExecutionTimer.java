package com.mehranghofrani.persian_group_guard_bot.util;

import java.util.Date;

public class ExecutionTimer {

    private Long start;
    private String name;

    public ExecutionTimer(String name) {
        start = System.nanoTime();
        this.name = name;
    }

    public void end() {
        Long elapsed = System.nanoTime() - start;
        System.out.println("time elapsed for " + name + ": " + elapsed);
    }
}
