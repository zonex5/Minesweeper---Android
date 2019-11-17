package biz.towaypro.games.minesweeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.socialsharing.common.AuthListener;
import com.nostra13.socialsharing.facebook.FacebookEvents;
import com.nostra13.socialsharing.facebook.FacebookFacade;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener
{
    private GestureDetector gestureScanner;
    private AlertDialog alert;

    private int maxMines;
    private int totalRows, totalCols, totalMines;

    private int width = 0;

    //----------------- social ---------------//
    FacebookFacade facebook;
    boolean shareResult;

    protected AuthListener authListener = new AuthListener()
    {
        public void onAuthSucceed()
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    if(shareResult)
                    {
                        // результаты
                        facebook.publishMessage(getString(R.string.achiev1), "https://play.google.com/store/apps/details?id=biz.toway.games.minesweeper", getString(R.string.appName), String.format(getString(R.string.achiev2), Game.seconds, Game.getLevelName()), "http://toway.biz/Projects/images/minesweeper.png");
                        Toast.makeText(Game.mainActivity, R.string.share2, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // рукомендовать
                        facebook.publishMessage(getString(R.string.share0), "https://play.google.com/store/apps/details?id=biz.toway.games.minesweeper", getString(R.string.share), getString(R.string.share1), "http://toway.biz/Projects/images/minesweeper.png");
                        Toast.makeText(Game.mainActivity, R.string.share2, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void onAuthFail(String error)
        {
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Toast.makeText(Game.mainActivity, "Error was occurred during Facebook authentication", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public void shareToFacebook(boolean shareResults)
    {
        this.shareResult = shareResults;
        facebook = new FacebookFacade(this, "523042544453214");
        if(!facebook.isAuthorized())
        {
            facebook.authorize();
        }
        else
        {
            if(shareResults)
            {
                // результаты
                facebook.publishMessage(getString(R.string.achiev1), "https://play.google.com/store/apps/details?id=biz.towaypro.games.minesweeper", getString(R.string.appName), String.format(getString(R.string.achiev2), Game.seconds, Game.getLevelName()), "http://toway.biz/Projects/images/minesweeper.png");
                Toast.makeText(this, R.string.share2, Toast.LENGTH_SHORT).show();
            }
            else
            {
                // рукомендовать
                facebook.publishMessage(getString(R.string.share0), "https://play.google.com/store/apps/details?id=biz.towaypro.games.minesweeper", getString(R.string.share), getString(R.string.share1), "http://toway.biz/Projects/images/minesweeper.png");
                Toast.makeText(this, R.string.share2, Toast.LENGTH_SHORT).show();
            }
        }
    }
    //---------------------------------------//

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        Game.mainActivity = this;

        gestureScanner = new GestureDetector(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Game.DPI = metrics.density;
        width = metrics.widthPixels;

        Assets.loadAssets(getResources());

        Game.vibro = Game.prefs.getBoolean("vibro", true);
        Game.online = Game.prefs.getBoolean("online", false);
    }

    public void onResume()
    {
        super.onResume();

        if(Game.isFirstRun())
        {
            int lev = Game.prefs.getInt("startlevel", 0);
            if(lev < 3)
                startNewGame(lev);
            else
            {
                Game.newGame(Game.prefs.getInt("totalrows", 9), Game.prefs.getInt("totalcols", 9), Game.prefs.getInt("totalmines", 10), lev);
                Game.initAppearance();
                Game.layout.postInvalidate();
            }
            Game.changeZoom(Game.prefs.getInt("zoom", Math.min((int) ((width * 30f) / 320), 72)), false);
        }
        if(Game.state == Game.GameState.RUNNING)
            Game.startTimer();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        /*if(Game.prefs.getBoolean("rate", false))
        {
            menu.removeItem(R.id.rate);
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    private void startNewGame(final int level)
    {
        switch(level)
        {
            case 0:
                Game.newGame(9, 9, 10, level);
                break;
            case 1:
                Game.newGame(16, 16, 40, level);
                break;
            case 2:
                Game.newGame(16, 30, 99, level);
                break;
            case 3:
                totalRows = 9;
                totalCols = 9;
                totalMines = 10;
                maxMines = 10;

                AlertDialog.Builder bldr = new AlertDialog.Builder(this);
                LayoutInflater infltr = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View lt = infltr.inflate(R.layout.level, (ViewGroup) findViewById(R.id.levelgroup));
                bldr.setView(lt);
                bldr.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Game.prefs.edit().putInt("totalrows", totalRows).commit();
                        Game.prefs.edit().putInt("totalcols", totalCols).commit();
                        Game.prefs.edit().putInt("totalmines", totalMines).commit();

                        Game.newGame(totalRows, totalCols, totalMines, level);
                        Game.initAppearance();
                        Game.layout.postInvalidate();
                    }
                });
                bldr.setNegativeButton("Cancel", null);
                ((SeekBar) lt.findViewById(R.id.lev_rows_count)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                    {
                        totalRows = 9 + i;
                        maxMines = (totalCols * totalRows * 80) / 100;
                        ((TextView) lt.findViewById(R.id.lev_rows_cnt)).setText(String.valueOf(totalRows));
                        ((SeekBar) lt.findViewById(R.id.lev_mines_count)).setMax(maxMines);
                        ((SeekBar) lt.findViewById(R.id.lev_mines_count)).setProgress(0);
                    }

                    public void onStartTrackingTouch(SeekBar seekBar)
                    {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                    }
                });
                ((SeekBar) lt.findViewById(R.id.lev_cols_count)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {

                    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                    {
                        totalCols = 9 + i;
                        maxMines = (totalCols * totalRows * 80) / 100;
                        ((TextView) lt.findViewById(R.id.lev_cols_cnt)).setText(String.valueOf(totalCols));
                        ((SeekBar) lt.findViewById(R.id.lev_mines_count)).setMax(maxMines);
                        ((SeekBar) lt.findViewById(R.id.lev_mines_count)).setProgress(0);
                    }

                    public void onStartTrackingTouch(SeekBar seekBar)
                    {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                    }
                });
                ((SeekBar) lt.findViewById(R.id.lev_mines_count)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {

                    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                    {
                        totalMines = 10 + i;
                        ((TextView) lt.findViewById(R.id.lev_mines_cnt)).setText(String.valueOf(totalMines));
                    }

                    public void onStartTrackingTouch(SeekBar seekBar)
                    {

                    }

                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                    }
                });
                final AlertDialog alrt = bldr.create();
                alrt.show();
                break;
        }
        Game.initAppearance();
        Game.layout.postInvalidate();
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        AlertDialog.Builder builder;
        LayoutInflater inflater;
        View layout;
        switch(item.getItemId())
        {
            case R.id.menzoom:
                builder = new AlertDialog.Builder(this);
                inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.zoom, null);
                builder.setView(view).setPositiveButton("OK", null).setTitle(getString(R.string.men_zoom));

                ((SeekBar) view.findViewById(R.id.zoom)).setProgress(Game.cellSize - 18);
                ((SeekBar) view.findViewById(R.id.zoom)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
                    {
                        Game.changeZoom(18 + i, false);
                    }

                    public void onStartTrackingTouch(SeekBar seekBar)
                    {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                    }
                });

                final AlertDialog alrt = builder.create();
                alrt.show();
                break;

            case R.id.level:
                final CharSequence[] items = {getString(R.string.level0), getString(R.string.level1), getString(R.string.level2), getString(R.string.level3)};
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.level);
                builder.setItems(items, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int item)
                    {
                        Game.prefs.edit().putInt("startlevel", item).commit();
                        startNewGame(item);
                        closeDialog();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                alert = builder.create();
                alert.show();
                break;

            case R.id.settings:
                final boolean[] tmp = new boolean[]{Game.vibro, Game.online};
                final CharSequence[] settings = {getString(R.string.set_vibro), getString(R.string.set_online)};
                final boolean[] chekeds = {Game.vibro, Game.online};
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.set_caption);
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Game.vibro = tmp[0];
                        Game.online = tmp[1];
                        Game.prefs.edit().putBoolean("vibro", Game.vibro).commit();
                        Game.prefs.edit().putBoolean("online", Game.online).commit();
                    }
                });

                builder.setMultiChoiceItems(settings, chekeds, new DialogInterface.OnMultiChoiceClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i, boolean checked)
                    {
                        // vibro = 0, online = 1
                        tmp[i] = checked;
                    }
                });
                alert = builder.create();
                alert.show();
                break;

            case R.id.score:
                if(Game.online)
                {
                    Intent intent = new Intent(getApplicationContext(), TabScoreActivity.class);
                    intent.putExtra("level", 0);
                    startActivity(intent);
                }
                else
                {
                    builder = new AlertDialog.Builder(this);
                    inflater = getLayoutInflater();
                    layout = inflater.inflate(R.layout.score, null);
                    builder.setView(layout);
                    builder.setTitle(getString(R.string.score_caption));
                    builder.setNeutralButton(getString(R.string.reset), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Game.prefs.edit().clear().commit();
                        }
                    });
                    builder.setPositiveButton("OK", null);
                    ((TextView) layout.findViewById(R.id.ch_lev0)).setText(getString(R.string.level0) + ":\t " + Game.prefs.getString("name0", "Anonim") + "\t" + Game.prefs.getString("time0", "999") + "s");
                    ((TextView) layout.findViewById(R.id.ch_lev1)).setText(getString(R.string.level1) + ":\t " + Game.prefs.getString("name1", "Anonim") + "\t" + Game.prefs.getString("time1", "999") + "s");
                    ((TextView) layout.findViewById(R.id.ch_lev2)).setText(getString(R.string.level2) + ":\t " + Game.prefs.getString("name2", "Anonim") + "\t" + Game.prefs.getString("time2", "999") + "s");
                    alert = builder.create();
                    alert.show();
                }
                break;

            case R.id.info:
                builder = new AlertDialog.Builder(this);
                inflater = getLayoutInflater();
                layout = inflater.inflate(R.layout.about, null);
                builder.setView(layout);
                //builder.setTitle(getString(R.string.score_caption));
                builder.setNeutralButton("OK", null);
                alert = builder.create();
                alert.show();
                break;

            case R.id.help:
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.help);
                builder.setNeutralButton("OK", null);
                builder.setIcon(R.drawable.minesweeper);
                builder.setTitle(R.string.hepl_caption);
                alert = builder.create();
                alert.show();
                break;

            /*case R.id.pro:
                goToPro();
                break;*/
        }
        return false;
    }

    private void goToPro()
    {
        try
        {
            Uri address;
            Intent openlink;
            address = Uri.parse("market://details?id=biz.towaypro.games.minesweeper");
            openlink = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openlink);
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Unfortunately on your device do not have installed service Google Play", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_btAdClose(View view)
    {
        alert.cancel();
    }

    public void onClick_Ad(View view)
    {
        alert.cancel();
        goToPro();
    }

    public void onClick_AboutURL(View view)
    {
        Uri address = Uri.parse("http://toway.biz");
        Intent openlink = new Intent(Intent.ACTION_VIEW, address);
        startActivity(openlink);
    }

    public void onClick_About(View view)
    {
        if(alert != null && alert.isShowing())
            alert.cancel();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean result = false;
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            Game.changeZoom(-1, true);
            result = true;
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            Game.changeZoom(1, true);
            result = true;
        }

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.quit).setCancelable(false).setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            result = true;
        }
        return result;
    }

    public void onPause()
    {
        super.onPause();
        Game.stopTimer();
    }

    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        return gestureScanner.onTouchEvent(motionEvent);
    }

    public boolean onSingleTapUp(MotionEvent motionEvent)
    {
        Game.onSimplePress((int) motionEvent.getX(), (int) motionEvent.getY());
        return false;
    }

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY)
    {
        Game.onScroll((int) event1.getY(), -(int) distanceX, -(int) distanceY);
        return false;
    }

    public void onLongPress(MotionEvent motionEvent)
    {
        Game.onLongPress((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    void closeDialog()
    {
        alert.cancel();
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1)
    {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent)
    {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent)
    {

    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    protected void onStart()
    {
        super.onStart();
        FacebookEvents.addAuthListener(authListener);
    }

    protected void onStop()
    {
        super.onStop();
        FacebookEvents.removeAuthListener(authListener);
    }
}
