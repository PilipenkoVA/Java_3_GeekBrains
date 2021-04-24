package ru.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    List<String> blacklist;                                                                    // 2. создаем "Blacklist"


    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = AutoService.getBlacklistByNickname(nick);

            startWorkerThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWorkerThread() {
        new Thread(() -> {
            try {
                socket.setSoTimeout(120000);
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth ")) {
                        String[] tokens = msg.split(" ");
                        // 1. подключение БД
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
//                                    System.out.println(blacklist);
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
                    }
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
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
}
