package fr.cned.emdsgil.suividevosfrais;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker.OnDateChangedListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EtapeActivity extends AppCompatActivity {
    // informations affichées dans l'activité
    private Integer annee ;
    private Integer mois ;
    private Integer qte ;
    private FraisMois fraisMois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etape);
        setTitle("GSB : Frais Etape");
        // modification de l'affichage du DatePicker
        Global.changeAfficheDate((DatePicker) findViewById(R.id.datEtape), false) ;
        // valorisation des propriétés
        try {
            valoriseProprietes() ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // chargement des méthodes événementielles
        imgReturn_clic() ;
        cmdValider_clic() ;
        cmdPlus_clic() ;
        cmdMoins_clic() ;
        dat_clic() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Valorisation des propriétés avec les informations affichées
     */
    private void valoriseProprietes() throws InterruptedException {
        ((DatePicker)findViewById(R.id.datEtape)).setMaxDate(new Date().getTime());
        annee = ((DatePicker)findViewById(R.id.datEtape)).getYear() ;
        mois = ((DatePicker)findViewById(R.id.datEtape)).getMonth() + 1 ;
        // récupération de la qte et du vehicule correspondant au mois actuel
        qte = 0 ;
        fraisMois = new FraisMois(ConnectActivity.idUser, annee, mois);
        TimeUnit.MILLISECONDS.sleep(1000);
        qte = fraisMois.getEtape();
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == annee && now.get(Calendar.MONTH) == mois -1) {
            findViewById(R.id.txtEtape).setEnabled(true);
            findViewById(R.id.cmdEtapeMoins).setEnabled(true);
            findViewById(R.id.cmdEtapePlus).setEnabled(true);
            findViewById(R.id.cmdEtapeValider).setEnabled(true);
        } else {
            findViewById(R.id.txtEtape).setEnabled(false);
            findViewById(R.id.cmdEtapeMoins).setEnabled(false);
            findViewById(R.id.cmdEtapePlus).setEnabled(false);
            findViewById(R.id.cmdEtapeValider).setEnabled(false);
        }
        ((EditText)findViewById(R.id.txtEtape)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
    }

    /**
     * Sur la selection de l'image : retour au menu principal
     */
    private void imgReturn_clic() {
        findViewById(R.id.imgEtapeReturn).setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                retourActivityPrincipale() ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton valider : sérialisation
     */
    private void cmdValider_clic() {
        findViewById(R.id.cmdEtapeValider).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte = Integer.parseInt(((EditText)findViewById(R.id.txtEtape)).getText().toString());
                fraisMois.setEtape(qte);
                fraisMois.register(fraisMois);
                retourActivityPrincipale() ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton plus : ajout de 10 dans la quantité
     */
    private void cmdPlus_clic() {
        findViewById(R.id.cmdEtapePlus).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte+=1 ;
                ((EditText)findViewById(R.id.txtEtape)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
            }
        }) ;
    }

    /**
     * Sur le clic du bouton moins : enlève 10 dans la quantité si c'est possible
     */
    private void cmdMoins_clic() {
        findViewById(R.id.cmdEtapeMoins).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte = Math.max(0, qte-1) ; // suppression de 10 si possible
                ((EditText)findViewById(R.id.txtEtape)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
            }
        }) ;
    }

    /**
     * Sur le changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {
        final DatePicker uneDate = (DatePicker) findViewById(R.id.datEtape);
        uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new OnDateChangedListener(){
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    valoriseProprietes() ;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(EtapeActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }
}
