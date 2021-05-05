package ru.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler {
    private static final Logger loger = Logger.getLogger(ClientHandler.class.getName());         // <-- создаем "Logger"
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    List<String> blacklist;


    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket, ExecutorService service) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = AutoService.getBlacklistByNickname(nick);

            startWorkerThread(service);

            loger.setLevel(Level.ALL);                                                                  // <-- добавляем
            Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);                                  // <-- добавляем

            Handler handler = new ConsoleHandler();                                      // <-- создаем "ConsoleHandler"
            handler.setLevel(Level.ALL);                                                                // <-- добавляем
            loger.log(Level.ALL,"Logger to the ClientHandler for new client is running...");  // <-- запись события

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWorkerThread(ExecutorService service) {
        service.execute(() -> {
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
                            loger.info("Клиент: " + nick + " прислал команду на выход из чата...");// <-- запись события
                            closeConnection();
                        }
                        if (msg.startsWith("/getBlack")){
                            loger.info("Клиент: " + nick + " запросил Вlacklist...");         // <-- запись события
                            sendMessage("Your blacklist: ");
                            for (int i = 0; i < blacklist.size() ; i++) {
                                sendMessage(blacklist.get(i));
                            }
                        }
                        if (msg.startsWith("/blacklist ")) {
                            loger.info("Клиент: " + nick + " редактирует Вlacklist...");      // <-- запись события
                            String[] token = msg.split(" ");
                            if (AutoService.getBlacklistByNickname(nick).contains(token[1])){
                                if (AutoService.deleteUserfromBlacklist(nick, token[1]) == 1) {
                                    sendMessage("Вы удалили "+token[1]+" из blacklist");
                                    blacklist.clear();
                                }else {
                                    sendMessage("Something Wrong! Exsclude");
                                    loger.warning("Something Wrong! Exsclude...");            // <-- запись события
                                }
                            }else {
                                if (AutoService.addUserToBlacklist(nick, token[1]) == 1) {
                                    blacklist.add(token[1]);
                                    sendMessage("Вы добавили "+token[1]+" в blacklist");
                                } else {
                                    sendMessage("Something Wrong! Add ");
                                    loger.warning("Something Wrong! Message NOT Add");        // <-- запись события
                                }
                            }
                        }
                        if(msg.equals("/getHistory")){
                            loger.info("Клиент: " + nick + " запросил историю сообщений..."); // <-- запись события
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
                loger.info("Входящий поток клиента: " + nick + " закрыт....");                // <-- запись события
                loger.info("Исходящий поток от клиента: " + nick + " закрыт....");            // <-- запись события
                loger.info("Socket клиента: " + nick + " закрыт....");                        // <-- запись события
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

