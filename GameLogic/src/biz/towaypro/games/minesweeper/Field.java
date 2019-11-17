package biz.towaypro.games.minesweeper;

import java.util.ArrayList;
import java.util.Random;

public class Field
{
    static private Field ourInstance;
    static private int fieldCols, fieldRows, minesCount;
    static private final Random rnd = new Random();
    //---------------------------------------------------//
    public Cell[][] field;

    static Field getField(int rows, int cols, int mines)
    {
        if((rows == fieldRows && cols == fieldCols && minesCount == mines) && ourInstance != null)
        {
            // инициализация игрового поля
            for(int r = 0; r < fieldRows; r++)
                for(int c = 0; c < fieldCols; c++)
                    ourInstance.field[r][c].init();
        }
        else
        {
            fieldRows = rows;
            fieldCols = cols;
            minesCount = mines;
            ourInstance = new Field();
            ourInstance.field = new Cell[rows][cols];
            for(int r = 0; r < rows; r++)
                for(int c = 0; c < cols; c++)
                    ourInstance.field[r][c] = new Cell(r, c);
        }
        return ourInstance;
    }

    void setMines()
    {
        // устанавливаем мины
        int installedMines = 0;
        while(installedMines < minesCount)
        {
            int row = rnd.nextInt(fieldRows);
            int col = rnd.nextInt(fieldCols);
            if(!field[row][col].isMine && field[row][col].marked == Cell.Mark.NONE)
            {
                field[row][col].isMine = true;
                installedMines++;
            }
        }
        // устанавливаем цифры
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
                if(!field[r][c].isMine)
                    field[r][c].minesNear = getMinesCountNearCell(r, c);
    }

    int getMinesCountNearCell(int cellRow, int cellCol)
    {
        int result = 0;
        if(cellRow - 1 >= 0 && field[cellRow - 1][cellCol].isMine)
            result++;
        if(cellRow - 1 >= 0 && cellCol + 1 < fieldCols && field[cellRow - 1][cellCol + 1].isMine)
            result++;
        if(cellCol + 1 < fieldCols && field[cellRow][cellCol + 1].isMine)
            result++;
        if(cellRow + 1 < fieldRows && cellCol + 1 < fieldCols && field[cellRow + 1][cellCol + 1].isMine)
            result++;
        if(cellRow + 1 < fieldRows && field[cellRow + 1][cellCol].isMine)
            result++;
        if(cellRow + 1 < fieldRows && cellCol - 1 >= 0 && field[cellRow + 1][cellCol - 1].isMine)
            result++;
        if(cellCol - 1 >= 0 && field[cellRow][cellCol - 1].isMine)
            result++;
        if(cellRow - 1 >= 0 && cellCol - 1 >= 0 && field[cellRow - 1][cellCol - 1].isMine)
            result++;
        return result;
    }

