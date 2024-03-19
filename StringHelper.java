package todo_list;

public class StringHelper {
    public static boolean containsSubstring(String s1, String s2) {
        // check if s2 appears anywhere as a substring of s1.

        int N = s1.length();
        int n = s2.length();

        if (n > N) {
            return false;
        }


        for (int start = 0; start <= N-n; start++) {
            if (s1.substring(start, start+n).equals(s2)) {
                return true;
            }
        }

        return false;
    }

    /*
    public static void main(String[] args) {
        // make sure it's false when it's false

        System.out.println("expect false (obvious): " + containsSubstring("system", "idk"));
        System.out.println("expect false (length): " + containsSubstring("something", "longerSomething"));
        System.out.println("expect true: " + containsSubstring("system fail", "system"));
        System.out.println("expect true: " + containsSubstring("system fail", "fail"));
    }
    */
}