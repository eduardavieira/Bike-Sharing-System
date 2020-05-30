package messages;

import jade.core.AID;

public class ai implements java.io.Serializable{

	private int bikes;
	private AID id_ai;
	private String type;
	private float posX;
	private float posY;

	public ai(int bikes, AID id_ai, String type, float aiPosX, float aiPosY) {
	
		
		this.bikes = bikes;
		this.id_ai = id_ai;
		this.type = type;
		this.posX=aiPosX;
		this.posY=aiPosY;
	}
	
	public float getposX() {
		return posX;
	}


	public void setposX(float posX) {
		this.posX = posX;
	}
	
	public float getposY() {
		return posY;
	}


	public void setposY(float posY) {
		this.posY = posY;
	}
	
	public int getBikes() {
		return bikes;
	}


	public void setBikes(int bikes) {
		this.bikes = bikes;
	}
	
	public AID getId_ai() {
		return id_ai;
	}

	public void setId_ae(AID id_ai) {
		this.id_ai = id_ai;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}