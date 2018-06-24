/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEngine;

import static Constant.Constants.*;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author 1stks
 */
public class PageHandler implements ActionListener {

    private int pageNumber;
    private List<Posting> result;
    private List<File> fileList;
    private StringBuilder builder;

    public PageHandler(int pageNumber, List<Posting> result, List<File> fileList, StringBuilder builder) {
        this.pageNumber = pageNumber;
        this.result = result;
        this.builder = builder;
        this.fileList = fileList;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (result != null) {
            builder.append("<b style='font-size: 130%'>*** " + result.size() + " results matched ***</b>");
            for (int i = 0; i < result.size(); i++) {
                String innerDoc = retrieveIndex(fileList.get(result.get(i).getFilePos()).getPath(), result.get(i).getTermPos());
                String fileName = fileList.get(result.get(i).getFilePos()).getName();
                builder.append("<p style='color: blue; font-size= 130%'>\"<b>"
                        + "<a href='file:///" + fileList.get(result.get(i).getFilePos()).getAbsolutePath() + "'>" + fileName + "</a>"
                        + "\"</b>, <em>Position:</em> " + result.get(i).getTermPos());
                builder.append("<div>" + innerDoc + "</div></p>");
            }
            builder.append("<br/>");
        } else {
            builder.append("No matches found");
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

}
