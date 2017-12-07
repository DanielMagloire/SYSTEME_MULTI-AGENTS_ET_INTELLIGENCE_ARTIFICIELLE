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
import sma.ClientContainer;

public class ClientAgent extends GuiAgent {
	private ClientContainer gui;
	private String prixPropriete="0";

	@Override
	protected void setup() {
		gui = (ClientContainer) getArguments()[0];
		gui.setClientAgent(this);
		System.out.println("INITIALISATION CLIENT" + this.getAID().getName());
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage message = receive(messageTemplate);
				if(message != null){
					System.out.println("Reception d'un message" + message.getContent());
					GuiEvent guiEvent = new GuiEvent(this, 1);
					guiEvent.addParameter(message.getSender().getLocalName());
					guiEvent.addParameter(message.getContent());
					gui.viewMessage(guiEvent);
				}
				else{
					block();
				}
			}
		});
	}
	
	@Override
	public void onGuiEvent(GuiEvent guiEvent) {
		if (guiEvent.getType() == 1){
			prixPropriete = guiEvent.getParameter(0).toString();
			ACLMessage aclMessageReponse = new ACLMessage(ACLMessage.REQUEST);
			
			String prixMin = guiEvent.getParameter(0).toString();
			String prixMax = guiEvent.getParameter(1).toString();
			
			aclMessageReponse.setContent(prixMin + " " + prixMax);
			//aclMessageReponse.addReceiver(aclMessage.getSender());
			aclMessageReponse.addReceiver(new AID("Negociateur", AID.ISLOCALNAME));
			send(aclMessageReponse);
		}
		if (guiEvent.getType() == 2){
			ACLMessage aclMessageReponse = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			
			String prix = guiEvent.getParameter(0).toString();
			
			aclMessageReponse.setContent(prix);
			//aclMessageReponse.addReceiver(aclMessage.getSender());
			aclMessageReponse.addReceiver(new AID("Negociateur", AID.ISLOCALNAME));
			send(aclMessageReponse);
		}
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
}
