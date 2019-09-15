import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainProgramm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        
        Parent client = FXMLLoader.load(getClass().getResource("view/client/client.fxml"));
        Stage clientStage = new Stage();
        clientStage.setTitle("Client");
        clientStage.setScene(new Scene(client, 600, 400));
        clientStage.setResizable(false);
        clientStage.titleProperty();
        clientStage.show();
    
        Parent server = FXMLLoader.load(getClass().getResource("view/server/server.fxml"));
        Stage serverStage = primaryStage;
        serverStage.setTitle("Server");
        serverStage.setScene(new Scene(server, 600, 400));
        serverStage.setResizable(false);
        serverStage.show();
        
        
        clientStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
