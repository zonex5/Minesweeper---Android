package biz.towaypro.games.minesweeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Game
{
    static public boolean vibro = true;
    static public boolean online = true;

    static private final int MAX_TIME = 3600;

    static public MainActivity mainActivity;

    static public Vibrator vibrator;
    static public SharedPreferences prefs;
    static public Context context;
    static public LinearLayout layout;

    private static SmileState smile = SmileState.SMILE;
    static public int SCREEN_HEIGHT, SCREEN_WIDTH;
    static public float DPI;
    static public int cellSize = 24;
    private static int ROWS = 9;
    private static int COLS = 9;
    private static int MINES = 15;
    static private int startX, startY;
    static private int offsetX, offsetY;
    static private int width, height;
    static int deltaY;
    private static int markedMines;
    static int seconds;
    private static int explodedCellRow;
    private static int explodedCellCol;
    static private Drawable[] mines, time;
    static private Timer timer;
    static private Drawable button;
    static private Rect buttonBound;
    static GameState state;
    static int level;

    static Drawable like;

    private static Field field;

    static public void newGame(int rows, int cols, int mine, int lev)
    {
        level = lev;
        ROWS = rows;
        COLS = cols;
        MINES = mine;
        state = GameState.IDLE;
        seconds = 0;
        markedMines = 0;
        smile = SmileState.SMILE;
        mines = new Drawable[3];
        time = new Drawable[3];
        // инициализируем игровое поле
        field = Field.getField(rows, cols, mine);
    }

    private static void newGame()
    {
        newGame(ROWS, COLS, MINES, level);
    }

    static public boolean isFirstRun()
    {
        return field == null;
    }

    static public void initAppearance()
    {
        Assets.drw_back.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        Assets.drw_caption.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // расчитываем смещене
        offsetX = (COLS * cellSize > SCREEN_WIDTH) ? 10 : (SCREEN_WIDTH - COLS * cellSize) / 2;
        offsetY = (ROWS * cellSize > SCREEN_HEIGHT - 65) ? 65 : (SCREEN_HEIGHT - ROWS * cellSize - 65) / 2 + 65;
        startX = offsetX;
        startY = offsetY;
        width = COLS * cellSize;
        height = ROWS * cellSize;

        // установим смайл
        buttonBound = new Rect(SCREEN_WIDTH / 2 - toDPI(15), toDPI(14), SCREEN_WIDTH / 2 + toDPI(15), toDPI(44));
        setSmile(smile);
        //TODO на правильно иконку ставит
        int lng;
        if(SCREEN_WIDTH >= 480)
        {
            lng = toDPI(80);
            like = Assets.recommend;
        }
        else
        {
            lng = 28;
            like = Assets.like;
        }
        like.setBounds(buttonBound.left - lng - toDPI(10), buttonBound.top, buttonBound.left - toDPI(10), buttonBound.bottom);
    }

    private static void setSmile(SmileState smileState)
    {
        smile = smileState;
        switch(smile)
        {
            case LOSS:
                button = Assets.sm_loss;
                break;
            case SMILE:
                button = Assets.sm_smile;
                break;
            case VICTORY:
                button = Assets.sm_victory;
                break;
        }
        button.setBounds(buttonBound);
    }

    static private int toDPI(int n)
    {
        return (int) (n * DPI);
    }

    static void startTimer()
    {
        if(timer == null)
        {
            timer = new Timer();
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    if(seconds < 999)
                        seconds++;
                    layout.postInvalidate();
                }
            }, 0, 1000);
        }
    }

    static void stopTimer()
    {
        if(timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    static public void onScroll(int y, int deltaX, int deltaY)
    {
        if(y > 55)
        {
            if(startX + deltaX < offsetX + 50 && startX + width + deltaX > SCREEN_WIDTH - offsetX - 50)
                startX += deltaX;
            if(startY + deltaY < offsetY + toDPI(50) && startY + height + deltaY > SCREEN_HEIGHT - toDPI(10))
                startY += deltaY;
        }
        layout.postInvalidate();
    }

    static public void onSimplePress(int x, int y)
    {
        y -= deltaY;
        // facebook
        if(like.getBounds().contains(x, y))
        {
            vibrate(150);
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setMessage(R.string.recommande).setTitle("Share to Facebook").setIcon(R.drawable.facebook);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    mainActivity.shareToFacebook(false);
                }
            });
            builder.setNegativeButton("NO", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        // нажатие кнопки
        if(button.getBounds().contains(x, y))
        {
            vibrate(150);
            newGame();
            setSmile(SmileState.SMILE);
        }
        Cell cell = getCell(x, y);
        // первй ход в игре
        if(state == GameState.IDLE && cell != null)
        {
            // эмитируем занятость клетки
            cell.marked = Cell.Mark.UNKNOWN;
            field.setMines();
            cell.marked = Cell.Mark.NONE;
            openCell(cell);
            startTimer();
            state = GameState.RUNNING;
        }
        if(state == GameState.RUNNING && cell != null)
        {
            if(!cell.isOpen)
            {
                // наживаем на ячейку
                openCell(cell);
            }
            else
            {
                if(cell.minesNear > 0)
                {
                    ArrayList<Cell> cells = field.getNeighboringCells(cell.row, cell.col);
                    int marked = 0;
                    for(Cell c : cells)
                        if(c.marked == Cell.Mark.MINE)
                            marked++;
                    if(marked == cell.minesNear)
                    {
                        for(Cell c : cells)
                            if(!c.isOpen) openCell(c);
                    }
                }
            }
            vibrate(30);
            // проверка на победу игрока
            checkPlayerWins();
        }
        layout.postInvalidate();
    }

    static public void onLongPress(int x, int y)
    {
        y -= deltaY;
        Cell cell = getCell(x, y);
        if(state == GameState.RUNNING && cell != null && !cell.isOpen)
        {
            switch(cell.marked)
            {
                case NONE:
                    cell.marked = Cell.Mark.MINE;
                    markedMines++;
                    break;
                case MINE:
                    cell.marked = Cell.Mark.UNKNOWN;
                    markedMines--;
                    break;
                case UNKNOWN:
                    cell.marked = Cell.Mark.NONE;
                    break;
            }
            vibrate(80);
            // проверка на победу игрока
            //checkPlayerWins();
        }
        layout.postInvalidate();
    }

    static public void draw(Canvas canvas)
    {
        // отрисовка
        Assets.drw_back.draw(canvas);
        for(int row = 0; row < ROWS; row++)
            for(int col = 0; col < COLS; col++)
            {
                getCellDrawable(field.field[row][col], row, col).draw(canvas);
            }
        Assets.drw_caption.draw(canvas);
        //if(Math.abs(MINES - markedMines) < 100)
        {
            mines = getDrawableNumber(MINES - markedMines);
            mines[0].setBounds(toDPI(13), toDPI(13), toDPI(29), toDPI(44));
            mines[0].draw(canvas);
            mines[1].setBounds(toDPI(29), toDPI(13), toDPI(45), toDPI(44));
            mines[1].draw(canvas);
            mines[2].setBounds(toDPI(45), toDPI(13), toDPI(61), toDPI(44));
            mines[2].draw(canvas);
        }
        time = getDrawableNumber(seconds);
        time[0].setBounds(SCREEN_WIDTH - toDPI(63), toDPI(13), SCREEN_WIDTH - toDPI(47), toDPI(44));
        time[0].draw(canvas);
        time[1].setBounds(SCREEN_WIDTH - toDPI(47), toDPI(13), SCREEN_WIDTH - toDPI(31), toDPI(44));
        time[1].draw(canvas);
        time[2].setBounds(SCREEN_WIDTH - toDPI(31), toDPI(13), SCREEN_WIDTH - toDPI(15), toDPI(44));
        time[2].draw(canvas);
        button.draw(canvas);
        like.draw(canvas);
    }

    static private Drawable[] getDrawableNumber(int n)
    {
        int tmp = n;
        n = Math.abs(n);
        Drawable[] result = new Drawable[3];
        result[2] = getDrawableDigit(n % 10);
        result[1] = getDrawableDigit((n / 10) % 10);
        result[0] = getDrawableDigit((n / 100) % 10);
        if(tmp < 0)
            result[0] = Assets.digit_minus;
        return result;
    }

    static private Drawable getDrawableDigit(int digit)
    {
        Drawable result = null;
        switch(digit)
        {
            case 0:
                result = Assets.digit_0;
                break;
            case 1:
                result = Assets.digit_1;
                break;
            case 2:
                result = Assets.digit_2;
                break;
            case 3:
                result = Assets.digit_3;
                break;
            case 4:
                result = Assets.digit_4;
                break;
            case 5:
                result = Assets.digit_5;
                break;
            case 6:
                result = Assets.digit_6;
                break;
            case 7:
                result = Assets.digit_7;
                break;
            case 8:
                result = Assets.digit_8;
                break;
            case 9:
                result = Assets.digit_9;
                break;
        }
        return result;
    }

    static private Drawable getCellDrawable(Cell cell, int row, int col)
    {
        Drawable result = null;
        if(!cell.isOpen)
        {
            switch(cell.marked)
            {
                case NONE:
                    result = Assets.drw_cell_closed;
                    break;
                case MINE:
                    result = Assets.drw_cell_flag;
                    break;
                case UNKNOWN:
                    result = Assets.drw_cell_unknown;
                    break;
            }
        }
        else
        {
            if(cell.isMine)
            {
                result = Assets.drw_cell_mine;
                if(cell.row == explodedCellRow && cell.col == explodedCellCol)
                    result = Assets.drw_cell_mine_explode;
            }
            else
            {
                if(cell.minesNear == 0)
                    result = Assets.drw_cell_opened;
                else
                {
                    switch(cell.minesNear)
                    {
                        case 1:
                            result = Assets.drw_digit_1;
                            break;
                        case 2:
                            result = Assets.drw_digit_2;
                            break;
                        case 3:
                            result = Assets.drw_digit_3;
                            break;
                        case 4:
                            result = Assets.drw_digit_4;
                            break;
                        case 5:
                            result = Assets.drw_digit_5;
                            break;
                        case 6:
                            result = Assets.drw_digit_6;
                            break;
                        case 7:
                            result = Assets.drw_digit_7;
                            break;
                        case 8:
                            result = Assets.drw_digit_8;
                            break;
                    }
                }
                if(cell.marked == Cell.Mark.MINE)
                    result = Assets.drw_cell_no_mine;
            }
        }
        if(result != null)
        {
            int x = (col * cellSize) + startX;
            int y = (row * cellSize) + startY;
            result.setBounds(x, y, x + cellSize, y + cellSize);
        }
        return result;
    }

    static private void openCell(Cell cell)
    {
        if(cell.marked != Cell.Mark.MINE)
        {
            if(cell.isMine)
            {
                //--------  GAMEOVER -----------//
                Toast.makeText(context, context.getString(R.string.toast_gameover), Toast.LENGTH_SHORT).show();
                field.openAllMines();
                explodedCellRow = cell.row;
                explodedCellCol = cell.col;
                stopTimer();
                setSmile(SmileState.LOSS);
                state = GameState.GAMEOVER;
            }
            else
                if(cell.minesNear > 0)
                    cell.isOpen = true;
                else
                    field.openField(cell.row, cell.col);
        }
    }

    static private void checkPlayerWins()
    {
        if(field.fieldIsOpen() && state != GameState.GAMEOVER)
        {
            Toast.makeText(context, context.getString(R.string.toast_victory), Toast.LENGTH_SHORT).show();
            vibrate(200);
            stopTimer();
            setSmile(SmileState.VICTORY);
            state = GameState.GAMEOVER;
            if(level < 3)
            {
                if(online)
                {
                    if(Server.isOnline(context))
                    {
                        Toast.makeText(context, context.getString(R.string.getdata), Toast.LENGTH_SHORT).show();
                        // получим ID сессии
                        GetUniqueIDTask task = new GetUniqueIDTask();
                        task.execute();
                    }
                    else
                    {
                        Toast.makeText(context, context.getText(R.string.local), Toast.LENGTH_SHORT).show();
                        saveLocalLeaderboard();
                    }
                }
                else
                {
                    saveLocalLeaderboard();
                }
            }
            // facebook
            if(Server.isOnline(context))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.victory1) + " " + seconds + " sec.\n\n" + context.getString(R.string.victory2));
                builder.setIcon(R.drawable.facebook);
                builder.setTitle(context.getString(R.string.congretulation));
                builder.setNegativeButton(context.getString(R.string.btClose), null);
                builder.setPositiveButton(context.getString(R.string.btShare), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        mainActivity.shareToFacebook(true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    static public String getLevelName()
    {
        String levName = "";
        switch(level)
        {
            case 0:
                levName = context.getString(R.string.level0);
                break;
            case 1:
                levName = context.getString(R.string.level1);
                break;
            case 2:
                levName = context.getString(R.string.level2);
                break;
        }
        return levName;
    }

    static private void saveLocalLeaderboard()
    {
        // проверим достижение
        int levelTime = Integer.parseInt(prefs.getString("time" + String.valueOf(level), "999"));
        if(seconds < levelTime)
        {
            // проверим рекорды
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View lt = inflater.inflate(R.layout.record, (ViewGroup) layout.findViewById(R.id.record));
            ((TextView) lt.findViewById(R.id.rec_seconds)).setText(seconds + "s");
            builder.setView(lt);
            final AlertDialog alert = builder.create();
            alert.show();

            final EditText editText = (EditText) lt.findViewById(R.id.rec_input);
            lt.findViewById(R.id.rec_button).setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View view)
                {
                    // запишем новый рекорд
                    final String name = editText.getText().toString().trim().length() > 0 ? editText.getText().toString().trim() : "Anonim";
                    prefs.edit().putString("name" + String.valueOf(level), name).commit();
                    prefs.edit().putString("time" + String.valueOf(level), String.valueOf(seconds)).commit();
                    alert.cancel();
                }
            });
        }
    }

    static private Cell getCell(int x, int y)
    {
        Cell result = null;
        int cellCol = x < startX ? -1 : ((x - startX) / cellSize);
        int cellRow = y < startY ? -1 : ((y - startY) / cellSize);
        if(cellRow < ROWS && cellRow >= 0 && cellCol < COLS && cellCol >= 0)
            result = field.field[cellRow][cellCol];
        return result;
    }

    static public void changeZoom(int zoom, boolean hardkey)
    {
        if(hardkey)
        {
            if(zoom > 0)
            {
                cellSize += 6;
                if(cellSize > 144)
                    cellSize = 144;
            }
            if(zoom < 0)
            {
                cellSize -= 6;
                if(cellSize < 18)
                    cellSize = 18;
            }
        }
        else
        {
            cellSize = zoom;
        }
        Game.prefs.edit().putInt("zoom", cellSize).commit();
        initAppearance();
        layout.postInvalidate();
    }

    static private void vibrate(int mlsec)
    {
        if(vibro)
            vibrator.vibrate(mlsec);
    }

    static private String corectString(String s)
    {
        String result = s.replaceAll("([^А-Яа-яA-Za-z0-9-\\!\\?\\,\\.\\s]+)", "");
        //result = result.replace(" ", "%20").replace(",", "%2C").replace(".", "%2E");
        try
        {
            result = URLEncoder.encode(result, "UTF-8");
        }
        catch(UnsupportedEncodingException e) { /**/ }
        return result;
    }

    static public enum GameState
    {
        IDLE, RUNNING, GAMEOVER
    }

    static public enum SmileState
    {
        SMILE, VICTORY, LOSS
    }

    static private class GetUniqueIDTask extends AsyncTask<Void, Void, Void>
    {
        String responce;

        protected Void doInBackground(Void... voids)
        {
            // получаем идентификатор для сессии
            responce = Server.getUniqueID();

            return null;
        }

        protected void onPostExecute(Void voids)
        {
            responce = responce.replaceAll("\\n", "");
            // надо показать диалог
            if(responce.length() > 0)
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View lt = inflater.inflate(R.layout.recordonline, (ViewGroup) layout.findViewById(R.id.record));
                ((TextView) lt.findViewById(R.id.rec_seconds)).setText(seconds + "s");
                builder.setView(lt);
                final AlertDialog alert = builder.create();
                alert.show();

                final EditText user = (EditText) lt.findViewById(R.id.rec_input);
                final EditText comment = (EditText) lt.findViewById(R.id.rec_comment);
                lt.findViewById(R.id.rec_button).setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Toast.makeText(context, context.getString(R.string.saving), Toast.LENGTH_SHORT).show();
                        // запишем
                        final String name = user.getText().toString().trim().length() > 0 ? user.getText().toString().trim() : "Anonim";
                        SetScoreTask task = new SetScoreTask();
                        task.execute(name, String.valueOf(seconds), comment.getText().toString().trim(), String.valueOf(level), responce);
                        alert.cancel();
                    }
                });
            }
            else
            {
                Toast.makeText(context, context.getString(R.string.data_error), Toast.LENGTH_SHORT).show();
            }
        }

        static class SetScoreTask extends AsyncTask<String, Void, Void>
        {
            private boolean error = false;

            protected Void doInBackground(String... params)
            {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user", corectString(params[0])));
                nameValuePairs.add(new BasicNameValuePair("score", params[1]));
                nameValuePairs.add(new BasicNameValuePair("comment", corectString(params[2])));
                nameValuePairs.add(new BasicNameValuePair("level", params[3]));
                nameValuePairs.add(new BasicNameValuePair("id", params[4]));
                error = !Server.executeQueryPOST("http://toway.biz/minesweeper/pro/set.php", nameValuePairs);
                return null;
            }

            protected void onPostExecute(Void voids)
            {
                if(error)
                {
                    Toast.makeText(context, context.getString(R.string.score_error), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // показывааем таблицу рекордов
                    Intent intent = new Intent(context, TabScoreActivity.class);
                    intent.putExtra("level", level);
                    context.startActivity(intent);
                }
            }
        }
    }
}
