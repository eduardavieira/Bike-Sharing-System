package messages;

import jade.core.AID;

public class incentivo implements java.io.Serializable {
	private double incentivo;

	public incentivo(double incentivo) {
		super();
		this.incentivo = incentivo;
	}

	public double getIncentivo() {
		return incentivo;
	}

	public void setIncentivo(double incentivo) {
		this.incentivo = incentivo;
	}
	

}