package Agents;

import java.io.IOException;
import java.util.Random;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import messages.ai;
import messages.coordenadas_ae;
import messages.incentivo;

public class AE extends Agent{
	int pos_x, pos_y, bikes,bikes_expected;
	AID id_ae;
	private AID user;
	Integer contar=1;
	/*
	->Agente estacao
	*/
	
	@Override
	protected void setup() {
		super.setup();
		
		// Agent can get arguments
		Object[] args = getArguments();
		pos_x = (int) args[0]; 
		pos_y = (int) args[1];
		
		// Coordenadas da Estação, ID, Número de Bicicletas disponíveis
		Random rand = new Random();
		id_ae=getAID();
		bikes=rand.nextInt(8)+4; //minimo de bicicletas = 7
		
		
		// Registar Station nas Páginas Amarelas
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("AE");
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new INIT_AI());
		this.addBehaviour(new Receiver());
	}
	

	private class INIT_AI extends OneShotBehaviour
	{
		
		//envio de informacao ao agente interface
		//envia numero de bicicletas inicialmente
		public void action()
		{
			AID receiver = new AID();
			receiver.setLocalName("AI");
			ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
			ai info = new ai(bikes, id_ae, "INIT", 0, 0 );
			mensagem.addReceiver(receiver);
			try {
				mensagem.setContentObject(info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myAgent.send(mensagem);
		}

		
	}
	
	
private class Receiver extends CyclicBehaviour {
	private String type;
	public void action() {
		ACLMessage msg = receive();
		if (msg != null) {
			
			// Está a receber o pedido para informar sobre número de bicicletas e coordenadas
			if (msg.getPerformative() == ACLMessage.REQUEST &&  msg.getContent().contentEquals("infoEstacoes")) {
				ACLMessage response = msg.createReply();
				response.setPerformative(ACLMessage.INFORM);
				int discount=CalculoIncentivo1();
				int a=5;
				int b=5;
				coordenadas_ae s = new coordenadas_ae(pos_x,pos_y,bikes,discount,id_ae,"infoEstacoes");
				try {
					response.setContentObject(s);
					send(response);
				} catch(IOException e) {
					e.printStackTrace();}
				}
			
			// Está a receber informação que User alugou uma bicicleta da sua estação
			else if (msg.getPerformative() == ACLMessage.CONFIRM &&  msg.getContent().contentEquals("estacaoEscolhida")) {
				
				System.out.println("AE: AU alugou bicicleta");
				bikes=bikes-1;
				
				//informar o agente interface que tem menos uma bicicleta
				AID receiver = new AID();
				receiver.setLocalName("AI");
				ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
				ai info = new ai(bikes, id_ae, "Bicicletas", 0,  0);
				mensagem.addReceiver(receiver);
				try {
					mensagem.setContentObject(info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myAgent.send(mensagem);
				
				
			}
			
			// User chegou aos 3/4 do caminho
			else if(msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().contentEquals("getIncentivos")) {
				System.out.println("AE: Recebi um pedido de incentivo");
				
				double incent=CalculoIncentivo();
				
				ACLMessage response = msg.createReply();
				response.setPerformative(ACLMessage.PROPOSE);
				incentivo incen = new incentivo(incent);
				try {
					response.setContentObject(incen);
					send(response);
				} catch(IOException e) {
					e.printStackTrace();}
				}
			
			else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				
				//é estação escolhida
				System.out.println("AE: Estação aceite");
				bikes_expected++;
				
			}
			
			else if(msg.getPerformative() == ACLMessage.CONFIRM) {
				
				//AU chegou
				System.out.println("AE: Recebi o " + msg.getSender().getLocalName() + "\n");
				bikes++;
				bikes_expected-=1;
				
				//informar a alteracao de bicicletas ao agente interface
				AID receiver = new AID();
				receiver.setLocalName("AI");
				ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
				ai info = new ai(bikes, id_ae, "Bicicletas", 0, 0 );
				mensagem.addReceiver(receiver);
				try {
					mensagem.setContentObject(info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myAgent.send(mensagem);
				
				
			}
		}
		else {
			block();
			}

	}

}
	

public double CalculoIncentivo(){
    
    //Incentivo é dado pelo número de bicicletas que tem
    double discount, empty_spaces, discount_per_space;
    empty_spaces=12-bikes; // numero de bicicletas = 12 - Neste caso o incentivo será zero
    discount_per_space=1.0 / 24.0 ;
    discount= empty_spaces*discount_per_space*100;
    System.out.println("AE:" + " Desconto a enviar ("+ discount +")" );
    return discount;
}

public int CalculoIncentivo1(){
    
    //Incentivo é dado pelo número de bicicletas que tem
    int discount, discount_per_bike;
    discount_per_bike=1/24;
    discount=bikes*discount_per_bike*100;
    
    return discount;
}

}



