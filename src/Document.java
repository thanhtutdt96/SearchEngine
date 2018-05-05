/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tu.phamthanh
 */
public class Document {
    private int docPos;
    private String docText;

    public Document(int docPos, String docText) {
        this.docPos = docPos;
        this.docText = docText;
    }

    public int getDocPos() {
        return docPos;
    }

    public void setDocPos(int docPos) {
        this.docPos = docPos;
    }

    public String getDocText() {
        return docText;
    }

    public void setDocText(String docText) {
        this.docText = docText;
    }
    
}
