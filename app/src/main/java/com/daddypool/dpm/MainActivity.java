package com.daddypool.dpm;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.support.v7.widget.Toolbar;
import com.google.android.gms.ads.MobileAds;

//>>グラフ追加
import android.graphics.Color;
import android.graphics.DashPathEffect;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import android.widget.Spinner;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//<<グラフ追加

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private TextView textView;
    private TextView textDiffData;
    private TextView textHashData;
    private TextView textBalData;
    private TextView textPaidData;
    private TextView textWorkersData;
    private TextView textMinerData;
    private TextView textHashu;
    private TextView pool_stats;
    private EditText editText;
    private String fileName = "Address.txt";
    private String text ="";
    private LineChart mChart;
    private int[] [] HashHistorys = null;
    private int MaxHash;
    private int MinHash;
    private SharedPreferences Lastselect;
    private int serverindex;
    private int currencyindex;
    private String[] addresslist;
    private String serveraddress ="http://zny.daddy-pool.work";
    private String Currency ="bitzeny";
    private String NoData ="NoData";
    private String lastselectserver = "lastselect.txt";
    private String selectcurrency  ="ZENY";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ここで1秒間スリープし、スプラッシュを表示させたままにする。
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        // スプラッシュthemeを通常themeに変更する
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);


        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // アイコンを指定
        toolbar.setNavigationIcon(R.drawable.daddypool48);

        //>>  広告用
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        MobileAds.initialize(this, "ca-app-pub-3125769458134434~5664139191");
        //<<  広告用



        //通貨選択用