    void openField(int startRow, int startCol)
    {
        /*
        if(field[startRow][startCol].minesNear > 0)
        {
            if(field[startRow][startCol].marked != Cell.Mark.MINE)
                field[startRow][startCol].isOpen = true;
        }
        else
        {
            if(field[startRow][startCol].marked != Cell.Mark.MINE)
                field[startRow][startCol].isOpen = true;
            // рекурсивно проходим соседние клетки
            if(startRow - 1 >= 0 && !field[startRow - 1][startCol].isOpen)
                openField(startRow - 1, startCol);
            if(startRow - 1 >= 0 && startCol + 1 < fieldCols && !field[startRow - 1][startCol + 1].isOpen)
                openField(startRow - 1, startCol + 1);
            if(startCol + 1 < fieldCols && !field[startRow][startCol + 1].isOpen)
                openField(startRow, startCol + 1);
            if(startRow + 1 < fieldRows && startCol + 1 < fieldCols && !field[startRow + 1][startCol + 1].isOpen)
                openField(startRow + 1, startCol + 1);
            if(startRow + 1 < fieldRows && !field[startRow + 1][startCol].isOpen)
                openField(startRow + 1, startCol);
            if(startRow + 1 < fieldRows && startCol - 1 >= 0 && !field[startRow + 1][startCol - 1].isOpen)
                openField(startRow + 1, startCol - 1);
            if(startCol - 1 >= 0 && !field[startRow][startCol - 1].isOpen)
                openField(startRow, startCol - 1);
            if(startRow - 1 >= 0 && startCol - 1 >= 0 && !field[startRow - 1][startCol - 1].isOpen)
                openField(startRow - 1, startCol - 1);
        }    */
        ArrayList<Cell> list = new ArrayList<Cell>();
        list.add(field[startRow][startCol]);
        while(list.size() > 0)
        {
            Cell cell = list.get(0);
            cell.isOpen = true;
            if(cell.minesNear == 0)
            {
                if(cell.row - 1 >= 0 && !field[cell.row - 1][cell.col].isOpen && !list.contains(field[cell.row - 1][cell.col]))
                    list.add(field[cell.row - 1][cell.col]);
                if(cell.row - 1 >= 0 && cell.col + 1 < fieldCols && !field[cell.row - 1][cell.col + 1].isOpen && !list.contains(field[cell.row - 1][cell.col + 1]))
                    list.add(field[cell.row - 1][cell.col + 1]);
                if(cell.col + 1 < fieldCols && !field[cell.row][cell.col + 1].isOpen && !list.contains(field[cell.row][cell.col + 1]))
                    list.add(field[cell.row][cell.col + 1]);
                if(cell.row + 1 < fieldRows && cell.col + 1 < fieldCols && !field[cell.row + 1][cell.col + 1].isOpen && !list.contains(field[cell.row + 1][cell.col + 1]))
                    list.add(field[cell.row + 1][cell.col + 1]);
                if(cell.row + 1 < fieldRows && !field[cell.row + 1][cell.col].isOpen && !list.contains(field[cell.row + 1][cell.col]))
                    list.add(field[cell.row + 1][cell.col]);
                if(cell.row + 1 < fieldRows && cell.col - 1 >= 0 && !field[cell.row + 1][cell.col - 1].isOpen && !list.contains(field[cell.row + 1][cell.col - 1]))
                    list.add(field[cell.row + 1][cell.col - 1]);
                if(cell.col - 1 >= 0 && !field[cell.row][cell.col - 1].isOpen && !list.contains(field[cell.row][cell.col - 1]))
                    list.add(field[cell.row][cell.col - 1]);
                if(cell.row - 1 >= 0 && cell.col - 1 >= 0 && !field[cell.row - 1][cell.col - 1].isOpen && !list.contains(field[cell.row - 1][cell.col - 1]))
                    list.add(field[cell.row - 1][cell.col - 1]);
            }
            list.remove(0);
        }
    }

    void openAllMines()
    {
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
            {
                if(field[r][c].isMine || field[r][c].marked == Cell.Mark.MINE)
                    field[r][c].isOpen = true;
            }
    }

    boolean fieldIsOpen()
    {
        //boolean result = true;
        /*
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
                if(!field[r][c].isOpen && field[r][c].marked != Cell.Mark.MINE)
                    result = false;
        // проверим правильность отмеченных
        int count = 0;
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
                if(field[r][c].marked == Cell.Mark.MINE && field[r][c].isMine)
                    count++;
        int countch = 0;
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
                if(field[r][c].marked == Cell.Mark.MINE && !field[r][c].isOpen)
                    countch++;
        return result && (count == minesCount) && (count == countch);    */
        int count = 0;
        for(int r = 0; r < fieldRows; r++)
            for(int c = 0; c < fieldCols; c++)
                if(field[r][c].isOpen)
                    count++;
        return count == fieldRows * fieldCols - minesCount;
    }

    ArrayList<Cell> getNeighboringCells(int cellRow, int cellCol)
    {
        ArrayList<Cell> cells = new ArrayList<Cell>();

        if(cellRow - 1 >= 0)
            cells.add(field[cellRow - 1][cellCol]);
        if(cellRow - 1 >= 0 && cellCol + 1 < fieldCols)
            cells.add(field[cellRow - 1][cellCol + 1]);
        if(cellCol + 1 < fieldCols)
            cells.add(field[cellRow][cellCol + 1]);
        if(cellRow + 1 < fieldRows && cellCol + 1 < fieldCols)
            cells.add(field[cellRow + 1][cellCol + 1]);
        if(cellRow + 1 < fieldRows)
            cells.add(field[cellRow + 1][cellCol]);
        if(cellRow + 1 < fieldRows && cellCol - 1 >= 0)
            cells.add(field[cellRow + 1][cellCol - 1]);
        if(cellCol - 1 >= 0)
            cells.add(field[cellRow][cellCol - 1]);
        if(cellRow - 1 >= 0 && cellCol - 1 >= 0)
            cells.add(field[cellRow - 1][cellCol - 1]);
        return cells;
    }
}
