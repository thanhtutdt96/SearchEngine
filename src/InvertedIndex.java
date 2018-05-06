
import java.awt.Desktop;
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
import java.util.HashMap;
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
    private List<File> fileList = new ArrayList<>();

    public static InvertedIndex getInstance() {
        if (instance == null) {
            instance = new InvertedIndex();
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
                        postings.add(new Posting(filePos, termPos));
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String retrieveIndex(String path, int position) {
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
        if (position < noOfLetter) {
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
            for (int i = 0; i <= position + noOfLetter; i++) {
                if (i >= position - noOfLetter) {
                    if (input.hasNext()) {
                        stringBuilder.append(input.next() + " ");
                    }
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
                String innerDoc = retrieveIndex(fileList.get(postingResult.get(i).getFilePos()).getPath(), postingResult.get(i).getTermPos());
                String fileName = fileList.get(postingResult.get(i).getFilePos()).getName();
                result.append("<p style='color: blue; font-size= 130%'>\"<b>" 
                        + "<a href='file:///"+fileList.get(postingResult.get(i).getFilePos()).getAbsolutePath()+"'>" + fileName + "</a>"
                        + "\"</b>, <em>Position:</em> " + postingResult.get(i).getTermPos());
                result.append("<div>" + innerDoc.replaceAll("(?i)(" + query + ")", "<b>$1</b>") + "</div></p>");
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
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InvertedIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
