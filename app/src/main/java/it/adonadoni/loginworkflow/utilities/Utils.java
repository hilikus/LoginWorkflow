package it.adonadoni.loginworkflow.utilities;

public class Utils {

    // Validate the string based on length and characters requirements
    public static boolean validateInputString(String str) {
        if (str.length() == 0 || str.contains("{")  || str.contains("\\") ||
                str.contains(",") || str.contains(".")  || str.contains("}")) {
            return false;
        }
        return true;
    }
}
