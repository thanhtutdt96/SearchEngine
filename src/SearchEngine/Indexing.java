package SearchEngine;

import Tokenizer.Parser;
import Constant.Constants;
import static SearchEngine.MainFrame.helper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;

public class Indexing {

    public HashMap<String, List<Posting>> hashMap_a_d;
    public HashMap<String, List<Posting>> hashMap_e_h;
    public HashMap<String, List<Posting>> hashMap_i_l;
    public HashMap<String, List<Posting>> hashMap_m_p;
    public HashMap<String, List<Posting>> hashMap_q_t;
    public HashMap<String, List<Posting>> hashMap_u_w;
    public HashMap<String, List<Posting>> hashMap_x_z;
    public HashMap<String, List<Posting>> hashMap_other;

    public void setHashMap_a_d(HashMap<String, List<Posting>> hashMap_a_d) {
        this.hashMap_a_d = hashMap_a_d;
    }

    public void setHashMap_e_h(HashMap<String, List<Posting>> hashMap_e_h) {
        this.hashMap_e_h = hashMap_e_h;
    }

    public void setHashMap_i_l(HashMap<String, List<Posting>> hashMap_i_l) {
        this.hashMap_i_l = hashMap_i_l;
    }

    public void setHashMap_m_p(HashMap<String, List<Posting>> hashMap_m_p) {
        this.hashMap_m_p = hashMap_m_p;
    }

    public void setHashMap_q_t(HashMap<String, List<Posting>> hashMap_q_t) {
        this.hashMap_q_t = hashMap_q_t;
    }

    public void setHashMap_u_w(HashMap<String, List<Posting>> hashMap_u_w) {
        this.hashMap_u_w = hashMap_u_w;
    }

    public void setHashMap_x_z(HashMap<String, List<Posting>> hashMap_x_z) {
        this.hashMap_x_z = hashMap_x_z;
    }

    public void setHashMap_other(HashMap<String, List<Posting>> hashMap_other) {
        this.hashMap_other = hashMap_other;
    }

    private static Indexing instance = null;
    public Helper helper;
    private Parser parser = null;
    private String newline = System.getProperty("line.separator");

    private List<String> excludeList = Arrays.asList("<<", ">>", ">", "<", "»", "/t>><<c", "<<a", "<<t", " » ", "");

//    private HashMap<String, List<Posting>> indexMap;
    private HashMap<String, List<Integer>> termMap;
    private HashMap<String, List<Postings>> tempMap;
    private List<File> fileList;
    private HashMap<Integer, List<Postings>> phraseMap;

    private int[] posOfTerm = {0, 0, 0, 0, 0, 0, 0, 0};

    public static Indexing getInstance() {
        if (instance == null) {
            instance = new Indexing();
        }
        return instance;
    }

