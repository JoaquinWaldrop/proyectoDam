package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{

    private Button scanBtn;
    private TextView contentTxt;
    String scanContent = null;
    EditText et1, et2;
    Button agregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        et1 = (EditText)findViewById(R.id.et1);
        et2 = (EditText)findViewById(R.id.et2);
        agregar = (Button)findViewById(R.id.agregar);
        //Se Instancia el botón de Scan
        scanBtn = (Button)findViewById(R.id.scan_button);
        //Se Instancia el Campo de Texto para el contenido  del código de barra
        contentTxt = (TextView)findViewById(R.id.scan_content);
        //Se agrega la clase MainActivity.java como Listener del evento click del botón de Scan
        scanBtn.setOnClickListener(this);
        agregar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //Se responde al evento click
        if(view.getId()==R.id.scan_button){
            //Se instancia un objeto de la clase IntentIntegrator
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            //Se procede con el proceso de scaneo
            scanIntegrator.initiateScan();
        }
        else if(view.getId()==R.id.agregar){
            Agregar();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Se obtiene el resultado del proceso de scaneo y se parsea
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //Quiere decir que se obtuvo resultado pro lo tanto:
            //Desplegamos en pantalla el contenido del código de barra scaneado
            scanContent = scanningResult.getContents();

            getProducto();
        }else{
            //Quiere decir que NO se obtuvo resultado
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getProducto(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/products";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resultado = new String(responseBody);
                CargarLista(listar(resultado));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddProductActivity.this, "Mal: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CargarLista(ArrayList<String> datos){
        if(datos.size()>0){
            et1.setText(datos.get(0));
            et2.setText(datos.get(1));
            contentTxt.setText(datos.get(2));
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "http://inworknet.net:8000/api/bachaqueros/me/products";
            SessionSQL ss = new SessionSQL(this);
            HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
            client.addHeader("token", paramMap.get("token"));
            RequestParams params = new RequestParams();
            params.put("name",datos.get(0));
            params.put("description",datos.get(1));
            params.put("barcode",scanContent);
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(AddProductActivity.this, "Producto Agregado", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(AddProductActivity.this, "No se pudo agregar el producto", Toast.LENGTH_LONG).show();
                }
            });

        }
        else{
            Toast.makeText(this, "No existe producto con el codigo de barra indicado", Toast.LENGTH_LONG).show();
            contentTxt.setText(scanContent);
            agregar.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<String> listar(String response){
        ArrayList<String> listado = new ArrayList<>();
        try{
            JSONArray json = new JSONArray(response);
            String texto;
            for(int i=0; i<json.length(); i++){
                texto = json.getJSONObject(i).getString("barcode");
                if(texto.equals(scanContent)){
                    listado.add(json.getJSONObject(i).getString("name"));
                    listado.add(json.getJSONObject(i).getString("description"));
                    listado.add(texto);
                    break;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listado;
    }

    public void Agregar(){
        if(scanContent!=null && et1.getText().toString().length()!=0 && et2.getText().toString().length()!=0){
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "http://inworknet.net:8000/api/bachaqueros/me/products";
            SessionSQL ss = new SessionSQL(this);
            HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
        client.addHeader("token", paramMap.get("token"));
            RequestParams params = new RequestParams();
            params.put("name",et1.getText().toString());
            params.put("description",et2.getText().toString());
            params.put("barcode",scanContent);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(AddProductActivity.this, "Producto Agregado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddProductActivity.this, "No se pudo agregar el producto: " + statusCode, Toast.LENGTH_LONG).show();
                }
            });

        }
        else{
            Toast.makeText(this, "Proporcione un nombre, una descripcion y un codigo de barra", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_product, menu);
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
                    final SessionSQL sm = new SessionSQL(AddProductActivity.this);
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
                            Toast.makeText(AddProductActivity.this, "No se pudo cerrar sesion: "+ statusCode, Toast.LENGTH_LONG).show();
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
