package Control;

import model.List;
import view.server.serverController;


public class EchoServer extends Control.Server {
    private serverController panelHandler;
    private List<String> clients;
    
    public EchoServer(int pPort, serverController panel) {
        super(pPort);
        this.clients = new List();
        this.panelHandler = panel;
        
        if (isOpen()) {
            this.panelHandler.buttonSwitch();
        } else {
            System.err.println("Problem beim starten");
            this.panelHandler.addToSyslog("Problem beim starten");
        }
    }
    
    
    public void processNewConnection(String pClientIP, int pClientPort) {
        this.clients.append(pClientIP + ":" + pClientPort);
        this.panelHandler.newConnection(pClientIP, pClientPort);
    }
    
    
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        this.panelHandler.processMessage(pClientIP, pClientPort, pMessage);
        sendToAll(pClientIP + " > " +pMessage);
    }
    
    
    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.clients.toFirst();
        while (this.clients.hasAccess()) {
            if (((String) this.clients.getContent()).toString().contains(pClientIP)) {
                this.clients.remove();
                continue;
            }
            this.clients.next();
        }
        
        
        this.panelHandler.closingConnection(pClientIP, pClientPort);
    }
    
    
    public void close() {
        super.close();
        this.panelHandler.buttonSwitch();
    }
    
    
    public String[] getClients() {
        if (!this.clients.isEmpty()) {
            int lenth = 0;
            
            this.clients.toFirst();
            while (this.clients.hasAccess()) {
                lenth++;
                this.clients.next();
            }
            
            String[] clientArr = new String[lenth];
            
            this.clients.toFirst();
            for (int i = 0; i < lenth; i++) {
                clientArr[i] = (String) this.clients.getContent();
                this.clients.next();
            }
            
            System.err.println("clients");
            for (String ip : clientArr) {
                System.err.println(ip);
            }
            return clientArr;
        }
        return new String[]{"0000:0000"};
    }
}

