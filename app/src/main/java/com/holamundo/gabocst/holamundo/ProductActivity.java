package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class ProductActivity extends AppCompatActivity {

    TextView nombre, des, lista;
    String id = null;
    String owner = null;
    ListView others;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        nombre = (TextView) findViewById(R.id.textView);
        des = (TextView) findViewById(R.id.textView2);
        lista = (TextView) findViewById(R.id.title);
        others = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            id = bundle.get("id").toString();
            owner = bundle.get("owner").toString();
            //getProduct(id);
            getOthers();
        }

    }

    public void getOthers(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/bachaqueros/"+owner+"/products";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resultado= new String(responseBody);
                CargarLista(getJsonOthers(resultado));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode>=400 && statusCode<500){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductActivity.this);
                    builder1.setTitle("Imposible");
                    builder1.setMessage("No se encontro la peticion");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else if(statusCode>=500){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductActivity.this);
                    builder1.setTitle("Ups!");
                    builder1.setMessage("Problemas con el servidor... Intente mas tarde");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
    }

    public ArrayList<String> getJsonOthers(String response){
        ArrayList<String> listado = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(response);
            for(int i=0; i<json.length(); i++){
                if(json.getJSONObject(i).get("id").toString().equals(id)){
                    String name = json.getJSONObject(i).get("name").toString();
                    String desc = json.getJSONObject(i).get("description").toString();
                    nombre.setText(name);
                    des.setText(desc);
                }
                else{
                    listado.add(json.getJSONObject(i).get("name").toString());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }

    public void CargarLista(ArrayList<String> datos){
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,datos);
        others.setAdapter(adapter);
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
            Intent s = new Intent(ProductActivity.this, SettingsBachActivity.class);
            startActivity(s);
        }
        if (id == R.id.action_logout)
        {

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Cerrar Sesión");
            dialogo1.setMessage("¿Está seguro que desea cerrar sesión?");
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
                            if(statusCode>=400 && statusCode<500){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductActivity.this);
                                builder1.setTitle("Imposible");
                                builder1.setMessage("No se encontro la peticion");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                            else if(statusCode>=500){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProductActivity.this);
                                builder1.setTitle("Ups!");
                                builder1.setMessage("Problemas con el servidor... Intente mas tarde");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
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
