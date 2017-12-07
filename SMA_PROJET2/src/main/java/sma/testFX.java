package sma;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
//import sma.agent.ConsommateurAgent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField; 
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class testFX extends Application {
	private ObservableList<String> observableList;
	
	public static void main(String[] args) {
		launch(testFX.class);	
	}
	


	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Consommateur");
		BorderPane borderPane = new BorderPane();
		
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		
		Label labLivre = new Label("Livre:");
		TextField tfLivre = new TextField();
		Button btAcheterLivre = new Button("Acheter");
		hBox.getChildren().add(labLivre);
		hBox.getChildren().add(tfLivre);
		hBox.getChildren().add(btAcheterLivre);
		borderPane.setTop(hBox);
		
		VBox vBox = new VBox();
		GridPane gridPane = new GridPane();
		
		observableList = FXCollections.observableArrayList();
		ListView<String> listeViewMessages = new ListView<String>(observableList);
		
		gridPane.add(listeViewMessages, 0, 0);
		vBox.getChildren().add(gridPane);
		
		vBox.setPadding(new Insets(10));
		vBox.setSpacing(10);
		borderPane.setCenter(vBox);
		
		Scene scene = new Scene(borderPane, 400, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		btAcheterLivre.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//String livre = tfLivre.getText();
				//observableList.add(livre);
				//viewMessage("xx");
				
				final Stage stageResponse = new Stage();
	        	stageResponse.setWidth(310);
	        	stageResponse.setHeight(350);
	        	BorderPane borderPaneReply = new BorderPane();
	        	
	        	final HBox hBoxDestinataire = new HBox();
	        	hBoxDestinataire.setPadding(new Insets(10));
	        	hBoxDestinataire.setSpacing(10);
	    		
	        	final Label labMsgDestinataire = new Label("Destinataire:");
	        	final TextField tfMsgDestinataire = new TextField();
	        	hBoxDestinataire.getChildren().addAll(labMsgDestinataire, tfMsgDestinataire);
	    		
	        	final HBox hBoxTypeMessage = new HBox();
	        	hBoxTypeMessage.setPadding(new Insets(10));
	        	hBoxTypeMessage.setSpacing(10);
	        	
	        	final HBox hBoxContenu = new HBox();
	        	hBoxContenu.setPadding(new Insets(10));
	        	hBoxContenu.setSpacing(10);
	        	
	        	final TextArea ttaMsgContenu = new TextArea();
	    		ttaMsgContenu.setPrefRowCount(10);
	    		ttaMsgContenu.setPrefColumnCount(150);
	    		ttaMsgContenu.setWrapText(true);
	    		ttaMsgContenu.setPrefWidth(100);
	        	
	    		final Label labMsgContenu = new Label("Contenu:");
	        	hBoxContenu.getChildren().addAll(labMsgContenu, ttaMsgContenu);
	        	
	        	final VBox vBoxMessage = new VBox();
	        	vBoxMessage.setPadding(new Insets(10));
	        	vBoxMessage.setSpacing(10);
	        	
	        	final Button btMsgEnvoyer = new Button("Envoyer");
	        	btMsgEnvoyer.setOnAction((ActionEvent e) -> {
	        		System.out.println("xxxxxxxxxxxxx");
	        	});
	        	
	        	vBoxMessage.getChildren().addAll(hBoxDestinataire, hBoxContenu, btMsgEnvoyer);
	    		   		
	    		borderPaneReply.setTop(vBoxMessage);
	    		
	    		final Scene sceneResponse = new Scene(borderPaneReply);
	        	stageResponse.setScene(sceneResponse);
	        	stageResponse.show();
			}
		});
	}

	

	public void viewMessage(String msg){
		//String message = guiEvent.getParameter(0).toString();
		observableList.add(msg);
	}
}
