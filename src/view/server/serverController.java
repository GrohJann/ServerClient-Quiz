package view.server;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;

public class serverController {
    
    private Control.EchoServer server;
    
    @FXML private JFXTextField port_input;
    @FXML private JFXTextField message_input;
    @FXML private JFXTextArea log_field;
    @FXML private JFXTextArea clients_field;
    @FXML private JFXButton open_btn;
    @FXML private JFXButton close_btn;
    @FXML private JFXButton send_btn;
    
    @FXML
    private void initialize(){
        port_input.setText("56789");
        clients_field.setText("Kein Client angemeldet.");
    
        open_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { openServer(); }
        });
        close_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { closeServer(); }
        });
        send_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { sendToClients(); }
        });
        message_input.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) { sendToClients(); } });
    }
    
    /**
     * Es wird dem Output Text hinzugefügt.
     * @param text
     */
    public void addToSyslog(String text){
        if(log_field.getText().isEmpty()){
            log_field.setText(text);
        }else{
            log_field.setText(log_field.getText() + "\n" + text);
        }
    }
    
    /**
     * Es wird ein neues EchoServer-Objekt instanziiert.
     */
    private void openServer(){
        server = new Control.EchoServer(Integer.parseInt(port_input.getText()), this);
    }
    
    /**
     * Das EchoServer-Objekt schließt den Port.
     */
    private void closeServer(){
        server.close();
        clients_field.setText("Kein Client angemeldet.");
    }
    
    /**
     * Die eingetragene Nachricht wird genau so wie sie eingetragen wird ohne weitere Zusätze an alle Clients versendet.
     */
    private void sendToClients(){
        if (!message_input.getText().equals("")) {
            server.sendToAll("Server > " + message_input.getText());
            addToSyslog("Sever > " + message_input.getText());
            message_input.setText("");
        }
    }
    
    /**
     * Der Status der Knöpfe wird geändert.
     * Diese Methode sollte vom EchoServer-Objekt aufgerufen werden, sobald dieses einen Port geöffnet geschlossen hat.
     */
    public void buttonSwitch(){
        open_btn.setDisable(!open_btn.isDisabled());
        close_btn.setDisable(!close_btn.isDisabled());
        send_btn.setDisable(!send_btn.isDisabled());
    }
    
    /**
     * Methode wird vom EchoServer-Objekt aufgerufen, sobald ein neuer Client sich angemeldet hat.
     * @param pClientIP
     * @param pClientPort
     */
    public void newConnection(String pClientIP, int pClientPort){
        addToSyslog(new java.util.Date().toString() + " - " + "Neuer Client hat sich verbunden: " + pClientIP + ":" + pClientPort);
        updateConnections();
    }
    
    /**
     * Methode wird vom EchoServer-Objekt aufgerufen, sobald ein Client eine Nachricht geschickt hat.
     * @param pClientIP
     * @param pClientPort
     * @param pMessage
     */
    public void processMessage(String pClientIP, int pClientPort, String pMessage){
        addToSyslog(new java.util.Date().toString() + " - " + pClientIP + ":" + pClientPort +" - " + pMessage);
    }
    
    /**
     * Methode gibt den Text zurück, der im Message-Textfield steht.
     * @return
     */
    public String getMessage(){
        return message_input.getText();
    }
    
    /**
     * Methode wird aufgerufen, sobald ein Client seine Verbindung zum Server geschlossen hat.
     * @param pClientIP
     * @param pClientPort
     */
    public void closingConnection(String pClientIP, int pClientPort){
        addToSyslog(new java.util.Date().toString() + " - " + "Client hat sich abgemeldet: " + pClientIP + ":" + pClientPort);
        updateConnections();
    }
    
    /**
     * Methode wird aufgerufen, sobald ein neuer Client sich angemeldet oder ein alter Client sich abgemeldet hat.
     */
    private void updateConnections(){
        clients_field.setText("Kein Client angemeldet.");
        String[] connections = server.getClients();
        for(int i = 0; i < connections.length; i++){
            if(i == 0){
                clients_field.setText(connections[i]);
            }else{
                clients_field.setText(clients_field.getText() + "\n" + connections[i]);
            }
        }
    }
}
