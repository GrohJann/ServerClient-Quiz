package Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public abstract class Client {
    private MessageHandler messageHandler;
    
    private class MessageHandler extends Thread {
        private SocketWrapper socketWrapper;
        private boolean active;
        
        private class SocketWrapper {
            private Socket socket;
            private BufferedReader fromServer;
            private PrintWriter toServer;
            
            public SocketWrapper(String pServerIP, int pServerPort) {
                try {
                    this.socket = new Socket(pServerIP, pServerPort);
                    this.toServer = new PrintWriter(this.socket.getOutputStream(), true);
                    this.fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                } catch (IOException e) {
                    
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
                if (this.socket != null) {
                    
                    try {
                        this.socket.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }
        
        
        private MessageHandler(String pServerIP, int pServerPort) {
            this.socketWrapper = new SocketWrapper(pServerIP, pServerPort);
            start();
            if (this.socketWrapper.socket != null) {
                this.active = true;
            }
        }
        
        public void run() {
            String message = null;
            while (this.active) {
                
                message = this.socketWrapper.receive();
                if (message != null) {
                    Client.this.processMessage(message);
                    continue;
                }
                close();
            }
        }
        
        
        private void send(String pMessage) {
            if (this.active) {
                this.socketWrapper.send(pMessage);
            }
        }
        
        private void close() {
            if (this.active) {
                
                this.active = false;
                this.socketWrapper.close();
            }
        }
    }
    
    
    public Client(String pServerIP, int pServerPort) {
        this.messageHandler = new MessageHandler(pServerIP, pServerPort);
    }
    
    
    public boolean isConnected() {
        return this.messageHandler.active;
    }
    
    
    public void send(String pMessage) {
        this.messageHandler.send(pMessage);
    }
    
    
    public void close() {
        this.messageHandler.close();
    }
    
    public abstract void processMessage(String paramString);
}
