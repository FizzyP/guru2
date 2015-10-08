package com.fabriziopolo.guru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by fizzy on 10/4/15.
 */
public class GuruModel
{
    private HashSet<GuruItem> items = new HashSet<>();

    void addItem(GuruItem item)
    {
        items.add(item);
    }

    void addDependency(GuruItem item, GuruItem dependency)
    {
        item.dependencies.add(dependency);

        //  Our dependencies must have at least our priority
        if (item.getPriority() > dependency.getPriority()) {
            dependency.increasePriority(item.getPriority());
        }
    }

    void removeItem(GuruItem item)
    {
        //  Remove all dependencies on this item
        for (GuruItem i : items) {
            i.dependencies.remove(item);
        }

        items.remove(item);
    }

    GuruItem[] getTopItems()
    {
        //  Find the top priority items
        HashSet<GuruItem> topItems = new HashSet<>();
        int topPriority = Integer.MIN_VALUE;
        for (GuruItem item : items) {
            if (item.getPriority() > topPriority) {
                topItems = new HashSet<>();
                topItems.add(item);
                topPriority = item.getPriority();
            }
            else if (item.getPriority() == topPriority) {
                topItems.add(item);
            }
        }

        //  Remove items that are dependent on others
        HashSet<GuruItem> topItemsCopy = new HashSet<>(topItems);
        for (GuruItem item : topItemsCopy) {
            if (item.dependencies.size() != 0) {
                topItems.remove(item);
            }
        }

        return topItems.toArray( new GuruItem[topItems.size()] );
    }

    GuruItem[] getMatches(GuruPattern pattern)
    {
        ArrayList<GuruItem> matches = new ArrayList<>();
        for (GuruItem item : items) {
            if (pattern.matches(item))
                matches.add(item);
        }
        return matches.toArray(new GuruItem[matches.size()]);
    }

}























