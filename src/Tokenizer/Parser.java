package Tokenizer;

import static Constant.Constants.EMAIL;
import static Constant.Constants.NUMBER;
import static Constant.Constants.PERCENT;
import static Constant.Constants.PHONE_NUMBER;
//import static Constant.Constants.PUNCTUATION;
import static Constant.Constants.REMOVE_COMMENT_REGEX;
import static Constant.Constants.REMOVE_REDUNDANT_CHAR_REGEX;
import static Constant.Constants.REMOVE_SPACE_REGEX;
import static Constant.Constants.TIME;
import static Constant.Constants.URL;
import java.util.ArrayList;
import java.util.List;
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
    
    private static List<String> regexRules = null;

    public static List<String> getRegexRules() {
        if (regexRules == null) {
            regexRules = new ArrayList<>();
        }
        regexRules.add(EMAIL);
        regexRules.add(TIME);
        regexRules.add(PHONE_NUMBER);
        regexRules.add(URL);
        regexRules.add(PERCENT);
        regexRules.add(NUMBER);
//        regexRules.add(PUNCTUATION);

        return regexRules;
    }
}
