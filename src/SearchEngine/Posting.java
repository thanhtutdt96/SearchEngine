package SearchEngine;


public class Posting {

    private int filePos;
    private int termPos;

    public Posting(int filePos, int termPos) {
        this.filePos = filePos;
        this.termPos = termPos;
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

    @Override
    public boolean equals(Object o) {
        boolean isEquals = false;

        if (o instanceof Posting){
            Posting pt = (Posting) o;
            if (pt.termPos == this.termPos && pt.filePos == this.filePos){
               isEquals = true;
            };
        }

     return isEquals;
    }
    
    
}