    public void clearIndexedFolder(String path) {
        File folder = new File(path);
        File files[] = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void initContaner() {
        hashMap_a_d = new HashMap<>();
        hashMap_e_h = new HashMap<>();
        hashMap_i_l = new HashMap<>();
        hashMap_m_p = new HashMap<>();
        hashMap_q_t = new HashMap<>();
        hashMap_u_w = new HashMap<>();
        hashMap_x_z = new HashMap<>();
        hashMap_other = new HashMap<>();
    }

    private HashMap<String, List<Posting>> getHashMapByTerm(String term) {
        helper = Helper.getInstance();

        if (helper.isA_DFirst(term)) {
            return hashMap_a_d;
        } else if (helper.isE_HFirst(term)) {
            return hashMap_e_h;
        } else if (helper.isI_LFirst(term)) {
            return hashMap_i_l;
        } else if (helper.isM_PFirst(term)) {
            return hashMap_m_p;
        } else if (helper.isQ_TFirst(term)) {
            return hashMap_q_t;
        } else if (helper.isU_WFirst(term)) {
            return hashMap_u_w;
        } else if (helper.isX_ZFirst(term)) {
            return hashMap_x_z;
        }
        return hashMap_other;
    }

    public String getIndexFileNameByHashMap(HashMap<String, List<Posting>> hashMap) {
        if (hashMap.equals(hashMap_a_d)) {
            return "a-d";
        } else if (hashMap.equals(hashMap_e_h)) {
            return "e-h";
        } else if (hashMap.equals(hashMap_i_l)) {
            return "i-l";
        } else if (hashMap.equals(hashMap_m_p)) {
            return "m-p";
        } else if (hashMap.equals(hashMap_q_t)) {
            return "q-t";
        } else if (hashMap.equals(hashMap_u_w)) {
            return "u-w";
        } else if (hashMap.equals(hashMap_x_z)) {
            return "x-z";
        }
        return "other";
    }

    private void setHashMapByFileName(String fileName, HashMap<String, List<Posting>> hashMap) {
        String name = fileName.replace(".bin", "");
        switch (name) {
            case "a-d":
                setHashMap_a_d(hashMap);
                break;

            case "e-h":
                setHashMap_e_h(hashMap);
                break;

            case "i-l":
                setHashMap_i_l(hashMap);
                break;

            case "m-p":
                setHashMap_m_p(hashMap);
                break;

            case "q-t":
                setHashMap_q_t(hashMap);
                break;

            case "u-w":
                setHashMap_u_w(hashMap);
                break;

            case "x-z":
                setHashMap_x_z(hashMap);
                break;

            case "other":
                setHashMap_other(hashMap);
                break;
        }
    }

    public void buildIndex(List<File> files) {
        parser = Parser.getInstance();
//        indexMap = new HashMap<>();
        initContaner();

        for (int i = 0; i < files.size(); i++) {
//            indexMap.clear();
            String filePath = files.get(i).getAbsolutePath();
            int termPos = 0;
            int filePos = fileList.indexOf(files.get(i));
            // Check if the file was indexed or not
            if (filePos == -1) {
                fileList.add(files.get(i));
                filePos = fileList.size() - 1;
            }

            helper = Helper.getInstance();
            String[] line = helper.readFileByExtenstions(filePath);
            for (int j = 0; j < line.length; j++) {

                if (line[j].trim().length() == 0) {
                    continue;
                }
                if (parser.checkComment(line[j])) {
                    continue;
                }
                line[j] = line[j].replaceAll("(<<\\w)|(\\w>>)", " ");
                line[j] = line[j].replaceAll("[^\\p{L}\\s\\d]", " ");
                for (String tmp : parser.removeSpace(line[j])) {

                    if (excludeList.contains(tmp)) {
                        continue;
                    }
                    if (parser.checkRedundant(tmp)) {
                        continue;
                    }
                    System.out.println(tmp);
                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());

                    termPos++;

                    List<Posting> postings = getHashMapByTerm(term).get(term);
                    if (postings == null) {
                        postings = new ArrayList<>();
                        getHashMapByTerm(term).put(term, postings);
                    }
                    if (helper.checkExcelExtention(filePath)) {
                        postings.add(new Posting(filePos, helper.getSheetByIndex(j)));
                    } else {
                        postings.add(new Posting(filePos, termPos));
                    }
                }
            }
//          bufferedReader.close();
//          fileReader.close();
//          sortIndex();
//            saveIndex();
        }
    }

