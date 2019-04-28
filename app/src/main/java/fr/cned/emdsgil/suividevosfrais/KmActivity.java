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

public class KmActivity extends AppCompatActivity {

	// informations affichées dans l'activité
	private Integer annee ;
	private Integer mois ;
	private Integer qte ;
	private FraisMois fraisMois;
	private String typeVehicule;
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_km);
        setTitle("GSB : Frais Km");
		// modification de l'affichage du DatePicker
		Global.changeAfficheDate((DatePicker) findViewById(R.id.datKm), false) ;
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
		((DatePicker)findViewById(R.id.datKm)).setMaxDate(new Date().getTime());
		annee = ((DatePicker)findViewById(R.id.datKm)).getYear() ;
		mois = ((DatePicker)findViewById(R.id.datKm)).getMonth() + 1 ;
        //Remplissage de la liste des véhicules
        spinner = findViewById(R.id.spnVeh);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_vehicule, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
		// récupération de la qte et du vehicule correspondant au mois actuel
		qte = 0 ;
		fraisMois = new FraisMois(ConnectActivity.idUser, annee, mois);
        TimeUnit.MILLISECONDS.sleep(1000);
		qte = fraisMois.getKm();
		typeVehicule = fraisMois.getTypeVehicule();
		switch (typeVehicule) {
            case "E4":
                spinner.setSelection(2);
                break;
            case "E5":
                spinner.setSelection(3);
                break;
            case "D4":
                spinner.setSelection(0);
                break;
            case "D5":
                spinner.setSelection(1);
                break;
        }
		Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == annee && now.get(Calendar.MONTH) == mois -1) {
			findViewById(R.id.txtKm).setEnabled(true);
			findViewById(R.id.cmdKmMoins).setEnabled(true);
			findViewById(R.id.cmdKmPlus).setEnabled(true);
			findViewById(R.id.cmdKmValider).setEnabled(true);
		} else {
			findViewById(R.id.txtKm).setEnabled(false);
			findViewById(R.id.cmdKmMoins).setEnabled(false);
			findViewById(R.id.cmdKmPlus).setEnabled(false);
			findViewById(R.id.cmdKmValider).setEnabled(false);
		}
		((EditText)findViewById(R.id.txtKm)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
	}
	
	/**
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgKmReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
    			retourActivityPrincipale() ;    		
    		}
    	}) ;
    }

    /**
     * Sur le clic du bouton valider : sérialisation
     */
    private void cmdValider_clic() {
    	findViewById(R.id.cmdKmValider).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    		    qte = Integer.parseInt(((EditText)findViewById(R.id.txtKm)).getText().toString());
    		    Integer pos = spinner.getSelectedItemPosition();
    		    switch (pos) {
                    case 0:
                        typeVehicule = "D4";
                        break;
                    case 1:
                        typeVehicule = "D5";
                        break;
                    case 2:
                        typeVehicule = "E4";
                        break;
                    case 3:
                        typeVehicule = "E5";
                        break;
                }
    			fraisMois.setKm(qte);
    		    fraisMois.setTypeVehicule(typeVehicule);
				fraisMois.register(fraisMois);
                retourActivityPrincipale() ;
            }
    	}) ;    	
    }
    
    /**
     * Sur le clic du bouton plus : ajout de 10 dans la quantité
     */
    private void cmdPlus_clic() {
    	findViewById(R.id.cmdKmPlus).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			qte+=10 ;
                ((EditText)findViewById(R.id.txtKm)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
    		}
    	}) ;    	
    }
    
    /**
     * Sur le clic du bouton moins : enlève 10 dans la quantité si c'est possible
     */
    private void cmdMoins_clic() {
    	findViewById(R.id.cmdKmMoins).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
   				qte = Math.max(0, qte-10) ; // suppression de 10 si possible
                ((EditText)findViewById(R.id.txtKm)).setText(String.format(Locale.FRANCE, "%d", qte)) ;
     		}
    	}) ;    	
    }
    
    /**
     * Sur le changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {   	
    	final DatePicker uneDate = (DatePicker) findViewById(R.id.datKm);
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
		Intent intent = new Intent(KmActivity.this, MainActivity.class) ;
		startActivity(intent) ;   					
	}
}
