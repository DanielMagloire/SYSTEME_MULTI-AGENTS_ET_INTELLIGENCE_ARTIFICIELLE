package sma.agent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import sma.ProprietaireContainer;

public class ProprietaireAgent extends GuiAgent {
	private ProprietaireContainer gui;
	private String prixPropriete="0";
	@Override
	protected void setup() {
		gui = (ProprietaireContainer) getArguments()[0];
		gui.setProprietaireAgent(this);
		System.out.println("INITIALISATION PROPRIETAIRE" + this.getAID().getName());
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("xxxxxxxxCREATION DFxxxxxxxxxx");
				DFAgentDescription dfa = new DFAgentDescription();
				dfa.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Location");
				sd.setName("Location-Maison");
				sd.setOwnership("100");
				dfa.addServices(sd);
				try {
					DFService.register(myAgent, dfa);
					System.out.println("xxxxxxxxxENREGISTREMENT DES SERVICESxxxxxxxxxxxxxx");
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
					GuiEvent guiEvent;
					
					switch (aclMessage.getPerformative()) {
						case ACLMessage.CFP:
							guiEvent = new GuiEvent(this, 1); 
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(aclMessage.getContent());
							System.out.println("Prix" + guiEvent.getParameter(0).toString());
							
							ACLMessage aclMessageReponse = new ACLMessage(ACLMessage.PROPOSE);
							
							////String msgContenu = guiEvent.getParameter(0).toString();
							////String msgDestinataire = guiEvent.getParameter(1).toString();
							
							aclMessageReponse.setContent(prixPropriete);
							//aclMessageReponse.addReceiver(aclMessage.getSender());
							aclMessageReponse.addReceiver(new AID("Negociateur", AID.ISLOCALNAME));
							send(aclMessageReponse);
							
							gui.viewMessage(guiEvent);
							break;
						
						case ACLMessage.ACCEPT_PROPOSAL: 
							guiEvent = new GuiEvent(this, 2); 
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(aclMessage.getContent());
							gui.viewMessage(guiEvent);						
							break;
							
						case ACLMessage.REJECT_PROPOSAL: 
							guiEvent = new GuiEvent(this, 3); 
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(aclMessage.getContent());
							gui.viewMessage(guiEvent);						
							break;
							
						case ACLMessage.CONFIRM: 
							guiEvent = new GuiEvent(this, 4); 
							guiEvent.addParameter(aclMessage.getSender().getLocalName());
							guiEvent.addParameter(aclMessage.getContent());
							gui.viewMessage(guiEvent);						
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
	public void onGuiEvent(GuiEvent guiEvent) {
		// TODO Auto-generated method stub
		if (guiEvent.getType() == 1){
			prixPropriete = guiEvent.getParameter(0).toString();
			/*ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
			
			String msgContenu = guiEvent.getParameter(0).toString();
			String msgDestinataire = guiEvent.getParameter(1).toString();
			
			aclMessage.setContent(msgContenu);
			aclMessage.addReceiver(new AID(msgDestinataire, AID.ISLOCALNAME));
			send(aclMessage);*/
		}
	}
}
