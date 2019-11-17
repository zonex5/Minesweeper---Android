package biz.towaypro.games.minesweeper;

import java.io.Serializable;

public class Cell implements Serializable
{
    public boolean isMine;
    public Mark marked;
    public boolean isOpen;
    public int minesNear;
    public final int row;
    public final int col;

    public Cell(int r, int c)
    {
        row = r;
        col = c;
        init();
    }

    public void init()
    {
        isMine = false;
        marked = Mark.NONE;
        isOpen = false;
        minesNear = 0;
    }

    public enum Mark implements Serializable
    {
        NONE, MINE, UNKNOWN
    }
}
