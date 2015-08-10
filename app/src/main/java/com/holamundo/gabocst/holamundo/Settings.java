package com.holamundo.gabocst.holamundo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

import java.util.HashMap;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    EditText et1, et2;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
                    final SessionSQL sm = new SessionSQL(Settings.this);
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
                            Toast.makeText(Settings.this, "No se pudo cerrar sesion: "+ statusCode, Toast.LENGTH_LONG).show();
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
        if(v.getId() == R.id.button){
            String first = et1.getText().toString();
            String second = et2.getText().toString();
            if(first.equals(second)){
                putData();
            }
            else{
                Toast.makeText(Settings.this, "Introduzca dos contraseñas iguales", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void putData(){

        SessionSQL ss = new SessionSQL(this);

        HashMap<String, String> paramMap = new HashMap<>(ss.getUserDetails());
        HashMap<String, String> id = new HashMap<>(ss.getUser());

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/users/"+id.get("user");
        client.addHeader("token", paramMap.get("token"));
        RequestParams params = new RequestParams();
        params.put("password",et1.getText().toString());

        client.put(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(Settings.this, statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Settings.this, statusCode, Toast.LENGTH_LONG).show();
            }
        });


    }


}
