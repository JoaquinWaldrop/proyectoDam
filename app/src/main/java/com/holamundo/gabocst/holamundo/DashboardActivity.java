package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.holamundo.gabocst.holamundo.Services.LocationService;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    ListView lista;
    ArrayList<Integer> rows = new ArrayList<>();
    EditText busqueda;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        lista = (ListView) findViewById(R.id.listProducts);
        busqueda = (EditText) findViewById(R.id.busqueda);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = rows.get(position).toString();
                Intent intent = new Intent(DashboardActivity.this, BachaProductActivity.class);
                intent.putExtra("id", s);
                startActivity(intent);
            }
        });

        busqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getLista();

       // LocationService myService = new LocationService(DashboardActivity.this);
        //myService.mostrar(this);

    }


    public void getLista(){
        SessionSQL ss = new SessionSQL(this);
        HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/bachaqueros/me/products";
        client.addHeader("token", paramMap.get("token"));
        client.get(url, new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String resultado = new String(responseBody);
            CargarLista(listar(resultado));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if(statusCode>=400 && statusCode<500){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardActivity.this);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardActivity.this);
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


    public void CargarLista(ArrayList<String> datos){
        if(datos.size()>0) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
            lista.setAdapter(adapter);
        }
        else{
            Toast.makeText(DashboardActivity.this, "No posee productos", Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<String> listar(String response){
        ArrayList<String> listado = new ArrayList<>();
        try{
            JSONArray json = new JSONArray(response);
            String texto;
            for(int i=0; i<json.length(); i++){
                texto = json.getJSONObject(i).getString("name");
                listado.add(texto);
                rows.add(json.getJSONObject(i).getInt("id"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
            Intent s = new Intent(DashboardActivity.this, SettingsBachActivity.class);
            startActivity(s);
        }
        if (id == R.id.action_agregar) {
            Intent i = new Intent(this, AddProductActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_logout)
        {

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Cerrar Sesión");
            dialogo1.setMessage("¿Está seguro que desea cerrar sesión?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    final SessionSQL sm = new SessionSQL(DashboardActivity.this);
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
                            Toast.makeText(DashboardActivity.this, "No se pudo cerrar sesion: "+ statusCode, Toast.LENGTH_LONG).show();
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
