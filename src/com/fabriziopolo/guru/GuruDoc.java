package com.fabriziopolo.guru;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fizzy on 10/4/15.
 */
public class GuruDoc {
    private String[] lines;
    private GuruDocItem[] docItems;
    GuruModel model = new GuruModel();
    private HashMap<GuruDocItem, GuruItem> declarationDocItemToGuruItemMap = new HashMap<>();


    GuruDoc(String[] source) throws GuruDocParsingException {
        lines = source;
        updateDocItemsFromLines();
        updateModelFromDocItems();
    }


    void updateDocItemsFromLines() throws GuruDocParsingException
    {
        ArrayList<GuruDocItem> docItemList = new ArrayList<GuruDocItem>();
        for (int i=0; i < lines.length; i++) {
            if (lines[i].equals(""))
                continue;

            docItemList.add(GuruDocItem.new_FromSource(lines[i], i + 1));
        }
        docItems = docItemList.toArray(new GuruDocItem[docItemList.size()]);
    }


    void updateModelFromDocItems() throws GuruDocParsingException
    {
        //  First extract all the item definitions (worry about references later)
        addItemsToModelFromDocItems();

        //  Now traverse the tree and insert all the relationships implied by it
        //  Also handle references

        //  Keep track of where we are inside a tree expression
        ArrayList<GuruItem> dependencyStack = new ArrayList<>();
        for (GuruDocItem docItem : docItems)
        {
            //  Cannot indent more than one space at a time
            if (docItem.indentionLevel > dependencyStack.size()) {
                throw new GuruDocParsingException("Line overindented.", docItem.lineNumber);
            }
            //  Indent 1 tab
            else if (docItem.indentionLevel == dependencyStack.size()) {
                //  Note newItem might be null
                GuruItem item = getItemFromDocItem(docItem);
                //  Make head of dependency stack depend on this item
                if (dependencyStack.size() != 0)
                    dependencyStack.get(dependencyStack.size() - 1).dependencies.add(item);
                //  Indent
                dependencyStack.add(item);
            }
            else {
                //  Dedent 0 or more times
                //  Pop dependency stack at least once
                dependencyStack.remove(dependencyStack.size() - (dependencyStack.size() - docItem.indentionLevel));
                //  Create an item to put on top
                GuruItem item = getItemFromDocItem(docItem);
                //  Make head of dependency stack depend on this item
                if (dependencyStack.size() != 0)
                    dependencyStack.get(dependencyStack.size() - 1).dependencies.add(item);
                //  Indent
                dependencyStack.add(item);
            }
        }
    }

    GuruItem getItemFromDocItem(GuruDocItem docItem) throws GuruDocParsingException {
        if (docItem.isRef) {
            GuruItem[] matches = model.getMatches(docItem.pattern);
            if (matches.length == 1) {
                //  The ref makes sense!
                return matches[0];
            }
            else if (matches.length == 0) {
                throw new GuruDocParsingException("Reference matches no items.", docItem.lineNumber);
            }
            else {
                throw new GuruDocParsingException("Reference is ambiguous.", docItem.lineNumber);
            }
        }
        else {
            //  Guaranteed to be in this map due to addItemsToModelFromDocItems()
            return declarationDocItemToGuruItemMap.get(docItem);
        }
    }


    void addItemsToModelFromDocItems() {
        for (GuruDocItem docItem : docItems) {
            if (docItem.isRef)
                continue;

            GuruItem item = new GuruItem(docItem.text);
            model.addItem(item);

            declarationDocItemToGuruItemMap.put(docItem, item);
        }
    }

}

























