/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchEngine;

import java.util.List;

/**
 *
 * @author 1stks
 */
public class ResultHandler {

    public static ResultHandler handler = null;

    public static ResultHandler getInstance() {
        if (handler == null) {
            handler = new ResultHandler();
        }
        return handler;
    }

    public int countResult(List<Posting> results) {
        return results.size();
    }

    public boolean isMoreTenResults(int resultSize) {
        if (resultSize > 10) {
            return true;
        }
        return false;
    }

    public String appendSearchPages(int size) {
        StringBuilder builder = new StringBuilder();
        int start = 0;
        int end = 10;
        if (end > size / 10) {
            end = size / 10;
        }
        builder.append("<div style='text-align: center;'>");
        if (MainFrame.currentPage > 5) {
            start = MainFrame.currentPage - 6;
            end = MainFrame.currentPage + 5;
            if (end > size / 10) {
                end = size / 10;
            }
        }
        for (int i = start; i < end; i++) {
            if (i == MainFrame.currentPage) {
                builder.append("<b>" + (i + 1) + "</b>&nbsp&nbsp");
            } else {
                builder.append("<a href='file:///C:/" + (i + 1) + "'>" + (i + 1) + "</a>" + "&nbsp&nbsp");
            }
            if (i == (size / 10 - 1) && (size % 10 != 0)) {
                builder.append("<a href='file:///C:/" + (size / 10 + 1) + "'>" + (size / 10 + 1) + "</a>" + "&nbsp&nbsp");

            }
        }
        builder.append("</div>");
        return builder.toString();
    }

    public int getPostingsSize(List<Postings> results) {
        int size = 0;
        for (int i = 0; i < results.size(); i++) {
            size += results.get(i).getSize();
        }
        return size;
    }

    public boolean isLastPage(int firstRow, int lastRow) {
        if ((firstRow - 1) % 10 == 0 && lastRow % 10 == 0) {
            return false;
        }
        return true;
    }

    public List<Posting> resultHandler(int firstRow, int lastRow, List<Posting> results) {
        return results.subList(firstRow, lastRow);
    }

}
