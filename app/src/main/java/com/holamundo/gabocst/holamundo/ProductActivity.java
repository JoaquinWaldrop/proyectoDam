package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class ProductActivity extends AppCompatActivity {

    TextView nombre;
    TextView des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        nombre = (TextView) findViewById(R.id.textView);
        des = (TextView) findViewById(R.id.textView2);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String id = bundle.get("id").toString();
            getProduct(id);
        }

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
        getMenuInflater().inflate(R.menu.menu_product, menu);
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
        if (id == R.id.action_logout)
        {

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Cerrar Sesión");
            dialogo1.setMessage("¿ Está seguro que desea cerrar sesión ?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    final SessionSQL sm = new SessionSQL(ProductActivity.this);
                    AsyncHttpClient client = new AsyncHttpClient();
                    String url = "http://inworknet.net:8000/api/sessions";
                    HashMap<String, String> paramMap = new HashMap<>(sm.getUserDetails());
                    client.addHeader("token", paramMap.get("token"));
                    client.delete(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            sm.logoutUser();
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(ProductActivity.this, "No se pudo cerrar sesion: " + statusCode, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                }
            });
            dialogo1.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
