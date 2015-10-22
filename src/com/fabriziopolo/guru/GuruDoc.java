package com.fabriziopolo.guru;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by fizzy on 10/4/15.
 */
public class GuruDoc {
    public String[] lines;
    private GuruDocItem[] docItems;
    GuruModel model = new GuruModel();
    private HashMap<GuruDocItem, GuruItem> declarationDocItemToGuruItemMap = new HashMap<>();
    private HashMap<GuruItem, HashSet<Integer>> itemToLineNumbersMap = new HashMap<>();


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
            docItemList.add( GuruDocItem.new_FromSource(lines[i], i) );
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
            //  Keep track of what line numbers refer to what items
            GuruItem item = getItemFromDocItem(docItem);
            if (docItem.isRef) {
                itemToLineNumbersMap.get(item).add(docItem.lineNumber);
            }

            //  Cannot indent more than one space at a time
            if (docItem.indentionLevel > dependencyStack.size()) {
                throw new GuruDocParsingException("Line overindented.", docItem.lineNumber);
            }
            //  Indent 1 tab
            else if (docItem.indentionLevel == dependencyStack.size()) {
                //  Note newItem might be null
                //  Make head of dependency stack depend on this item
                if (dependencyStack.size() != 0) {
                    GuruItem parentItem = dependencyStack.get(dependencyStack.size() - 1);
                    parentItem.dependencies.add(item);
                    //  If X depends on Y then Y is at least as important as X.
                    if (parentItem.getPriority() > item.getPriority()) {
                        item.setPriority(parentItem.getPriority());
                    }
                }
                //  Indent
                dependencyStack.add(item);
            }
            else {
                //  Dedent 0 or more times
                //  Pop dependency stack at least once
                int dedentSize = dependencyStack.size() - docItem.indentionLevel;
                for (int i=0; i < dedentSize; i++) {
                    dependencyStack.remove(dependencyStack.size() - 1);
                }
                //  Create an item to put on top
                //  Make head of dependency stack depend on this item
                if (dependencyStack.size() != 0) {
                    GuruItem parentItem = dependencyStack.get(dependencyStack.size() - 1);
                    parentItem.dependencies.add(item);
                    //  If X depends on Y then Y is at least as important as X.
                    if (parentItem.getPriority() > item.getPriority()) {
                        item.setPriority(parentItem.getPriority());
                    }
                }
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
                throw new GuruDocParsingException("Reference \"" + docItem.text + "\" matches no items.", docItem.lineNumber);
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

            GuruItem item = new GuruItem(docItem.text, docItem.priority);
            model.addItem(item);

            declarationDocItemToGuruItemMap.put(docItem, item);

            HashSet<Integer> lineNumbers = new HashSet<>();
            lineNumbers.add(docItem.lineNumber);
            itemToLineNumbersMap.put(item, lineNumbers);
        }
    }


    HashSet<Integer> getReferencingLineNumbers(GuruItem item)
    {
        return itemToLineNumbersMap.get(item);
    }
}

























