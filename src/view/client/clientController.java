package view.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;

import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class clientController implements Initializable {
    
    private Control.EchoClient client;
    
    @FXML
    private JFXTextField ip_input;
    @FXML
    private JFXTextField port_input;
    @FXML
    private JFXTextField message_input;
    @FXML
    private JFXTextArea output_field;
    @FXML
    private JFXButton connect_btn;
    @FXML
    private JFXButton disconnect_btn;
    @FXML
    private JFXButton send_btn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connect_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                connect();
            }
        });
        disconnect_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                closeConnection();
            }
        });
        send_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                send();
            }
        });
        message_input.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                send();
            }
        });
        
        ip_input.setText("127.0.0.1");
        port_input.setText("56789");
        
        addToOutput("Willkommen beim Test-Client.");
        addToOutput("Tragen Sie eine IP-Adresse eines Test-Servers samt passenden Port oben ein. Die Nachricht können Sie überarbeiten.");
        addToOutput("Die Nachricht kann an den Server gesendet werden, der Server kann mit einer beliebingen Antwort antworten.");
        addToOutput("-----------------------------------------------------------------------------------");
    }
    
    
    /**
     * Es wird dem Output Text hinzugefügt.
     *
     * @param text
     */
    private void addToOutput(String text) {
        if (output_field.getText().isEmpty()) {
            output_field.setText(text);
        } else {
            output_field.setText(output_field.getText() + "\n" + text);
        }
    }
    
    /**
     * Es wird ein Objekt der Klasse EchoClient instanziiert.
     */
    private void connect() {
        client = new Control.EchoClient(ip_input.getText(), Integer.parseInt(port_input.getText()), this);
    }
    
    /**
     * Die Verbindung zum Server wird geschlossen.
     */
    private void closeConnection() {
        client.closeClient();
    }
    
    /**
     * An den Server wird die Nachricht geschickt, die sich im TextField message befindet. Diese wird über den client gesendet.
     */
    private void send() {
        if (!message_input.getText().equals("")) {
            client.send(this.message_input.getText());
            this.message_input.setText("");
        }
    }
    
    /**
     * Der Status der Knöpfe wird geändert.
     * Diese Methode sollte vom EchoClient-Objekt aufgerufen werden, sobald sich dieser mit einem Server verbunden oder die Verbindung geschlossen hat.
     */
    public void switchButtons() {
        connect_btn.setDisable(!connect_btn.isDisabled());
        disconnect_btn.setDisable(!disconnect_btn.isDisabled());
        send_btn.setDisable(!send_btn.isDisabled());
    }
    
    /**
     * Methode wird vom EchoClient aufgerufen, sobald dieser eine Nachricht erhalten und gefiltert hat.
     *
     * @param text
     */
    public void processMessage(String text) {
        addToOutput(text);
    }
    
}
