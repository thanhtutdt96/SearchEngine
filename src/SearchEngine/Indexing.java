package SearchEngine;

import Tokenizer.Parser;
import Constant.Constants;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Indexing {

    public HashMap<String, List<Posting>> hashMap_a_d;
    public HashMap<String, List<Posting>> hashMap_e_h;
    public HashMap<String, List<Posting>> hashMap_i_l;
    public HashMap<String, List<Posting>> hashMap_m_p;
    public HashMap<String, List<Posting>> hashMap_q_t;
    public HashMap<String, List<Posting>> hashMap_u_w;
    public HashMap<String, List<Posting>> hashMap_x_z;
    public HashMap<String, List<Posting>> hashMap_other;
    public HashMap<String, List<Posting>> tempHashMap;

    Pattern pattern1 = Pattern.compile("^[aáàảãạăắặằẳẵâấầẩẫậbcdđAÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪBCD]");
    Pattern pattern2 = Pattern.compile("^[eéèẻẽẹêếềểễệfghEÉÈẺẼẸÊẾỀỂỄỆFGH]");
    Pattern pattern3 = Pattern.compile("^[iíìỉĩịjklIÍÌỈĨỊJKL]");
    Pattern pattern4 = Pattern.compile("^[mnoóòỏõọôốồổỗộơớờởỡợpMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]");
    Pattern pattern5 = Pattern.compile("^[qrstQRST]");
    Pattern pattern6 = Pattern.compile("^[uúùủũụưứừửữựvwUÚÙỦŨỤƯỨỪỬỮỰVW]");
    Pattern pattern7 = Pattern.compile("^[xyýỳỷỹỵzXYÝỲỶỸỴZ]");

    Matcher m;

    public static ResultHandler handler = null;

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

    public static HashMap<String, List<Posting>> copy(
            HashMap<String, List<Posting>> original) {
        HashMap<String, List<Posting>> copy = new HashMap<String, List<Posting>>();
        for (Map.Entry<String, List<Posting>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<Posting>(entry.getValue()));
        }
        return copy;
    }

    private static Indexing instance = null;
    public Helper helper;
    private Parser parser = null;
    private MainFrame mainFrame;
    //private String newline = System.getProperty("line.separator");

    private final List<String> excludeList = Arrays.asList("<<", ">>", ">", "<", "»", "/t>><<c", "<<a", "<<t", " » ", "");

