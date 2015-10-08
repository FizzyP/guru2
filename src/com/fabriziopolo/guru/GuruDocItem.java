package com.fabriziopolo.guru;


/**
 * Created by fizzy on 10/4/15.
 */
public class GuruDocItem {
    int indentionLevel;
    int priority;
    boolean isRef;
    String text;
    GuruPattern pattern;    //  if "isRef" then there's a pattern here
    int lineNumber;

    static GuruDocItem new_FromSource(String sourceLine, int lineNumber) throws GuruDocParsingException
    {
        GuruDocItem docItem = new GuruDocItem();

        docItem.lineNumber = lineNumber;

        if (sourceLine.equals(""))
            return null;

        //  Read indention: look for tabs on the left
        int i;
        for (i=0; i < sourceLine.length(); i++) {
            char c = sourceLine.charAt(i);
            if (c != '\t') {
                if (c == ' ') {
                    throw new GuruDocParsingException("A guru item begins with one or more tabs.  Leading spaces are not permitted.  Ensure your editor is not converting tabs to spaces.", lineNumber);
                }
                else {
                    break;
                }
            }
            docItem.indentionLevel++;
        }

        //  Read stars and dashes to count rank
        for (; i < sourceLine.length(); i++) {
            char c = sourceLine.charAt(i);
            if (c == '*') {
                docItem.priority++;
            }
            else if (c == '-') {
                docItem.priority--;
            }
            else {
                break;
            }
        }

        String body = sourceLine.substring(i, sourceLine.length());

        //  Determine whether the "body" is a new definition or a quoted regex ref
        if (body == "") {
            throw new GuruDocParsingException("Item cannot have an empty body.", lineNumber);
        }
        if (body.charAt(0) == '"') {
            if (body.length() < 3) {
                throw new GuruDocParsingException("Cannot use empty regex to refer to an item.", lineNumber);
            }
            if (body.charAt(body.length() - 1) != '"') {
                throw new GuruDocParsingException("Item regex ref missing final quote.", lineNumber);
            }
            //  We found a decent regex ref
            docItem.isRef = true;
            docItem.text = body.substring(1, body.length() - 1);
        }
        else {
            docItem.text = body;
        }


        //  Parse references
        if (docItem.isRef) {
            try {
                docItem.pattern = new GuruPattern(docItem.text);
            }
            catch (Exception e) {
                throw new GuruDocParsingException("Bad item reference pattern \"" + body + "\": " + e.getMessage(), lineNumber);
            }
        }

        return docItem;
    }
}






















