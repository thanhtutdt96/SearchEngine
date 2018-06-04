package SearchEngine;

import static SearchEngine.Constants.REMOVE_COMMENT_REGEX;
import static SearchEngine.Constants.REMOVE_REDUNDANT_CHAR_REGEX;
import static SearchEngine.Constants.REMOVE_SPACE_REGEX;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Parser instance = null;

    public static Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        };
        return instance;
    }

    public String[] removeSpace(String line) {
        return line.split(REMOVE_SPACE_REGEX);
    }

    public String removeRedundantCharacters(String word) {
        return word.split(REMOVE_REDUNDANT_CHAR_REGEX)[0];
    }

    public boolean checkRedundant(String word) {
        Pattern pattern = Pattern.compile("<<\\w");
        Matcher matcher = pattern.matcher(word);
        while (matcher.find()) {
            return true;
        }
        return false;
    }

    public boolean checkComment(String word) {
        Pattern pattern = Pattern.compile(REMOVE_COMMENT_REGEX);
        Matcher matcher = pattern.matcher(word);
        while (matcher.find()) {
            return true;
        }
        return false;
    }
}
