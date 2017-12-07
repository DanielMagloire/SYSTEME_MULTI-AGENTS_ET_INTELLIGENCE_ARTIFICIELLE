package sma;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import sma.agent.NegociateurAgent;

public class NegociateurContainer extends Application {
	private NegociateurContainer negociateurContainer;
	private NegociateurAgent negociateurAgent;
	private AgentContainer agentContainer;
	private AgentController agentContoller;
	private final ObservableList<Message> data = FXCollections.observableArrayList();
	private TableView table = new TableView();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(NegociateurContainer.class);	
	}
	
	public void startContainer() {
		Runtime runtime = Runtime.instance();
		Profile profile = new ProfileImpl(false);//"false" Pour signifier que nous creons un profile pour un container et non pour un mainContainer
		profile.setParameter(Profile.MAIN_HOST, "127.0.0.1");//Pour signifier que cette machine va se connecter au mainContainer qui est en local
		agentContainer = runtime.createAgentContainer(profile);
		try {
			agentContoller = agentContainer.createNewAgent
					("Negociateur", "sma.agent.NegociateurAgent", new Object[]{this});
			agentContoller.start();
		} catch (StaleProxyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		startContainer();
		primaryStage.setTitle("Negociateur");
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
        
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(table);
 
        ((Group) scenePricipale.getRoot()).getChildren().addAll(vBox);
 
        primaryStage.setScene(scenePricipale);
        primaryStage.show();
	}
	
	public NegociateurAgent getNegociateurAgent() {
		return negociateurAgent;
	}

	public void setNegociateurAgent(NegociateurAgent negociateurAgent) {
		this.negociateurAgent = negociateurAgent;
	}
	
	public void viewMessage(GuiEvent guiEvent){
		String msgContent = null;
		String msgSender = null;
		switch (guiEvent.getType()) {
			case 1:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("PROPOSE");
				System.out.println(msgContent);
				data.add(new Message("PROPOSE", msgContent, msgSender));
				break;
				
			case 2:
				msgSender = guiEvent.getParameter(0).toString();
				msgContent = guiEvent.getParameter(1).toString();
				System.out.println("REQUEST");
				System.out.println(msgContent);
				data.add(new Message("REQUEST", msgContent, msgSender));
				break;
				
			default:
				break;
		}
	}	
}
