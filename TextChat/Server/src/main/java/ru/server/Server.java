package ru.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class Server {
    private static final Logger loger = Logger.getLogger(Server.class.getName());                // <-- создаем "Logger"

    private ServerSocket serverSocket;
    private Vector<ClientHandler> clients;
    private ExecutorService service;

    public Server() {
        loger.setLevel(Level.ALL);                                                                      // <-- добавляем
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);                                      // <-- добавляем

        Handler handler = new ConsoleHandler();                                          // <-- создаем "ConsoleHandler"
        handler.setLevel(Level.ALL);                                                                    // <-- добавляем
        loger.log(Level.ALL,"Logger to the Server writer...");                                     // <-- добавляем
        try {
            // 1. запуск БД
            AutoService.connect();
            service = Executors.newFixedThreadPool(1000);
            serverSocket = new ServerSocket(8689);
            clients = new Vector<ClientHandler>();
            System.out.println("Сервер запущен");
            loger.info("Сервер запущен...");                                                  // <-- запись события
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент "+socket.getInetAddress()+" пытается подключится");
                loger.info("\"Клиент "+socket.getInetAddress()+" пытается подключится...");   // <-- запись события
                new ClientHandler(this, socket, service);
                System.out.println("Клиент "+socket.getInetAddress()+" подключился");
                loger.info("\"Клиент "+socket.getInetAddress()+" подключился...");            // <-- запись события
                System.out.println("Ожидание нового клиента");
                loger.info("Ожидание нового клиента...");                                     // <-- запись события
            }
        } catch (Exception e) {
            e.printStackTrace();
            loger.warning("Exception...");                                                    // <-- запись события
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                loger.warning("Exception...");                                                // <-- запись события
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
                loger.info("Клиент: "+from.getNick()+" отправил личное сообщение: "+nickname);// <-- запись события
                // записываем личные сообщения в историю
                AutoService.addRecordToDB(from.getNick(),nickname,from.getNick() + ": [прислал вам личное сообщение]: " + msg);
                return;
            }
        }
        from.sendMessage("Клиент " + nickname + " отсутствует");
    }
    public void broadcastMsg(ClientHandler client, String msg) {
        String outMsg = client.getNick() + ": " + msg;
        loger.info("Клиент: "+client.getNick()+" отправил сообщение в общий чат");            // <-- запись события
        for (ClientHandler o : clients) {
            // если клиент в "Blacklist" то от его сообщения не получаем
            if (!o.checkBlackList(client.getNick())) {
                o.sendMessage(outMsg);

                // записываем все сообщения в БД
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

