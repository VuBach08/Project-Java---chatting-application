package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerThreadBus {
    private List<ServerThread> listServerThreads;

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public ServerThreadBus() {
        listServerThreads = new ArrayList<>();
    }

    public void add(ServerThread serverThread){
        listServerThreads.add(serverThread);
    }

    public void mutilCastSend(String message){ //like sockets.emit in socket.io
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            try {
                serverThread.write(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void boardCast(String id, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if (!serverThread.getuserID().equals(id)) {
                continue;
            } else {
            	System.out.println(id+"|"+message);
                try {
                    serverThread.write(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.print(ex.getMessage());
                }
            }
        }
    }

    public void boardCastUser(String user_id, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
        	if(serverThread.getActualUserID() != null) {
            if (!serverThread.getActualUserID().equals(user_id)) {
                continue;
            } else {
                try {
                    serverThread.write(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        }
    }

    public int getLength(){
        return listServerThreads.size();
    }

    public void remove(String id){
        for(int i=0; i<Server.serverThreadBus.getLength(); i++){
            if(Server.serverThreadBus.getListServerThreads().get(i).getuserID()==id){
                Server.serverThreadBus.listServerThreads.remove(i);
            }
        }
    }
}
