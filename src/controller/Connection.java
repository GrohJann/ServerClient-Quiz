package Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Connection {
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;
    
    public Connection(String pServerIP, int pServerPort) {
        try {
            this.socket = new Socket(pServerIP, pServerPort);
            this.toServer = new PrintWriter(this.socket.getOutputStream(), true);
            this.fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (Exception e) {
            
            
            this.socket = null;
            this.toServer = null;
            this.fromServer = null;
        }
    }
    
    
    public String receive() {
        if (this.fromServer != null) {
            
            try {
                return this.fromServer.readLine();
            } catch (IOException iOException) {
            }
        }
        
        return null;
    }
    
    
    public void send(String pMessage) {
        if (this.toServer != null) {
            this.toServer.println(pMessage);
        }
    }
    
    
    public void close() {
        if (this.socket != null && !this.socket.isClosed())
            
            try {
                this.socket.close();
            } catch (IOException iOException) {
            }
    }
}