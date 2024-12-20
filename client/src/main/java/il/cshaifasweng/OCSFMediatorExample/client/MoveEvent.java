package il.cshaifasweng.OCSFMediatorExample.client;

public class MoveEvent {
    // this class represent the information we want to send for us to update the board correctly
    private int row ;
    private int col ;
    private char mark;

    public MoveEvent(int row, int col, char mark){
        this.row = row;
        this.col = col;
        this.mark = mark;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public char getMark() {
        return mark;
    }

}

