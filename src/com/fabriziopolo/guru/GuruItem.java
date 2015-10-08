package com.fabriziopolo.guru;

import java.util.HashSet;


/**
 * Created by fizzy on 10/4/15.
 */
public class GuruItem {
    private int priority = 0;
    private String description;
    final HashSet<GuruItem> dependencies = new HashSet<>();

    GuruItem(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }


    int getPriority() {
        return priority;
    }

    void setPriority(int p) {
        priority = p;
    }

    String getDescription() { return description; }

    void increasePriority(int value) {
        if (value > priority) {
            value = priority;
            for (GuruItem item : dependencies) {
                item.increasePriority(value);
            }
        }
    }
}
