package kr.ac.kumoh.s20130870.termprojectcustomlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
* Term Project. 커스텀 리스트
*        //o 커스텀 리스트 프로그래밍 (Level 1) (70점 만점)
*        //- Homework #5에서 개발한 5개 Field를 갖는 모델(class)을 만들고 리스트를 통해 출력(게임 캐릭터 이름, 공격력, 방어력, 레벨, 스킬 이름)하는 프로그램을 개선하여 커스텀 리스트로 출력
*        //- DB에 테이블 만들고 데이터를 저장한 후에 PHP를 통해서 JSON 형태로 얻어옴
*        //- Volley 사용
*        //- 아이템을 위한 커스텀 레이아웃 개발
*        //- 아이템 레이아웃에 ImageView를 사용하여 이미지 출력 (이미지 경로는 DB에서 가지고 오지 않아도 됨)
*
*        //o 리스트 선택하면 Detail 페이지 출력 (Level 2) (90점 만점)
*        //- 리스트를 선택하면 Intent를 사용하여 별도로 개발한 액티비티를 실행하여 세부 내용 출력
*
*        //o NetworkImageView 사용 (Level 3) (100점 만점)
*        //- Level 2를 완성한 경우에만 인정
*        //- DB에 이미지 URL 경로를 저장하고 Level 1의 ImageView 대신 NetworkImageView를 사용하여 웹서버에서 이미지 가져와서 출력
*
*        o 다음의 내용을 포함하여 hwp 파일로 제출할 것
*        1. 겉장 (Homework 번호 및 제목, 과목명, 분반, 학과, 학년, 학번, 이름 포함)
*        2. 과제 개요 (레벨 몇까지 개발 했는지와 설명)
*        3. 실행 화면 및 기능 설명 (레벨 확인할 수 있는 실행 화면 필수)
*        4. 소스 코드
*        5. 과제 수행시 문제 및 해결 방법
*
*        * 페이지 하단에 페이지 번호 출력 (Ctrl+N,P)
*        * 설명이 자세할수록 점수를 많이 얻을 수 있는 확률이 높음
*/
public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

    public static final String HEROTAG = "HeroTag";
    protected RequestQueue mQueue = null;
    protected ArrayList<GameField> mArray = new ArrayList<GameField>();
    protected GameFieldAdapter mAdapter = null;
    protected ListView mList = null;
    protected JSONObject mResult = null;
    protected ImageLoader mImageLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (ListView)findViewById(R.id.list);
        mAdapter = new GameFieldAdapter(this, R.layout.character_spec);
        mList.setAdapter(mAdapter);

        mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(this));
        requestJSON();

        mList.setOnItemClickListener(this);
    }

    protected void requestJSON(){
        String url = "http://192.168.0.5/selectjson.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse (JSONObject response){
//                Toast.makeText(MainActivity.this,
//                        response.toString(),
//                        Toast.LENGTH_SHORT).show();
                mResult = response;
                drawList();
            }
        }
                ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(MainActivity.this,
                        error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mQueue.add(request);
    }

    protected void drawList() {
        mArray.clear();
        try {
            JSONArray list = mResult.getJSONArray("list");
            for(int i=0;i<list.length();i++){
                JSONObject node = list.getJSONObject(i);
                String name = node.getString("name");
                String damage = node.getString("damage");
                String shield = node.getString("shield");
                String level = node.getString("level");
                String skillname = node.getString("skillname");
                String image = node.getString("image");
                String info = node.getString("info");

                mArray.add(new GameField(name,damage,shield,level,skillname,image,info));
                Log.i(HEROTAG, "name=" + name + "damage=" + damage + "shield="
                        + shield + "level=" + level + "skillname=" + skillname+"image=" + image);
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error: "+ e.toString(), Toast.LENGTH_SHORT).show();
            mResult = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mQueue != null)
            mQueue.cancelAll(HEROTAG);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent detail = new Intent(this, GameFieldActivity.class);
        detail.putExtra("name",mArray.get(i).getName());
        detail.putExtra("damage",mArray.get(i).getDamage());
        detail.putExtra("shield",mArray.get(i).getShield());
        detail.putExtra("level",mArray.get(i).getLevel());
        detail.putExtra("skill",mArray.get(i).getSkillname());
        detail.putExtra("info",mArray.get(i).getInfo());

        startActivity(detail);
    }

    public class GameField {
        String name;
        String damage;
        String shield;
        String level;
        String skillname;
        String image;
        String info;

        public GameField(String name,String damage,String shield,String level,String skillname,String image,String info) {
            this.name = name;
            this.damage = damage;
            this.shield = shield;
            this.level = level;
            this.skillname = skillname;
            this.image = image;
            this.info = info;
        }

        public String getName() { return name; }
        public String getDamage() { return damage; }
        public String getShield() { return shield; }
        public String getLevel() { return level; }
        public String getSkillname() { return skillname; }
        public String getImage() { return image; }
        public String getInfo() { return info; }
    }

    static class GameFieldViewHolder {
        NetworkImageView imimage;
        TextView txName;
        TextView txSkill;
        TextView txLevel;
        TextView txDamage;
        TextView txShield;
    }

    public class GameFieldAdapter extends ArrayAdapter<GameField> {

        LayoutInflater mInflater = null;

        public GameFieldAdapter(Context context, int resource){
            super(context,resource);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mArray.size();
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            GameFieldViewHolder viewHolder;

            if (v == null) {
                v = mInflater.inflate(R.layout.character_spec ,parent, false);

                viewHolder = new GameFieldViewHolder();
                viewHolder.txName = (TextView)v.findViewById(R.id.name);
                viewHolder.txSkill = (TextView)v.findViewById(R.id.skillname);
                viewHolder.txLevel = (TextView)v.findViewById(R.id.level);
                viewHolder.txDamage = (TextView)v.findViewById(R.id.damage);
                viewHolder.txShield = (TextView)v.findViewById(R.id.shield);
                viewHolder.imimage = (NetworkImageView)v.findViewById(R.id.image);
                v.setTag(viewHolder);
            }
            else {
                viewHolder = (GameFieldViewHolder)v.getTag();
            }

            GameField info = mArray.get(position);
            if(info != null){
                viewHolder.txName.setText(info.getName());
                viewHolder.txSkill.setText("  - 스킬명 : "+info.getSkillname());
                viewHolder.txLevel.setText("(LEVEL: "+info.getLevel()+")");
                viewHolder.txDamage.setText(info.getDamage());
                viewHolder.txShield.setText(info.getShield());
                viewHolder.imimage.setImageUrl(info.getImage(), mImageLoader);
            }
            return v;
        }
    }
}
