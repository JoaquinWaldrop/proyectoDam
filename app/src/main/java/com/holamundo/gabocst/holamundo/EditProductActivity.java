package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener{

    EditText nombre, descripcion;
    Button guardar;
    String id = null, barcode=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        nombre = (EditText)findViewById(R.id.et1);
        descripcion = (EditText)findViewById(R.id.et2);
        guardar = (Button) findViewById(R.id.button2);
        setearVar();

        guardar.setOnClickListener(this);

    }

    public void setearVar(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            id = bundle.get("id").toString();
            String name = bundle.get("name").toString();
            String des = bundle.get("description").toString();
            barcode = bundle.get("barcode").toString();
            nombre.setText(name);
            descripcion.setText(des);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_product, menu);
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
            Intent s = new Intent(EditProductActivity.this, SettingsBachActivity.class);
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
                    final SessionSQL sm = new SessionSQL(EditProductActivity.this);
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProductActivity.this);
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditProductActivity.this);
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button2){
            if(nombre.getText().toString().length()>0 && descripcion.getText().toString().length()>0){
                AsyncHttpClient client = new AsyncHttpClient();
                String url = "http://inworknet.net:8000/api/bachaqueros/me/products/"+id;
                SessionSQL ss = new SessionSQL(this);
                HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
                client.addHeader("token", paramMap.get("token"));
                RequestParams params = new RequestParams();
                params.put("name",nombre.getText().toString());
                params.put("description",descripcion.getText().toString());
                params.put("barcode",barcode);
                client.put(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(EditProductActivity.this, "Actualizacion de producto exitosa!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(EditProductActivity.this, "Actualizacion de producto no realizada: "+statusCode, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                Toast.makeText(EditProductActivity.this, "Proporcione un nombre y una descripcion para este producto", Toast.LENGTH_LONG).show();
            }
        }
    }
}