//    private HashMap<String, List<Posting>> indexMap;
    private HashMap<String, List<Integer>> termMap;
    private HashMap<String, List<Postings>> tempMap;
    public List<String> fileList;
    private HashMap<Integer, List<Postings>> phraseMap;
    private List<Posting> postingResults;

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

    public void initContainer() {
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

    public void addFileList(List<String> files) {
        readFileList();
        fileList.addAll(files);
    }

    public void buildIndex(List<String> files) {
        mainFrame = MainFrame.getInstance();
//        mainFrame.txtResult.setText("Building index ...");
        parser = Parser.getInstance();
        String filePath;
        String[] line;
        HashMap<String, Boolean> stopWordsMap = new HashMap<>();
        int termPos;
        int filePos;

        for (int i = 0; i < files.size(); i++) {
            for (int k = 0; k < Constants.STOP_WORDS.size(); k++) {
                stopWordsMap.put(Constants.STOP_WORDS.get(k), Boolean.FALSE);
            }
            filePath = files.get(i);
            termPos = 0;
            filePos = fileList.indexOf(files.get(i));
            if (filePos == -1) {
                fileList.add(files.get(i));
                filePos = fileList.size() - 1;
            }

            helper = Helper.getInstance();
            line = helper.readFileByExtenstions(filePath);
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
                    termPos++;

                    String term = parser.removeRedundantCharacters(tmp.toLowerCase());
                    if (stopWordsMap.get(term) != null) {
                        if (stopWordsMap.get(term) != Boolean.TRUE) {
                            stopWordsMap.put(term, Boolean.TRUE);
                        } else if (getHashMapByTerm(term).get(term) != null) {
                            continue;
                        }
                    }

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
        }
//        mainFrame.txtResult.setText("Saving ...");
    }

    public void mergeIndex() {
//        mainFrame = MainFrame.getInstance();
//        mainFrame.txtResult.setText("Merging index ...");
        mergeMap(hashMap_a_d);
        mergeMap(hashMap_e_h);
        mergeMap(hashMap_i_l);
        mergeMap(hashMap_m_p);
        mergeMap(hashMap_q_t);
        mergeMap(hashMap_u_w);
        mergeMap(hashMap_x_z);
        mergeMap(hashMap_other);
//        mainFrame.txtResult.setText("Saving index ...");
    }

    public void mergeMap(HashMap<String, List<Posting>> hashMap) {
        try {
            FileInputStream fis;
            ObjectInputStream ois;
            fis = new FileInputStream("indexed/" + getIndexFileNameByHashMap(hashMap) + ".bin");
            GZIPInputStream gzipIn = new GZIPInputStream(fis);
            ois = new ObjectInputStream(gzipIn);
            tempHashMap = (HashMap<String, List<Posting>>) ois.readObject();
            ois.close();
            fis.close();
            for (Map.Entry<String, List<Posting>> entry : hashMap.entrySet()) {
                List<Posting> postings = tempHashMap.get(entry.getKey());
                if (postings == null) {
                    postings = new ArrayList<>();
                }
                postings.addAll(entry.getValue());
            }
            for (Map.Entry<String, List<Posting>> entry : tempHashMap.entrySet()) {
                if (!hashMap.containsKey(entry.getKey())) {
                    hashMap.put(entry.getKey(), entry.getValue());
                }
            }
            saveIndexBinary(hashMap);
            tempHashMap = new HashMap<>();
            System.gc();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveAllIndex() {
        long timeStart = System.currentTimeMillis();
        Thread thread1 = new Thread(() -> {
            saveIndexBinary(hashMap_a_d);
        });

        Thread thread2 = new Thread(() -> {
            saveIndexBinary(hashMap_e_h);
        });

        Thread thread3 = new Thread(() -> {
            saveIndexBinary(hashMap_i_l);
        });

        Thread thread4 = new Thread(() -> {
            saveIndexBinary(hashMap_m_p);
        });

        Thread thread5 = new Thread(() -> {
            saveIndexBinary(hashMap_q_t);
        });

        Thread thread6 = new Thread(() -> {
            saveIndexBinary(hashMap_u_w);
        });

        Thread thread7 = new Thread(() -> {
            saveIndexBinary(hashMap_x_z);
        });

        Thread thread8 = new Thread(() -> {
            saveIndexBinary(hashMap_other);
        });

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
        File file;
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            file = new File("indexed/" + getIndexFileNameByHashMap(hashMap) + ".bin");
            if (file != null) {
                fos = new FileOutputStream(file);
                fos.close();
                file.delete();
            }
            fos = new FileOutputStream(file);
            GZIPOutputStream gz = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gz);
            oos.writeObject(hashMap);
            oos.close();
        } catch (IOException e) {
            System.err.println(e);
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
            new Thread(() -> {
                readBinFile(file.getName());
            }).start();
        }
    }

    public void readBinFile(String fileName) {
        HashMap<String, List<Posting>> hashMap = new HashMap<>();
        FileInputStream fis;
        ObjectInputStream ois;
        // Read serializable file
        try {
            fis = new FileInputStream("indexed/" + fileName);
            GZIPInputStream gzipIn = new GZIPInputStream(fis);
            ois = new ObjectInputStream(gzipIn);
            hashMap = (HashMap<String, List<Posting>>) ois.readObject();
            setHashMapByFileName(fileName, hashMap);
            ois.close();
            fis.close();
            System.out.println(fileName);
        } catch (IOException | ClassNotFoundException e) {
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
            for (String file : fileList) {
                builder.append(file);
                builder.append(System.lineSeparator());

            }
            bufferedWriter.write(builder.toString());
            bufferedWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("File list saved");
        }
        mainFrame = MainFrame.getInstance();
        mainFrame.txtResult.setText("Done");
    }

    private String checkDistributionRange(String term) {
        // áàảãạăắặằẳẵâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẶẰẲẴÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ

        m = pattern1.matcher(term);
        if (m.find()) {
            return "a-d";
        }
        m = pattern2.matcher(term);
        if (m.find()) {
            return "e-h";
        }
        m = pattern3.matcher(term);
        if (m.find()) {
            return "i-l";
        }
        m = pattern4.matcher(term);
        if (m.find()) {
            return "m-p";
        }
        m = pattern5.matcher(term);
        if (m.find()) {
            return "q-t";
        }
        m = pattern6.matcher(term);
        if (m.find()) {
            return "u-w";
        }
        m = pattern7.matcher(term);
        if (m.find()) {
            return "x-z";
        }
        return "other";
    }

//    public String getFilePath(String path, String prefix) {
//        File folder = new File(path);
//        File[] listOfFiles = folder.listFiles();
//
//        for (File file : listOfFiles) {
//            if (file.getName().startsWith(prefix)) {
//                return file.getPath();
//            }
//        }
//        return "";
//    }
    public void readFileList() {
        try {
            fileList = new ArrayList<>();
            FileReader fileReader = new FileReader("indexed/list.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileList.add(line);
            }
            bufferedReader.close();
            fileReader.close();
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
        int count = -1;
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

                    count++;
                    if (count == position - 1) {
                        stringBuilder.append("<b>");
                    }
                    if (count == position) {
                        stringBuilder.append("</b>");
                    }
                    if (count == position + noOfLetter) {
                        stringBuilder.append("...");
                        return stringBuilder.toString();
                    }
                    stringBuilder.append(tmp).append(" ");
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
                            stringBuilder.append(tmp).append(" ");
                            if (count == position + noOfLetter) {
                                return stringBuilder.toString();
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

    public String retrieveIndex(String path, List<Integer> position) {
        String[] line = helper.readFileByExtenstions(path);
        parser = Parser.getInstance();
        int noOfLetter = 20;
        int count = 0;
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

                    stringBuilder.append(tmp).append(" ");
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
                    count++;
                    if (count <= posMax + noOfLetter) {
                        if (count >= posMin - noOfLetter) {
                            stringBuilder.append(tmp).append(" ");
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

    public String searchOne(String keyword, int pageNumber) {
        String query = keyword.toLowerCase().split("(\\s|[.]|[,]|[:]|[?])+")[0];
        int shown = 0;
        byte byteArray[] = query.getBytes();
        try {
            query = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Posting> postingResult = getHashMapByTerm(query).get(query);
        StringBuilder result = new StringBuilder();
        if (postingResult == null) {
            result.append("No results were found");
            return result.toString();
        }

        handler = ResultHandler.getInstance();
        if (pageNumber - 1 >= 0) {
            int firstRow = (pageNumber - 1) * 10;
            int lastRow = firstRow + 10;
            if (lastRow > postingResult.size() - 1) {
                lastRow = postingResult.size();
            }
            List<Integer> shownPos = new ArrayList<>();
//            List<Posting> postingResult = pResult.subList(firstRow, lastRow);

            int numberOfFile = 0;
            for (Posting p : postingResult) {
                if (!shownPos.contains(p.getFilePos())) {
                    shownPos.add(p.getFilePos());
                    numberOfFile++;
                }
            }
            if (!shownPos.isEmpty()) {
                shownPos.clear();
            }
            if (postingResult != null) {
                this.postingResults = new ArrayList<>();
                this.postingResults.addAll(postingResult);
                if (phraseMap != null) {
                    phraseMap.clear();
                }
                result.append("<div style='text-align: center;'><b style='font-size: 130%'>*** " + postingResult.size() + " results matched in " + numberOfFile + " files ***</b></div>");
                result.append("<div><b style='font-size: 110%; color:red'><---- Page: " + pageNumber + " ----</b></div>");
                boolean isShown = false;
                shownPos.add(postingResult.get(0).getFilePos());
//                for (int i = firstRow; i < lastRow; i++) {
                for (int i = 0; i < postingResult.size(); i++) {
                    if (isShown == false) {
                        isShown = true;
                        shown += 1;
                        if (shown > firstRow) {
                            if (shown > lastRow) {
                                break;
                            }
                            String innerDoc = retrieveIndex(fileList.get(postingResult.get(i).getFilePos()), postingResult.get(i).getTermPos());
                            String fileName = getName(fileList.get(postingResult.get(i).getFilePos()));
                            result.append("<p style='color: blue; font-size= 130%'><b>\""
                                    + "<a href='file:///" + fileList.get(postingResult.get(i).getFilePos()) + "'>" + fileName + "</a>"
                                    + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos() + "&nbsp&nbsp&nbsp&nbsp<a href='file:///m" + postingResult.get(i).getFilePos() + "'>" + "More from this file" + "</a>");
                            result.append("<div>" + innerDoc + "</div></p>");
                        }
                    }
                    if (shownPos.contains(postingResult.get(postingResult.size() - 1).getFilePos())) {
                        break;
                    }
                    if (!shownPos.contains(postingResult.get(i).getFilePos())) {
                        shownPos.add(postingResult.get(i).getFilePos());
                        i--;
                        isShown = false;
                    }
                }
                result.append("<br/>");
            } else {
                result.append("No matches found");
            }
            result.append(handler.appendSearchPages(numberOfFile));//postingResult.size()));
        }

        return result.toString();
    }

    public String getName(String path) {
        String name = path.split("\\\\")[path.split("\\\\").length - 1];
        return name;
    }

    public String searchPhrase(String keyword, int pageNumber) {
        handler = ResultHandler.getInstance();
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
            return searchOne(meaningfulWords.get(0), pageNumber);
        } else if (lengthOfPhrase < query.length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i< meaningfulWords.size(); i++){
                sb.append(meaningfulWords.get(i)).append(" ");
            }
            return searchPhrase(sb.toString(), pageNumber);
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
                    for (int k = -5; k < 6; k++) {
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
                    return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
                }
            });
            int count = 0;
            boolean isFound = false;
            int noOfResults = 0;
            int numberOfFile = 0;
            int shown = 0;
            List<Integer> shownPos = new ArrayList<>();
            if (hasResult == true) {
                if (this.postingResults != null) {
                    this.postingResults.clear();
                }
                for (int i = 0; i < valueList.size(); i++) {
                    noOfResults += phraseMap.get(valueList.get(i)).size();
                    for (Postings p : phraseMap.get(valueList.get(i))) {
                        if (!shownPos.contains(p.getFilePos())) {
                            numberOfFile++;
                            shownPos.add(p.getFilePos());
                        }
                    }
                }
                if (!shownPos.isEmpty()) {
                    shownPos.clear();
                }
                result.append("<div><b style='font-size: 130%'>*** " + noOfResults + " results matched in " + numberOfFile + " files ***</b></div>");
                result.append("<b style='font-size: 110%, color:red'>---- Page: " + pageNumber + " ----</b>");

                for (int i = 0; i < valueList.size(); i++) {
                    String innerDoc;
                    List<Postings> termsPos = phraseMap.get(valueList.get(i));
                    if (pageNumber - 1 >= 0) {
                        int firstRow = (pageNumber - 1) * 10;
                        int lastRow = firstRow + 10;
                        if (lastRow > termsPos.size() - 1) {
                            lastRow = termsPos.size();
                        }
                        for (int j = 0; j < termsPos.size(); j++) {
                            if (!shownPos.contains(termsPos.get(j).getFilePos())) {
                                shownPos.add(termsPos.get(j).getFilePos());
                                shown++;
                                if (shown > firstRow) {
                                    if (shown > lastRow) {
                                        isFound = true;
                                        break;
                                    }
                                    String fileName = getName(fileList.get(termsPos.get(j).getFilePos()));
                                    String filePath = fileList.get(termsPos.get(j).getFilePos());
                                    innerDoc = retrieveIndex(filePath, termsPos.get(j).getTermPos());
                                    result.append("<p style='color: blue; font-size= 130%'>\"<b>"
                                            + "<a href='file:///" + fileList.get(termsPos.get(j).getFilePos()) + "'>" + fileName + "</a>"
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
                                    result.append("&nbsp&nbsp&nbsp&nbsp<a href='file:///m" + termsPos.get(j).getFilePos() + "'>" + "More from this file" + "</a>" + "<div>" + innerDoc + "</div></p>");
                                    if (count == pageNumber * 10 + 10) {
                                        isFound = true;
                                    }
                                }
                            }
                            if (isFound) {
                                break;
                            }
                        }
                    } else {
                        result.append("The chosen page is invalid");
                    }
                }
                result.append("<br/>");
                result.append(handler.appendSearchPages(numberOfFile));
            } else {
                result.append("No matches found");
            }
            return result.toString();
        }
    }

    public int calculateValue(List<Integer> posting) {
        int value = 0;
        Collections.sort(posting);
        for (int i = 1; i < posting.size() - 1; i++) {
            if (posting.get(i) + 1 == posting.get(i + 1)) {
                continue;
            }
            value += Math.abs((posting.get(i) - posting.get(i + 1)));
        }
        value += Math.abs(posting.get(posting.size() - 1) - posting.get(1));
        return value;
    }

//    public void printMap(HashMap<String, List<Posting>> map) {
//        for (Map.Entry<String, List<Posting>> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + "=>" + entry.getValue());
//        }
//    }
    public List<String> indexFileList(String[] paths, boolean isFile) {
        fileList = new ArrayList<>();
        if (isFile) {
            File[] listOfFiles = new File[paths.length];
            for (int i = 0; i < paths.length; i++) {
                listOfFiles[i] = new File(paths[i]);
            }
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (!fileList.contains(file.getAbsolutePath())) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
            }
        } else {
            File folder = new File(paths[0]);
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (!fileList.contains(file.getAbsolutePath())) {
                        fileList.add(file.getAbsolutePath());
                    }
                }
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

    public String performSearch(String word, int pageNumber) {
        tempMap = new HashMap<>();
        String[] tokens = word.split("\\s+");

        if (tokens.length == 1) {
            return searchOne(tokens[0], pageNumber);
        } else {
            return searchPhrase(word, pageNumber);
        }
    }

    public String performSearchInFilePos(int filePos) {
        StringBuilder result = new StringBuilder();
        if (this.postingResults != null) {
            if (this.postingResults.size() > 0) {
                int noAppear = 0;
                for (Posting p : this.postingResults) {
                    if (p.getFilePos() == filePos) {
                        noAppear++;
                    }
                }
                result.append("<div><b style='font-size: 130%'>*** " + noAppear + " results in this file ***</b></div>");
                for (int i = 0; i < this.postingResults.size(); i++) {
                    if (postingResults.get(i).getFilePos() == filePos) {
                        String innerDoc = retrieveIndex(fileList.get(postingResults.get(i).getFilePos()), postingResults.get(i).getTermPos());
                        String fileName = getName(fileList.get(postingResults.get(i).getFilePos()));
                        result.append("<p style='color: blue; font-size= 130%'><b>"
                                + "<a href='file:///" + fileList.get(postingResults.get(i).getFilePos()) + "'>" + fileName + "</a>"
                                + "\"</b>, <em>Position:</em> " + postingResults.get(i).getTermPos());
                        result.append("<div>" + innerDoc + "</div></p>");
                    }
                }
            }
        } else if (phraseMap != null) {
            int noAppear = 0;
            int querySize = 0;
            for (Map.Entry<Integer, List<Postings>> entry : phraseMap.entrySet()) {
                List<Postings> termsPos = entry.getValue();
                for (int i = 0; i < termsPos.size(); i++) {
                    for (int j = 0; j < entry.getValue().get(i).getSize(); j++) {
                        if (entry.getValue().get(i).getFilePos() == filePos) {
                            noAppear++;
                        }
                        querySize = entry.getValue().get(i).getSize();
                    }
                }
            }
            result.append("<div><b style='font-size: 130%'>*** " + (noAppear/querySize) + " results in this file ***</b></div>");
            for (Map.Entry<Integer, List<Postings>> entry : phraseMap.entrySet()) {
                List<Postings> termsPos = entry.getValue();
                for (int i = 0; i < termsPos.size(); i++) {
                    if (termsPos.get(i).getFilePos() == filePos) {
                        String innerDoc;
                        String fileName = getName(fileList.get(termsPos.get(i).getFilePos()));
                        String filePath = fileList.get(termsPos.get(i).getFilePos());
                        innerDoc = retrieveIndex(filePath, termsPos.get(i).getTermPos());
                        result.append("<p style='color: blue; font-size= 130%'>\"<b>"
                                + "<a href='file:///" + fileList.get(termsPos.get(i).getFilePos()) + "'>" + fileName + "</a>"
                                + "\"</b>, <em>Position:</em> ");
                        Collections.sort(termsPos.get(i).getTermPos(), new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
                            }
                        });
                        for (int k = 0; k < termsPos.get(i).getTermPos().size() - 1; k++) {
                            result.append(termsPos.get(i).getTermPos().get(k) + " & ");
                        }
                        result.append(termsPos.get(i).getTermPos().get(termsPos.get(i).getTermPos().size() - 1));
                        result.append("<div>" + innerDoc + "</div></p>");
                    }
                }
            }
        }
        return result.toString();
    }

//    public void readIndex() {
//        termMap = new HashMap<>();
//        try {
//            BufferedReader bR = new BufferedReader(new FileReader("indexed/pos.txt"));
//            String line;
//            while ((line = bR.readLine()) != null) {
//                String term = line.split("=>")[0];
//                int pos = Integer.parseInt(line.split("=>")[1]);
//                List<Integer> tempPos = termMap.get(term);
//                if (tempPos == null) {
//                    tempPos = new ArrayList<>();
//                    termMap.put(term, tempPos);
//                }
//                tempPos.add(pos);
//            }
//            bR.close();
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Indexing.class
//                    .getName()).log(Level.SEVERE, null, ex);
//
//        } catch (IOException ex) {
//            Logger.getLogger(Indexing.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public String getDefaultPath() {
        File file = new File("res/");
        return file.getAbsolutePath();
    }

    public void saveFolderPath(String folderPath) {
        Preferences preferences = Preferences.userRoot().node(Constants.PREF_NAME);
        preferences.put(Constants.FOLDER_PATH, folderPath);
    }

}
