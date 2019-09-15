package Control;

import model.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class Server {
    private NewConnectionHandler connectionHandler;
    private List<ClientMessageHandler> messageHandlers;
    
    private class NewConnectionHandler
            extends Thread {
        private ServerSocket serverSocket;
        private boolean active;
        
        public NewConnectionHandler(int pPort) {
            try {
                this.serverSocket = new ServerSocket(pPort);
                start();
                this.active = true;
            } catch (Exception e) {
                
                this.serverSocket = null;
                this.active = false;
            }
        }
        
        
        public void run() {
            while (this.active) {
                
                
                try {
                    
                    Socket clientSocket = this.serverSocket.accept();
                    
                    
                    Server.this.addNewClientMessageHandler(clientSocket);
                    Server.this.processNewConnection(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                    
                } catch (IOException iOException) {
                }
            }
        }
        
        
        public void close() {
            this.active = false;
            if (this.serverSocket != null) {
                
                try {
                    this.serverSocket.close();
                } catch (IOException iOException) {
                }
            }
        }
    }
    
    
    private class ClientMessageHandler
            extends Thread {
        private ClientSocketWrapper socketWrapper;
        
        private boolean active;
        
        
        private class ClientSocketWrapper {
            private Socket clientSocket;
            
            private BufferedReader fromClient;
            
            private PrintWriter toClient;
            
            
            public ClientSocketWrapper(Socket pSocket) {
                try {
                    this.clientSocket = pSocket;
                    this.toClient = new PrintWriter(this.clientSocket.getOutputStream(), true);
                    this.fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                } catch (IOException e) {
                    
                    this.clientSocket = null;
                    this.toClient = null;
                    this.fromClient = null;
                }
            }
            
            
            public String receive() {
                if (this.fromClient != null) {
                    
                    try {
                        return this.fromClient.readLine();
                    } catch (IOException iOException) {
                    }
                }
                
                return null;
            }
            
            
            public void send(String pMessage) {
                if (this.toClient != null) {
                    this.toClient.println(pMessage);
                }
            }
            
            
            public String getClientIP() {
                if (this.clientSocket != null) {
                    return this.clientSocket.getInetAddress().getHostAddress();
                }
                return null;
            }
            
            
            public int getClientPort() {
                if (this.clientSocket != null) {
                    return this.clientSocket.getPort();
                }
                return 0;
            }
            
            
            public void close() {
                if (this.clientSocket != null) {
                    
                    try {
                        this.clientSocket.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }
        
        
        private ClientMessageHandler(Socket pClientSocket) {
            this.socketWrapper = new ClientSocketWrapper(pClientSocket);
            if (pClientSocket != null) {
                
                start();
                this.active = true;
            } else {
                
                this.active = false;
            }
        }
        
        
        public void run() {
            String message = null;
            while (this.active) {
                
                message = this.socketWrapper.receive();
                if (message != null) {
                    Server.this.processMessage(this.socketWrapper.getClientIP(), this.socketWrapper.getClientPort(), message);
                    continue;
                }
                ClientMessageHandler aMessageHandler = Server.this.findClientMessageHandler(this.socketWrapper.getClientIP(), this.socketWrapper.getClientPort());
                if (aMessageHandler != null) {
                    
                    aMessageHandler.close();
                    Server.this.removeClientMessageHandler(aMessageHandler);
                    Server.this.processClosingConnection(this.socketWrapper.getClientIP(), this.socketWrapper.getClientPort());
                }
            }
        }
        
        
        public void send(String pMessage) {
            if (this.active) {
                this.socketWrapper.send(pMessage);
            }
        }
        
        public void close() {
            if (this.active) {
                
                this.active = false;
                this.socketWrapper.close();
            }
        }
        
        
        public String getClientIP() {
            return this.socketWrapper.getClientIP();
        }
        
        
        public int getClientPort() {
            return this.socketWrapper.getClientPort();
        }
    }
    
    
    public Server(int pPort) {
        this.connectionHandler = new NewConnectionHandler(pPort);
        this.messageHandlers = new List();
    }
    
    
    public boolean isOpen() {
        return this.connectionHandler.active;
    }
    
    
    public boolean isConnectedTo(String pClientIP, int pClientPort) {
        ClientMessageHandler aMessageHandler = findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null) {
            return aMessageHandler.active;
        }
        return false;
    }
    
    
    public void send(String pClientIP, int pClientPort, String pMessage) {
        ClientMessageHandler aMessageHandler = findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null) {
            aMessageHandler.send(pMessage);
        }
    }
    
    public void sendToAll(String pMessage) {
        synchronized (this.messageHandlers) {
            
            this.messageHandlers.toFirst();
            while (this.messageHandlers.hasAccess()) {
                
                ((ClientMessageHandler) this.messageHandlers.getContent()).send(pMessage);
                this.messageHandlers.next();
            }
        }
    }
    
    
    public void closeConnection(String pClientIP, int pClientPort) {
        ClientMessageHandler aMessageHandler = findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null) {
            
            processClosingConnection(pClientIP, pClientPort);
            aMessageHandler.close();
            removeClientMessageHandler(aMessageHandler);
        }
    }
    
    
    public void close() {
        this.connectionHandler.close();
        
        synchronized (this.messageHandlers) {
            
            
            this.messageHandlers.toFirst();
            while (this.messageHandlers.hasAccess()) {
                
                ClientMessageHandler aMessageHandler = (ClientMessageHandler) this.messageHandlers.getContent();
                processClosingConnection(aMessageHandler.getClientIP(), aMessageHandler.getClientPort());
                aMessageHandler.close();
                this.messageHandlers.remove();
            }
        }
    }
    
    
    private void addNewClientMessageHandler(Socket pClientSocket) {
        synchronized (this.messageHandlers) {
            
            this.messageHandlers.append(new ClientMessageHandler(pClientSocket));
        }
    }
    
    
    private void removeClientMessageHandler(ClientMessageHandler pClientMessageHandler) {
        synchronized (this.messageHandlers) {
            
            this.messageHandlers.toFirst();
            while (this.messageHandlers.hasAccess()) {
                
                if (pClientMessageHandler == this.messageHandlers.getContent()) {
                    
                    this.messageHandlers.remove();
                    
                    return;
                }
                this.messageHandlers.next();
            }
        }
    }
    
    
    private ClientMessageHandler findClientMessageHandler(String pClientIP, int pClientPort) {
        synchronized (this.messageHandlers) {
            
            
            this.messageHandlers.toFirst();
            
            while (this.messageHandlers.hasAccess()) {
                
                ClientMessageHandler aMessageHandler = (ClientMessageHandler) this.messageHandlers.getContent();
                if (aMessageHandler.getClientIP().equals(pClientIP) && aMessageHandler.getClientPort() == pClientPort)
                    return aMessageHandler;
                this.messageHandlers.next();
            }
            return null;
        }
    }
    
    public abstract void processNewConnection(String paramString, int paramInt);
    
    public abstract void processMessage(String paramString1, int paramInt, String paramString2);
    
    public abstract void processClosingConnection(String paramString, int paramInt);
}
