package hibernate;


public class MyItem {
	 private int idItem;
	    private String nombre;
	    private int WearLocation;
	    private int bonusSalud;
	    private int bonusEnergia;
	    private int bonusFuerza;
	    private int bonusDestreza;
	    private int bonusInteligencia;
	    private String foto;
	    private String fotoEquipado;
	    
	    public int getIdItem() {
		return idItem;
	    }

	    public void setIdItem(final int idItem) {
		this.idItem = idItem;
	    }

	    public String getNombre() {
		return nombre;
	    }


	    public void setNombre(final String nombre) {
		this.nombre = nombre;
	    }


	    public int getWearLocation() {
		return WearLocation;
	    }


	    public void setWearLocation(final int WearLocation) {
		this.WearLocation = WearLocation;
	    }


	    public int getBonusSalud() {
		return bonusSalud;
	    }


	    public void setBonusSalud(final int bonusSalud) {
		this.bonusSalud = bonusSalud;
	    }


	    public int getBonusEnergia() {
		return bonusEnergia;
	    }


	    public void setBonusEnergia(final int bonusEnergia) {
		this.bonusEnergia = bonusEnergia;
	    }

	    public int getBonusFuerza() {
		return bonusFuerza;
	    }


	    public void setBonusFuerza(final int bonusFuerza) {
		this.bonusFuerza = bonusFuerza;
	    }


	    public int getBonusDestreza() {
		return bonusDestreza;
	    }


	    public void setBonusDestreza(final int bonusDestreza) {
		this.bonusDestreza = bonusDestreza;
	    }


	    public int getBonusInteligencia() {
		return bonusInteligencia;
	    }

	  
	    public void setBonusInteligencia(final int bonusInteligencia) {
		this.bonusInteligencia = bonusInteligencia;
	    }


	    public String getFoto() {
		return foto;
	    }

	 
	    public void setFoto(final String foto) {
		this.foto = foto;
	    }


	    public String getFotoEquipado() {
		return fotoEquipado;
	    }


	    public void setFotoEquipado(final String fotoEquipado) {
		this.fotoEquipado = fotoEquipado;
	    }

}