//        final Spinner spinnerCurrency = findViewById(R.id.spinnercurrency);
        // ArrayAdapter
        final ArrayAdapter<String> adapterserver
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);

        adapterserver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> adapterCurrency
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //前回選択したサーバーデータを読み込む
        SharedPreferences Lastselect = getSharedPreferences("Lastselect", MODE_PRIVATE);
        serverindex = Lastselect.getInt("serverindex", 0);
        currencyindex = Lastselect.getInt("currencyindex", 0);


        //項目の追加
        adapterserver.add("Daddy-Pool");
        adapterserver.add("MACYAN.NET MINING-POOL");
        adapterserver.add("MOFUMOFU.ME");
        adapterserver.add("NOMP.ZNY.POWERPOOL");
        adapterserver.add("SEMI-POOL");
        adapterserver.add("みんなのプール");
        adapterserver.add("採掘だよ！全員集合！");
        adapterserver.add("人のプール");

        final Spinner spS=(Spinner)findViewById(R.id.spinnerserver);
        final Spinner spC=(Spinner)findViewById(R.id.spinnercurrency);

        //アダプターのセット
        spS.setAdapter(adapterserver);

        Spinner spinner = (Spinner)findViewById(R.id.spinnerserver);
        spinner.setSelection(serverindex);



        //スピナーの内容選択時に呼び出されるコールバックリスナーを登録
        spS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                adapterCurrency.clear();

                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                if (item.equals("Daddy-Pool")){
                    adapterCurrency.add("ZENY");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("MACYAN.NET MINING-POOL")){
                    adapterCurrency.add("ZENY");
                    adapterCurrency.add("BELL");
                    adapterCurrency.add("MONA");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("MOFUMOFU.ME")){
                    adapterCurrency.add("ZENY");
                    adapterCurrency.add("KOTO");
//                    adapterCurrency.add("SUSU");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("みんなのプール")){
                    adapterCurrency.add("ZENY");
                    adapterCurrency.add("BELL");
                    adapterCurrency.add("KOTO");
                    adapterCurrency.add("MONA");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("SEMI-POOL")){
                    adapterCurrency.add("ZENY");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("人のプール")){
                    adapterCurrency.add("ZENY");
                    adapterCurrency.add("BELL");
                    adapterCurrency.add("KOTO");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("NOMP.ZNY.POWERPOOL")){
                    adapterCurrency.add("ZENY");
                    spC.setAdapter(adapterCurrency);
                }else if (item.equals("採掘だよ！全員集合！")){
                    adapterCurrency.add("ZENY");
                    adapterCurrency.add("MONA");
                    spC.setAdapter(adapterCurrency);
                }

                spC.setSelection(0);
                //保存してあるアドレスがあれば読み込んで表示する
                String str = readFile(fileName);
                if (str != null) {
                    textView.setText(str);
                    editText.setText(str);
                } else {
                    textView.setText(R.string.read_error);
                    editText.setText("");
                    HashHistorys = null;

                }
                // エディットテキストのテキストを取得
                text = editText.getText().toString();
                GetJsonData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                }

        });

        spC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner2 = (Spinner) parent;


                // 選択されたアイテムを取得します
                String item1 = (String) spS.getSelectedItem();
                String item2 = (String) spC.getSelectedItem();
                if (item1.equals("Daddy-Pool")){
                    serveraddress ="http://zny.daddy-pool.work";
                    if (item2.equals("ZENY")) {
                        Currency = "bitzeny";
                        fileName = "Daddybitzeny.txt";
                    }

                }else if (item1.equals("MACYAN.NET MINING-POOL")){
                    serveraddress ="http://macyan.net:8080";
                    if (item2.equals("ZENY") ) {
                        Currency = "bitzeny";
                        fileName = "Macyanbitzeny.txt";
                    }else if (item2.equals("BELL") ){
                        Currency ="bellcoin";
                        fileName = "Macyanbellcoin.txt";
                    }else if (item2.equals("MONA") ) {
                        Currency = "monacoin";
                        fileName = "Macyanmonacoin.txt";
                    }

                }else if (item1.equals("MOFUMOFU.ME")){
                    serveraddress ="https://zny.mofumofu.me";
                    if (item2.equals("ZENY") ) {
                        Currency = "bitzeny";
                        fileName = "Mofumofubitzeny.txt";
                    }else if (item2.equals("KOTO") ) {
                        Currency = "koto";
                        fileName = "Mofumofukotocoin.txt";
                    }

                }else if (item1.equals("みんなのプール")){
                    serveraddress ="https://www.minnano-pool.work";
                    if (item2.equals("ZENY") ) {
                        Currency = "bitzeny";
                        fileName = "Minnabitzeny.txt";
                    }else if (item2.equals("MONA") ) {
                        Currency = "monacoin";
                        fileName = "Minnamonacoin.txt";
                    }else if (item2.equals("BELL") ) {
                        Currency = "bellcoin";
                        fileName = "Minnabellcoin.txt";
                    }else if (item2.equals("KOTO") ) {
                        Currency = "koto";
                        fileName = "Minnakotocoin.txt";
                    }

                }else if (item1.equals("SEMI-POOL")){
                    serveraddress ="https://zny.semi-pool.com";
                    if (item2.equals("ZENY")) {
                        Currency = "bitzeny";
                        fileName = "Semibitzeny.txt";
                    }

                }else if (item1.equals("人のプール")){
                    serveraddress ="https://mining.zinntikumugai.xyz";
                    if (item2.equals("ZENY")) {
                        Currency = "bitzeny mainnet";
                        fileName = "Hitobitzeny.txt";
                    }else if (item2.equals("BELL") ) {
                        Currency = "bellcoin";
                        fileName = "Hitobellcoin.txt";
                    }else if (item2.equals("KOTO") ) {
                        Currency = "koto";
                        fileName = "Hitokotocoin.txt";
                    }

                }else if (item1.equals("NOMP.ZNY.POWERPOOL")){
                    serveraddress ="https://nomp.zny.powerpool.jp";
                    if (item2.equals("ZENY") ) {
                        Currency = "bitzeny";
                        fileName = "powerpoolbitzeny.txt";
                    }

                }else if (item1.equals("採掘だよ！全員集合！")){
                    serveraddress ="http://letsminezny.orz.hm:8080";
                    if (item2.equals("ZENY")) {
                        Currency = "bitzeny";
                        fileName = "Zeninbitzeny.txt";
                    }else if (item2.equals("MONA") ) {
                        Currency = "monacoin";
                        fileName = "Zeninmonacoin.txt";
                    }
                }

                //保存してあるアドレスがあれば読み込んで表示する
                String str = readFile(fileName);
                if (str != null) {
                    textView.setText(str);
                    editText.setText(str);
                } else {
                    textView.setText(R.string.read_error);
                    editText.setText("");
                    HashHistorys = null;

                }
                // エディットテキストのテキストを取得
                text = editText.getText().toString();
                GetJsonData();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });



        textDiffData = findViewById(R.id.textDiffData);
        textHashData = findViewById(R.id.textHashData);
        textBalData = findViewById(R.id.textBalData);
        textPaidData = findViewById(R.id.textPaidData);
        textWorkersData = findViewById(R.id.textWorkersData);
        textMinerData = findViewById(R.id.textMinerData);
        textHashu = findViewById(R.id.textHash);

        textView = findViewById(R.id.textView2);
        editText = findViewById(R.id.editText);

        //保存してあるアドレスがあれば読み込んで表示する
        String str = readFile(fileName);
        if (str != null) {
            textView.setText(str);
            editText.setText(str);
        } else {
            textView.setText(R.string.read_error);
            editText.setText("");
        }
        // エディットテキストのテキストを取得
        text = editText.getText().toString();


        // JSONの取得
        GetJsonData();

       final Button buttonSave = findViewById(R.id.button);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSave.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        buttonSave.setEnabled(true);
                    }
                }, 1000L);
                // エディットテキストのテキストを取得
