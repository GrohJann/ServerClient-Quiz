package view.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Class Created by Jannis
 */
public class clientController implements Initializable {
    
    private Control.QuizClient client;
    
    @FXML
    private JFXTextField ip_input;
    @FXML
    private JFXTextField port_input;
    @FXML
    private JFXListView player_list;
    @FXML
    private JFXTextArea question_field;
    @FXML
    private JFXButton connect_btn;
    @FXML
    private JFXButton disconnect_btn;
    @FXML
    private JFXButton answer_one;
    @FXML
    private JFXButton answer_two;
    @FXML
    private JFXButton answer_three;
    
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
        answer_one.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                send(1);
            }
        });
        answer_two.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                send(2);
            }
        });
        answer_three.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                send(3);
            }
        });
        
        ip_input.setText("127.0.0.1");
        port_input.setText("56789");
        
        addToOutput("Willkommen beim Quiz-Client.");
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
        if (question_field.getText().isEmpty()) {
            question_field.setText(text);
        } else {
            question_field.setText(question_field.getText() + "\n" + text);
        }
    }
    
    /**
     * Es wird ein Objekt der Klasse QuizClient instanziiert.
     */
    private void connect() {
        client = new Control.QuizClient(ip_input.getText(), Integer.parseInt(port_input.getText()), this);
        client.send("ANMELDUNG>Jannis");
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
    private void send(int answer) {
        if (answer <= 3){
            //TODO hier muss was gemacht werden
            System.out.println("Answer " + answer + " pressed");
        }
    }
    
    /**
     * Der Status der Knöpfe wird geändert.
     * Diese Methode sollte vom QuizClient-Objekt aufgerufen werden, sobald sich dieser mit einem Server verbunden oder die Verbindung geschlossen hat.
     */
    public void switchButtons() {
        connect_btn.setDisable(!connect_btn.isDisabled());
        disconnect_btn.setDisable(!disconnect_btn.isDisabled());
        answer_one.setDisable(!answer_one.isDisabled());
        answer_two.setDisable(!answer_two.isDisabled());
        answer_three.setDisable(!answer_three.isDisabled());
    }
    
    /**
     * Methode wird vom QuizClient aufgerufen, sobald dieser eine Nachricht erhalten und gefiltert hat.
     *
     * @param text
     */
    public void processMessage(String text) {
        addToOutput(text);
    }
    
    //TODO methode zu anzeigen der Spieler mit punktzahl
}
