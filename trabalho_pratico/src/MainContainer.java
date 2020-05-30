

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class MainContainer {

	Runtime rt;
	ContainerController container;

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainContainer a = new MainContainer();
		int n;
		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");
		
		/*
		 * Starting AE agents
		 * */
		
		try {
			ContainerController newcontainer1 = a.initContainerInPlatform("localhost", "9888", "Container1");
			
			try {
                a.startAgentInPlatform("AI" , "Agents.AI");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			Object[] args_input = new Object[2];
			args_input[0] = 12;
			args_input[1] = 12;
			try {
				AgentController AE = newcontainer1.createNewAgent("AE", "Agents.AE", args_input);// arguments
				AE.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input2 = new Object[2];
			args_input2[0] = 12;
			args_input2[1] = 37;
			
			try {
				AgentController AE2 = newcontainer1.createNewAgent("AE2", "Agents.AE", args_input2);// arguments
				AE2.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input3 = new Object[2];
			args_input3[0] = 12;
			args_input3[1] = 62;
			try {
				AgentController AE3 = newcontainer1.createNewAgent("AE3", "Agents.AE", args_input3);// arguments
				AE3.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input4 = new Object[2];
			args_input4[0] = 12;
			args_input4[1] = 87;
			try {
				AgentController AE4 = newcontainer1.createNewAgent("AE4", "Agents.AE", args_input4);// arguments
				AE4.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			/*
			 * Segunda coluna
			 * */
			Object[] args_input5 = new Object[2];
			args_input5[0] = 37;
			args_input5[1] = 12;
			try {
				AgentController AE5 = newcontainer1.createNewAgent("AE5", "Agents.AE", args_input5);// arguments
				AE5.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input6 = new Object[2];
			args_input6[0] = 37;
			args_input6[1] = 37;
			try {
				AgentController AE6 = newcontainer1.createNewAgent("AE6", "Agents.AE", args_input6);// arguments
				AE6.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input7 = new Object[2];
			args_input7[0] = 37;
			args_input7[1] = 62;
			try {
				AgentController AE7 = newcontainer1.createNewAgent("AE7", "Agents.AE", args_input7);// arguments
				AE7.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input8 = new Object[2];
			args_input8[0] = 37;
			args_input8[1] = 87;
			try {
				AgentController AE8 = newcontainer1.createNewAgent("AE8", "Agents.AE", args_input8);// arguments
				AE8.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			/*
			 * Terceira coluna
			 * */
			Object[] args_input9 = new Object[2];
			args_input9[0] = 62;
			args_input9[1] = 12;
			try {
				AgentController AE9 = newcontainer1.createNewAgent("AE9", "Agents.AE", args_input9);// arguments
				AE9.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input10 = new Object[2];
			args_input10[0] = 62;
			args_input10[1] = 37;
			try {
				AgentController AE10 = newcontainer1.createNewAgent("AE10", "Agents.AE", args_input10);// arguments
				AE10.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input11 = new Object[2];
			args_input11[0] = 62;
			args_input11[1] = 62;
			try {
				AgentController AE11 = newcontainer1.createNewAgent("AE11", "Agents.AE", args_input11);// arguments
				AE11.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input12 = new Object[2];
			args_input12[0] = 62;
			args_input12[1] = 87;
			try {
				AgentController AE12 = newcontainer1.createNewAgent("AE12", "Agents.AE", args_input12);// arguments
				AE12.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			
			/*
			 * Quarta coluna
			 * */
			Object[] args_input13 = new Object[2];
			args_input13[0] = 87;
			args_input13[1] = 12;
			try {
				AgentController AE13 = newcontainer1.createNewAgent("AE13", "Agents.AE", args_input13);// arguments
				AE13.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input14 = new Object[2];
			args_input14[0] = 87;
			args_input14[1] = 37;
			try {
				AgentController AE14 = newcontainer1.createNewAgent("AE14", "Agents.AE", args_input14);// arguments
				AE14.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input15 = new Object[2];
			args_input15[0] = 87;
			args_input15[1] = 62;
			try {
				AgentController AE15 = newcontainer1.createNewAgent("AE15", "Agents.AE", args_input15);// arguments
				AE15.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
			Object[] args_input16 = new Object[2];
			args_input16[0] = 87;
			args_input16[1] = 87;
			try {
				AgentController AE16 = newcontainer1.createNewAgent("AE16", "Agents.AE", args_input16);// arguments
				AE16.start();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		//a.startAgentInPlatform("AE", "Agents.AE");
		//a.startAgentInPlatform("AE2", "Agents.AE");
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//create infinite Customers
		
		int limit_Au = 11; // Limit number of Customers
        // Start Agents Customers!
		while (1==1) {
			 
	        for (n = 1; n < limit_Au; n++) {
	            try {
	                a.startAgentInPlatform("AU" + n, "Agents.AU");
	                Thread.sleep(5000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	        try {
				Thread.sleep(15000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}