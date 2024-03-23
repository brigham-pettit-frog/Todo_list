package todo_list;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;



public class ListDriver {

    private static HashMap<String, Todo> lists = new HashMap<>();
    private static Todo currentList = null;

    private static Scanner scanLine;
    private static String line;
    private static Scanner sc;
    private static String cmd;

    private static boolean isKillCommand(String str) {
       return (str.equals("quit") || str.equals("kill") || str.equals("q") || str.equals("exit"));
    }

    private static void saveLists() {
        for (Map.Entry<String, Todo> entry : lists.entrySet()) {
            Todo list = entry.getValue();
            Todo.saveList(list);
        }
    }

    private static void accept_input_line() {
        scanLine = new Scanner(System.in);
        line = scanLine.nextLine();
        sc = new Scanner(line);
        cmd = "";
        cmd = sc.next();
    }

    private static String restOfLine(Scanner sc) {
        if (sc.hasNextLine()) {
            return sc.nextLine().substring(1);
        } else {
            return "";
        }
    }

    private static void prompt_input() {
        System.out.print("input command: ");
    }

    private static boolean clear() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        return false;
    }

    private static void error(String msg) {
        System.out.println("Error: " + msg);
    }

    private static void showListIfRelevant() {
        if (currentList != null) {
            currentList.show();
        }
    }

    private static boolean endProgram() {
        return true;
    }

    private static boolean updateProgress() {
        int prog = sc.nextInt();
        cmd = restOfLine(sc);
        currentList.updateProgress(cmd, prog);
        return false;
    }

    private static boolean completeItem() {
        cmd = restOfLine(sc);

        if (currentList != null) {
            currentList.completeItem(cmd);
        }

        return false;
    }

    private static Todo getListByName(String name) {
        for (String listName : lists.keySet()) {
            if (StringHelper.containsSubstring(listName, name)) {
                return lists.get(listName);
            }
        }
        return null;
    }

    private static void list_exit() {
        if (currentList != null) {  // save the list if there's a list to save
            Todo.saveList(currentList);
        }
        
        currentList = null;
        clear();
    }

    private static void list_select() {
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
            cmd = restOfLine(sc);                    // list, then, "name"
        }
    }

    private static void list_from_get_name() {
        Todo nextList = getListByName(cmd);
        
        if (nextList == null) {
            error("No such list exists.");
        } else {
            Todo.saveList(currentList);
            currentList = nextList;
            currentList.show();
        }
    }

    private static boolean list() {
        
        if (!sc.hasNext()) {
            if (lists.size() == 0) {
                error("no lists to report.");
                return false;
            } else if (lists.size() == 1) {
                currentList = lists.get(lists.keySet().iterator().next());
                currentList.show();
                return false;
            } else { // there are lists to choose from, and none was named.
                list_select(); // updates static variable cmd
            }
        } 

        // list exit will exit from the current list. Otherwise, remain in the current list.

        else {
            cmd = sc.next();

            if (cmd.equals("exit")) {       // list exit
                list_exit();
                return false;
            }

        }

        list_from_get_name();
        
        return false;
        
    }

    private static void add_list() {
        cmd = sc.next();                     // add list "cmd"
        if (!lists.containsKey(cmd)) {
            lists.put(cmd, new Todo(cmd));
            Todo.saveList(currentList);
            currentList = lists.get(cmd);
        } else {
            error("A list already exists with name: " + cmd);
        }
    }

    private static void add_section() {
        try {
            cmd = restOfLine(sc);                     // add section name (clear the initial whitespace)
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

    private static void add_item() {
        cmd = sc.next(); // expect a section name.   // add item "section" -- where "section" must be one word (be careful with naming)
        if(currentList == null) {
            error("no list selected");
        } else {
            if(!currentList.hasSection(cmd)) {
                error("no section exists named: " + cmd);
            } else {
                line = restOfLine(sc);
                currentList.addItem(cmd, line);    // add item "section" "item"
            }
        }
    }

    private static void add() {

        cmd = sc.next();
        if (cmd.equals("list")) {       // add list
            add_list();
        }
        if (cmd.equals("section")) {    // add section
            add_section();
        }
        if (cmd.equals("item")) {               // add item
            add_item();
        }

    }

    private static void delete_item() {
        cmd = restOfLine(sc);
        cmd = restOfLine(sc);
        if (currentList != null) {
            currentList.deleteItem(cmd);  
        }
    }

    private static void delete_section() {
        cmd = restOfLine(sc);
        if (currentList != null) {
            currentList.deleteSection(cmd);
        }
    }

    private static void delete_list() {
        cmd = restOfLine(sc);
        Todo list = null;
        for (String listName : lists.keySet()) {
            if (StringHelper.containsSubstring(listName, cmd)) { // grab the first match
                list = lists.get(listName);
                lists.remove(list.getName());
                break;
            }
        }

        if (list != null) {
            if (currentList != null) {
                if (currentList.getName().equals(list.getName())) {
                    currentList = null;
                }
            }
            Todo.deleteList(list);
        } else {
            if (!Todo.deleteList(cmd)) {
                error("couldn't delete list " + cmd);
            }
        } 
    }

    private static void delete_unspecified() {
        // assume I'm trying to remove an item first, then assume a section.

        if (sc.hasNext()) {
            cmd = cmd + ' ' + restOfLine(sc);
        }
        
        boolean success = false;
        if (currentList != null) {
            if (currentList.deleteItem(cmd)) {
                success = true;
            } else {
                if (currentList.deleteSection(cmd)) {
                    success = true;
                }
            }
        }

        if (!success) {
            error("couldn't delete item or section named " + cmd);
        }
    }

    private static void delete() {

        if (sc.hasNext()) {
            cmd = sc.next();
            if (cmd.equals("item")) {
                delete_item();
            } 

            else if (cmd.equals("section")) {
                delete_section();
            }

            else if (cmd.equals("list")) {
                delete_list();
            }

            else {  
                delete_unspecified();
            }
        }
        
    }

    private static void collapse() {
        cmd = restOfLine(sc);
        if (currentList != null) {
            currentList.collapse(cmd);
        }
    }

    private static void expand() {
        cmd = restOfLine(sc);
        if (currentList != null) {
            currentList.expand(cmd);
        }
    }

    private static boolean await_input() {
        prompt_input();

        accept_input_line();

        if (isKillCommand(cmd)) {
            return endProgram();
        }

        else if (cmd.equals("clear")) {
            clear();
            return false; // don't show the list after clear command.
        }

        else if (cmd.equals("progress")) { // progress
            if (updateProgress()) {
                return true;
            }
        }

        else if (cmd.equals("complete")) { // complete
            if(completeItem()) {
                return true;
            }
        }

        else if (cmd.equals("list")) {     // list
            if (list()) {
                return true;
            }
        }

        else if (cmd.equals("add") || cmd.equals("make")) {            // add
            add();
        }

        else if (cmd.equals("delete") || cmd.equals("remove")) {         // delete
            delete();
        }

        else if (cmd.equals("collapse")) {
            collapse();
        }

        else if (cmd.equals("expand")) {
            expand();
        }

        showListIfRelevant();

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

        saveLists();
        
    }

}


/*
*   
*  
 *  next: add a confirmation prompt for removing lists.
 * 
 * then: implement flush: flush (currentlist implied), flush list "listname", flush all
 * 
 * then: implement help (list possible commands)
 * 
 * then: investigate methods for events/meetings, priorities, calendar
 * 
 * investgate possibility of pulling in Google Calendar
 * -- -- at this point, why not make a web app?
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
    9. note remove index ind
    10. note remove title title
*/