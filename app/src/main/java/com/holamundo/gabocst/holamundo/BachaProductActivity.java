package com.holamundo.gabocst.holamundo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.holamundo.gabocst.holamundo.Services.LocationService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;

public class BachaProductActivity extends AppCompatActivity implements View.OnClickListener{
    TextView nombre;
    TextView des;
    Button editar, eliminar;
    String id = null;
    String barcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacha_product);

        nombre = (TextView) findViewById(R.id.textView);
        des = (TextView) findViewById(R.id.textView2);
        editar = (Button) findViewById(R.id.editar);
        eliminar = (Button) findViewById(R.id.eliminar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            id = bundle.get("id").toString();
            getProduct(id);
        }

        editar.setOnClickListener(this);
        eliminar.setOnClickListener(this);
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
                if(statusCode>=400 && statusCode<500){
                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                    builder1.setTitle("Imposible");
                    builder1.setMessage("No se encontro la peticion");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else if(statusCode>=500){
                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                    builder1.setTitle("Ups!");
                    builder1.setMessage("Problemas con el servidor... Intente mas tarde");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    android.support.v7.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
    }

    public void getJsonProduct(String response){
        try {
            JSONObject json = new JSONObject(response);
            String name = json.get("name").toString();
            String desc = json.get("description").toString();
            barcode = json.get("barcode").toString();

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
            Intent s = new Intent(BachaProductActivity.this, SettingsBachActivity.class);
            startActivity(s);
        }
        if (id == R.id.action_logout)
        {

            android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(this);
            dialogo1.setTitle("Cerrar Sesión");
            dialogo1.setMessage("¿Está seguro que desea cerrar sesión?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    final SessionSQL sm = new SessionSQL(BachaProductActivity.this);
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
                                android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                                builder1.setTitle("Imposible");
                                builder1.setMessage("No se encontro la peticion");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                            else if(statusCode>=500){
                                android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                                builder1.setTitle("Ups!");
                                builder1.setMessage("Problemas con el servidor... Intente mas tarde");
                                builder1.setCancelable(true);
                                builder1.setNeutralButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert11 = builder1.create();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editar:
                Intent intent = new Intent(this, EditProductActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name",nombre.getText().toString());
                intent.putExtra("description",des.getText().toString());
                intent.putExtra("barcode",barcode);
                startActivity(intent);
                break;
            case R.id.eliminar:
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Está seguro que desea eliminar el producto?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id1) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        String url = "http://inworknet.net:8000/api/bachaqueros/me/products/"+id;
                        SessionSQL ss = new SessionSQL(BachaProductActivity.this);
                        final HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
                        client.addHeader("token", paramMap.get("token"));
                        client.delete(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Toast.makeText(BachaProductActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BachaProductActivity.this, DashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if(statusCode>=400 && statusCode<500){
                                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                                    builder1.setTitle("Imposible");
                                    builder1.setMessage("No se encontro la peticion");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    android.support.v7.app.AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                                else if(statusCode>=500){
                                    android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(BachaProductActivity.this);
                                    builder1.setTitle("Ups!");
                                    builder1.setMessage("Problemas con el servidor... Intente mas tarde");
                                    builder1.setCancelable(true);
                                    builder1.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    android.support.v7.app.AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            }
                        });

                    }
                });
                dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                dialogo1.show();
                break;
        }
    }
}
