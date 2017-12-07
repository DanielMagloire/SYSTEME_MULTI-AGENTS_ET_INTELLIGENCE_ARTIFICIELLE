package sma.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import sma.NegociateurContainer;

public class NegociateurAgent extends GuiAgent {
	private NegociateurContainer gui;
	private AID[] listeProprietaire;
	private int prixMin = 0;
	private int prixMax = 0;
	private ArrayList listePrix = null;
	
	@Override
	protected void setup() {
		gui = (NegociateurContainer) getArguments()[0];
		gui.setNegociateurAgent(this);
		System.out.println("INITIALISATION NEGOCIATEUR" + this.getAID().getName());
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 10000) {
			@Override
			protected void onTick() {
				DFAgentDescription description = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Location");
				//sd.setName("Vente-Livres");
				description.addServices(sd);
				
				try {
					DFAgentDescription[] agentDescription = DFService.search(myAgent, description);
					listeProprietaire = new AID[agentDescription.length];
					for(int i = 0; i < agentDescription.length; i++){
						listeProprietaire[i] = agentDescription[i].getName();
					}
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage != null){
					System.out.println("Reception Message:");
					GuiEvent guiEvent  = null;
					ACLMessage aclMessageReponse = null;
					String msg = null;
					switch (aclMessage.getPerformative()) {
						case ACLMessage.PROPOSE:
							guiEvent = new GuiEvent(this, 1); 
							msg = aclMessage.getContent();
							
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(msg);
							gui.viewMessage(guiEvent);
							
							if((Integer.parseInt(msg) >= prixMin) && (Integer.parseInt(msg) <= prixMax)){
								aclMessageReponse = new ACLMessage(ACLMessage.INFORM);
								aclMessageReponse.setContent(msg);
								aclMessageReponse.addReceiver(new AID("Client", AID.ISLOCALNAME));
								send(aclMessageReponse);
							}
							listePrix.add(msg + " " + aclMessage.getSender().getLocalName());
							break;
						
						case ACLMessage.REQUEST: 
							guiEvent = new GuiEvent(this, 2); 
							listePrix = new ArrayList();
							msg = aclMessage.getContent();
							try{
								Scanner in = new Scanner(msg);
								prixMin = in.nextInt();
								prixMax = in.nextInt();
								in.close();
							}catch(Exception ex){
								ex.printStackTrace();
							}							
							aclMessageReponse = new ACLMessage(ACLMessage.CFP);
							aclMessageReponse.setContent("Demande de prix");
							for(AID aid:listeProprietaire){
								aclMessageReponse.addReceiver(aid);
							}
							send(aclMessageReponse);
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(msg);
							gui.viewMessage(guiEvent);						
							break;
							
						case ACLMessage.ACCEPT_PROPOSAL: 	
							msg = aclMessage.getContent();
							int prix = 0;
							String nomSender = "";
							Boolean trouve = false;
							for(Object liste : listePrix){
							    //System.out.println(o);
								try{
									Scanner in = new Scanner(liste.toString());
									prix = in.nextInt();
									nomSender = in.next();
									in.close();
									if ((prix == Integer.parseInt(msg)) && (trouve == false)){
										aclMessageReponse = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
										aclMessageReponse.setContent("Accepter");
										aclMessageReponse.addReceiver(new AID(nomSender, AID.ISLOCALNAME));
										send(aclMessageReponse);
										
										aclMessageReponse = new ACLMessage(ACLMessage.INFORM);
										aclMessageReponse.setContent(nomSender);
										aclMessageReponse.addReceiver(new AID("Client", AID.ISLOCALNAME));
										send(aclMessageReponse);
										trouve = true;
									}else{
										aclMessageReponse = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
										aclMessageReponse.setContent("Refuser");
										aclMessageReponse.addReceiver(new AID(nomSender, AID.ISLOCALNAME));
										send(aclMessageReponse);
									}
								}catch(Exception ex){
									ex.printStackTrace();
								}	
							}
							break;
							
						default:
							break;
					}
				}else{
					block();
				}
			}
		});
	}
	
	@Override
	protected void takeDown() {
		System.out.println("xxxxxxxxxxxxAVANT DESTRUCTIONxxxxxxxxxxxxxxxx");
	}
	
	@Override
	protected void beforeMove() {
		try {
			System.out.println("xxxxxxxxxxAVANT MIGRATIONxxxxxxxxxxxxxxx");
			System.out.println("du container:" + this.getContainerController().getContainerName());
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void afterMove() {
		try {
			System.out.println("xxxxxxxxxxAPRES MIGRATIONxxxxxxxxxxxxxxx");
			System.out.println("du container:" + this.getContainerController().getContainerName());
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onGuiEvent(GuiEvent guiEvent) {
		// TODO Auto-generated method stub
		
	}
}
