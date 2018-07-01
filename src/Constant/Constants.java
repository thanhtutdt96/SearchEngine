package Constant;

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

    public static final List<String> STOP_WORDS = Arrays.asList("bị", "bởi", "cả", "các", "cái", "cần", "càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "có_thể", "cứ", "của", "cùng", "cũng", "đã", "đang", "đây", "để", "đến_nỗi", "đều", "điều", "do", "đó", "được", "dưới", "gì", "khi", "không", "là", "lại", "lên", "lúc", "mà", "mỗi", "một_cách", "này", "nên", "nếu", "ngay", "nhiều", "như", "nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rằng", "rất", "rất", "rồi", "sau", "sẽ", "so", "sự", "tại", "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì", "việc", "với", "vừa");
    public static final String PREF_NAME = "MY_PREFS";
    public static final String FOLDER_PATH = "FOLDER_PATH";
    public static final String REMOVE_SPACE_REGEX = "\\s+";
//    public static final String REMOVE_REDUNDANT_CHAR_REGEX="[^a-zA-Z0-9'.,;:[...]&!\"ăâđêôơưĂÂĐÊÔƠƯ\\p{L}]+";
//    public static final String REMOVE_REDUNDANT_CHAR_REGEX = "\\W+ăâđêôơưĂÂĐÊÔƠƯ[\\p{Punct}&&[^']]";
    public static final String REMOVE_REDUNDANT_CHAR_REGEX = "[^\\p{L}\\s\\d]";
    public static final String REMOVE_COMMENT_REGEX = "<!--.*-->";

    public static final int MODE_OPEN = 0;
    public static final int MODE_PAGE = 1;

}
