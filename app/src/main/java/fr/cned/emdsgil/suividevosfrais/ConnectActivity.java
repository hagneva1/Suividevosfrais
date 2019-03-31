package fr.cned.emdsgil.suividevosfrais;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;

public class ConnectActivity extends AppCompatActivity {

    protected static String idUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        connect();
    }

    private void connect() {
        ((Button) findViewById(R.id.btnConnect)).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String login = ((EditText)findViewById(R.id.txtLogin)).getText().toString();
                String pwd = ((EditText)findViewById(R.id.txtPwd)).getText().toString();
                Connect process = new Connect();
                process.execute(login, pwd);
                // ouvre l'activité principale si combinaison login / mdp trouvée
            }
        });
    }

    public class Connect extends AsyncTask<String, Void, Void> {
        String idUser = "";

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView textView = findViewById(R.id.txtError);
            if (ConnectActivity.idUser.equals("Erreur") || ConnectActivity.idUser.equals("")) {
                textView.setText("Login ou mot de passe incorrect");
                findViewById(R.id.txtError).setVisibility(View.VISIBLE);
            } else if (ConnectActivity.idUser.equals("Comptable")) {
                textView.setText("Cette application n'est pas destinée au comptable");
                findViewById(R.id.txtError).setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("http://www.hagneva1ppe.fr/controleurs/appConnect.php");
                String login = params[0];
                String pwd = params[1];

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = "login="+login+"&mdp="+pwd;
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                idUser = idUser + line;

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                ConnectActivity.idUser = this.idUser;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}