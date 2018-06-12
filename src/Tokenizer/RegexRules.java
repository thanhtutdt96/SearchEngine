/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tu
 */
public class RegexRules {

    public static final String ALPHABET = "[A-Za-záàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ_0-9 ]+";

    public static final String EMAIL = "([\\w\\d_\\.-]+)@(([\\d\\w-]+)\\.)*([\\d\\w-]+)";

    public static final String FULL_DATE = "(ngày )?(?<day>0?[1-9]|[12][0-9]|3[01])(( tháng )|\\/|-|\\.)(?<month>1[0-2]|(0?[1-9]))(( năm )|\\/|-|\\.)(?<years>\\d{4})";

    public static final String MONTH = "(tháng )?(?<month>1[0-2]|(0?[1-9]))((( năm )|\\/|-)(?<years>\\d{4}))";

    public static final String DATE = "(ngày )?(?<day>0?[1-9]|[12][0-9]|3[01])(( tháng )|\\/|-)(?<month>1[0-2]|(0?[1-9]))";

    public static final String TIME = "(((\\d\\d|0?\\d)(:|h))?(\\d\\d|0?\\d)(â€™|'|p|ph|:)(\\d\\d|0?\\d)(s)?)";

    public static final String MONEY = "\\p{Sc}\\d+([\\.,]\\d+)*|\\d+([\\.,]\\d+)*\\p{Sc}";

    public static final String PHONE_NUMBER = "(\\(?\\+\\d{1,2}\\)?[\\s\\.-]?)?\\d{2,}[\\s\\.-]?\\d{3,}[\\s\\.-]?\\d{3,}";

    public static final String URL = "(((https?|ftp|http):\\/\\/|www\\.)[^\\s/$.?#].[^\\s]*)|(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    public static final String PERCENT = "([-+]?\\d+([\\.,]\\d+)*)(%)";

    public static final String NUMBER = "[-+]?\\d+([\\.,]\\d+)*";

    public static final String SPECIAL_CHAR = "\\~|\\@|\\#|\\^|\\&|\\*|\\+|\\-|\\Ă¢â‚¬â€œ|<|>|\\|";

    public static final String DECREE_SYMBOL = "((" + ALPHABET + "+)(-))?(" + ALPHABET + "+)(((-)(" + ALPHABET
            + "+))*)(([1-9][0-9]+|0?[1-9])?)";

    public static final String DECREE = "(([1-9][0-9]+|0?[1-9])(\\/)((([12]\\d{3})(\\/))?)(" + DECREE_SYMBOL + "))";

    public static final String PUNCTUATION = "[^\\w\\s\\dA-Za-záàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ]";

    public static final String SPACE_CHARACTER = "(\\s)+";

    public static final String SPECIAL_WORD = ALPHABET + "([\\-'‘’&]" + ALPHABET + ")+";

    public static final String SPECIAL_NAME = "([^áàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ])([a-zA-Z0-9][a-zA-Z0-9]*[\\.][a-zA-Z0-9][a-zA-Z0-9]*)([^áàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ])";

    public static final String MEASURE = NUMBER
            + "((Ge|K|k|M|m|G|g|T|t|P|p|E|e|Z|z|Y|y|B|b)?(b|B)|(cc|ml|m3|dm3|cm3)|(k|K|Da|da|H|h|m|M)?(m|M|G|g){1}|(s))";

    public static final String ABBREVIATION = "(BT|Co|Corp|Dr|Ltd|Miss|MISS|MR|Mr|MRS|Mrs|MS|Ms|P|Q|Tp|TP|TT|TBT)(\\.)";

    public static final String FORMAT_TAG = "(<)(" + "([A-Za-záàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ_0-9., ]+)" + ")(>#)([A-Z_]+)";

    private static List<String> regexRules = null;

    public static List<String> getRegexRules() {
        if (regexRules == null) {
            regexRules = new ArrayList<>();
        }
        regexRules.add(ALPHABET);
        regexRules.add(EMAIL);
        regexRules.add(FULL_DATE);
        regexRules.add(DATE);
        regexRules.add(MONTH);
        regexRules.add(TIME);
        regexRules.add(MONEY);
        regexRules.add(PHONE_NUMBER);
        regexRules.add(URL);
        regexRules.add(PERCENT);
        regexRules.add(NUMBER);
        regexRules.add(SPECIAL_CHAR);
        regexRules.add(DECREE_SYMBOL);
        regexRules.add(DECREE);
        regexRules.add(PUNCTUATION);
        regexRules.add(SPECIAL_WORD);
        regexRules.add(SPACE_CHARACTER);
        regexRules.add(SPECIAL_NAME);
        regexRules.add(MEASURE);
        regexRules.add(ABBREVIATION);
        regexRules.add(FORMAT_TAG);

        return regexRules;
    }
}
