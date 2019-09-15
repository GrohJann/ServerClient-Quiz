package Control;

import view.client.clientController;



public class EchoClient
        extends Control.Client
{
    private clientController panelHandler;
    
    public EchoClient(String pServerIP, int pServerPort, clientController panelHandler) {
        super(pServerIP, pServerPort);
        
        this.panelHandler = panelHandler;
        
        
        if (isConnected()) {
            this.panelHandler.switchButtons();
        } else {
            System.err.println("Es konnte keine Verbindung hergestellt werden. ?berpr?fen sie ob IP und Port richtig eingetragen sind.");
        }
    }
    
    
    
    
    public void processMessage(String pMessage) {
        if (pMessage != null)
            this.panelHandler.processMessage(pMessage);
    }
    
    

    public void closeClient() {
        if (this.isConnected()) {
            close();
            this.panelHandler.switchButtons();
        }
    }
}