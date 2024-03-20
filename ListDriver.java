package todo_list;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;



public class ListDriver {

    private static HashMap<String, Todo> lists = new HashMap<>();
    private static Todo currentList = null;

    private static void clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    private static void error(String msg) {
        System.out.println("Error: " + msg);
    }
    
    private static boolean await_input() {
        System.out.print("input command: ");

        Scanner scanLine = new Scanner(System.in);

        String line = "";

        while (!scanLine.hasNextLine()) {
            scanLine.close();
            scanLine = new Scanner(System.in);
        }

        Scanner sc = new Scanner(line);

        String cmd = "";

        if (sc.hasNext()) {
            cmd = sc.next();
        }
        

        if (cmd.equals("quit") || cmd.equals("kill") || cmd.equals("q") || cmd.equals("exit")) {
            sc.close();
            scanLine.close();
            return true;
        }

        if (cmd.equals("clear")) {
            clear();
            sc.close();
            scanLine.close();
            return false;
        }

        if (cmd.equals("progress")) { // progress
            int prog = sc.nextInt();
            cmd = sc.nextLine().substring(1);
            currentList.updateProgress(cmd, prog);
        }

        if (cmd.equals("complete")) { // complete
            cmd = sc.nextLine();

            if (currentList != null) {
                currentList.completeItem(cmd);
            }

        }

        if (cmd.equals("list")) {                                       // list
            if (lists.size() == 0 && !sc.hasNext()) {
                error("no lists to report.");
                sc.close();
                scanLine.close();
                return false;
            } else if (lists.size() == 1) {
                currentList = lists.get(lists.keySet().iterator().next());
                currentList.show();

            } else {

                // list exit will exit from the current list. Otherwise, remain in the current list.

                if (sc.hasNext()) {
                    cmd = sc.next();

                    if (cmd.equals("exit")) {       // list exit
                        
                        if (currentList != null) {  // save the list if there's a list to save
                            Todo.saveList(currentList);
                        }
                        
                        currentList = null;
                        clear();
                        sc.close();
                        scanLine.close();
                        return false;
                    }

                } else {

                    if (currentList != null) {
                        cmd = currentList.getName();        // list "name"
                    } else {
                        System.out.println("Select a list: ");
                        for (String name : lists.keySet()) {
                            System.out.print(name + " ");
                        }
                        System.out.println();

                        line = scanLine.nextLine();
                        sc.close();
                        sc = new Scanner(line);
                        cmd = sc.next();                    // list, then, "name"
                    }
                    
                }

                if (!lists.containsKey(cmd)) {
                    error("No such list exists.");
                } else {
                    currentList = lists.get(cmd);
                    currentList.show();
                }
            }
            

        }

        if (cmd.equals("add")) {            // add
            cmd = sc.next();
            if (cmd.equals("list")) {       // add list
                cmd = sc.next();                     // add list "cmd"
                if (!lists.containsKey(cmd)) {
                    lists.put(cmd, new Todo(cmd));
                    currentList = lists.get(cmd);
                } else {
                    error("A list already exists with name: " + cmd);
                }
                
            }
            if (cmd.equals("section")) {    // add section
                try {
                    cmd = sc.nextLine().substring(1);                     // add section name (clear the initial whitespace)
                } catch (Exception e) {
                    error("What section?");
                }
                
                if(currentList == null) {
                    error("no list selected");
                } else {
                    if (!currentList.hasSection(cmd)) {
                        // add the section
                        currentList.addSection(cmd);
                    } else {
                        error("This list already has a section named: " + cmd);
                    }
                }

            }

            if (cmd.equals("item")) {               // add item
                cmd = sc.next(); // expect a section name.   // add item "section" -- where "section" must be one word (be careful with naming)
                if(currentList == null) {
                    error("no list selected");
                } else {
                    if(!currentList.hasSection(cmd)) {
                        error("no section exists named: " + cmd);
                    } else {
                        line = sc.nextLine();
                        currentList.addItem(cmd, line);    // add item "section" "item"
                    }
                }
            }
        }

        if (cmd.equals("delete") || cmd.equals("remove")) {
            cmd = sc.next();
            if (cmd.equals("item")) {
                cmd = sc.nextLine().substring(1);
                if (currentList != null) {
                    currentList.deleteItem(cmd);
                }
            } 

            if (cmd.equals("section")) {
                cmd = sc.nextLine().substring(1);
                if (currentList != null) {
                    currentList.deleteSection(cmd);
                }
            }

            if (cmd.equals("list")) {
                cmd = sc.nextLine().substring(1);
                for (String listName : lists.keySet()) {
                    if (StringHelper.containsSubstring(listName, cmd)) { // grab the first match
                        lists.remove(lists.get(listName));
                    }
                }
            }
        }

        if (currentList != null) {
            currentList.show();
        }

        sc.close();
        scanLine.close();
        return false;
    }

    public static void main(String[] args) {

        // load existing lists 

        lists = Todo.loadLists();

        try {
            while(true) {
                if(await_input()) {
                    break;
                }
            }
        } catch (Exception e) {
            error("there was a fatal exception.");
            e.printStackTrace();
        }

        // save lists
        for (Map.Entry<String, Todo> entry : lists.entrySet()) {
            Todo list = entry.getValue();
            Todo.saveList(list);
        }

    }

}


/*
 * make sections identifiable by hints like items
 * 
 * then: be able to delete a specific item
 * then: delete section
 * then: delete lists
 * 
 * collapse "sectionName"
 * 
 * then: implement flush: flush (currentlist implied), flush list "listname", flush all
 * 
 * then: implement help (list possible commands)
 */

/* desired commands/features: 

    The current list will have to be stored in a file 
    when the program is not running for persistence.

    1. list
        will open the app and show the todo list (if there is only one), including all Sections with their Items
        if there is more than one list to show, it will show their names and allow the user to select one by entering its name.
        Alternatively, they can use "list work" (for example) to open the list with the given name.
    2. section "name"
        will show only the named Section
    2.5 add section "name"
        will add a section with the given name. it must be unique.
    3. complete "itemName"
        will mark this item as completed
    3.5 add item "section" "name"
        will add an item with the given name.
    3.75 add item "name"
        will add the item with the given name -- ONLY IF AFTER SECTION command.
    4. progress percent "itemName" 
        will create or modify a progress bar on this item at a given progress level.
    5. flush
        will remove all completed items from the list.
    5.5 exit
        will close the app

    6. (LATER) --   add functionality for events/meetings, Priorities, and Calendar 
                    as in the true List

    NOTETAKING FUNCTIONALITY
    7. note -title "Title of the note" "string to remember"
    8. note show
        will show all notes
    9. note remove -index ind
    10. note remove -title title
*/