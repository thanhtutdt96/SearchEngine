package Tokenizer;

import static Constant.Constants.REMOVE_COMMENT_REGEX;
import static Constant.Constants.REMOVE_REDUNDANT_CHAR_REGEX;
import static Constant.Constants.REMOVE_SPACE_REGEX;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static Parser instance = null;
    Pattern pattern1 = Pattern.compile("<<\\w");
    Pattern pattern2 = Pattern.compile(REMOVE_COMMENT_REGEX);

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
        while (pattern1.matcher(word).find()) {
            return true;
        }
        return false;
    }

    public boolean checkComment(String word) {
        while (pattern2.matcher(word).find()) {
            return true;
        }
        return false;
    }
}
