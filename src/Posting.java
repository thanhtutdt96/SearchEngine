/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tu.phamthanh
 */
public class Posting {

    private int filePos;
    private int termPos;
    private int docPos;
    private int frequency;

    public Posting(int filePos, int termPos, int docPos) {
        this.filePos = filePos;
        this.termPos = termPos;
        this.docPos = docPos;
    }

    public int getFilePos() {
        return filePos;
    }

    public void setFilePos(int filePos) {
        this.filePos = filePos;
    }

    public int getTermPos() {
        return termPos;
    }

    public void setTermPos(int termPos) {
        this.termPos = termPos;
    }

    public int getDocPos() {
        return docPos;
    }

    public void setDocPos(int docPos) {
        this.docPos = docPos;
    }


}
