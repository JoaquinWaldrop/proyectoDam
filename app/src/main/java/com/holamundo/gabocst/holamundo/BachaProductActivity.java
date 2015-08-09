package com.holamundo.gabocst.holamundo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.holamundo.gabocst.holamundo.Services.LocationService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class BachaProductActivity extends AppCompatActivity {
    TextView nombre;
    TextView des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacha_product);

        nombre = (TextView) findViewById(R.id.textView);
        des = (TextView) findViewById(R.id.textView2);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String id = bundle.get("id").toString();
            getProduct(id);
        }
    }

    public void starService(View v){
        Intent i = new Intent(this, LocationService.class);
        startService(i);

    }

    public void stopService(View v){
        Intent i = new Intent(this, LocationService.class);
        stopService(i);
    }

    public void getProduct(String id){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/products/"+id;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resultado= new String(responseBody);
                getJsonProduct(resultado);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void getJsonProduct(String response){
        try {
            JSONObject json = new JSONObject(response);
            String name = json.get("name").toString();
            String desc = json.get("description").toString();

            nombre.setText(name);
            des.setText(desc);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bacha_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