//                serveraddress = editText.getText().toString();
                text = editText.getText().toString();
                // JSONの取得
                GetJsonData();

                saveFile(fileName, text);

                //最後に選択したサーバーと通貨の情報を保存
                Spinner spinner = (Spinner)findViewById(R.id.spinnerserver);
                Spinner spinner2 = (Spinner)findViewById(R.id.spinnercurrency);
                // 選択されているアイテムのIndexを取得
                serverindex = spinner.getSelectedItemPosition();
                currencyindex = spinner2.getSelectedItemPosition();
                SharedPreferences Lastselect = getSharedPreferences("Lastselect", MODE_PRIVATE);
                Editor editor = Lastselect.edit();
                // Key: input, value: text
                editor.putInt("serverindex", serverindex);
                editor.putInt("currencyindex", currencyindex);
                //editor.commit();
                editor.apply();

                if(text.length() == 0){
                    textView.setText(R.string.no_text);
                }
                else{
                    textView.setText(R.string.saved);
                }
                String str = readFile(fileName);
                if (str != null) {
                    textView.setText(str);
                } else {
                    textView.setText(R.string.read_error);
                }

            }
        });

        //QRコード読み取りボタンイベント
        final Button buttonQRRead = findViewById(R.id.btnQRRead);
        buttonQRRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonQRRead.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        buttonQRRead.setEnabled(true);
                    }
                }, 1000L);
                new IntentIntegrator(MainActivity.this).initiateScan();
            }

        });
        //>>グラフ追加
        mChart = findViewById(R.id.line_chart);

        // Grid背景色
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(false);

        // Grid縦軸を破線
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
                // 右側の目盛り
        mChart.getAxisRight().setEnabled(false);
        // add data
