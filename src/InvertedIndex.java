
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvertedIndex {

    private static InvertedIndex instance = null;

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
            "will", "with", "would", "yet", "you", "your");

    private HashMap<String, List<Posting>> indexMap = new HashMap<>();
    private HashMap<Integer, List<Document>> docMap = new HashMap<>();
    private List<String> fileList = new ArrayList<>();

    public static InvertedIndex getInstance() {
        if (instance == null) {
            instance = new InvertedIndex();
        }
        return instance;
    }

    public void buildIndex(File file) {
        int termPos = 0;
        int docPos = 0;
        int filePos = fileList.indexOf(file.getName());
        // Check if the file was indexed or not
        if (filePos == -1) {
            fileList.add(file.getName());
            filePos = fileList.size() - 1;
        }

        try {
            // Read input file
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                List<Document> documents = docMap.get(filePos);
                if (documents == null) {
                    documents = new ArrayList<>();
                    docMap.put(filePos, documents);
                }
                documents.add(new Document(docPos + 1, line));

                for (String tmp : line.split("\\W+")) {
                    String term = tmp.toLowerCase();
                    termPos++;
                    // Exclude the stop words
                    if (stopwordsList.contains(term)) {
                        continue;
                    }

                    List<Posting> postings = indexMap.get(term);
                    if (postings == null) {
                        postings = new ArrayList<>();
                        indexMap.put(term, postings);
                    }
                    postings.add(new Posting(filePos, termPos, docPos + 1));
                }
                docPos++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveIndex() {
        String timeLog = "indexed-" + new SimpleDateFormat("YYYYMMdd").format(Calendar.getInstance().getTime());
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        StringBuilder builder = new StringBuilder();

        try {
            for (Map.Entry<String, List<Posting>> element : indexMap.entrySet()) {
                String term = element.getKey();
                List<Posting> postings = element.getValue();
                fileWriter = new FileWriter("indexed/" + timeLog + ".txt");
                builder.append(term + "=>");
                bufferedWriter = new BufferedWriter(fileWriter);
                int curPos = postings.get(0).getFilePos();
                builder.append("(" + postings.get(0).getFilePos() + ":" + postings.get(0).getTermPos() + "-" + postings.get(0).getDocPos());
                if (postings.size() > 1) {
                    for (int i = 1; i < postings.size(); i++) {
                        if (postings.get(i).getFilePos() == curPos) {
                            builder.append("," + postings.get(i).getTermPos() + "-" + postings.get(i).getDocPos());
                        } else {
                            curPos = postings.get(i).getFilePos();
                            builder.append(")(" + curPos + ":" + postings.get(i).getTermPos() + "-" + postings.get(i).getDocPos());
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
                bufferedWriter.close();

            }
        } catch (IOException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Index saved");
        }
    }

    public String retrieveIndex(String path, int pos) {
        File file = new File(path);
        Scanner input = null;
        int noOfLetter = 20;
        try {
            input = new Scanner(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
        int count = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if (pos < noOfLetter) {
            while (input.hasNext()) {
                stringBuilder.append(input.next() + " ");
                count++;
                if (count == noOfLetter) {
                    stringBuilder.append("...");
                    break;
                }
            }
        } else {
            stringBuilder.append("...");
            for (int i = 0; i <= pos + noOfLetter; i++) {
                if (i >= pos - noOfLetter) {
                    stringBuilder.append(input.next() + " ");
                } else {
                    input.next();
                }
            }
            stringBuilder.append("...");
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
                List<Document> documents = docMap.get(postingResult.get(i).getFilePos());
                String innerDoc = documents.get(postingResult.get(i).getDocPos()).getDocText();
                String fileName = fileList.get(postingResult.get(i).getFilePos());
                result.append("<p style='color: blue; font-size= 130%'>\"<b>" + fileName + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos() + ", <em>Paragraph:</em> " + (postingResult.get(i).getDocPos() + 1));
                result.append("<div>" + innerDoc.replaceAll("(?i)(" + query + ")", "<span style='background: yellow'>$1</span>") + "</div></p>");
                //result.append("<div>" + innerDoc.substring(i)+"</div></p>");
            }
            result.append("<br/>");
        } else {
            result.append("No matches found");
        }
        return result.toString();
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

    public void readIndex(String path) {
        try {
            FileReader fileReader = new FileReader(path);
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
                        String docPos = postings[j].split("-")[0];
                        list.add(new Posting(Integer.parseInt(filePos), Integer.parseInt(termPos), Integer.parseInt(docPos)));
                    }
                }
                indexMap.put(term, list);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
