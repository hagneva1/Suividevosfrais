package fr.cned.emdsgil.suividevosfrais;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Date;

public class HfActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hf);
        setTitle("GSB : Frais HF");
        // modification de l'affichage du DatePicker
        Global.changeAfficheDate((DatePicker) findViewById(R.id.datHf), true) ;
		// mise à 0 du montant
		((EditText)findViewById(R.id.txtHf)).setText("0") ;
        // chargement des méthodes événementielles
		imgReturn_clic() ;
		cmdAjouter_clic() ;
		((DatePicker)findViewById(R.id.datHf)).setMaxDate(new Date().getTime());
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
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgHfReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
    			retourActivityPrincipale() ;    		
    		}
    	}) ;
    }

    /**
     * Sur le clic du bouton ajouter : enregistrement dans la liste et sérialisation
     */
    private void cmdAjouter_clic() {
    	findViewById(R.id.cmdHfAjouter).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
				// récupération des informations saisies
				Integer annee = ((DatePicker)findViewById(R.id.datHf)).getYear() ;
				Integer mois = ((DatePicker)findViewById(R.id.datHf)).getMonth() + 1 ;
				Integer jour = ((DatePicker)findViewById(R.id.datHf)).getDayOfMonth() ;
				Float montant = Float.valueOf((((EditText)findViewById(R.id.txtHf)).getText().toString()));
				String motif = ((EditText)findViewById(R.id.txtHfMotif)).getText().toString() ;
                Calendar now = Calendar.getInstance();
                if (now.get(Calendar.YEAR) == annee && now.get(Calendar.MONTH) == mois -1) {
                    FraisMois fraisMois = new FraisMois(ConnectActivity.idUser, annee, mois);
                    fraisMois.addFraisHf(montant, motif, jour, -1);
                    fraisMois.register(fraisMois);
                    retourActivityPrincipale() ;
                } else {
                    findViewById(R.id.txtErrorHf).setVisibility(View.VISIBLE);
                }
    		}
    	}) ;    	
    }

	/**
	 * Retour à l'activité principale (le menu)
	 */
	private void retourActivityPrincipale() {
		Intent intent = new Intent(HfActivity.this, MainActivity.class) ;
		startActivity(intent) ;   					
	}
}
