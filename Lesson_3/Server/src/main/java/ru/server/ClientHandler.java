package ru.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private String nickname;                                                           // подключаем БД

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {

                 // подключаем БД
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth")) {                                    // подключ. по команде
                        String[] tokens = msg.split("\\s");                      // разбиваем строку
                        String nick = AutoService.getNicknameByLoginAndPassword(tokens[1],tokens[2]);
                        System.out.println(nick);
                        if (nick != null){                                             // если ник не равен нулю
                            sendMessage("/auth-OK "+ nick);
                            System.out.println(nick+ " successfully connected");
                            setNickName(nick);
                            server.subscribe(this);
                            break;                                                     // если все ОК закрываем соед.
                        }else {
                            sendMessage("/login_failed");                       // если не ОК печатаем
                        }
                    }
                }

//                while (true) { // Цикл авторизации
//                    String msg = in.readUTF();
//                    if (msg.startsWith("/login ")) {
//                        // login Bob
//                        String usernameFromLogin = msg.split("\\s")[1];
//
//                        if (server.isUserOnline(usernameFromLogin)) {
//                            sendMessage("/login_failed Указанное имя уже используется");
//                            continue;
//                        }
//
//                        username = usernameFromLogin;
//                        sendMessage("/login_ok " + username);
//                        server.subscribe(this);
//                        break;
//                    }
//                }

                while (true) { // Цикл общения с клиентом
                    String msg = in.readUTF();
     /*               if (msg.startsWith("/")) {
                        executeCommand(msg);
                        continue;
                    }*/
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
                AutoService.disconnect();                                           // подключаем БД
            }
        }).start();
    }

    private void setNickName(String nick) {                                          // подключаем БД добавляем ник
        this.nickname = nick;
    }

    private void executeCommand(String cmd) {
        // /@ Bob Hello, Bob!!!
        if (cmd.startsWith("/@ ")) {
            String[] tokens = cmd.split("\\s", 3);
            server.sendPrivateMessage(this, tokens[1], tokens[2]);
            return;
        }
        if (cmd.equals("/end")){
            disconnect();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
