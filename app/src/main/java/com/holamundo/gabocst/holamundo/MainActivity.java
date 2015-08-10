package com.holamundo.gabocst.holamundo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText email;
    EditText password;
    Button login;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionSQL sm = new SessionSQL(this);
        if(sm.isLoggedIn()){
            HashMap<String, String> paramMap = new HashMap<>(sm.getType());
            if(paramMap.get("userType").equals("0")){
                Intent intent = new Intent(this, ClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }



        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signup = (TextView) findViewById(R.id.signup);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.signup:
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.login:
                postSesion(email.getText().toString(), password.getText().toString());
            default:
                break;
        }
    }

    public void postSesion(String Email, String Password){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://inworknet.net:8000/api/sessions?";
        String parametros ="email=" + Email + "&password=" + Password;
        client.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String resultado= new String(responseBody);
                sqlSesion(resultado);

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String resultado= new String(responseBody);
                Toast.makeText(MainActivity.this, "Mal: " + resultado, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sqlSesion(String resultado){
        try {
            JSONObject json = new JSONObject(resultado);
            int userType = parseInt(json.get("userType").toString());
            String token = json.get("token").toString();

            SessionSQL sm = new SessionSQL(this);
            sm.createLoginSession(token, json.get("user").toString(), json.get("userType").toString());

            if(userType==0){
                Intent intent = new Intent(this, ClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }





}
