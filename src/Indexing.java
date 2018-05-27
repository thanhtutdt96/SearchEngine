
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Indexing {

    private static Indexing instance = null;

    private String newline = System.getProperty("line.separator");
    
    private List<String> excludeList = Arrays.asList("<<", "<", "»","");

    private HashMap<String, List<Posting>> indexMap;
    private List<File> fileList;
    private HashMap<Integer, List<Postings>> phraseMap;

    public static Indexing getInstance() {
        if (instance == null) {
            instance = new Indexing();
        }
        return instance;
    }

    public void convertFileToUTF8(List<File> file) {
//        for(int i=0; i<file.size(); i++){
//      con      String charset="UTF-8";
//            FileReader fileReader=new FileReader(file);
//        }
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
        indexMap = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            int termPos = 0;
            int filePos = fileList.indexOf(files.get(i));
            // Check if the file was indexed or not
            if (filePos == -1) {
                fileList.add(files.get(i));
                filePos = fileList.size() - 1;
            }

            try {
                // Read input file
                FileReader fileReader = new FileReader(files.get(i));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    byte byteArray[] = line.getBytes();
                    line = new String(byteArray, "UTF-8");

                    if (line.trim().length() == 0) {
                        continue;
                    }

                    for (String tmp : line.split("[^a-zA-Z0-9'\\p{L}]+")) {
                        String termTemp = tmp.toLowerCase().trim();
                        termPos++;
                        
//                        String term = termTemp.split("\\W+")[0];                        
                        String term = termTemp;

                        // Exclude the stop words
                        if (excludeList.contains(term)) {
                            continue;
                        }
                        List<Posting> postings = indexMap.get(term);
                        if (postings == null) {
                            postings = new ArrayList<>();
                            indexMap.put(term, postings);
                        }
                        postings.add(new Posting(filePos, termPos));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public void saveIndex() {
//        String timeLog = "indexed-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        StringBuilder builder = new StringBuilder();

        try {
            fileWriter = new FileWriter("indexed/index.txt");
            bufferedWriter = new BufferedWriter(fileWriter);

            for (Map.Entry<String, List<Posting>> element : indexMap.entrySet()) {
                String term = element.getKey();
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
            }
            bufferedWriter.write(builder.toString());
            bufferedWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Index saved");
        }
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
            while (input.hasNext()) {
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
                    if (input.hasNext()) {
                        if (i == position - 1) {
                            stringBuilder.append("<b>");
                        }
                        if (i == position) {
                            stringBuilder.append("</b>");
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
        input.useDelimiter("\\s+");
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

    public String searchOne(String keyword) {
        String query = keyword.toLowerCase().split("(\\s|[.]|[,]|[:]|[?])+")[0];
        byte byteArray[] = query.getBytes();
        try {
            query = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Indexing.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuilder result = new StringBuilder();
        List<Posting> postingResult = indexMap.get(query);

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
    }

    public String searchPhrase(String keyword) {
        phraseMap = new HashMap<>();
        String[] query = keyword.toLowerCase().split("\\W+");
        StringBuilder result = new StringBuilder();
        List<List<Posting>> postingResult = new ArrayList<>();
        boolean hasResult = false;
        int smallestPosting = Integer.MAX_VALUE;
        List<Integer> valueList = new ArrayList<>();
        int lengthOfPhrase = 0;
        List<String> meaningfulWords = new ArrayList<>();
        for (int i = 0; i < query.length; i++) {
            List<Posting> list = (List<Posting>) indexMap.get(query[i]);
            if (list == null) {
                continue;
            } else {
                meaningfulWords.add(query[i]);
                lengthOfPhrase++;
            }
            if (list.size() < smallestPosting) {
                smallestPosting = list.size();
                postingResult.add(0, indexMap.get(query[i]));
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

    public List<File> indexFileList(String path) {
        fileList = new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
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

    public void readIndex() {
        indexMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader("indexed/index.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String term = line.split("=>")[0];
                String posting = line.split("=>")[1];
                List<Posting> list = new ArrayList<>();
                String[] filePoses = posting.split("\\)");

                for (int i = 0; i < filePoses.length; i++) {
                    String filePos = filePoses[i].replaceAll("\\(|\\)", "").split(":")[0];
                    String[] postings = filePoses[i].replaceAll("\\(|\\)", "").split(":")[1].split(",");

                    for (int j = 0; j < postings.length; j++) {
                        String termPos = postings[j].split("-")[0];
                        list.add(new Posting(Integer.parseInt(filePos), Integer.parseInt(termPos)));
                    }
                }
                indexMap.put(term, list);

            }
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
