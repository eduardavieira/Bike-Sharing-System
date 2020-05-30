package messages;

import jade.core.AID;
/*
 Informação fornecida ao AU quando este pertende alugar uma bicicleta
 */
public class coordenadas_ae implements java.io.Serializable {
	private int pos_x, pos_y, bikes, discount;
	private AID id_ae;
	private String type;

	

	public coordenadas_ae(int pos_x, int pos_y, int bikes, int discount, AID id_ae, String type) {
		super();
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.bikes = bikes;
		this.discount = discount;
		this.id_ae = id_ae;
		this.type = type;
	}


	public int getBikes() {
		return bikes;
	}


	public void setBikes(int bikes) {
		this.bikes = bikes;
	}


	public int getPos_x() {
		return pos_x;
	}

	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}

	public int getPos_y() {
		return pos_y;
	}

	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}

	public AID getId_ae() {
		return id_ae;
	}

	public void setId_ae(AID id_ae) {
		this.id_ae = id_ae;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public int getDiscount() {
		return discount;
	}


	public void setDiscount(int discount) {
		this.discount = discount;
	}
	
	
	

}