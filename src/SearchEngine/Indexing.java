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

public class Indexing {

    private static Indexing instance = null;
    private Parser parser = null;
    private String newline = System.getProperty("line.separator");

    private List<String> excludeList = Arrays.asList("<<", ">>", ">", "<", "»", "/t>><<c", "<<a", "<<t", " » ", "");

    private HashMap<String, List<Posting>> indexMap;
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
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    public void buildIndex(List<File> files) {
        parser = Parser.getInstance();
        indexMap = new HashMap<>();

        for (int i = 0; i < files.size(); i++) {
            indexMap.clear();
            String filePath = files.get(i).getAbsolutePath();
            int termPos = 0;
            int filePos = fileList.indexOf(files.get(i));
            // Check if the file was indexed or not
            if (filePos == -1) {
                fileList.add(files.get(i));
                filePos = fileList.size() - 1;
            }

            // Read input file
            helper = Helper.getInstance();
            String[] line = helper.readFileByExtenstions(filePath);
            for (int j = 0; j < line.length; j++) {
                byte byteArray[] = line[j].getBytes();
//                    line = new String(byteArray, "UTF-8");

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

                    List<Posting> postings = indexMap.get(term);
                    if (postings == null) {
                        postings = new ArrayList<>();
                        indexMap.put(term, postings);
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

            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream("indexed/data.bin");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(indexMap);
                oos.close();
                fos.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void saveIndexBinary() {
        
        
    }

    public void printMap() {
        long timeStart = 0;
        long timeEnd = 0;
        indexMap = new HashMap<>();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        // Read serializable file
        try {
            timeStart = System.currentTimeMillis();
            fis = new FileInputStream("indexed/data.bin");
            ois = new ObjectInputStream(fis);

            indexMap = (HashMap<String, List<Posting>>) ois.readObject();
            timeEnd = System.currentTimeMillis();
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Time: " + (timeEnd - timeStart) / 1000);
        for (Map.Entry<String, List<Posting>> m : indexMap.entrySet()) {
            System.out.println(m.getKey() + "=>" + m.getValue());
        }
    }

    public void sortIndex() {
        Map<String, List<Posting>> map;
        map = new TreeMap<>(indexMap);
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

    public void saveIndex() {
//        String timeLog = "indexed-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
//        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        BufferedWriter posBuffered = null;
        FileWriter posWriter = null;
        FileWriter fileWriter = null;

        try {
            posWriter = new FileWriter("indexed/pos.txt", true);
            posBuffered = new BufferedWriter(posWriter);
            for (Map.Entry<String, List<Posting>> element : indexMap.entrySet()) {
                StringBuilder builder = new StringBuilder();
                String term = element.getKey();
                fileWriter = new FileWriter("indexed/" + checkDistributionRange(term).toString() + ".txt", true);
                bufferedWriter = new BufferedWriter(fileWriter); //helper.checkDistributionRange(term);
                int m = posOfTerm[helper.checkPosDistributionRange(term)];
                List<Posting> postings = element.getValue();
                builder.append(term + "=>");
                int curPos = postings.get(0).getFilePos();
                builder.append("(" + postings.get(0).getFilePos() + ":" + postings.get(0).getTermPos());
                if (postings.size() > 1) {
                    for (int i = 1; i < postings.size(); i++) {
                        if (postings.get(i).getFilePos() == curPos) {
                            builder.append("," + postings.get(i).getTermPos());
                        } else {
                            curPos = postings.get(i).getFilePos();
                            builder.append(")(" + curPos + ":" + postings.get(i).getTermPos());
                        }
                        if (i == postings.size() - 1) {
                            builder.append(")");
                            builder.append(System.lineSeparator());
                        }
                    }
                } else {
                    builder.append(")");
                    builder.append(System.lineSeparator());
                }
                bufferedWriter.write(builder.toString());
                bufferedWriter.flush();

                posBuffered.write((term + "=>" + posOfTerm[helper.checkPosDistributionRange(term)] + System.lineSeparator()).toString());
                posOfTerm[helper.checkPosDistributionRange(term)]++;

//                fileWriter.close();
            }
            bufferedWriter.close();
            fileWriter.close();
            posBuffered.close();
            posWriter.close();
//            helper.closeFile();
//            bufferedWriter.close();
//            fileWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Index saved");
        }
    }

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
        File file = new File(path);
        Scanner input = null;
        int noOfLetter = 20;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }

        int count = 0;
        int wordCount = 0;
        StringBuilder stringBuilder = new StringBuilder();

        input.useDelimiter("\\s+");
        if (position < noOfLetter) {
            while (!input.hasNext("^[(<<\\w)|(\\w>>)]^[\\p{L}\\s\\d]")) {
                stringBuilder.append(input.next() + " ");
                count++;
                if (count == position - 1) {
                    stringBuilder.append("<b>");
                }
                if (count == position) {
                    stringBuilder.append("</b>");
                }
                if (count == noOfLetter) {
                    stringBuilder.append("...");
                    break;
                }
            }
        } else {
            stringBuilder.append("...");
            for (int i = 0; i <= position + noOfLetter; i++) {
                if (i >= position - noOfLetter) {
                    if (!input.hasNext("^[(<<\\w)|(\\w>>)]^[\\p{L}\\s\\d]")) {
                        if (i == position - 1) {
                            stringBuilder.append("<b>");
                        }
                        if (i == position) {
                            stringBuilder.append("</b>");
                        }
                        stringBuilder.append(input.next() + " ");
                    }
                } else {
                    if (!input.hasNext("^[(<<\\w)|(\\w>>)]^[\\p{L}\\s\\d]")) {
                        input.next();
                    }
                }
            }
            if (input.hasNext()) {
                stringBuilder.append("...");
            } else {
                stringBuilder.append(".");
            }
        }
        return stringBuilder.toString();
    }

    public String retrieveIndex(String path, List<Integer> position) {
        File file = new File(path);
        Scanner input = null;
        int noOfLetter = 20;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
        int count = 0;
        int wordCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        input.useDelimiter("\\s(<<\\\\w)|(\\\\w>>)[^\\\\p{L}\\\\s\\\\d]");
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
        if (posMin < noOfLetter) {
            while (input.hasNext()) {
                stringBuilder.append(input.next() + " ");
                count++;
                for (int i = 0; i < position.size(); i++) {
                    if (count == position.get(i) - 1) {
                        stringBuilder.append("<b>");
                    }
                    if (count == position.get(i)) {
                        stringBuilder.append("</b>");
                    }
                }
                if (count == noOfLetter) {
                    stringBuilder.append("...");
                    break;
                }
            }
        } else {
            stringBuilder.append("...");
            for (int i = 0; i <= posMax + noOfLetter; i++) {
                if (i >= posMin - noOfLetter) {
                    if (input.hasNext()) {
                        for (int j = 0; j < position.size(); j++) {
                            if (i == position.get(j) - 1) {
                                stringBuilder.append("<b>");
                            }
                            if (i == position.get(j)) {
                                stringBuilder.append("</b>");
                            }
                        }
                        stringBuilder.append(input.next() + " ");
                    }
                } else {
                    if (input.hasNext()) {
                        input.next();
                    }
                }
            }
            if (input.hasNext()) {
                stringBuilder.append("...");
            } else {
                stringBuilder.append(".");
            }
        }
        return stringBuilder.toString();
    }

    public String searchOne(List<Postings> postingResult) {
//        String query = keyword.toLowerCase().split("(\\s|[.]|[,]|[:]|[?])+")[0];
//        byte byteArray[] = query.getBytes();
//        try {
//            query = new String(byteArray, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        List<Posting> postingResult = indexMap.get(query);
//        StringBuilder result = new StringBuilder();
//
//        if (postingResult != null) {
//            result.append("<b style='font-size: 130%'>*** " + postingResult.size() + " results matched ***</b>");
//            for (int i = 0; i < postingResult.size(); i++) {
//                String innerDoc = retrieveIndex(fileList.get(postingResult.get(i).getFilePos()).getPath(), postingResult.get(i).getTermPos());
//                String fileName = fileList.get(postingResult.get(i).getFilePos()).getName();
//                result.append("<p style='color: blue; font-size= 130%'>\"<b>"
//                        + "<a href='file:///" + fileList.get(postingResult.get(i).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
//                        + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos());
//                result.append("<div>" + innerDoc + "</div></p>");
//            }
//            result.append("<br/>");
//        } else {
//            result.append("No matches found");
//        }
//        return result.toString();

        StringBuilder result = new StringBuilder();

        if (postingResult != null) {
            result.append("<b style='font-size: 130%'>*** " + postingResult.get(0).getSize() + " results matched ***</b>");
            for (int j = 0; j < postingResult.size(); j++) {
                for (int i = 0; i < postingResult.get(j).getTermPos().size(); i++) {
                    String innerDoc = retrieveIndex(fileList.get(postingResult.get(j).getFilePos()).getPath(), postingResult.get(j).get(i));
                    String fileName = fileList.get(postingResult.get(j).getFilePos()).getName();
                    result.append("<p style='color: blue; font-size= 130%'>" + i + ". \"<b>"
                            + "<a href='file:///" + fileList.get(postingResult.get(j).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
                            + "\"</b>, <em>Position:</em> " + postingResult.get(j).get(i));
                    result.append("<div>" + innerDoc + "</div></p>");
                }
                result.append("<br/>");
            }
        } else {
            result.append("No matches found");
        }
        return result.toString();
    }

//    public String searchPhrase(String keyword) {
//        phraseMap = new HashMap<>();
//        String[] query = keyword.toLowerCase().split("\\W+");
//        StringBuilder result = new StringBuilder();
//        List<List<Posting>> postingResult = new ArrayList<>();
//        boolean hasResult = false;
//        int smallestPosting = Integer.MAX_VALUE;
//        List<Integer> valueList = new ArrayList<>();
//        int lengthOfPhrase = 0;
//        List<String> meaningfulWords = new ArrayList<>();
//        for (int i = 0; i < query.length; i++) {
//            List<Posting> list = (List<Posting>) indexMap.get(query[i]);
//            if (list == null) {
//                continue;
//            } else {
//                meaningfulWords.add(query[i]);
//                lengthOfPhrase++;
//            }
//            if (list.size() < smallestPosting) {
//                smallestPosting = list.size();
//                postingResult.add(0, indexMap.get(query[i]));
//            } else {
//                postingResult.add(list);
//            }
//        }
//        if (lengthOfPhrase == 1) {
//            return searchOne(meaningfulWords.get(0));
//        } else {
//            List<Posting> initialList = postingResult.get(0);
//            for (int i = 0; i < initialList.size(); i++) {
//                int curPos = initialList.get(i).getTermPos();
//                Postings phraseResult = new Postings(-1, new ArrayList<>());
//                boolean added = false;
//                for (int j = 1; j < postingResult.size(); j++) {
//                    int curFilePos = initialList.get(i).getFilePos();
//                    if (phraseResult.getFilePos() == -1) {
//                        phraseResult.setFilePos(curFilePos);
//                        phraseResult.add(curPos);
//                    }
//                    Posting curPosting;
//                    for (int k = -5; k < 6; k++) {
//                        curPosting = new Posting(curFilePos, curPos + k);
//                        if (postingResult.get(j).contains(curPosting)) {
//                            if (!phraseResult.getTermPos().contains(curPosting.getTermPos())) {
//                                phraseResult.add(curPosting.getTermPos());
//                                if (phraseResult.getTermPos().size() == lengthOfPhrase) {
//                                    added = true;
//                                }
//                            }
//                            break;
//                        }
//                    }
//                    if (added) {
//                        int value = calculateValue(phraseResult.getTermPos());
//                        if (!valueList.contains(value)) {
//                            valueList.add(value);
//                        }
//                        List<Postings> phraseTmp = new ArrayList<>();
//                        phraseTmp.add(phraseResult);
//                        List<Postings> tmpList = phraseMap.get(value);
//                        hasResult = true;
//                        if (tmpList == null) {
//                            phraseMap.put(value, phraseTmp);
//                        } else {
//                            tmpList.add(phraseResult);
//                        }
//                    }
//                }
//            }
//            Collections.sort(valueList, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//                    return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
//                }
//            });
//            if (hasResult == true) {
//                int noOfResults = 0;
//                for (int i = 0; i < valueList.size(); i++) {
//                    noOfResults += phraseMap.get(valueList.get(i)).size();
//                }
//                result.append("<b style='font-size: 130%'>*** " + noOfResults + " results matched ***</b>");
//                for (int i = 0; i < valueList.size(); i++) {
//                    String innerDoc;
//                    List<Postings> termsPos = phraseMap.get(valueList.get(i));
//                    for (int j = 0; j < termsPos.size(); j++) {
//                        String fileName = fileList.get(termsPos.get(j).getFilePos()).getName();
//                        String filePath = fileList.get(termsPos.get(j).getFilePos()).getPath();
//                        innerDoc = retrieveIndex(filePath, termsPos.get(j).getTermPos());
//                        result.append("<p style='color: blue; font-size= 130%'>\"<b>"
//                                + "<a href='file:///" + fileList.get(termsPos.get(j).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
//                                + "\"</b>, <em>Position:</em> ");
//                        Collections.sort(termsPos.get(j).getTermPos(), new Comparator<Integer>() {
//                            @Override
//                            public int compare(Integer o1, Integer o2) {
//                                return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
//                            }
//                        });
//                        for (int k = 0; k < termsPos.get(j).getTermPos().size() - 1; k++) {
//                            result.append(termsPos.get(j).getTermPos().get(k) + " & ");
//                        }
//                        result.append(termsPos.get(j).getTermPos().get(termsPos.get(j).getTermPos().size() - 1));
//                        //+ posMin + " & " + posMax);
//                        result.append("<div>" + innerDoc + "</div></p>");
//                    }
//                }
//                result.append("<br/>");
//            } else {
//                result.append("No matches found");
//            }
//            return result.toString();
//        }
//    }
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
        List<Postings> listTerm = new ArrayList<>();
        BufferedReader bR;
        String line = "";
        helper = Helper.getInstance();
        try {
            for (int k = 0; k < tokens.length; k++) {
                if (termMap.containsKey(tokens[k])) {
                    bR = helper.getDistributionRange(tokens[k]);
                    List<Integer> listPosting = new ArrayList<>();
                    String filePos = "";
                    for (int l = 0; l < termMap.get(tokens[k]).size(); l++) {
                        for (int m = 0; m <= termMap.get(tokens[k]).get(l); m++) {
                            line = bR.readLine();
                        }
                        String posting = line.split("=>")[1];
                        List<Postings> list = new ArrayList<>();
                        String[] filePoses = posting.split("\\)");

                        for (int i = 0; i < filePoses.length; i++) {
                            filePos = filePoses[i].replaceAll("\\(|\\)", "").split(":")[0];
                            String[] postings = filePoses[i].replaceAll("\\(|\\)", "").split(":")[1].split(",");
                            for (int m = 0; m < postings.length; m++) {
                                listPosting.add(Integer.parseInt(postings[m]));
                            }
                        }
                        Postings term = new Postings(Integer.parseInt(filePos), listPosting);
                        listTerm.add(term);
                    }
                    List<Postings> listPostings = tempMap.get(tokens[k]);
                    if (listPostings == null) {
                        listPostings = new ArrayList<>();
                        tempMap.put(tokens[k], listPostings);
                    }
                    listPostings.addAll(listTerm);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tokens.length == 1) {
            return searchOne(listTerm);
        } else {
            return null;
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
