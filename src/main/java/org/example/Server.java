package org.example;
import java.net.*;
import java.io.*;
public class Server {
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new User has Connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        catch(IOException e){

        }
    }
    public void closeServer(){
        try{
            if(serverSocket !=null){
                serverSocket.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(3333);
        Server server = new Server(ss);
        server.startServer();
    }
}
