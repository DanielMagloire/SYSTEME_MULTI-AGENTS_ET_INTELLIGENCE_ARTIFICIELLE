package sma;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sma.ProprietaireContainer.Message;
import sma.agent.ClientAgent;
import sma.agent.ProprietaireAgent;

public class ClientContainer  extends Application {
	private ClientContainer clientContainer;
	private ClientAgent clientAgent;
	
	private AgentContainer agentContainer;
	private AgentController agentContoller;
	private final ObservableList<Message> data = FXCollections.observableArrayList();
	private TableView table = new TableView();
	private GuiEvent guiEvent = null;

	public static void main(String[] args) {
		launch(ClientContainer.class);	
	}

	public void startContainer() {
		Runtime runtime = Runtime.instance();
		Profile profile = new ProfileImpl(false);//"false" Pour signifier que nous creons un profile pour un container et non pour un mainContainer
		profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");//Pour signifier que cette machine va se connecter au mainContainer qui est en local
		agentContainer = runtime.createAgentContainer(profile);
		try {
			agentContoller = agentContainer.createNewAgent
					("Client", "sma.agent.ClientAgent", new Object[]{this});
			agentContoller.start();
		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("Client");
		Scene scenePricipale = new Scene(new Group());
		primaryStage.setWidth(322);
		primaryStage.setHeight(450);
 
        TableColumn tcTableMsgSender = new TableColumn("Sender");
        tcTableMsgSender.setMinWidth(100);
        tcTableMsgSender.setCellValueFactory(
                new PropertyValueFactory<>("msgSender"));
 
        TableColumn tcTableMsgType = new TableColumn("Type");
        tcTableMsgType.setMinWidth(100);
        tcTableMsgType.setCellValueFactory(
                new PropertyValueFactory<>("msgType"));
 
        TableColumn tcTableMsgContent = new TableColumn("Content");
        tcTableMsgContent.setMinWidth(100);
        tcTableMsgContent.setCellValueFactory(
                new PropertyValueFactory<>("msgContent"));
        
        table.setItems(data);
        table.getColumns().addAll(tcTableMsgType, tcTableMsgContent, tcTableMsgSender);
            
        final HBox hBoxPrixMin = new HBox();
        final Label labPrixMin = new Label("Prix Min:");
        final TextField tfPrixMin = new TextField();    
        hBoxPrixMin.getChildren().addAll(labPrixMin, tfPrixMin);
        hBoxPrixMin.setSpacing(10);
        
        final HBox hBoxPrixMax = new HBox();
        final Label labPrixMax = new Label("Prix Max:");
        final TextField tfPrixMax = new TextField();    
        hBoxPrixMax.getChildren().addAll(labPrixMax, tfPrixMax);
        hBoxPrixMax.setSpacing(10);
        
        final HBox hBoxBoutton = new HBox();
        final Button btAjouterPrix = new Button("Ajouter"); 
        btAjouterPrix.setOnAction((ActionEvent e) -> {
        	guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(tfPrixMin.getText());
            guiEvent.addParameter(tfPrixMax.getText());
    		clientAgent.onGuiEvent(guiEvent);
        });
        
        final TextField tfPrix = new TextField(); 
        
        final Button btVider = new Button("Vider"); 
        btVider.setOnAction((ActionEvent e) -> {
        	tfPrixMin.clear();
        	tfPrixMax.clear();
        	tfPrix.clear();
        	data.clear();
        });
        hBoxBoutton.getChildren().addAll(btAjouterPrix, btVider);
        hBoxBoutton.setSpacing(10);
        
        final HBox hBoxPrix = new HBox();
        final Label labPrix = new Label("Prix:");
           
        hBoxPrix.getChildren().addAll(labPrix, tfPrix);
        hBoxPrix.setSpacing(10);
        
        final Button btValider = new Button("Valider"); 
        btValider.setOnAction((ActionEvent e) -> {
        	guiEvent = new GuiEvent(this, 2);
            guiEvent.addParameter(tfPrix.getText());
    		clientAgent.onGuiEvent(guiEvent);
        });
        
        final VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(hBoxPrixMin, hBoxPrixMax, hBoxBoutton, hBoxPrix, btValider,table);
 
        ((Group) scenePricipale.getRoot()).getChildren().addAll(vBox);
 
        primaryStage.setScene(scenePricipale);
        primaryStage.show();
	}
		
	public ClientAgent getClientAgent() {
		return clientAgent;
	}

	public void setClientAgent(ClientAgent clientAgent) {
		this.clientAgent = clientAgent;
	}

	public void viewMessage(GuiEvent guiEvent){
		String msgContent = null;
		String msgSender = null;
		if(guiEvent.getType()==1){
			msgSender = guiEvent.getParameter(0).toString();
			msgContent = guiEvent.getParameter(1).toString();
			System.out.println("INFORM");
			System.out.println(msgContent);
			data.add(new Message("INFORM", msgContent, msgSender));
		}
	}	
}
