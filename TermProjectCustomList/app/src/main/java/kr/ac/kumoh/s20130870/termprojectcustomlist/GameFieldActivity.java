package kr.ac.kumoh.s20130870.termprojectcustomlist;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

/**
 * Created by user on 2017-12-09.
 */

public class GameFieldActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail);

        TextView name = (TextView)findViewById(R.id.name);
        TextView level = (TextView)findViewById(R.id.level);
        TextView damage = (TextView)findViewById(R.id.damage);
        TextView shield = (TextView)findViewById(R.id.shield);
        TextView skillname = (TextView)findViewById(R.id.skillname);
        TextView info = (TextView)findViewById(R.id.info);

        Bundle bundle = getIntent().getExtras();
        name.setText( bundle.getString("name"));
        level.setText(bundle.getString("level"));
        damage.setText( bundle.getString("damage"));
        shield.setText( bundle.getString("shield"));
        skillname.setText(bundle.getString("skill"));
        info.setText(bundle.getString("info"));

    }
}
