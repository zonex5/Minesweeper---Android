package biz.towaypro.games.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GameLayout extends LinearLayout
{
    public GameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        Game.vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        Game.prefs = context.getSharedPreferences("MineSweeperScore", Context.MODE_PRIVATE);
        Game.context = context;
        Game.layout = this;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        Game.SCREEN_HEIGHT = h;
        Game.SCREEN_WIDTH = w;
        Game.deltaY = oldh == 0 ? 0 : oldh - h;     // оставляем место под рекламу
        Game.initAppearance();
    }

    public void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        Game.draw(canvas);
    }
}
