package ru.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private ServerSocket serverSocket;
    private Vector<ClientHandler> clients;

    public Server() {
        try {
            // 1. запуск БД
            AutoService.connect();
            serverSocket = new ServerSocket(8489);
            clients = new Vector<ClientHandler>();
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент "+socket.getInetAddress()+" пытается подключится");
                new ClientHandler(this, socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 1. закрытие БД
            AutoService.disconnect();
        }
    }

    public void sendPrivateMsg(ClientHandler from, String nickname, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickname)) {
                o.sendMessage(from.getNick()+" [Отправил для "+ nickname + "] Сообщение: " + msg);
                from.sendMessage("Пользователю: " + nickname + " Сообщение: " + msg);
                return;
            }
        }
        from.sendMessage("Клиент " + nickname + " отсутствует");
    }
    public void broadcastMsg(ClientHandler client, String msg) {
        String outMsg = client.getNick() + ": " + msg;
        for (ClientHandler o : clients) {
            // 4. если клиент в "Blacklist" то от его сообщения не получаем
            if (!o.checkBlackList(client.getNick())) {
                o.sendMessage(outMsg);

                // 5.записываем все сообщения в БД
                AutoService.addRecordToDB(client.getNick(),o.getNick(),msg);
            }
        }
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientslist ");
        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }
        String out = sb.substring(0, sb.length() - 1);
        for (ClientHandler o : clients) {
            o.sendMessage(out);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }
}

