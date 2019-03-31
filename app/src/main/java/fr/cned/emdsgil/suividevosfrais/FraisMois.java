package fr.cned.emdsgil.suividevosfrais;

import android.os.AsyncTask;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Classe métier contenant les informations des frais d'un mois
 */
class FraisMois implements Serializable {

    private String id; //idUser
    private Integer mois; // mois concerné
    private Integer annee; // année concernée
    private Integer etape; // nombre d'étapes du mois
    private String typeVehicule; // type du véhicule
    private Integer km; // nombre de km du mois
    private Integer nuitee; // nombre de nuitées du mois
    private Integer repas; // nombre de repas du mois
    private final ArrayList<FraisHf> lesFraisHf; // liste des frais hors forfait du mois

    public FraisMois(String id, Integer annee, Integer mois) {
        lesFraisHf = new ArrayList<>();
        GetData getData = new GetData();
        getData.execute(id, annee.toString(), mois.toString());
        /* Retrait du type de l'ArrayList (Optimisation Android Studio)
         * Original : Typage explicit =
         * lesFraisHf = new ArrayList<FraisHf>() ;
         */
    }

    /**
     * Ajout d'un frais hors forfait
     *
     * @param montant Montant en euros du frais hors forfait
     * @param motif   Justification du frais hors forfait
     */
    public void addFraisHf(Float montant, String motif, Integer jour, Integer id) {
        lesFraisHf.add(new FraisHf(montant, motif, jour, id));
    }

    /**
     * Suppression d'un frais hors forfait
     *
     * @param index Indice du frais hors forfait à supprimer
     */
    public void supprFraisHf(Integer index) {
        lesFraisHf.remove(index);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMois() {
        return mois;
    }

    public void setMois(Integer mois) {
        this.mois = mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getEtape() {
        return etape;
    }

    public void setEtape(Integer etape) {
        this.etape = etape;
    }

    public String getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(String type) {
        this.typeVehicule = type;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Integer getNuitee() {
        return nuitee;
    }

    public void setNuitee(Integer nuitee) {
        this.nuitee = nuitee;
    }

    public Integer getRepas() {
        return repas;
    }

    public void setRepas(Integer repas) {
        this.repas = repas;
    }

    public ArrayList<FraisHf> getLesFraisHf() {
        return lesFraisHf;
    }

    public void register(FraisMois fraisMois) {
        SendData sendData = new SendData();
        sendData.execute(fraisMois);
    }

    public class SendData extends AsyncTask<FraisMois, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(FraisMois... params) {
            try {

                URL url = new URL("https://www.hagneva1ppe.fr/controleurs/appGetData.php");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                ArrayList<FraisMois> unFraisMois = new ArrayList<>();
                unFraisMois.add(params[0]);
                String data = new Gson().toJson(unFraisMois);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();
                inputStream.close();
                bufferedReader.close();

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetData extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                URL url = new URL("https://www.hagneva1ppe.fr/controleurs/appSendData.php");

                String id = params[0];
                String annee = params[1];
                String mois = params[2];
                if (Integer.parseInt(mois) < 10) {
                    mois = annee+"0"+mois;
                } else {
                    mois = annee+mois;
                }

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = "id="+id+"&mois="+mois;
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                line = bufferedReader.readLine();

                JSONObject jsonObject = new JSONObject(line);
                setId(id);
                setMois(Integer.parseInt(mois.substring(4))); // mois concerné
                setAnnee(Integer.parseInt(mois.substring(0,4)));
                setEtape(Integer.parseInt(jsonObject.getString("ETP"))); // nombre d'étapes du mois
                setTypeVehicule(jsonObject.getString("typeVehicule")); // type du véhicule
                setKm(Integer.parseInt(jsonObject.getString("km"))); // nombre de km du mois
                setNuitee(Integer.parseInt(jsonObject.getString("NUI"))); // nombre de nuitées du mois
                setRepas(Integer.parseInt(jsonObject.getString("REP"))); // nombre de repas du mois
                JSONArray fraisHf = jsonObject.getJSONArray("lesFraisHF");
                for (int i = 0 ; i < fraisHf.length(); i++) {
                    JSONObject subset = fraisHf.getJSONObject(i);
                    addFraisHf(Float.parseFloat(subset.getString("montant")),
                            subset.getString("motif"),
                            Integer.parseInt(subset.getString("date").substring(0, 2)),
                            Integer.parseInt(subset.getString("id")));
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

