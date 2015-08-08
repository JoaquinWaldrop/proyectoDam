package com.holamundo.gabocst.holamundo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email;
    EditText password;
    Button regi;
    RadioGroup radioGroup;
    int radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        regi = (Button) findViewById(R.id.registrar);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        regi.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cliente) {
                    radioButton = 0;
                } else {
                    radioButton = 1;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registrar:

                postRegistro(email.getText().toString(), password.getText().toString(), radioButton);
                //getUsers();
                break;
            default:
                break;
        }
    }

    public void postRegistro(String Email, String Password, int Tipo){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/users?";
        String parametros ="email=" + Email + "&password=" + Password + "&type=" + Tipo;
        client.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String resultado= new String(responseBody);
                    Toast.makeText(SignupActivity.this, "Todo OK: " + resultado, Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(SignupActivity.this, "Mal: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUsers(){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/users";
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(SignupActivity.this, "Bien: " + statusCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(SignupActivity.this, "Mal: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
