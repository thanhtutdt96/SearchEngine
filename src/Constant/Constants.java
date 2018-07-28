package Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tu
 */
public class Constants {

    public static final int MODE_OPEN = 0;
    public static final int MODE_PAGE = 1;
    
    public static final List<String> STOP_WORDS = Arrays.asList("bị", "bởi", "cả", "các", "cái", "cần", "càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "cứ", "của", "cùng", "cũng", "đã", "đang", "đây", "để", "đều", "điều", "do", "đó", "được", "dưới", "gì", "khi", "không", "là", "lại", "lên", "lúc", "mà", "mỗi", "này", "nên", "nếu", "ngay", "nhiều", "như", "nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rằng", "rất", "rất", "rồi", "sau", "sẽ", "so", "sự", "tại", "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì", "việc", "với", "vừa");
    public static final String PREF_NAME = "MY_PREFS";
    public static final String FOLDER_PATH = "FOLDER_PATH";
    public static final String REMOVE_SPACE_REGEX = "\\s+";
//    public static final String REMOVE_REDUNDANT_CHAR_REGEX="[^a-zA-Z0-9'.,;:[...]&!\"ăâđêôơưĂÂĐÊÔƠƯ\\p{L}]+";
//    public static final String REMOVE_REDUNDANT_CHAR_REGEX = "\\W+ăâđêôơưĂÂĐÊÔƠƯ[\\p{Punct}&&[^']]";
    public static final String REMOVE_REDUNDANT_CHAR_REGEX = "[^\\p{L}\\s\\d]";
    public static final String REMOVE_COMMENT_REGEX = "<!--.*-->";

    public static final String EMAIL = "([\\w\\d_\\.-]+)@(([\\d\\w-]+)\\.)*([\\d\\w-]+)";
    public static final String TIME = "(((\\d\\d|0?\\d)(:|h))?(\\d\\d|0?\\d)('|p|ph|:)(\\d\\d|0?\\d)(s)?)";
    public static final String PHONE_NUMBER = "(\\(?\\+\\d{1,2}\\)?[\\s\\.-]?)?\\d{2,}[\\s\\.-]?\\d{3,}[\\s\\.-]?\\d{3,}";
    public static final String URL = "(((https?|ftp|http):\\/\\/|www\\.)[^\\s/$.?#].[^\\s]*)|(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    public static final String PERCENT = "([-+]?\\d+([\\.,]\\d+)*)(%)";
    public static final String NUMBER = "[-+]?\\d+([\\.,]\\d+)*";
//    public static final String PUNCTUATION = "[^\\w\\s\\dA-Za-záàạảãâấầậẩẫăắằặẳẵÁÀẠẢÃÂẤẦẬẨẪĂẮẰẶẲẴéèẹẻẽêếềệểễÉÈẸẺẼÊẾỀỆỂỄóòọỏõôốồộổỗơớờợởỡÓÒỌỎÕÔỐỒỘỔỖƠỚỜỢỞỠúùụủũưứừựửữÚÙỤỦŨƯỨỪỰỬỮíìịỉĩÍÌỊỈĨđĐýỳỵỷỹÝỲỴỶỸ]";
  
}
