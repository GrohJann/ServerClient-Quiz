package Control;

import model.List;
import model.Player;
import model.Questions;
import view.server.serverController;


public class QuizServer extends Control.Server {
    private serverController panelHandler;
    private Player[] player;
    private Questions questions;
    private String[] questionArr;
    private boolean spielstart;
    private int round;
    private int playerConnected;
    private List<String> clients;

    public QuizServer(int pPort, serverController panel) {
        super(pPort);
        this.clients = new List();
        this.panelHandler = panel;

        this.questions = new Questions();
        this.player = new Player[3];
        this.playerConnected = 0;
        spielstart = false;
        round = 0;

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

    //TODO fertig stellen
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        String[] message = pMessage.split(">");
        //Anmeldung
        if (message[0].equals("ANMELDUNG") && playerConnected <= 3){
            if (!message[1].equals("")) {
                System.out.println("Client mit namen " + message[1] + " angemeldet");
                player[playerConnected] = new Player(message[1]);
                playerConnected++;
                send(pClientIP, pClientPort, "ANGEMELDET");
                switch (playerConnected){
                    case 1: sendToAll("WARTE>2");
                        break;
                    case 2: sendToAll("WARTE>1");
                        break;
                    case 3: sendToAll("SPIELSTART");
                        spielstart = true;
                        break;

                    default: System.err.println("too many or to less player\n  ---> ERROR in switch/case");
                }
            } else {
                send(pClientIP, pClientPort, "AUFFORDERUNG");
            }
        } else {
            send(pClientIP, pClientPort, "SERVERVOLL");
        }

        //Spielphase
        while (spielstart && round <= 10){
            round++;
            questionArr = questions.getRandQuestion();
            System.out.println("FRAGE>" + questionArr[0] + ">" + questionArr[1] + ">" + questionArr[2] + ">" + questionArr[3]);
            sendToAll("FRAGE>" + questionArr[0] + ">" + questionArr[1] + ">" + questionArr[2] + ">" + questionArr[3]);
            
        }
    }

    //TODO fertig stellen
    public void processMessage(String pMessage) {
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

