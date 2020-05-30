package Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import messages.ai;

import org.jfree.ui.RefineryUtilities;


public class AI  extends Agent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//hashmap para o numero de bicicletas das estacoes
	HashMap<String, Integer> bic = new HashMap<String, Integer>();
	
	//hashmap para cada utilizador
	HashMap<String, String> position1 = new HashMap<String, String>();
	HashMap<String, String> position2 = new HashMap<String, String>();
	HashMap<String, String> position3 = new HashMap<String, String>();
	HashMap<String, String> position4 = new HashMap<String, String>();
	HashMap<String, String> position5 = new HashMap<String, String>();
	HashMap<String, String> position6 = new HashMap<String, String>();
	HashMap<String, String> position7 = new HashMap<String, String>();
	HashMap<String, String> position8 = new HashMap<String, String>();
	HashMap<String, String> position9 = new HashMap<String, String>();
	HashMap<String, String> position10 = new HashMap<String, String>();
	
	//JFrame frame = new JFrame("Charts");
	//JFrame frame = new JFrame("Data");
	int contar=0;
	AID id_ae;
	private AID user;
	AID Id_ai;
	int n;
	
	//definir a variavel para o grafico de barras
	BarChartEx a1 = new BarChartEx();
	
	double posY;
	double posX;
	String bicName="HELLO";
	
	//definir a variavel para o grafico dos utilizadores e estacoes 
	LineChartEx l1= new LineChartEx();
	
	protected void setup() {
		System.out.println("Station "+getAID().getName()+" starting.");
		super.setup();
		
		
		
		
		// Registar AI nas Páginas Amarelas
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("AI");
		sd.setName(getLocalName());
		dfd.addServices(sd);

		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		this.addBehaviour(new Receiver());
	}
	
	
	
	private class Receiver extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String type;
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				//&&  msg.getContent().contentEquals("infoEstacoes")
				// Está a receber o pedido para informar sobre número de bicicletas e coordenadas
				
				try {
					type=((ai)msg.getContentObject()).getType(); //tipo de informacao recebida			
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
				
				if (msg.getPerformative() == ACLMessage.INFORM && type.contentEquals("Bicicletas") ) {
					
					try {
						Id_ai=((ai)msg.getContentObject()).getId_ai(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						n=((ai)msg.getContentObject()).getBikes(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					System.out.println( Id_ai.getLocalName()+" tem "+ n +" Bicicletas");
						
					//insere os valores recebidos das estacoes no hashmap "bic"
					bic.put(Id_ai.getLocalName(), n);
					
					//a1.setVisible(false);
		
					a1.initUI();
					
					a1.setVisible(true);
					
				}
				//colocar toda a informacao inicial de cada estacao 
				if (msg.getPerformative() == ACLMessage.INFORM && type.contentEquals("INIT") ) {
					
					try {
						Id_ai=((ai)msg.getContentObject()).getId_ai(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						n=((ai)msg.getContentObject()).getBikes(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
					bic.put(Id_ai.getLocalName(), n);
					contar+=1;
					if (contar == 16) {
						
						a1.initUI();
						a1.setVisible(true);
						try {
							
			                Thread.sleep(1000);
			            } catch (InterruptedException e) {
			                e.printStackTrace();
			            }
					}
				}
				if (msg.getPerformative() == ACLMessage.INFORM && type.contentEquals("POS") ) {
					
					try {
						Id_ai=((ai)msg.getContentObject()).getId_ai(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						n=((ai)msg.getContentObject()).getBikes(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						posX=((ai)msg.getContentObject()).getposX(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					try {
						posY=((ai)msg.getContentObject()).getposY(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					
					bicName=Id_ai.getLocalName();
					
					//verifica qual utilizador para colocar no hashmap correto
					if (bicName.contentEquals("AU1")) {
						position1.put(posX+","+posY,bicName);
						
					}
					if (bicName.contentEquals("AU2")) {
						position2.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU3")) {
						position3.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU4")) {
						position4.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU5")) {
						position5.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU6")) {
						position6.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU7")) {
						position7.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU8")) {
						position8.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU9")) {
						position9.put(posX+","+posY,bicName);
					}
					if (bicName.contentEquals("AU10")) {
						position10.put(posX+","+posY,bicName);
					}
					l1.initUI();
					
					l1.setVisible(true);
					
				
				}
				if (msg.getPerformative() == ACLMessage.INFORM && type.contentEquals("FIM") ) {
					
					try {
						Id_ai=((ai)msg.getContentObject()).getId_ai(); //tipo de informacao recebida			
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					String nome=Id_ai.getLocalName();
					
					//verifica a mensagem de fim do utilizador e elimina o conteudo do respetivo hashmap
					if (nome.contentEquals("AU1")) {
						position1.clear();
						
					}
					if (nome.contentEquals("AU2")) {
						position2.clear();
					}
					if (nome.contentEquals("AU3")) {
						position3.clear();
					}
					if (nome.contentEquals("AU4")) {
						position4.clear();
					}
					if (nome.contentEquals("AU5")) {
						position5.clear();
					}
					if (nome.contentEquals("AU6")) {
						position6.clear();
					}
					if (nome.contentEquals("AU7")) {
						position7.clear();
					}
					if (nome.contentEquals("AU8")) {
						position8.clear();
					}
					if (nome.contentEquals("AU9")) {
						position9.clear();
					}
					if (nome.contentEquals("AU10")) {
						position10.clear();
					}
					
					l1.initUI();
					l1.setVisible(true);
					
				}

			}
		
	
	
		}
	}
	
	//class para criar o grafico de barras
	public class BarChartEx extends JFrame {
		
		
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void BarChartEx() {

	        initUI();
	    }
	    
	   
	    public void main() {
	    	 
	        EventQueue.invokeLater(() -> {
	            var ex = new BarChartEx();
	            ex.setVisible(true);
	        });
	    }

	    private void initUI() {

	        CategoryDataset dataset = createDataset();

	        JFreeChart chart = createChart(dataset);
	        
	        ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPreferredSize(new Dimension(730, 650));
	        
	        chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	        chartPanel.setBackground(Color.white);
	        add(chartPanel);

	        pack();
	        setTitle("Bar chart");
	        RefineryUtilities.positionFrameOnScreen(this, 1, 0);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }

	    private CategoryDataset createDataset() {		
	    	
	    	//criar um dataset com os valores dentro do hashmap "bic"
	        
	    	var dataset = new DefaultCategoryDataset();
	        
			System.out.println(bic);
			
			for(Map.Entry<String, Integer> entry : bic.entrySet()) {
			    String key = entry.getKey();
			    Integer value = entry.getValue();
			    dataset.setValue(value, "# Bicicletas", key);
	        
			}
	        return dataset;
	    
	    }
	    private JFreeChart createChart(CategoryDataset dataset) {

	        JFreeChart barChart = ChartFactory.createBarChart(
	                "Número de Bicicletas por Estação",
	                "",
	                "# Bicicletas",
	                dataset,
	                PlotOrientation.VERTICAL,
	                false, true, false);

	        return barChart;
	    }

	}
	
	


	
	/*
	 ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 300.0);

	 */
	
	//criacao do grafico com as estacoes e utilizadores
	
	public class LineChartEx extends JFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public LineChartEx() {

	        initUI();
	    }

	    private void initUI() {

	        XYDataset dataset = createDataset();
	        
	        JFreeChart chart = createChart(dataset);

	        ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
	        chartPanel.setBackground(Color.white);
	        
	        
	        
	        chartPanel.setPreferredSize(new Dimension(650, 650));
	        
	        add(chartPanel);

	        pack();
	        setTitle("Line chart");
	        //setLocationRelativeTo(null);
	        RefineryUtilities.positionFrameOnScreen(this, 0, 0);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }

	    private XYDataset createDataset() {
	    	
	    	//permite 10 utilizadores ao mesmo tempo 
	    	//cada utilizador tera o seu proprio hashmap e variavel para inserir no dataset
	    	//cada estacao esta colocada individualmente
	    	
	    	//Definir Dataset
	    	XYSeriesCollection dataset = new XYSeriesCollection();
	    	
	    	//Definir cada Estacao
		    XYSeries aes1 = new XYSeries("Estação1");
		    XYSeries aes2 = new XYSeries("Estação2");
		    XYSeries aes3 = new XYSeries("Estação3");
		    XYSeries aes4 = new XYSeries("Estação4");
		    XYSeries aes5 = new XYSeries("Estação5");
		    XYSeries aes6 = new XYSeries("Estação6");
		    XYSeries aes7 = new XYSeries("Estação7");
		    XYSeries aes8 = new XYSeries("Estação8");
		    XYSeries aes9 = new XYSeries("Estação9");
		    XYSeries aes10 = new XYSeries("Estação10");
		    XYSeries aes11 = new XYSeries("Estação11");
		    XYSeries aes12 = new XYSeries("Estação12");
		    XYSeries aes13 = new XYSeries("Estação13");
		    XYSeries aes14 = new XYSeries("Estação14");
		    XYSeries aes15 = new XYSeries("Estação15");
		    XYSeries aes16 = new XYSeries("Estação16");
		    
		    //Definir numero de utilizadores em simultaneo para aparece na Interface
		    XYSeries au1 = new XYSeries("AU1");
		    XYSeries au2 = new XYSeries("AU2");
		    XYSeries au3 = new XYSeries("AU3");
		    XYSeries au4 = new XYSeries("AU4");
		    XYSeries au5 = new XYSeries("AU5");
		    XYSeries au6 = new XYSeries("AU6");
		    XYSeries au7 = new XYSeries("AU7");
		    XYSeries au8 = new XYSeries("AU8");
		    XYSeries au9 = new XYSeries("AU9");
		    XYSeries au10 = new XYSeries("AU10");
		    
		    //Colocar estacoes no gráfico
		    aes1.add(12,12);
		    aes2.add(12,37);
		    aes3.add(12,62);
		    aes4.add(12,87);
		    
		    aes5.add(37,12);
		    aes6.add(37,37);
		    aes7.add(37,62);
		    aes8.add(37,87);
		    
		    aes9.add(62,12); 
		    aes10.add(62,37);
		    aes11.add(62,62);
		    aes12.add(62,87);
		    
		    aes13.add(87,12);
		    aes14.add(87,37);
		    aes15.add(87,62);
		    aes16.add(87,87);
		    
		//Para a bicicleta 1
		    	
			    for(Entry<String, String> entry : position1.entrySet()) {
				    String key = entry.getKey();
				    
				    String[] part= key.split(",");
				    double keyX=Double.valueOf(part[0]);
				    double keyY=Double.valueOf(part[1]);
				    
				    au1.add(keyX,keyY);
			    }
		    
			  //Para a bicicleta 2
		    for(Entry<String, String> entry : position2.entrySet()) {
			    String key = entry.getKey();
			   
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			   
			    au2.add(keyX,keyY);
		    }
		    
		  //Para a bicicleta 3
		    for(Entry<String, String> entry : position3.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au3.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position4.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au4.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position5.entrySet()) {
			    String key = entry.getKey();
			  
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au5.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position6.entrySet()) {
			    String key = entry.getKey();
			   
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au6.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position7.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au7.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position8.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au8.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position9.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au9.add(keyX,keyY);
		    }
		    for(Entry<String, String> entry : position10.entrySet()) {
			    String key = entry.getKey();
			    
			    String[] part= key.split(",");
			    double keyX=Double.valueOf(part[0]);
			    double keyY=Double.valueOf(part[1]);
			    
			    au10.add(keyX,keyY);
		    }
		    
		    //Add series to dataset
		    dataset.addSeries(au1);
		    dataset.addSeries(au2);
		    dataset.addSeries(au3);
		    dataset.addSeries(au4);
		    dataset.addSeries(au5);
		    dataset.addSeries(au6);
		    dataset.addSeries(au7);
		    dataset.addSeries(au8);
		    dataset.addSeries(au9);
		    dataset.addSeries(au10);
		    
		    
		    dataset.addSeries(aes1);
		    dataset.addSeries(aes2);
		    dataset.addSeries(aes3);
		    dataset.addSeries(aes4);
		    dataset.addSeries(aes5);
		    dataset.addSeries(aes6);
		    dataset.addSeries(aes7);
		    dataset.addSeries(aes8);
		    dataset.addSeries(aes9);
		    dataset.addSeries(aes10);
		    dataset.addSeries(aes11);
		    dataset.addSeries(aes12);
		    dataset.addSeries(aes13);
		    dataset.addSeries(aes14);
		    dataset.addSeries(aes15);
		    dataset.addSeries(aes16);
		    
		    return dataset;
	    }

	    private JFreeChart createChart(XYDataset dataset) {

	        JFreeChart chart = ChartFactory.createScatterPlot(
	                "Trajeto das Bicicletas",
	                "Longitude",
	                "Latitude",
	                dataset,
	                PlotOrientation.VERTICAL,
	                true,
	                true,
	                false
	        );
	        BufferedImage image = null;
	        File url = new File("C:/Users/andre/Desktop/OneDrive - Universidade do Minho/4ºano/2ºsemestre/SI/trabalho_pratico/Sem Título.png");
	        try {
				image = ImageIO.read(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        chart.setBackgroundImage(image);
	        chart.getPlot().setBackgroundAlpha(0);
	        XYPlot plot = chart.getXYPlot();
	        ValueAxis yAxis = plot.getRangeAxis();
			yAxis.setRange(0, 100);
			ValueAxis xAxis = plot.getDomainAxis();
			xAxis.setRange(0, 100);


	       
	      

	       

	        plot.setRangeGridlinesVisible(true);
	        plot.setRangeGridlinePaint(Color.BLACK);

	        plot.setDomainGridlinesVisible(true);
	        plot.setDomainGridlinePaint(Color.BLACK);

	        chart.getLegend().setFrame(BlockBorder.NONE);

	        chart.setTitle(new TextTitle("Trajeto das Bicicletas",
	                        new Font("Serif", java.awt.Font.BOLD, 18)
	                )
	        );

	        return chart;
	    }
	    public void main(String[] args) {

	        EventQueue.invokeLater(() -> {

	            var ex = new LineChartEx();
	            
	            ex.setVisible(true);
	        });

	    
	    }
	}

}



