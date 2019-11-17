package biz.towaypro.games.minesweeper;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class TabScoreActivity extends TabActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.tab);

        ScoreActivity.caption = ((TextView) findViewById(R.id.caption));

        // если онлайн - убираем надпись-предупреждение
        if(Server.isOnline(this))
            ((LinearLayout) findViewById(R.id.lay_main)).removeView(findViewById(R.id.offline));

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.level0));
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("level", 0);
        intent.putExtra("caption", getString(R.string.level0));
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.level1));
        intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("level", 1);
        intent.putExtra("caption", getString(R.string.level1));
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator(getString(R.string.level2));
        intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("level", 2);
        intent.putExtra("caption", getString(R.string.level2));
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        Bundle extras = getIntent().getExtras();
        tabHost.setCurrentTab(extras.getInt("level"));
    }
}
