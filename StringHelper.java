package todo_list;

public class StringHelper {
    public static boolean containsSubstring(String s1, String s2) {
        // check if s2 appears anywhere as a substring of s1.

        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

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

}