package biz.towaypro.games.minesweeper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScoreActivity extends android.app.ListActivity
{
    static TextView caption;
    private String tmpCaption;

    myAdapter adapter;
    final ArrayList<ScoreItem> items = new ArrayList<ScoreItem>();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Bundle extras = getIntent().getExtras();
        tmpCaption = extras.getString("caption");
        ScoreTask task = new ScoreTask();
        task.execute(extras.getInt("level"));
    }

    public void onResume()
    {
        super.onResume();
        caption.setText(tmpCaption);
    }

    private class myAdapter extends BaseAdapter
    {
        private LayoutInflater mLayoutInflater;

        public myAdapter()
        {
            mLayoutInflater = LayoutInflater.from(getApplicationContext());
        }

        public int getCount()
        {
            return items.size();
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.item, null);

            TextView vN = (TextView) convertView.findViewById(R.id.N);
            vN.setText(items.get(position).N);

            TextView vName = (TextView) convertView.findViewById(R.id.Name);
            vName.setText(items.get(position).Name);

            TextView vScore = (TextView) convertView.findViewById(R.id.Score);
            vScore.setText(items.get(position).Score);

            TextView vComment = (TextView) convertView.findViewById(R.id.Comment);
            vComment.setText(items.get(position).Comment);
            return convertView;
        }
    }

    private class ScoreTask extends AsyncTask<Integer, Void, Void>
    {
        private boolean error = false;

        protected Void doInBackground(Integer... params)
        {
            String response = Server.executeQueryGETForResult(String.format("http://toway.biz/minesweeper/pro/get.php?level=%1$s", String.valueOf(String.valueOf(params[0]))));
            // парсим ответ и заполняем спикок
            int n = 1;
            try
            {
                JSONArray jArray = new JSONArray(response);
                for(int i = 0; i < jArray.length(); i++)
                {
                    JSONObject json_data = jArray.getJSONObject(i);
                    items.add(new ScoreItem(String.valueOf(n++), json_data.getString("user"), json_data.getString("score"), json_data.getString("comment")));
                }
            }
            catch(Exception e) {/**/}
            return null;
        }

        protected void onPostExecute(Void voids)
        {
            if(error)
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            // свяжем список с данными и ListView
            adapter = new myAdapter();
            setListAdapter(adapter);
            // удалим Progressbar
            View progress = findViewById(R.id.progress);
            ((LinearLayout) findViewById(R.id.lay_main)).removeView(progress);
        }
    }

    private static class ScoreItem
    {
        public String N;
        public String Name;
        public String Score;
        public String Comment;

        public ScoreItem(String n, String name, String score, String comment)
        {
            N = n;
            Name = name;
            Score = score;
            Comment = comment;
        }
    }
}
