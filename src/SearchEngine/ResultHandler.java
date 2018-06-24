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

    public String appendSearchPages(List<Posting> results) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i< results.size(); i++)
        {
            if(i%10==0)
            {   int pageNumber=i/10;
                builder.append("<a href='file:///C:/" +pageNumber+ "'>" + pageNumber + "</a>"+"&nbsp");
            }
        }
        return builder.toString();
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
