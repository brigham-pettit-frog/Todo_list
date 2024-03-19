package todo_list;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

class Todo implements Serializable {

    private String name;                    // I may have several lists going at once; they will have unique names.
                                            // however, this will have to be implemented separately as another program.
    private ArrayList<Section> sections = new ArrayList<Section>();
    private Section currentSection = null;  // if I'm zoomed into a section, I should remember which it is.

    public static void saveList(Todo list) {
        try {
            FileOutputStream fileOut = new FileOutputStream("lists/" + list.name + ".list");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(list);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, Todo> loadLists() {

        HashMap<String, Todo> ret = new HashMap<>();
        // For every file in lists directory:
            // load the corresponding Todo object
            // add it to the ret hashmap.
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    protected class Section {
        private String name;
        private ArrayList<Item> items = new ArrayList<Item>();
        private boolean collapsed = false; // I can collapse a section.

        protected Section(String name) {
            this.name = name;
        }

        protected void show() {
            System.out.println(name);
            if (!collapsed) {
                for (Item i : items) {
                    i.show();
                }
            }
            else {
                System.out.println("\t...");
            }
            System.out.println();
        }

        protected void addItem(String name) {
            items.add(new Item(name));
        }
    }

    protected class Item {
        private String name;            // name of item
        private boolean completed = false;      // whether it's done
        private Progress progressBar = null;   // progress bar

        protected class Progress {      // class for progress bar
            private float progress;
            protected void show() {
                // for now, let's represent this as (e.g. 30% = [///      ] )
                int numSlashes = Math.round(progress/10);
                System.out.print('[');
                for (int i = 0; i<10; i++) {
                    if (i < numSlashes) {
                        System.out.print('/');
                    } else {
                        System.out.print(' ');
                    }
                }
                System.out.print(']');
            }
            protected Progress(int progress) {
                this.progress = progress;
            }
        }

        protected void show() {
            System.out.print("\t");
            if (!completed) {
                System.out.print("_ ");
            } else {
                System.out.print("✓ ");
            }
            System.out.print(name);
            if (progressBar != null) {
                progressBar.show();
            }
            System.out.println();
        }

        protected Item(String name, Progress progressBar) {
            this.name = name;
            this.progressBar = progressBar;
        }

        protected Item(String name) {
            this.name = name;
        }

        protected void updateProgress(int progress) { // as percentage, i.e., 30% = updateProgress(30);
            progressBar = new Progress(30);
        }
    }

    public void addSection(String name) {
        sections.add(new Section(name));
    }

    public String getName() {
        return name;
    }

    public boolean hasSection(String name) {
        for (Section sec : sections) {  // O(N) but that's okay because I don't expect a large number of sections in a list.
            if (sec.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected Item getItem(String query) { // returns only the first item matching the query. Try to keep item names unique.
        for (Section sec : sections) {
            for (Item i : sec.items) {
                if (StringHelper.containsSubstring(i.name, query)) {
                    return i;
                }
            }
        }
        return null;
    }

    public void completeItem(String query) {
        Item item = getItem(query);
        if (item != null) {
            item.completed=true;
        }
    }

    public void addItem(String sectionName, String itemName) {
        Section sec = getSection(sectionName);
        if (sec != null) {
            sec.addItem(itemName);
        }
    }

    protected Section getSection(String name) {
        for (Section sec : sections) {  
            if (sec.name.equals(name)) {
                return sec;
            }
        }
        return null;
    }

    public void show() {
        clear();
        System.out.println(name + "\n\n");
        for (Section section : sections) {
            section.show();
        }
    }

    public Todo(String name) {
        this.name = name;
    }

}