    public void saveAllIndex() {
        long timeStart = System.currentTimeMillis();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_a_d);
            }
        }
        );

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_e_h);
            }
        }
        );

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_i_l);
            }
        }
        );

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_m_p);
            }
        }
        );

        Thread thread5 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_q_t);
            }
        }
        );

        Thread thread6 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_u_w);
            }
        }
        );

        Thread thread7 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_x_z);
            }
        }
        );

        Thread thread8 = new Thread(new Runnable() {
            @Override
            public void run() {
                saveIndexBinary(hashMap_other);
            }
        }
        );

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();

    }

    public void saveIndexBinary(HashMap<String, List<Posting>> hashMap) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream("indexed/" + getIndexFileNameByHashMap(hashMap) + ".bin");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(hashMap);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAllIndexFiles() {
        File indexFolder = new File("indexed/");
        File[] listOfFiles = indexFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.matches("(([a-z]-[a-z])|other)\\.bin");
            }
        }
        );
        for (File file : listOfFiles) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readBinFile(file.getName());
                }
            }
            ).start();
        }

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
//        }
//           printMap(hashMap_u_w);
    }

    public void readBinFile(String fileName) {
        HashMap<String, List<Posting>> hashMap = new HashMap<>();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        // Read serializable file
        try {
            fis = new FileInputStream("indexed/" + fileName);
            ois = new ObjectInputStream(fis);
            System.out.println(fileName);
            hashMap = (HashMap<String, List<Posting>>) ois.readObject();
            setHashMapByFileName(fileName, hashMap);
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFileList() {
//        String timeLog = "list-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        StringBuilder builder = new StringBuilder();

        try {
            fileWriter = new FileWriter("indexed/list.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            for (File file : fileList) {
                String path = file.getPath();

                builder.append(path);
                builder.append(System.lineSeparator());

            }
            bufferedWriter.write(builder.toString());
            bufferedWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("File list saved");
        }
    }

//    public void saveIndex() {
//        BufferedWriter bufferedWriter = null;
//        BufferedWriter posBuffered = null;
//        FileWriter posWriter = null;
//        FileWriter fileWriter = null;
//
//        try {
//            posWriter = new FileWriter("indexed/pos.txt", true);
//            posBuffered = new BufferedWriter(posWriter);
//            for (Map.Entry<String, List<Posting>> element : indexMap.entrySet()) {
//                StringBuilder builder = new StringBuilder();
//                String term = element.getKey();
//                fileWriter = new FileWriter("indexed/" + checkDistributionRange(term).toString() + ".txt", true);
//                bufferedWriter = new BufferedWriter(fileWriter); //helper.checkDistributionRange(term);
//                int m = posOfTerm[helper.checkPosDistributionRange(term)];
//                List<Posting> postings = element.getValue();
//                builder.append(term + "=>");
//                int curPos = postings.get(0).getFilePos();
//                builder.append("(" + postings.get(0).getFilePos() + ":" + postings.get(0).getTermPos());
//                if (postings.size() > 1) {
//                    for (int i = 1; i < postings.size(); i++) {
//                        if (postings.get(i).getFilePos() == curPos) {
//                            builder.append("," + postings.get(i).getTermPos());
//                        } else {
//                            curPos = postings.get(i).getFilePos();
//                            builder.append(")(" + curPos + ":" + postings.get(i).getTermPos());
//                        }
//                        if (i == postings.size() - 1) {
//                            builder.append(")");
//                            builder.append(System.lineSeparator());
//                        }
//                    }
//                } else {
//                    builder.append(")");
//                    builder.append(System.lineSeparator());
//                }
//                bufferedWriter.write(builder.toString());
//                bufferedWriter.flush();
//
//                posBuffered.write((term + "=>" + posOfTerm[helper.checkPosDistributionRange(term)] + System.lineSeparator()).toString());
//                posOfTerm[helper.checkPosDistributionRange(term)]++;
//
////                fileWriter.close();
//            }
//            bufferedWriter.close();
//            fileWriter.close();
//            posBuffered.close();
//            posWriter.close();
////            helper.closeFile();
////            bufferedWriter.close();
////            fileWriter.close();
//
//        } catch (IOException ex) {
//            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            System.out.println("Index saved");
//        }
//    }
    private String checkDistributionRange(String term) {
        // áàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ

        Pattern pattern = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
        Matcher m = pattern.matcher(term);
        if (m.find()) {
            return "a-d";
        }
        pattern = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "e-h";
        }
        pattern = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "i-l";
        }
        pattern = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "m-p";
        }
        pattern = Pattern.compile("^[qrstQRST]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "q-t";
        }
        pattern = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "u-w";
        }
        pattern = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");
        m = pattern.matcher(term);
        if (m.find()) {
            return "x-z";
        }
        return "other";
    }

    public String getFilePath(String path, String prefix) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.getName().startsWith(prefix)) {
                return file.getPath();
            }
        }
        return "";
    }

    public void readFileList() {
        try {
            FileReader fileReader = new FileReader("indexed/list.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                fileList.add(new File(line));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String retrieveIndex(String path, int position) {
        String[] line = helper.readFileByExtenstions(path);
        parser = Parser.getInstance();
        int noOfLetter = 20;
        boolean isFound = false;
        int count = -1;
        int wordCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if (position > noOfLetter) {
            stringBuilder.append("...");
        }

        for (int j = 0; j < line.length; j++) {

            if (line[j].trim().length() == 0) {
                continue;
            }
            if (parser.checkComment(line[j])) {
                continue;
            }
            line[j] = line[j].replaceAll("(<<\\w)|(\\w>>)", " ");
            line[j] = line[j].replaceAll("[^\\p{L}\\s\\d]", " ");
            if (position < noOfLetter) {
                for (String tmp : parser.removeSpace(line[j])) {

                    if (excludeList.contains(tmp)) {
                        continue;
                    }
                    if (parser.checkRedundant(tmp)) {
                        continue;
                    }
                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());

                    stringBuilder.append(tmp + " ");
                    count++;
                    if (count == position - 1) {
                        stringBuilder.append("<b>");
                    }
                    if (count == position) {
                        stringBuilder.append("</b>");
                    }
                    if (count == position + noOfLetter) {
                        stringBuilder.append("...");
                        break;
                    }
                }
            } else {
                for (String tmp : parser.removeSpace(line[j])) {

                    if (excludeList.contains(tmp)) {
                        continue;
                    }
                    if (parser.checkRedundant(tmp)) {
                        continue;
                    }
                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());
                    count++;
                    if (count <= position + noOfLetter) {
                        if (count >= position - noOfLetter) {
                            if (count == position - 1) {
                                stringBuilder.append("<b>");
                            }
                            if (count == position) {
                                stringBuilder.append("</b>");
                            }
                            stringBuilder.append(tmp + " ");
                        }
                    } else {
                        if (tmp.equals(parser.removeSpace(line[j])[parser.removeSpace(line[j]).length - 1])) {
                            stringBuilder.append("...");
                            return stringBuilder.toString();
                        } else {
                            stringBuilder.append(".");
                            return stringBuilder.toString();
                        }
                    }

                }
            }
        }
        return stringBuilder.toString();
    }

    public String retrieveIndex(String path, List<Integer> position) {
        String[] line = helper.readFileByExtenstions(path);
        parser = Parser.getInstance();
        int noOfLetter = 20;
        boolean isFound = false;
        int count = 0;
        int wordCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        int posMin = position.get(1);
        int posMax = position.get(1);
        for (int i = 2; i < position.size(); i++) {
            if (position.get(i) < posMin) {
                posMin = position.get(i);
            }
        }
        for (int i = 1; i < position.size(); i++) {
            if (position.get(i) > posMax) {
                posMax = position.get(i);
            }
        }
        if (posMin > noOfLetter) {
            stringBuilder.append("...");
        }

        for (int j = 0; j < line.length; j++) {
//            line[j].concat(line[j+1]);
            if (line[j].trim().length() == 0) {
                continue;
            }
            if (parser.checkComment(line[j])) {
                continue;
            }
            line[j] = line[j].replaceAll("(<<\\w)|(\\w>>)", " ");
            line[j] = line[j].replaceAll("[^\\p{L}\\s\\d]", " ");
            if (posMin < noOfLetter) {
                for (String tmp : parser.removeSpace(line[j])) {

                    if (excludeList.contains(tmp)) {
                        continue;
                    }
                    if (parser.checkRedundant(tmp)) {
                        continue;
                    }
                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());

                    stringBuilder.append(tmp + " ");
                    count++;
                    for (int i = 0; i < position.size(); i++) {
                        if (count == position.get(i) - 1) {
                            stringBuilder.append("<b>");
                        }
                        if (count == position.get(i)) {
                            stringBuilder.append("</b>");
                        }
                        if (count == position.get(i) + noOfLetter) {
                            stringBuilder.append("...");
                            return stringBuilder.toString();
                        }
                    }
                }
            } else {
                for (String tmp : parser.removeSpace(line[j])) {

                    if (excludeList.contains(tmp)) {
                        continue;
                    }
                    if (parser.checkRedundant(tmp)) {
                        continue;
                    }
                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());
                    count++;
                    if (count <= posMax + noOfLetter) {
                        if (count >= posMin - noOfLetter) {
                            stringBuilder.append(tmp + " ");
                            for (int k = 0; k < position.size(); k++) {
                                if (count == position.get(k) - 1) {
                                    stringBuilder.append("<b>");
                                }
                                if (count == position.get(k)) {
                                    stringBuilder.append("</b>");
                                }
                            }
                        }
                    } else {
                        if (tmp.equals(parser.removeSpace(line[j])[parser.removeSpace(line[j]).length - 1])) {
                            stringBuilder.append("...");
                            return stringBuilder.toString();
                        } else {
                            stringBuilder.append(".");
                            return stringBuilder.toString();
                        }
                    }

                }
            }
        }
        return stringBuilder.toString();
    }

