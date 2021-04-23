package ru.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class History {
    private static PrintWriter out;   // <-- для записи будем использовать "PrintWriter"

    //   Метод  для создания уникального имени  файла:
    // - чтобы далее ни где в имени не допустить ошибку
    // - чтобы у каждого клиента был свой файл для истории сообщений
    private static String getHistoryFilenameByLogin (String login){
        return "history/history_"+login+".txt";
    }
    // Метод который будет открывать файл и записывать сообщения
    public static void start(String login){
        try {
            out = new PrintWriter(new FileOutputStream(getHistoryFilenameByLogin(login),true), true);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
    // Метод который будет закрывать файл
    public static void stop(){
        if (out != null){
            out.close();
        }
    }
    // Метод записи одной строки
    public static void writerLine(String msg){
        out.println(msg);
    }
    // Метод для чтения 100 последних строк
    public static String get100Lines (String login){
        // если нет файла т.е. клиент зашел первый раз, то клиенту вернется пустая строка
        if (!Files.exists(Paths.get(getHistoryFilenameByLogin(login)))){
            return  "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            List<String> historyLines = Files.readAllLines(Paths.get(getHistoryFilenameByLogin(login)));
            int startPosition = 0;                                // стартовая позиция для чтения
            if (historyLines.size()>100){                         // если строк больше чем 100 то
                startPosition = historyLines.size() - 100;        // переносим стартовую позицию
            }
            for (int i = startPosition; i < historyLines.size() ; i++) {
                sb.append(historyLines.get(i)).append(System.lineSeparator());
            }
        }catch (IOException e){
            e.printStackTrace();
        }return sb.toString();
    }

}
