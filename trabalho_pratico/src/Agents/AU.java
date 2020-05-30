package Agents;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import messages.ai;
import messages.coordenadas_ae;
import messages.incentivo;



public class AU extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long start, end;
	private String type,s;
	private int pos_x,pos_y, bikes, x_ori, y_ori, lastDistance, numero,x_dest, y_dest;
	private AID id_ae,choosenAE;
	private int  numAE ;
	private HashMap<AID, String> locations = new HashMap<AID, String>();
	private HashMap<AID, String> iniDist = new HashMap<AID, String>();
	
	private int pressa;
	private int distTarget;
	
	private int Comprimento;
	private float x_inst;
	private float m;
	
	private float b;  
	private float y_inst; 
	
	private int pos34;
	private double deltaX;
	private double deltaY;
	private String nome;
	private int x_est;
	private int y_est;
	private int APE;
	private int distEstacao;
	
	private float x_34, y_34,xdest_34,ydest_34;
	
	AID target = getAID();
	
	double betterIcent;
	
	private int estacoesIn=0;
	private int numberRe=0;
	private int discount=0;
	
	float aiPosY=0;
	float aiPosX=0;
	@Override
	protected void setup() {
		lastDistance=1000;
		numero=0;
		super.setup();
		start = (Calendar.getInstance()).getTimeInMillis();
		
		//------Calcular coordenadas inicias/Destino Aleatoriamente------
		Random rand = new Random();
        x_ori = rand.nextInt(100);
        y_ori = rand.nextInt(100);
        x_dest = rand.nextInt(100);
        y_dest = rand.nextInt(100);
        
        //definir pressa, se um o utiizador tem pressa se zero pondera descontos 
        pressa=rand.nextInt(2);
        
        nome=getAID().getLocalName();
        
        
       
        
		System.out.println("User "+getAID().getLocalName()+" starting.");
		//this.addBehaviour(new INIT_AI());
		this.addBehaviour(new pedeInfoEstacoes());
		this.addBehaviour(new receberInfoEstacoes());
		
	}
	//Para enviar informacoes ao agente interface (nao usado diretamente)
	private class INIT_AI extends OneShotBehaviour
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action()
		{
			AID receiver = new AID();
			receiver.setLocalName("AI");
			ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
			ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
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

	//mandar mensagem a pedir informcoes as estacoes
	private class PedirInfo extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AID AE;

		public PedirInfo(AID auAID) {
			this.AE = auAID;
		}

		@Override
		public void action() {

			ACLMessage mensagem = new ACLMessage(ACLMessage.REQUEST);
			mensagem.addReceiver(AE);
			mensagem.setContent("infoEstacoes");
			myAgent.send(mensagem);

		}

	}
	
	
	//procura todos os agentes estacoes e pede informacoes ate que todas sejam contactadas
	private class pedeInfoEstacoes extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {		

			// Time to contact all AEs
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("AE");
			template.addServices(sd);

			DFAgentDescription[] result;

			try {
				result = DFService.search(myAgent, template);
				AID[] AE;
				AE = new AID[result.length];
				numAE = result.length;
				ParallelBehaviour pb = new ParallelBehaviour(myAgent, ParallelBehaviour.WHEN_ALL) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public int onEnd() {
						System.out.println(nome + ": All AEs inquired.");
						return super.onEnd();
					}
				};
				myAgent.addBehaviour(pb);
				for (int i = 0; i < numAE; ++i) {
					AE[i] = result[i].getName();
					pb.addSubBehaviour(new PedirInfo(AE[i]));
					
					
				}

			} catch (FIPAException e) {
				e.printStackTrace();

			}
		}
		}
	
	
	//ciclo para receber informacoes
	private class receberInfoEstacoes extends CyclicBehaviour {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {
			
			ACLMessage msg = receive();
			// verifica que tipo de mensagem recebeu
			if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
				try {
					type=((coordenadas_ae)msg.getContentObject()).getType(); //tipo de informacao recebida			
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				//verifica o tipo da mensagem "type"
				//esta parte fara com que seja escolhida a estacao inicial dependendo da pressa
				//		 da distancia a cada estacao e do incentivo
				if (type.equals("infoEstacoes")) {
					numero=numero +1;
					try {
						pos_x=((coordenadas_ae)msg.getContentObject()).getPos_x(); //tipo de informacao recebida	
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						pos_y=((coordenadas_ae)msg.getContentObject()).getPos_y(); //tipo de informacao recebida
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						bikes=((coordenadas_ae)msg.getContentObject()).getBikes(); //tipo de informacao recebida	
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						id_ae=((coordenadas_ae)msg.getContentObject()).getId_ae(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						discount=((coordenadas_ae)msg.getContentObject()).getDiscount(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
					int distance = (int) Math
                            .sqrt(((Math.pow((pos_x - x_ori), 2)) + (Math.pow((pos_y - y_ori), 2))));
					s = Integer.toString(pos_x) +"," + Integer.toString(pos_y);
	                locations.put(id_ae, s);
	               			
	                if (bikes>0) {
						
	                	iniDist.put(id_ae, distance+","+discount+","+pos_x+","+pos_y); //guardar a distancia do utilizador para cada estação e coordenadas
	                }
	                
					
					}
					if (numero == numAE) {
						Integer minimum =999999;
						double firstDiscont=0;
						double savedDiscont=0;						
						for(Map.Entry<AID, String> entry : iniDist.entrySet()) {
						    AID key = entry.getKey();
						    String values = entry.getValue();
						    String[] parts = values.split(",");
						    if (Integer.parseInt(parts[0]) < minimum) {
						    	minimum=Integer.parseInt(parts[0]);
						    	choosenAE=key;
						    	pos_x=Integer.parseInt(parts[2]);
					    		pos_y=Integer.parseInt(parts[3]);
						    	savedDiscont=Integer.parseInt(parts[1]);
						    }
						    
						    
						}
						
						if (pressa==0) {
							for(Map.Entry<AID, String> entry2 : iniDist.entrySet()) {
							    AID key2 = entry2.getKey();
							    String value2 = entry2.getValue();
							    String[] parts2 = value2.split(",");
							    int value =Integer.parseInt(parts2[0]);
							    int aceitavel = minimum + 13; //aceitavel andar mais 13 se houver um melhor desconto 
							    
							    if (value <= aceitavel) {
							    	if (firstDiscont>=savedDiscont) {
							    		savedDiscont=firstDiscont;
							    		choosenAE=key2;
							    		pos_x=Integer.parseInt(parts2[2]);
							    		pos_y=Integer.parseInt(parts2[3]);
							    	}
							    }
						    }
						}
							
						x_ori=pos_x;  
						y_ori=pos_y;
						ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
						response.addReceiver(choosenAE);
						response.setContent("estacaoEscolhida");
						send(response);
						try {
							System.out.println(nome +": A bicicleta está na estação "+ choosenAE.getLocalName());
							addBehaviour(new EstacaoTarget());
						}
						 catch (Exception e) {
							System.out.println("Não existem Bicicletas no Mundo");
						}
							
					
					
				
			
		}
			}
			
			//recebe varias propostas/descontos e escolhe a que prefere consoante a pressa
			if (msg != null && msg.getPerformative() == ACLMessage.PROPOSE) {
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double incentivo=0.0;
				int minorDist = 999999;
				try {
					 incentivo=((incentivo)msg.getContentObject()).getIncentivo(); //incentivo recebido			
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
				System.out.println(nome +": Incentivo Recebido: "+incentivo +" da estação "+ msg.getSender().getLocalName() );
				
				if (pressa==1) {
					String val=locations.get(msg.getSender());
					String[] coords =  val.split(",");
					x_dest = Integer.parseInt(coords[0]);
					y_dest= Integer.parseInt(coords[1]);
					
					
					distTarget = (int) Math
							.sqrt(((Math.pow((x_inst - x_dest), 2)) + (Math.pow((y_inst - y_dest), 2))));
					if (distTarget<minorDist){
						minorDist=distTarget;
						target=msg.getSender();
					}
				}if (pressa==0) {
					if (incentivo>betterIcent) {
						betterIcent=incentivo;
						target=msg.getSender();
					}
				}
				numberRe++;
				
				if (numberRe>=estacoesIn) {
					System.out.println(nome +": Escolhi a estação "+ target.getLocalName());
					ACLMessage response = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					response.addReceiver(target);
					response.setContent("estacaoTarget");
					send(response);
					
					
					
					
					String val=locations.get(target);
					String[] coords =  val.split(",");
					xdest_34 = Integer.parseInt(coords[0]);
					ydest_34= Integer.parseInt(coords[1]);
					
		
					
					int Comprimento = (int) Math
							.sqrt(((Math.pow((x_34 - xdest_34), 2)) + (Math.pow((y_34 - ydest_34), 2))));
					
				
					float x_inst = x_34;
					float m= (float) ( (float) ydest_34 - (float) y_34  ) / ((float)  xdest_34 - (float) x_34);
				
					
					
					float b = (float) y_34  - m * (float) x_34;  
					float y_inst= m*x_inst + b; //equacao da nova reta
					
				
					double deltaX= xdest_34-x_inst;
					double deltaY= ydest_34-y_inst;
				
					System.out.println(nome +": Coordenadas de partida: ("+x_34+","+y_34+"), Coordenadas de destino: ("+ xdest_34 +","+ydest_34 + ")");
					int n=0;
					//simula que o utilizador anda em linha reta ate a estacao que ira entregar a bicicleta
					while(1==1) {
						
						if (Math.abs(deltaX)>=Math.abs(deltaY)) {
							if(deltaX>0) {
								while (1==1) {
									x_inst++;
									y_inst= m*x_inst + b;
									aiPosY=y_inst;
									aiPosX=x_inst;
									
									AID receiver = new AID();
									receiver.setLocalName("AI");
									ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
									ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
									mensagem.addReceiver(receiver);
									try {
										mensagem.setContentObject(info);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myAgent.send(mensagem);
									
									System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
									int distPercorrida = (int) Math
											.sqrt(((Math.pow((x_34 - x_inst), 2)) + (Math.pow((y_34-y_inst ), 2))));
									
									try {
										Thread.sleep(700);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (distPercorrida>=Comprimento) {
										
										addBehaviour(new fim());
										
										
										break;}
								}
							}
							
							if(deltaX<0) {
								while (1==1) {
									x_inst-=1;
									y_inst= m*x_inst + b;
									aiPosY=y_inst;
									aiPosX=x_inst;

									AID receiver = new AID();
									receiver.setLocalName("AI");
									ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
									ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
									mensagem.addReceiver(receiver);
									try {
										mensagem.setContentObject(info);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myAgent.send(mensagem);
									
									System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
									int distPercorrida = (int) Math
											.sqrt(((Math.pow((x_34 - x_inst), 2)) + (Math.pow((y_34-y_inst ), 2))));
									try {
										Thread.sleep(700);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (distPercorrida>=Comprimento) {
										
										addBehaviour(new fim());
										
										break;}
								}
							}
						}
						
						if (Math.abs(deltaX)<Math.abs(deltaY)) {
							
							if(deltaY>0) {
								while (1==1) {
									y_inst++;
									x_inst= (y_inst-b )/ m ;
									aiPosY=y_inst;
									aiPosX=x_inst;

									AID receiver = new AID();
									receiver.setLocalName("AI");
									ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
									ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
									mensagem.addReceiver(receiver);
									try {
										mensagem.setContentObject(info);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myAgent.send(mensagem);
									
									System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
									int distPercorrida = (int) Math
											.sqrt(((Math.pow((x_34 - x_inst), 2)) + (Math.pow((y_34-y_inst ), 2))));
									try {
										Thread.sleep(700);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (distPercorrida>=Comprimento) {
										
										addBehaviour(new fim());
										
										break;}
								}
							}
							
							if(deltaY<0) {
								while (1==1) {
									y_inst-=1;
									x_inst= (y_inst-b )/ m ;
									aiPosY=y_inst;
									aiPosX=x_inst;
									
									AID receiver = new AID();
									receiver.setLocalName("AI");
									ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
									ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
									mensagem.addReceiver(receiver);
									try {
										mensagem.setContentObject(info);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									myAgent.send(mensagem);
									
									System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
									int distPercorrida = (int) Math
											.sqrt(((Math.pow((x_34 - x_inst), 2)) + (Math.pow((y_34-y_inst ), 2))));
									try {
										Thread.sleep(700);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (distPercorrida>=Comprimento) {
										
										addBehaviour(new fim());
										
										break;}
								}
							}
							
						}
						break;
					}
						
					
					
				}
				
			//end of proprose	
			}
	}
	}

	private class EstacaoTarget extends OneShotBehaviour {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {
			
			//simulacao que o utilizador anda desde a estacao ate ao destino, masi concretamente,
			//ate a posicao 3/4 em linha reta
			//Note-se que foram considerados varias situacoes
			//para que o utilizador chegue sempre ate onde pretende
			//e para que nao chegue la instantaneamente 
			
			int Comprimento = (int) Math
					.sqrt(((Math.pow((x_ori - x_dest), 2)) + (Math.pow((y_ori - y_dest), 2))));
			float x_inst = x_ori;
			float m= (float) ( (float) y_dest - (float) y_ori  ) / ((float)  x_dest - (float) x_ori);
			
			float b = (float) y_ori  - m * (float) x_ori;  
			float y_inst= m*x_inst + b; //equacao da reta, pois supoe-se que o utilizador andara em linha reta
			
			int pos34=Comprimento*1/4;
			double deltaX= x_dest-x_inst;
			double deltaY= y_dest-y_inst;
			
			int x_est;
			int y_est;
			
			int distEstacao;
			APE=12; //TODO : Change value os area de proximidade
			System.out.println(nome +": Coordenadas de partida: ("+x_ori+","+y_ori+"), Coordenadas de destino: ("+ x_dest +","+y_dest + ")");
			while (1==1) {
			if (Math.abs(deltaX)>=Math.abs(deltaY)) {
				
				if(deltaX>0) {
					while (1==1) {
						x_inst++;
						y_inst= m*x_inst + b;
						aiPosY=y_inst;
						aiPosX=x_inst;

						AID receiver = new AID();
						receiver.setLocalName("AI");
						ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
						ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
						mensagem.addReceiver(receiver);
						try {
							mensagem.setContentObject(info);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myAgent.send(mensagem);
						
						System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
						int distInst = (int) Math
								.sqrt(((Math.pow((x_inst - x_dest), 2)) + (Math.pow((y_inst - y_dest), 2))));
						try {
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (pos34>=distInst) {
							x_34=x_inst;
							y_34=y_inst;
							//mandar mensagem aos AE 
							
							
							
							System.out.println(nome +": Cheguei a 3/4 do caminho");
							for (Map.Entry me : locations.entrySet()) {
								String[] parts = ((String) me.getValue()).split(",");
								
								x_est = Integer.parseInt(parts[0]);
								y_est= Integer.parseInt(parts[1]);
								
								distEstacao = (int) Math
										.sqrt(((Math.pow((x_inst - x_est), 2)) + (Math.pow((y_inst - y_est), 2))));
								
								if (distEstacao < APE) {
									ACLMessage mensagem2 = new ACLMessage(ACLMessage.REQUEST);
									mensagem2.addReceiver((AID) me.getKey());
									mensagem2.setContent("getIncentivos");
									myAgent.send(mensagem2);
									System.out.println(nome +": Pedido de incentivo enviado");
									estacoesIn++;
								}
								if (estacoesIn==0) {
									
									APE=24;
									if (distEstacao < APE) {
										ACLMessage mensagem3 = new ACLMessage(ACLMessage.REQUEST);
										mensagem3.addReceiver((AID) me.getKey());
										mensagem3.setContent("getIncentivos");
										myAgent.send(mensagem3);
										System.out.println(nome +": Pedido de incentivo enviado");
										estacoesIn++;
									}
								}
								
						        
						        }
							break;
							
						}
					}
					
				}if(deltaX<0) {
					while (1==1) {
						x_inst-=1;
						y_inst= m*x_inst + b;
						aiPosY=y_inst;
						aiPosX=x_inst;

						AID receiver = new AID();
						receiver.setLocalName("AI");
						ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
						ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
						mensagem.addReceiver(receiver);
						try {
							mensagem.setContentObject(info);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myAgent.send(mensagem);
						
						System.out.println(nome +": Estou nas coordenadas: (" + x_inst + ","+y_inst + ")");
						int distInst = (int) Math
								.sqrt(((Math.pow((x_inst - x_dest), 2)) + (Math.pow((y_inst - y_dest), 2))));
						
						try {
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (pos34>=distInst) {
							x_34=x_inst;
							y_34=y_inst;
							aiPosY=y_inst;
							aiPosX=x_inst;

							
							
							//mandar mensagem aos AE 
							System.out.println(nome +": Cheguei a 3/4 do caminho ");
							for (Map.Entry me : locations.entrySet()) {
								String[] parts = ((String) me.getValue()).split(",");
								x_est = Integer.parseInt(parts[0]);
								y_est= Integer.parseInt(parts[1]);
								
								distEstacao = (int) Math
										.sqrt(((Math.pow((x_inst - x_est), 2)) + (Math.pow((y_inst - y_est), 2))));
								
								
								if (distEstacao < APE) {
									ACLMessage mensagem5 = new ACLMessage(ACLMessage.REQUEST);
									mensagem5.addReceiver((AID) me.getKey());
									mensagem5.setContent("getIncentivos");
									myAgent.send(mensagem5);
									System.out.println(nome +": Pedido de incentivo enviado");
									estacoesIn++;
								}
								
								if (estacoesIn==0) {
									APE=24;
									if (distEstacao < APE) {
										ACLMessage mensagem6 = new ACLMessage(ACLMessage.REQUEST);
										mensagem6.addReceiver((AID) me.getKey());
										mensagem6.setContent("getIncentivos");
										myAgent.send(mensagem6);
										System.out.println(nome +": Pedido de incentivo enviado");
										estacoesIn++;
									}
								}
								
						        }
							break;
						}
					}
				}
			}
			if (Math.abs(deltaX)<Math.abs(deltaY)) {
				if(deltaY>0) {
					while (1==1) {
						y_inst++;
						aiPosY=y_inst;
						aiPosX=x_inst;

						AID receiver = new AID();
						receiver.setLocalName("AI");
						ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
						ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
						mensagem.addReceiver(receiver);
						try {
							mensagem.setContentObject(info);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myAgent.send(mensagem);
						x_inst= (y_inst-b )/ m ;
						System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
						int distInst = (int) Math
								.sqrt(((Math.pow((x_inst - x_dest), 2)) + (Math.pow((y_inst - y_dest), 2))));
						try {
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (pos34>=distInst) {
							x_34=x_inst;
							y_34=y_inst;
							//mandar mensagem aos AE 
							System.out.println(nome +": Cheguei a 3/4 do caminho");
							
							for (Map.Entry me : locations.entrySet()) {
								String[] parts = ((String) me.getValue()).split(",");
								x_est = Integer.parseInt(parts[0]);
								y_est= Integer.parseInt(parts[1]);
								
								distEstacao = (int) Math
										.sqrt(((Math.pow((x_inst - x_est), 2)) + (Math.pow((y_inst - y_est), 2))));
								
								if (distEstacao < APE) {
									ACLMessage mensagem7 = new ACLMessage(ACLMessage.REQUEST);
									mensagem7.addReceiver((AID) me.getKey());
									mensagem7.setContent("getIncentivos");
									myAgent.send(mensagem7);
									System.out.println(nome +": Pedido de incentivo enviado");
									estacoesIn++;
								}
								
								if (estacoesIn==0) {
									APE=24;
									if (distEstacao < APE) {
										ACLMessage mensagem8 = new ACLMessage(ACLMessage.REQUEST);
										mensagem8.addReceiver((AID) me.getKey());
										mensagem8.setContent("getIncentivos");
										myAgent.send(mensagem8);
										System.out.println(nome +": Pedido de incentivo enviado");
										estacoesIn++;
									}
								}
						        }
							break;
						}
					}
				}if(deltaY<0) {
					while (1==1) {
						y_inst-=1;
						x_inst= (y_inst-b )/ m ;
						
						aiPosX=x_inst;
						aiPosY=y_inst;
						
						AID receiver = new AID();
						receiver.setLocalName("AI");
						ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
						ai info = new ai(0, getAID(), "POS", aiPosX, aiPosY );
						mensagem.addReceiver(receiver);
						try {
							mensagem.setContentObject(info);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myAgent.send(mensagem);
						
						System.out.println(nome +": Ainda me encontro em: (" + x_inst + ","+y_inst + ")");
						int distInst = (int) Math
								.sqrt(((Math.pow((x_inst - x_dest), 2)) + (Math.pow((y_inst - y_dest), 2))));
						
						try {
							Thread.sleep(700);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (pos34>=distInst) {
							x_34=x_inst;
							y_34=y_inst;
							//mandar mensagem aos AE 
							System.out.println(nome +": Cheguei a 3/4 do caminho");
							
							for (Map.Entry me : locations.entrySet()) {
								String[] parts = ((String) me.getValue()).split(",");
								
								x_est = Integer.parseInt(parts[0]);
								y_est= Integer.parseInt(parts[1]);
								
								distEstacao = (int) Math
										.sqrt(((Math.pow((x_inst - x_est), 2)) + (Math.pow((y_inst - y_est), 2))));
								
								if (distEstacao < APE) {
									ACLMessage mensagem9 = new ACLMessage(ACLMessage.REQUEST);
									mensagem9.addReceiver((AID) me.getKey());
									mensagem9.setContent("getIncentivos");
									myAgent.send(mensagem9);
									System.out.println(nome +": Pedido de incentivo enviado");
									estacoesIn++;
									
								}
								if (estacoesIn==0) {
									APE=24;
									if (distEstacao < APE) {
										ACLMessage mensagem10 = new ACLMessage(ACLMessage.REQUEST);
										mensagem10.addReceiver((AID) me.getKey());
										mensagem10.setContent("getIncentivos");
										myAgent.send(mensagem10);
										System.out.println(nome +": Pedido de incentivo enviado");
										estacoesIn++;
									}
								}
								
						        
						        }
							
							break;
						}
					}
				}
			}

			break;

		}
		}
	}
		
	private class fim extends OneShotBehaviour{
		//entrega a bicicleta no destino 
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {
			ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
			response.addReceiver(target);
			response.setContent("cheguei");
			send(response);
			
			AID receiver = new AID();
			receiver.setLocalName("AI");
			ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
			ai info = new ai(0, getAID(), "FIM", 0, 0 );
			mensagem.addReceiver(receiver);
			try {
				mensagem.setContentObject(info);
			} catch (IOException e) {
				// TODO Auto-generated catch blocks
				e.printStackTrace();
			}
			myAgent.send(mensagem);
			myAgent.doDelete();
		}
	}
	
	@Override
	protected void takeDown() {
		super.takeDown();
		System.out.println("Ending User "+ getAID().getLocalName());
		end = (Calendar.getInstance()).getTimeInMillis();
		start = end - start;
		System.out.println("Tempo de execução do User:" + start);
	}

}
