package todo_list;

import java.util.Scanner;
import java.util.HashMap;



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

        String line = scanLine.nextLine();

        Scanner sc = new Scanner(line);

        String cmd = sc.next();

        if (cmd.equals("quit") || cmd.equals("kill") || cmd.equals("q") || cmd.equals("exit")) {
            return true;
        }

        if (cmd.equals("clear")) {
            clear();
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
            } else if (lists.size() == 1) {
                currentList = lists.get(lists.keySet().iterator().next());
                currentList.show();

            } else {

                // list exit will exit from the current list. Otherwise, remain in the current list.

                if (sc.hasNext()) {
                    cmd = sc.next();

                    if (cmd.equals("exit")) {       // list exit
                        currentList = null;
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
                cmd = sc.nextLine().substring(1);                     // add section name (clear the initial whitespace)
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
                cmd = sc.next(); // expect a section name.   // add item "section"
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

        if (currentList != null) {
            currentList.show();
        }

        return false;
    }

    public static void main(String[] args) {

        while(true) {
            if(await_input()) {
                break;
            }
        }

    }

}


/*
 * then: be able to delete a specific item
 * then: implement flush
 * 
 * then: make things persistent
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
    4. progress "itemName" percent
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