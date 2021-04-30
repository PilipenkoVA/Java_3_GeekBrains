package ru.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    List<String> blacklist;


    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket, ExecutorService service) {         // <-- добавил ExecutorService
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = AutoService.getBlacklistByNickname(nick);

            startWorkerThread(service);                                                           // <-- добавил service
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWorkerThread(ExecutorService service) {                              // <-- добавил ExecutorService
        service.execute(() -> {                                                          // <-- запустил ExecutorService
            try {
                socket.setSoTimeout(120000);
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth ")) {
                        String[] tokens = msg.split(" ");
                        // подключение БД
                        String nick = AutoService.getNicknameByLoginAndPassword(tokens[1],tokens[2]);
                        if (nick != null) {
                            if (server.isNickBusy(nick)) {
                                out.writeUTF("Учетная запись уже используется");
                                continue;
                            }
                            this.nick = nick;
                            out.writeUTF("/auth_OK " + nick);
                            socket.setSoTimeout(0);
                            server.subscribe(this);
                            readHistory(100);
                            break;
                        } else {
                            out.writeUTF("Неверный логин/пароль");
                        }
                    }
                }
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/@ ")) {
                            String[] tokens = msg.split(" ", 3);
                            server.sendPrivateMsg(this, tokens[1], tokens[2]);
                        }
                        if(msg.startsWith("/end")) {
                            closeConnection();
                        }
                        if (msg.startsWith("/getBlack")){
                            sendMessage("Your blacklist: ");
                            for (int i = 0; i < blacklist.size() ; i++) {
                                sendMessage(blacklist.get(i));
                            }
                        }
                        if (msg.startsWith("/blacklist ")) {
                            String[] token = msg.split(" ");
                            if (AutoService.getBlacklistByNickname(nick).contains(token[1])){
                                if (AutoService.deleteUserfromBlacklist(nick, token[1]) == 1) {
                                    sendMessage("Вы удалили "+token[1]+" из blacklist");
                                    blacklist.clear();
                                }else {
                                    sendMessage("Something Wrong! Exsclude");
                                }
                            }else {
                                if (AutoService.addUserToBlacklist(nick, token[1]) == 1) {
                                    blacklist.add(token[1]);
                                    sendMessage("Вы добавили "+token[1]+" в blacklist");
                                } else {
                                    sendMessage("Something Wrong! Add ");
                                }
                            }
                        }
                        if(msg.equals("/getHistory")){
                            List<String> historyList=AutoService.getHistoryListByNickname(nick);
                            sendMessage("----History Loaded----");
                            for (int i = 0; i < historyList.size() ; i++) {
                                sendMessage(historyList.get(i));
                            }
                            historyList.clear();
                        }
                    } else {
                        server.broadcastMsg(this, msg);
                        writeHistory(msg);
                    }
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        });
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        server.unsubscribe(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBlackList(String nick) {
        return blacklist.contains(nick);
    }
    public void readHistory(int n){
        File file =new File("test.txt");
        try{
            RandomAccessFile raf=new RandomAccessFile(file,"r");
            long length = file.length()-1;
            int readLine=0;
            StringBuilder sb =new StringBuilder();
            for(long i=length;i >=0; i--){
                raf.seek(i);
                char c=(char) raf.read();
                if(c == '\n'){
                    readLine++;
                    if(readLine == n){
                        break;
                    }
                }
                sb.append(c);
            }
            sendMessage(String.valueOf(sb.reverse()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeHistory (String msg){
        try(FileWriter writer = new FileWriter("test.txt", true))
        {
            String text =(nick + ": " + msg + "\n");
            writer.write(text);
            writer.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

