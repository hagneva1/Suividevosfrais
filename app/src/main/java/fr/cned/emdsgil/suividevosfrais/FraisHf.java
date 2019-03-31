package fr.cned.emdsgil.suividevosfrais;

import java.io.Serializable;

/**
 * Classe métier contenant la description d'un frais hors forfait
 *
 */
class FraisHf  implements Serializable {

	private final Integer id;
	private final Float montant ;
	private final String motif ;
	private final Integer jour ;
	
	public FraisHf(Float montant, String motif, Integer jour, Integer id) {
		this.montant = montant ;
		this.motif = motif ;
		this.jour = jour ;
		this.id = id;
	}

	public Float getMontant() {
		return montant;
	}

	public String getMotif() {
		return motif;
	}

	public Integer getJour() {
		return jour;
	}

	public Integer getId() { return id; }

}
