package com.fabriziopolo.guru;

/**
 * Created by fizzy on 10/4/15.
 */
public class GuruDocParsingException extends Exception
{
    int lineNumber;
    public GuruDocParsingException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }
}
