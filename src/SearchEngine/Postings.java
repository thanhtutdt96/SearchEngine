package SearchEngine;


import java.util.List;

public class Postings {

    private int filePos;
    private List<Integer> termPos;

    public Postings(int filePos, List<Integer> termPos) {
        this.filePos = filePos;
        this.termPos = termPos;
    }

    public int getFilePos() {
        return filePos;
    }

    public void setFilePos(int filePos) {
        this.filePos = filePos;
    }

    public List<Integer> getTermPos() {
        return termPos;
    }

    public void setTermPos(List<Integer> termPos) {
        this.termPos = termPos;
    }
    
    public void add(int pos){
        this.termPos.add(pos);
    }

    @Override
    public boolean equals(Object o) {
        boolean isEquals = false;

        if (o instanceof Posting) {
            Postings pt = (Postings) o;
            if (pt.termPos == this.termPos && pt.filePos == this.filePos) {
                isEquals = true;
            };
        }

        return isEquals;
    }
}
