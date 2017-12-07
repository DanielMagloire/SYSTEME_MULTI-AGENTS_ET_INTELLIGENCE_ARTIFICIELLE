package sma;

import java.util.ArrayList;
import java.util.List;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import sma.agent.ProprietaireAgent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ListView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

public class ProprietaireContainer extends Application {
	
	private TableView table = new TableView();
	//private ObservableList<Message> observableList;
	private final ObservableList<Message> data = FXCollections.observableArrayList();
	//private ArrayList<Message> listMessage; 
	
	private AgentContainer agentContainer;
	private AgentController agentContoller;
	private ProprietaireContainer proprietaireContainer;
	
	private ProprietaireAgent proprietaireAgent;
	
	public static void main(String[] args) {
		launch(ProprietaireContainer.class);	
	}
	
	public void startContainer() {
		Runtime runtime = Runtime.instance();
		Profile profile = new ProfileImpl(false);//"false" Pour signifier que nous creons un profile pour un container et non pour un mainContainer
		profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");//Pour signifier que cette machine va se connecter au mainContainer qui est en local
		agentContainer = runtime.createAgentContainer(profile);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		proprietaireContainer=this;
		primaryStage.setTitle("Proprietaire");
		Scene scenePricipale = new Scene(new Group());
		primaryStage.setWidth(322);
		primaryStage.setHeight(520);
 
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

        
        final HBox hBoxDeploiement = new HBox();
        final Label labNomProprietaire = new Label("Nom:");
        final TextField tfNomProprietaire = new TextField();
        final Button btDeploy = new Button("Deploy");
        btDeploy.setOnAction((ActionEvent e) -> {
        	String nomProprietaire = tfNomProprietaire.getText();
			try {
				agentContoller = agentContainer.createNewAgent
						(nomProprietaire, "sma.agent.ProprietaireAgent", new Object[]{proprietaireContainer});
				agentContoller.start();
			} catch (StaleProxyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			tfNomProprietaire.setDisable(true);
         });
 
        hBoxDeploiement.getChildren().addAll(labNomProprietaire, tfNomProprietaire, btDeploy);
        hBoxDeploiement.setSpacing(10);
        
        final HBox hBoxPrix = new HBox();
        final Label labPrix = new Label("Prix:");
        final TextField tfPrix = new TextField();
        final Button btAjouterPrix = new Button("Ajouter");
        btAjouterPrix.setOnAction((ActionEvent e) -> {
        	GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(tfPrix.getText());
    		proprietaireAgent.onGuiEvent(guiEvent);
    		tfPrix.setDisable(true);
        });
        hBoxPrix.getChildren().addAll(labPrix, tfPrix, btAjouterPrix);
        hBoxPrix.setSpacing(10);
        
         
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(hBoxDeploiement, hBoxPrix, table);
 
        ((Group) scenePricipale.getRoot()).getChildren().addAll(vBox);
 
        primaryStage.setScene(scenePricipale);
        primaryStage.show();
	}

	public ProprietaireAgent getProprietaireAgent() {
		return proprietaireAgent;
	}

	public void setProprietaireAgent(ProprietaireAgent proprietaireAgent) {
		this.proprietaireAgent = proprietaireAgent;
	}

	
	
	public void viewMessage(GuiEvent guiEvent){
		String msgContent = null;
		String msgSender = null;
		switch (guiEvent.getType()) {
			case 1:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("CFP");
				System.out.println(msgContent);
				data.add(new Message("CFP", msgContent, msgSender));
				break;
				
			case 2:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("ACCEPT_PROPOSAL");
				System.out.println(msgContent);
				data.add(new Message("ACCEPT_PROPOSAL", msgContent, msgSender));
				break;
				
			case 3:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("REJECT_PROPOSAL");
				System.out.println(msgContent);
				data.add(new Message("REJECT_PROPOSAL", msgContent, msgSender));
				break;
			
			case 4:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("CONFIRM");
				System.out.println(msgContent);
				data.add(new Message("CONFIRM", msgContent, msgSender));
				break;
				
			default:
				break;
		}
	}
	
	public static class Message {
		 
		private final SimpleStringProperty propMsgSender;
        private final SimpleStringProperty propMsgType;
        private final SimpleStringProperty propMsgContent;
 
        public Message(String msgType, String msgContent, String msgSender) {
        	this.propMsgSender = new SimpleStringProperty(msgSender);
            this.propMsgType = new SimpleStringProperty(msgType);
            this.propMsgContent = new SimpleStringProperty(msgContent);
        }
         
        public String getMsgSender() {
            return propMsgSender.get();
        }
 
        public void setMsgSender(String msgSender) {
        	propMsgSender.set(msgSender);
        }
        
        public String getMsgType() {
            return propMsgType.get();
        }
 
        public void setMsgType(String msgType) {
        	propMsgType.set(msgType);
        }
 
        public String getMsgContent() {
            return propMsgContent.get();
        }
 
        public void setMsgContent(String msgContent) {
        	propMsgContent.set(msgContent);
        }
    }

}
