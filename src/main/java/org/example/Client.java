package org.example;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }
        catch(IOException e) {
            closeAll(socket, bufferedWriter, bufferedReader);
        }
    }
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            int numberofMessages = 0;
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                String currentTime = dateFormat.format(currentDate);
                numberofMessages++;
                bufferedWriter.write(username + ": " + messageToSend + "   " + currentTime + "  " + numberofMessages + " message(s)");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
        catch(IOException e) {
            closeAll(socket, bufferedWriter, bufferedReader);
        }
    }
    public void listenForMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChatRoom;
                while(socket.isConnected()){
                    try {
                        msgFromChatRoom = bufferedReader.readLine();
                        System.out.println(msgFromChatRoom);
                    }
                    catch(IOException e){
                        closeAll(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
        }).start();
    }
    public void closeAll(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        Socket ss = new Socket("localhost", 3333);
        Client client = new Client(ss, username);
        client.listenForMessages();
        client.sendMessage();
    }
}
