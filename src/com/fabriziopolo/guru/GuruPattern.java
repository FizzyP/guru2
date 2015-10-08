package com.fabriziopolo.guru;

import java.util.regex.Pattern;

/**
 * Created by fizzy on 10/7/15.
 */
public class GuruPattern {
    private String descriptor;

    GuruPattern(String descriptor) {
        this.descriptor = descriptor;
    }

    boolean matches(String text)
    {
        return Pattern.matches(descriptor, text);
    }

    boolean matches(GuruDocItem item)
    {
        return matches(item.text);
    }

    boolean matches(GuruItem item)
    {
        return matches(item.getDescription());
    }
}
