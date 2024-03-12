package org.example;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    public ClientHandler(Socket socket){
        try{
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String currentTime = dateFormat.format(currentDate);
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.clientUsername = bufferedReader.readLine();
        clientHandlers.add(this);
        broadcastMessage("Server: " + clientUsername + " has entered the chat!   " + currentTime);
        }
        catch(IOException e){
            closeAll(socket, bufferedWriter, bufferedReader);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try{
            messageFromClient = bufferedReader.readLine();
            broadcastMessage(messageFromClient);
            }
            catch(IOException e){
                closeAll(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler: clientHandlers) {
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch(IOException e) {
                closeAll(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    public void removeClient(){
        clientHandlers.remove(this);
        broadcastMessage("Server: " + clientUsername + " has left the chat!");
    }
    public void closeAll(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClient();
        try{
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
