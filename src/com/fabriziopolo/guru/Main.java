package com.fabriziopolo.guru;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {

//    public static final String sourcePath = "guru.txt";
    public static final String sourcePath = "/Volumes/ninja/guru.txt";
    static GuruDoc doc;

    public static void main(String[] args)
    {
        args = new String[]{"done"};

        //  Open "guru.txt" and try to parse it
        try {
            doc = readGuruDotTxt();
        }
        catch (GuruDocParsingException ex) {
            System.out.println("Parsing exception on line " + ex.lineNumber + ": " + ex.getMessage());
            failAndExit();
            return;
        }
        catch (FileNotFoundException ex) {
            System.out.println("Unable to open \"" + sourcePath + "\".  This folder doesn't contain a guru project.  Use 'guru init' to create one.");
            failAndExit();
            return;
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            failAndExit();
            return;
        }


        //
        if (args.length == 0) {
            recommendNextTask();
            return;
        }
        else {
            switch (args[0]) {
                case "what": {
                    recommendAllNextTasks();
                    break;
                }
                case "done": {
                    completeLastRecommendedTask();
                    break;
                }
                default: {
                    System.out.println("Unknown command \"" + args[0] + "\".");
                    failAndExit();
                }
            }
        }

    }

    static void failAndExit()
    {
        System.exit(1);
    }


    static GuruDoc readGuruDotTxt() throws FileNotFoundException, IOException, GuruDocParsingException
    {
        return readGuruDocFromFile(sourcePath);
    }

    static GuruDoc readGuruDocFromFile(String fileName) throws FileNotFoundException, IOException, GuruDocParsingException {
        // FileReader reads text files in the default encoding.
        FileReader fileReader = new FileReader(fileName);

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        ArrayList<String> lines = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        // Always close files.
        bufferedReader.close();

        return new GuruDoc(lines.toArray( new String[0] ));
    }

    static void recommendNextTask()
    {
        GuruItem[] topItems = doc.model.getTopItems();
        if (topItems.length == 0) {
            System.out.println("There are no tasks to do.  Add them to your 'guru.txt' file.");
        }
        System.out.println(topItems[0].getDescription());
    }

    static void recommendAllNextTasks()
    {
        GuruItem[] topItems = doc.model.getTopItems();
        if (topItems.length == 0) {
            System.out.println("There are no tasks to do.  Add them to your 'guru.txt' file.");
        }
        for (GuruItem item : topItems) {
            System.out.println(item.getDescription());
        }
    }

    static void completeLastRecommendedTask()
    {
        GuruItem[] topItems = doc.model.getTopItems();
        if (topItems.length == 0) {
            System.out.println("There are no tasks to complete.  Add new tasks to your 'guru.txt' file.");
            return;
        }

        //  Complete the first task
        GuruItem item = topItems[0];
        //  Find what line numbers refer to this item
        HashSet<Integer> linesToRemove = doc.getReferencingLineNumbers(item);

        //  Write out the guru.txt file leaving out linesToRemove
        File guruTxtFile = new File(sourcePath);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(guruTxtFile));
            for (int i=0; i < doc.lines.length; i++) {
                if (!linesToRemove.contains(i)) {
                    writer.write(doc.lines[i]);
                    writer.write("\n");
                }
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}




































