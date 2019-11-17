package biz.towaypro.games.minesweeper;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

abstract class Assets
{
    //********* Drawables **********//
    static Drawable drw_cell_closed;
    static Drawable drw_cell_opened;
    static Drawable drw_cell_flag;
    static Drawable drw_cell_mine;
    static Drawable drw_cell_mine_explode;
    static Drawable drw_cell_no_mine;
    static Drawable drw_cell_unknown;
    static Drawable drw_digit_1;
    static Drawable drw_digit_2;
    static Drawable drw_digit_3;
    static Drawable drw_digit_4;
    static Drawable drw_digit_5;
    static Drawable drw_digit_6;
    static Drawable drw_digit_7;
    static Drawable drw_digit_8;
    static Drawable drw_back;
    static Drawable drw_caption;

    static Drawable digit_0;
    static Drawable digit_1;
    static Drawable digit_2;
    static Drawable digit_3;
    static Drawable digit_4;
    static Drawable digit_5;
    static Drawable digit_6;
    static Drawable digit_7;
    static Drawable digit_8;
    static Drawable digit_9;
    static Drawable digit_minus;

    static Drawable sm_smile;
    static Drawable sm_victory;
    static Drawable sm_loss;

    static Drawable facebook;
    static Drawable recommend;
    static Drawable like;
    //******************************//

    static void loadAssets(Resources resources)
    {
        Assets.drw_cell_closed = resources.getDrawable(R.drawable.cell_closed);
        Assets.drw_cell_opened = resources.getDrawable(R.drawable.cell_opened);
        Assets.drw_cell_flag = resources.getDrawable(R.drawable.cell_flag);
        Assets.drw_cell_mine = resources.getDrawable(R.drawable.cell_mine);
        Assets.drw_cell_mine_explode = resources.getDrawable(R.drawable.cell_mine_explode);
        Assets.drw_cell_no_mine = resources.getDrawable(R.drawable.cell_no_mine);
        Assets.drw_cell_unknown = resources.getDrawable(R.drawable.cell_unknown);
        Assets.drw_digit_1 = resources.getDrawable(R.drawable.number_1);
        Assets.drw_digit_2 = resources.getDrawable(R.drawable.number_2);
        Assets.drw_digit_3 = resources.getDrawable(R.drawable.number_3);
        Assets.drw_digit_4 = resources.getDrawable(R.drawable.number_4);
        Assets.drw_digit_5 = resources.getDrawable(R.drawable.number_5);
        Assets.drw_digit_6 = resources.getDrawable(R.drawable.number_6);
        Assets.drw_digit_7 = resources.getDrawable(R.drawable.number_7);
        Assets.drw_digit_8 = resources.getDrawable(R.drawable.number_8);
        Assets.drw_back = resources.getDrawable(R.drawable.back);
        Assets.drw_caption = resources.getDrawable(R.drawable.caption);

        Assets.digit_0 = resources.getDrawable(R.drawable.digit_0);
        Assets.digit_1 = resources.getDrawable(R.drawable.digit_1);
        Assets.digit_2 = resources.getDrawable(R.drawable.digit_2);
        Assets.digit_3 = resources.getDrawable(R.drawable.digit_3);
        Assets.digit_4 = resources.getDrawable(R.drawable.digit_4);
        Assets.digit_5 = resources.getDrawable(R.drawable.digit_5);
        Assets.digit_6 = resources.getDrawable(R.drawable.digit_6);
        Assets.digit_7 = resources.getDrawable(R.drawable.digit_7);
        Assets.digit_8 = resources.getDrawable(R.drawable.digit_8);
        Assets.digit_9 = resources.getDrawable(R.drawable.digit_9);
        Assets.digit_minus = resources.getDrawable(R.drawable.minus);

        Assets.sm_loss = resources.getDrawable(R.drawable.sm_losse);
        Assets.sm_smile = resources.getDrawable(R.drawable.sm_smile);
        Assets.sm_victory = resources.getDrawable(R.drawable.sm_victory);

        Assets.facebook = resources.getDrawable(R.drawable.facebook);
        Assets.recommend = resources.getDrawable(R.drawable.share2);
        Assets.like = resources.getDrawable(R.drawable.like);
    }
}
