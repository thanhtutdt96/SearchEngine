
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexing {

    private static Indexing instance = null;

    private List<String> stopwordsList = Arrays.asList("a", "able", "about",
            "across", "after", "all", "almost", "also", "am", "among", "an",
            "and", "any", "are", "as", "at", "be", "because", "been", "but",
            "by", "can", "cannot", "could", "dear", "did", "do", "does",
            "either", "else", "ever", "every", "for", "from", "get", "got",
            "had", "has", "have", "he", "her", "hers", "him", "his", "how",
            "however", "i", "if", "in", "into", "is", "it", "its", "just",
            "least", "let", "like", "likely", "may", "me", "might", "most",
            "must", "my", "neither", "no", "nor", "not", "of", "off", "often",
            "on", "only", "or", "other", "our", "own", "rather", "said", "say",
            "says", "she", "should", "since", "so", "some", "than", "that",
            "the", "their", "them", "then", "there", "these", "they", "this",
            "tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
            "what", "when", "where", "which", "while", "who", "whom", "why",
            "will", "with", "would", "yet", "you", "your", "s", "t");

    private HashMap<String, List<Posting>> indexMap = new HashMap<>();
    private List<File> fileList = new ArrayList<>();
    private HashMap<Integer, List<Integer>> phraseMap = new HashMap<>();

    public static Indexing getInstance() {
        if (instance == null) {
            instance = new Indexing();
        }
        return instance;
    }

    public void buildIndex(List<File> files) {
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
                    if (line.trim().length() == 0) {
                        continue;
                    }

                    for (String tmp : line.split("\\s+")) {
                        String termTemp = tmp.toLowerCase();
                        termPos++;

                        String term = termTemp.split("\\W+")[0];
                        // Exclude the stop words
                        if (stopwordsList.contains(term)) {
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
        String timeLog = "list-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        StringBuilder builder = new StringBuilder();

        try {
            fileWriter = new FileWriter("indexed/" + timeLog + ".txt");
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
        String timeLog = "indexed-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        StringBuilder builder = new StringBuilder();

        try {
            fileWriter = new FileWriter("indexed/" + timeLog + ".txt");
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
            FileReader fileReader = new FileReader(getFilePath("indexed/", "list"));
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
                            if (i == position.get(i) - 1) {
                                stringBuilder.append("<b>");
                            }
                            if (i == position.get(i)) {
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

    public String searchResult(String keyword) {
        String query = keyword.toLowerCase().split("\\W+")[0];
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

//    public String searchPhrase(String keyword) {
//        String[] query = keyword.toLowerCase().split("\\W+");
//        StringBuilder result = new StringBuilder();
//        List<Posting>[] postingResult = new ArrayList[query.length];
//        boolean hasResult = false;
//        for (int i = 0; i < query.length; i++) {
//            postingResult[i] = indexMap.get(query[i]);
//        }
//        List<Phrase> phrase = new ArrayList<>();
//        if (postingResult.length > 1) {
//            for (int j = 0; j < postingResult.length; j++) {
//                for (int k = j + 1; k < postingResult.length; k++) {
//                    if (postingResult[j] != null && postingResult[k] != null) {
//                        for (int m = 0; m < postingResult[j].size(); m++) {
//                            for (int n = 0; n < postingResult[k].size(); n++) {
//                                if (postingResult[j].get(m).getFilePos() == postingResult[k].get(n).getFilePos()) {
//                                    Phrase temp = new Phrase();
//                                    temp.setTermPos1(postingResult[j].get(m).getTermPos());
//                                    temp.setFilePos(postingResult[j].get(m).getFilePos());
//                                    temp.setTermPos2(postingResult[k].get(n).getTermPos());
//                                    int value = Math.abs(temp.getTermPos1() - temp.getTermPos2());
//                                    if (temp.getTermPos2() - temp.getTermPos1() == 1) {
//                                        value = 0;
//                                    }
//                                    temp.setValue(value);
//                                    if (!phrase.contains(temp) && value <= 20) {
//                                        phrase.add(temp);
//                                        hasResult = true;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Collections.sort(phrase, new Comparator<Phrase>() {
//                @Override
//                public int compare(Phrase t, Phrase t1) {
//                    return t.getValue() < t1.getValue() ? -1 : t.getValue() == t1.getValue() ? 0 : 1;
//                }
//            });
//
//            if (hasResult == true) {
//                result.append("<b style='font-size: 130%'>*** " + phrase.size() + " results matched ***</b>");
//                for (int i = 0; i < phrase.size(); i++) {
//                    String innerDoc;
//                    int[] position = new int[2];
//                    position[0] = phrase.get(i).getTermPos1();
//                    position[1] = phrase.get(i).getTermPos2();
//                    int posMin = position[0];
//                    int posMax = position[0];
//                    for (int j = 0; j < position.length; j++) {
//                        if (position[j] < posMin) {
//                            posMin = position[j];
//                        }
//                    }
//                    for (int j = 0; j < position.length; j++) {
//                        if (position[j] > posMax) {
//                            posMax = position[j];
//                        }
//                    }
//                    innerDoc = retrieveIndex(fileList.get(phrase.get(i).getFilePos()).getPath(), position);
//                    String fileName = fileList.get(phrase.get(i).getFilePos()).getName();
//                    result.append("<p style='color: blue; font-size= 130%'>\"<b>"
//                            + "<a href='file:///" + fileList.get(phrase.get(i).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
//                            + "\"</b>, <em>Position:</em> " + posMin + " & " + posMax);
//                    result.append("<div>" + innerDoc + "</div></p>");
//                }
//                result.append("<br/>");
//            } else {
//                result.append("No matches found");
//            }
//        }
//        return result.toString();
//    }
    //        List<Integer> valueList = new ArrayList<>();
//        List<Integer> posting = new ArrayList<>();
//        if (postingResult.length > 1) {
//            for (int j = 0; j < postingResult.length; j++) {
//                for (int k = j + 1; k < postingResult.length; k++) {
//                    if (postingResult[j] != null && postingResult[k] != null) {
//                        for (int m = 0; m < postingResult[j].size(); m++) {
//                            for (int n = 0; n < postingResult[k].size(); n++) {
//                                if (postingResult[j].get(m).getFilePos() == postingResult[k].get(n).getFilePos()) {
//                                    if (posting.isEmpty()) {
//                                        posting.add(postingResult[j].get(m).getFilePos());
//                                    }
//                                    if (!posting.contains(postingResult[j].get(m).getTermPos())) {
//                                        posting.add(j, postingResult[j].get(m).getTermPos());
//                                    }
//                                    if (!posting.contains(postingResult[k].get(n).getTermPos())) {
//                                        posting.add(j, postingResult[k].get(n).getTermPos());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                valueList.add(calculateValue(posting));
//                phraseMap.put(calculateValue(posting), posting);
//            }
    public String searchPhrase(String keyword) {
        String[] query = keyword.toLowerCase().split("\\W+");
        StringBuilder result = new StringBuilder();
        List<List<Posting>> postingResult = new ArrayList<>();
        boolean hasResult = false;
        int smallestPosting = Integer.MAX_VALUE;
        List<Integer> valueList = new ArrayList<>();
        for (int i = 0; i < query.length; i++) {
            List<Posting> list = (List<Posting>) indexMap.get(query[i]);
            if (list.size() < smallestPosting) {
                smallestPosting = list.size();
                postingResult.add(0, indexMap.get(query[i]));
            } else {
                postingResult.add(list);
            }
        }
        List<Posting> initialList = postingResult.get(0);
        for (int i = 0; i < initialList.size(); i++) {
            int curPos = initialList.get(i).getTermPos();
            List<Integer> phraseResult = new ArrayList<>();
            boolean added = false;
            for (int j = 1; j < postingResult.size(); j++) {
                for (int l = 0; l < postingResult.get(j).size(); l++) {
                    if (postingResult.get(j).get(l).getFilePos() == initialList.get(i).getFilePos()) {
                        int curFilePos = postingResult.get(j).get(l).getFilePos();
                        if (phraseResult.isEmpty()) {
                            phraseResult.add(curFilePos);
                            phraseResult.add(curPos);
                        }
                        Posting curPosting;
                        for (int k = -5; k < 6; k++) {
                            curPosting = new Posting(curFilePos, curPos + k);
                            if (postingResult.get(j).contains(curPosting)) {
                                if (!phraseResult.contains(curPosting.getTermPos())) {
                                    phraseResult.add(curPosting.getTermPos());
                                }
                                if (phraseResult.size() == query.length + 1) {
                                    added = true;
                                }
                                break;
                            }
                        }
                    }
                    if (added == true) {
                        break;
                    }
                }
                if (phraseResult.size() == query.length + 1) {
                    if (!valueList.contains(calculateValue(phraseResult))) {
                        valueList.add(calculateValue(phraseResult));
                    }
                    List<Integer> phraseTmp = phraseResult;
                    List<Integer> tmpList = phraseMap.get(calculateValue(phraseResult));
                    hasResult = true;
                    if (tmpList == null) {
                        phraseMap.put(calculateValue(phraseResult), phraseTmp);
                    } else {
                        for (int m = 0; m <= query.length; m++) {
                            tmpList.add(phraseResult.get(m));
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
//        if (hasResult == true) {
//            result.append("<b style='font-size: 130%'>*** " + phraseMap.size() + " results matched ***</b>");
//            for (int i = 0; i < valueList.size(); i++) {
//                String innerDoc;
//                List<Integer> termsPos = phraseMap.get(valueList.get(i));
//                int posMin = termsPos.get(0);
//                int posMax = termsPos.get(0);
//                for (int j = 0; j < termsPos.size(); j++) {
//                    if (termsPos.get(j) < posMin) {
//                        posMin = termsPos.get(j);
//                    }
//                }
//                for (int j = 0; j < termsPos.size(); j++) {
//                    if (termsPos.get(j) > posMax) {
//                        posMax = termsPos.get(j);
//                    }
//                }
//                innerDoc = retrieveIndex(fileList.get(termsPos.get(0)).getPath(), termsPos);
//                String fileName = fileList.get(termsPos.get(0)).getName();
//                result.append("<p style='color: blue; font-size= 130%'>\"<b>"
//                        + "<a href='file:///" + fileList.get(termsPos.get(i)).getAbsolutePath() + "'>" + fileName + "</a>"
//                        + "\"</b>, <em>Position:</em> " + posMin + " & " + posMax);
//                result.append("<div>" + innerDoc + "</div></p>");
//            }
//            result.append("<br/>");
//        } else {
//            result.append("No matches found");
//        }
//    }

//            return result.toString();
        }
        if (hasResult == true) {
            result.append("<b style='font-size: 130%'>*** " + phraseMap.size() + " results matched ***</b>");
            for (int i = 0; i < valueList.size(); i++) {
                String innerDoc;
                List<Integer> termsPos = phraseMap.get(valueList.get(i));
                String fileName = fileList.get(termsPos.get(0)).getName();
                String filePath = fileList.get(termsPos.get(0)).getPath();
                List<Integer> finalTermsPos = new ArrayList<>();
                for (int j = 1; j < termsPos.size(); j++) {
                    finalTermsPos.add(termsPos.get(j));
                }
                innerDoc = retrieveIndex(filePath, finalTermsPos);
                result.append("<p style='color: blue; font-size= 130%'>\"<b>"
                        + "<a href='file:///" + fileList.get(termsPos.get(i)).getAbsolutePath() + "'>" + fileName + "</a>"
                        + "\"</b>, <em>Position:</em> ");
                Collections.sort(finalTermsPos, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
                    }
                });
                for (int j = 0; j < finalTermsPos.size()-1; j++) {
                    result.append(finalTermsPos.get(j) + " & ");
                }
                result.append(finalTermsPos.get(finalTermsPos.size() - 1));
                //+ posMin + " & " + posMax);
                result.append("<div>" + innerDoc + "</div></p>");
            }
            result.append("<br/>");
        } else {
            result.append("No matches found");
        }
        return result.toString();
    }

    public void clearPhraseMap(){
        phraseMap.clear();
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
        List<File> fileList = new ArrayList<>();
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
        try {
            FileReader fileReader = new FileReader(getFilePath("indexed/", "indexed"));
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
}