//    public String searchOne(List<Postings> postingResult) {
////        String query = keyword.toLowerCase().split("(\\s|[.]|[,]|[:]|[?])+")[0];
////        byte byteArray[] = query.getBytes();
////        try {
////            query = new String(byteArray, "UTF-8");
////        } catch (UnsupportedEncodingException ex) {
////            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        List<Posting> postingResult = indexMap.get(query);
////        StringBuilder result = new StringBuilder();
////
////        if (postingResult != null) {
////            result.append("<b style='font-size: 130%'>*** " + postingResult.size() + " results matched ***</b>");
////            for (int i = 0; i < postingResult.size(); i++) {
////                String innerDoc = retrieveIndex(fileList.get(postingResult.get(i).getFilePos()).getPath(), postingResult.get(i).getTermPos());
////                String fileName = fileList.get(postingResult.get(i).getFilePos()).getName();
////                result.append("<p style='color: blue; font-size= 130%'>\"<b>"
////                        + "<a href='file:///" + fileList.get(postingResult.get(i).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
////                        + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos());
////                result.append("<div>" + innerDoc + "</div></p>");
////            }
////            result.append("<br/>");
////        } else {
////            result.append("No matches found");
////        }
////        return result.toString();
//
//        StringBuilder result = new StringBuilder();
//
//        if (postingResult != null) {
//            result.append("<b style='font-size: 130%'>*** " + postingResult.get(0).getSize() + " results matched ***</b>");
//            for (int j = 0; j < postingResult.size(); j++) {
//                for (int i = 0; i < postingResult.get(j).getTermPos().size(); i++) {
//                    String innerDoc = retrieveIndex(fileList.get(postingResult.get(j).getFilePos()).getPath(), postingResult.get(j).get(i));
//                    String fileName = fileList.get(postingResult.get(j).getFilePos()).getName();
//                    result.append("<p style='color: blue; font-size= 130%'>" + i + ". \"<b>"
//                            + "<a href='file:///" + fileList.get(postingResult.get(j).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
//                            + "\"</b>, <em>Position:</em> " + postingResult.get(j).get(i));
//                    result.append("<div>" + innerDoc + "</div></p>");
//                }
//                result.append("<br/>");
//            }
//        } else {
//            result.append("No matches found");
//        }
//        return result.toString();
//    }
    public String searchOne(String keyword) {
        String query = keyword.toLowerCase().split("(\\s|[.]|[,]|[:]|[?])+")[0];
        byte byteArray[] = query.getBytes();
        try {
            query = new String(byteArray, "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Indexing.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        List<Posting> postingResult = getHashMapByTerm(query).get(query);
        StringBuilder result = new StringBuilder();

        if (postingResult != null) {
            result.append("<b style='font-size: 130%'>*** " + postingResult.size() + " results matched ***</b>");
            for (int i = 0; i < postingResult.size(); i++) {
                String innerDoc = retrieveIndex(fileList.get(postingResult.get(i).getFilePos()).getPath(), postingResult.get(i).getTermPos());
                String fileName = fileList.get(postingResult.get(i).getFilePos()).getName();
                result.append("<p style='color: blue; font-size= 130%'>\"<b>"
                        + "<a href='file:///" + fileList.get(postingResult.get(i).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
                        + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos());
                result.append("<div>" + innerDoc + "</div></p>");
            }
            result.append("<br/>");
        } else {
            result.append("No matches found");
        }
        return result.toString();

//        StringBuilder result = new StringBuilder();
//
//        if (postingResult != null) {
//            result.append("<b style='font-size: 130%'>*** " + postingResult.get(0).getSize() + " results matched ***</b>");
//            for (int j = 0; j < postingResult.size(); j++) {
//                for (int i = 0; i < postingResult.get(j).getTermPos().size(); i++) {
//                    String innerDoc = retrieveIndex(fileList.get(postingResult.get(j).getFilePos()).getPath(), postingResult.get(j).get(i));
//                    String fileName = fileList.get(postingResult.get(j).getFilePos()).getName();
//                    result.append("<p style='color: blue; font-size= 130%'>" + i + ". \"<b>"
//                            + "<a href='file:///" + fileList.get(postingResult.get(j).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
//                            + "\"</b>, <em>Position:</em> " + postingResult.get(j).get(i));
//                    result.append("<div>" + innerDoc + "</div></p>");
//                }
//                result.append("<br/>");
//            }
//        } else {
//            result.append("No matches found");
//        }
//        return result.toString();
    }

    public String searchPhrase(String keyword) {
        phraseMap = new HashMap<>();
        String[] query = keyword.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        List<List<Posting>> postingResult = new ArrayList<>();
        boolean hasResult = false;
        int smallestPosting = Integer.MAX_VALUE;
        List<Integer> valueList = new ArrayList<>();
        int lengthOfPhrase = 0;
        List<String> meaningfulWords = new ArrayList<>();
        for (int i = 0; i < query.length; i++) {
            List<Posting> list = (List<Posting>) getHashMapByTerm(query[i]).get(query[i]);
            if (list == null) {
                continue;
            } else {
                meaningfulWords.add(query[i]);
                lengthOfPhrase++;
            }
            if (list.size() < smallestPosting) {
                smallestPosting = list.size();
                postingResult.add(0, getHashMapByTerm(query[i]).get(query[i]));
            } else {
                postingResult.add(list);
            }
        }
        if (lengthOfPhrase == 1) {
            return searchOne(meaningfulWords.get(0));
        } else {
            List<Posting> initialList = postingResult.get(0);
            for (int i = 0; i < initialList.size(); i++) {
                int curPos = initialList.get(i).getTermPos();
                Postings phraseResult = new Postings(-1, new ArrayList<>());
                boolean added = false;
                for (int j = 1; j < postingResult.size(); j++) {
                    int curFilePos = initialList.get(i).getFilePos();
                    if (phraseResult.getFilePos() == -1) {
                        phraseResult.setFilePos(curFilePos);
                        phraseResult.add(curPos);
                    }
                    Posting curPosting;
                    for (int k = -10; k < 11; k++) {
                        curPosting = new Posting(curFilePos, curPos + k);
                        if (postingResult.get(j).contains(curPosting)) {
                            if (!phraseResult.getTermPos().contains(curPosting.getTermPos())) {
                                phraseResult.add(curPosting.getTermPos());
                                if (phraseResult.getTermPos().size() == lengthOfPhrase) {
                                    added = true;
                                }
                            }
                            break;
                        }
                    }
                    if (added) {
                        int value = calculateValue(phraseResult.getTermPos());
                        if (!valueList.contains(value)) {
                            valueList.add(value);
                        }
                        List<Postings> phraseTmp = new ArrayList<>();
                        phraseTmp.add(phraseResult);
                        List<Postings> tmpList = phraseMap.get(value);
                        hasResult = true;
                        if (tmpList == null) {
                            phraseMap.put(value, phraseTmp);
                        } else {
                            tmpList.add(phraseResult);
                        }
                    }
                }
            }
            Collections.sort(valueList, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 > o2 ? -1 : o1 == o2 ? 0 : 1;
                }
            });
            if (hasResult == true) {
                int noOfResults = 0;
                for (int i = 0; i < valueList.size(); i++) {
                    noOfResults += phraseMap.get(valueList.get(i)).size();
                }
                result.append("<b style='font-size: 130%'>*** " + noOfResults + " results matched ***</b>");
                for (int i = 0; i < valueList.size(); i++) {
                    String innerDoc;
                    List<Postings> termsPos = phraseMap.get(valueList.get(i));
                    for (int j = 0; j < termsPos.size(); j++) {
                        String fileName = fileList.get(termsPos.get(j).getFilePos()).getName();
                        String filePath = fileList.get(termsPos.get(j).getFilePos()).getPath();
                        innerDoc = retrieveIndex(filePath, termsPos.get(j).getTermPos());
                        result.append("<p style='color: blue; font-size= 130%'>\"<b>"
                                + "<a href='file:///" + fileList.get(termsPos.get(j).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
                                + "\"</b>, <em>Position:</em> ");
                        Collections.sort(termsPos.get(j).getTermPos(), new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
                            }
                        });
                        for (int k = 0; k < termsPos.get(j).getTermPos().size() - 1; k++) {
                            result.append(termsPos.get(j).getTermPos().get(k) + " & ");
                        }
                        result.append(termsPos.get(j).getTermPos().get(termsPos.get(j).getTermPos().size() - 1));
                        //+ posMin + " & " + posMax);
                        result.append("<div>" + innerDoc + "</div></p>");
                    }
                }
                result.append("<br/>");
            } else {
                result.append("No matches found");
            }
            return result.toString();
        }
    }

    public int calculateValue(List<Integer> posting) {
        int value = 0;
        for (int i = 1; i < posting.size() - 1; i++) {
            if (posting.get(i) + 1 == posting.get(i + 1)) {
                continue;
            }
            value += Math.abs((posting.get(i) - posting.get(i + 1)));
        }
        value += Math.abs(posting.get(posting.size() - 1) - posting.get(1));
        return value;
    }

    public void printMap(HashMap<String, List<Posting>> map) {
        for (Map.Entry<String, List<Posting>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=>" + entry.getValue());
        }
    }

    public List<File> indexFileList(String path) {
        fileList = new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public boolean isIndexed(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                return true;
            }
        }
        return false;
    }

    public String performSearch(String word) {
        tempMap = new HashMap<>();
        String[] tokens = word.split("\\s+");
//        List<Postings> listTerm = new ArrayList<>();
//        BufferedReader bR;
//        String line = "";
//        helper = Helper.getInstance();
//        try {
//            for (int k = 0; k < tokens.length; k++) {
//                if (termMap.containsKey(tokens[k])) {
//                    bR = helper.getDistributionRange(tokens[k]);
//                    List<Integer> listPosting = new ArrayList<>();
//                    String filePos = "";
//                    for (int l = 0; l < termMap.get(tokens[k]).size(); l++) {
//                        for (int m = 0; m <= termMap.get(tokens[k]).get(l); m++) {
//                            line = bR.readLine();
//                        }
//                        String posting = line.split("=>")[1];
//                        List<Postings> list = new ArrayList<>();
//                        String[] filePoses = posting.split("\\)");
//
//                        for (int i = 0; i < filePoses.length; i++) {
//                            filePos = filePoses[i].replaceAll("\\(|\\)", "").split(":")[0];
//                            String[] postings = filePoses[i].replaceAll("\\(|\\)", "").split(":")[1].split(",");
//                            for (int m = 0; m < postings.length; m++) {
//                                listPosting.add(Integer.parseInt(postings[m]));
//                            }
//                        }
//                        Postings term = new Postings(Integer.parseInt(filePos), listPosting);
//                        listTerm.add(term);
//                    }
//                    List<Postings> listPostings = tempMap.get(tokens[k]);
//                    if (listPostings == null) {
//                        listPostings = new ArrayList<>();
//                        tempMap.put(tokens[k], listPostings);
//                    }
//                    listPostings.addAll(listTerm);
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if (tokens.length == 1) {
            return searchOne(tokens[0]);
        } else {
            return searchPhrase(word);
        }
    }

    public void readIndex() {
        termMap = new HashMap<>();
        try {
            BufferedReader bR = new BufferedReader(new FileReader("indexed/pos.txt"));
            String line = null;
            while ((line = bR.readLine()) != null) {
                String term = line.split("=>")[0];
                int pos = Integer.parseInt(line.split("=>")[1]);
                List<Integer> tempPos = termMap.get(term);
                if (tempPos == null) {
                    tempPos = new ArrayList<>();
                    termMap.put(term, tempPos);
                }
                tempPos.add(pos);
            }
            bR.close();

//            else {
//                searchPhrase(listTerm);
//            }
//            
//            indexMap = new HashMap<>();
//            FileReader fileReader = new FileReader("indexed/index.txt");
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
////            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
////                String term = line.split("=>")[0];
////                String posting = line.split("=>")[1];
////                List<Posting> list = new ArrayList<>();
////                String[] filePoses = posting.split("\\)");
////
////                for (int i = 0; i < filePoses.length; i++) {
////                    String filePos = filePoses[i].replaceAll("\\(|\\)", "").split(":")[0];
////                    String[] postings = filePoses[i].replaceAll("\\(|\\)", "").split(":")[1].split(",");
////
////                    for (int j = 0; j < postings.length; j++) {
////                        String termPos = postings[j].split("-")[0];
////                        list.add(new Posting(Integer.parseInt(filePos), Integer.parseInt(termPos)));
////                    }
////                }
////                indexMap.put(term, list);
//
//            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(Indexing.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDefaultPath() {
        File file = new File("res/");
        return file.getAbsolutePath();
    }

    public void saveFolderPath(String folderPath) {
        Preferences preferences = Preferences.userRoot().node(Constants.PREF_NAME);
        preferences.put(Constants.FOLDER_PATH, folderPath);
    }
}