//        setData();

        mChart.animateX(2500);
        //mChart.invalidate();

        //凡例
        mChart.getLegend().setEnabled(false);
        // dont forget to refresh the drawing
        // mChart.invalidate();
        //<<グラフ追加

    }



    @Override
    //QRコード読み取り後の処理
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String qrAddress = null;

        if (result.getContents()== null){
            return;
        }
        qrAddress = result.getContents().replace("bitzeny:", "").replace("monacoin:", "").replace("bellcoin:", "").replace("koto:", "").replace("susucoin:", "");

        if(qrAddress != null) {
            //読み取った情報を入力テキストへセット
            qrAddress.trim();
//            editText.setText( qrAddress.substring(8,42));

            //前方一致でKOTOの場合は35ｹﾀ　それ以外は34ｹﾀに
            if(qrAddress.startsWith("k")) {
                editText.setText( qrAddress.substring(0,35));
            } else {
                editText.setText(qrAddress.substring(0, 34));
            }
            Log.d("readQR", qrAddress);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // ファイルを保存
    public void saveFile(String file, String str) {

        // try-with-resources
        try (FileOutputStream fileOutputstream = openFileOutput(file,
                Context.MODE_PRIVATE);){

            fileOutputstream.write(str.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ファイルを読み出し
    public String readFile(String file) {
        String text = null;

        // try-with-resources
        try (FileInputStream fileInputStream = openFileInput(file);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(fileInputStream, "UTF-8"));
        ) {

            String lineBuffer;
            while ((lineBuffer = reader.readLine()) != null) {
                text = lineBuffer;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {

        String urlText = "";
        //MyData 用Json取得　API（http://zny.daddy-pool.work/api/worker_stats?）
        if (id == 1){
            urlText = serveraddress+"/api/worker_stats?"+ text ;
        }

        //PoolStats 用Json取得　API（http://zny.daddy-pool.work/api/stats）
        if (id == 2){
            urlText = serveraddress+"/api/stats";
        }
        //グラフ 用Json取得　API（http://zny.daddy-pool.work/api/worker_stats?）
        if (id == 3){
            urlText = serveraddress+"/api/worker_stats?"+ text ;
        }


        AsyncJsonLoader jsonLoader = new AsyncJsonLoader(this, urlText);
        jsonLoader.forceLoad();
        return  jsonLoader;

    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        String addressname = "";

        if (data != null) {
            try {
                //MyData の表示

                if (loader.getId()==1) {

                    JSONObject jsonObject = data.getJSONObject("workers");
                    if (jsonObject.length() == 0) {
                        textDiffData.setText(NoData);
                        textHashData.setText(NoData);
                        textBalData.setText(NoData);
                        textPaidData.setText(NoData);
                    }else{
                        addressname = text.replace("\"", "").replace("[","").replace("]","");
                        textDiffData.setText(jsonObject.getJSONObject(addressname).getString("diff"));
                        textHashData.setText(jsonObject.getJSONObject(addressname).getString("hashrateString"));
                        textBalData.setText(jsonObject.getJSONObject(addressname).getString("balance"));
                        textPaidData.setText(jsonObject.getJSONObject(addressname).getString("paid"));

                    }

                }
                //PoolStatsの表示
                if (loader.getId()==2){
                    JSONObject jsonObject = data.getJSONObject("pools");
                    textWorkersData.setText(jsonObject.getJSONObject(Currency).getString("workerCount"));
                    textMinerData.setText(jsonObject.getJSONObject(Currency).getString("minerCount"));
                    textHashu.setText(jsonObject.getJSONObject(Currency).getString("hashrateString"));

                    // 入力補完リストを読み込む
                    //JSONからリストを引っ張ってくる処理を入れる

                    JSONArray jsonArray = jsonObject.getJSONObject(Currency).getJSONObject("workers").names();
                    if (jsonArray != null){
                        addresslist = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            addresslist[i] = jsonArray.getString(i);
                        }
                        final AutoCompleteTextView editText = (AutoCompleteTextView) findViewById(R.id.editText);
                        // ArrayAdapterを作成
                        if (addresslist != null){
                            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, addresslist);
                            // ViewにAdapterを設定
                            editText.setAdapter(adapter);
                        }
                    }

                }
                if (loader.getId()==3){
                    //グラフに値をセット
                    JSONObject jsonObject = data.getJSONObject("history");
                    if (jsonObject.length() == 0){
                        HashHistorys = null;
                        setData();
                        return;}
                    else{
                    addressname = text.replace("\"", "").replace("[","").replace("]","");
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray(addressname);

                    int j = 0;

                    if (jsonArray.length() == 0)
                    {return;}
                    if (jsonArray.length() <= 20){
                        HashHistorys = new int[jsonArray.length()][2];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            HashHistorys[j][0] = jsonObject1.getInt("time");
                            HashHistorys[j][1] = jsonObject1.getInt("hashrate");
                            j++;
                        }
                    }
                    else{
                        //全てを表示すると多いのでループカウントを２０からに設定。
                        HashHistorys = new int[jsonArray.length() - 20][2];
                        for (int i = 20; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            HashHistorys[j][0] = jsonObject1.getInt("time");
                            HashHistorys[j][1] = jsonObject1.getInt("hashrate");
                            j++;
                        }
                    }

                    //グラフY軸表示用に最大値と最小値を取得しておく
                    MaxHash = 0;
                    MinHash = 0;
                    for (int i = 1; i < HashHistorys.length; i++) {
                        int v = HashHistorys[i][1];
                        if (v > MaxHash) {
                            MaxHash = v;
                        }
                        if (v < MinHash) {
                            MinHash = v;
                        }
                    }
                 setData();

                }

            } catch (JSONException e) {
                Log.d("onLoadFinished","JSONのパースに失敗しました。 JSONException=" + e);
                setData();
            }
        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {
        // 処理なし
    }

    //APIからJson
     public void GetJsonData() {
        // JSONの取得
        //MyData 用
        getLoaderManager().restartLoader(1, null, this);
        //PoolStats 用
        getLoaderManager().restartLoader(2, null, this);
         //グラフ 用
        getLoaderManager().restartLoader(3, null, this);
    }
    //グラフ追加
    private void setData() {


        ArrayList<Entry> values = new ArrayList<>();

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        if (HashHistorys == null){values.add(new Entry('0', 0, null, null));}
        else{for (int i = 0; i < HashHistorys.length; i++) {
//            Date time = new Date(HashHistorys[i][0] * 1000L);
//            time = sdf.parse(sdf.format(time));
//
//            String strtime = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(time);

            values.add(new Entry(HashHistorys[i][0] * 1000L, HashHistorys[i][1], null, null));
        }}



        LineDataSet set1;

        if (mChart.getData() != null &&
               mChart.getData().getDataSetCount() > 0) {

           LineData data = mChart.getData();

           set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
           set1.setValues(values);
           mChart.getData().notifyDataChanged();
           mChart.notifyDataSetChanged();
           mChart.invalidate();
       }

            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");

            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setFillColor(Color.BLUE);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData lineData = new LineData(dataSets);

            mChart.notifyDataSetChanged();
            mChart.invalidate();

            YAxis leftAxis = mChart.getAxisLeft();
            // Y軸最大最小設定
            //自分のデータの最大最小プラスマイナス1000としておく
            leftAxis.setAxisMaximum(MaxHash+1000);
            leftAxis.setAxisMinimum(MinHash-1000);
            // Grid横軸を破線
            leftAxis.enableGridDashedLine(10f, 10f, 0f);
            leftAxis.setDrawZeroLine(true);



            // set data
            mChart.setData(lineData);

//        }
    }
    // アクションバーを表示するメソッド
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // オプションメニューのアイテムが選択されたときに呼び出されるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        TextView varTextView = (TextView) findViewById(R.id.textView);
        switch (item.getItemId()) {
            case R.id.item0:
                Intent varIntent_back =
                        new Intent(MainActivity.this, aboutDPM.class);
                startActivity(varIntent_back);
                return true;
            case R.id.item1:
                Intent varIntent2 =
                        new Intent(MainActivity.this, faucet.class);
                startActivity(varIntent2);
                return true;
            case R.id.item2:
                Intent varIntent1 =
                        new Intent(MainActivity.this, OssLicensesMenuActivity.class);
                startActivity(varIntent1);
                return true;
            case R.id.item3:
                Intent varIntent3 =
                        new Intent(MainActivity.this, Terms.class);
                startActivity(varIntent3);
                return true;
            case R.id.item5:
                Intent varIntent4 =
                        new Intent(MainActivity.this, privacy.class);
                startActivity(varIntent4);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
