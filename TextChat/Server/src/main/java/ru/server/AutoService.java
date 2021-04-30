package ru.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutoService {
    private static Connection connection;
    private static Statement statement;

    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");                                           // 1. подключаем к проекту драйвер необход.  для соед. с БД
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");        // 1. для соед. с БД
            statement = connection.createStatement();                                   // 1. для созд. запросов
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getNicknameByLoginAndPassword (String login, String password){
        String query = String.format("select nickname from \"main\".users where login='%s' and password='%s'",login,password);
        try {
            // 1. вернет выборку т.е. нашу команду (select)
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }
    // 3. добавляем в "Blacklist"
    public static int addUserToBlacklist(String owner, String blackClient){
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO blacklist (owner, black) VALUES (?, ?)");
            ps.setString(1, owner);
            ps.setString(2, blackClient);
            return ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(ps);
        }
        return 0;
    }
    // 3. удаляем из "Blacklist"
    public static int deleteUserfromBlacklist(String owner, String blackClient){
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("DELETE FROM blacklist WHERE owner = ? AND black = ?");
            ps.setString(1, owner);
            ps.setString(2, blackClient);
            return ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(ps);
        }
        return 0;
    }
    // 3. созд. и выводим. список в "Blacklist"
    public static List<String> getBlacklistByNickname(String nickname){
        List<String> blacklist = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement("SELECT * FROM blacklist WHERE owner = ?");
            ps.setString(1, nickname);
            rs = ps.executeQuery();

            while (rs.next()){
                blacklist.add(rs.getString(2));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            resultSetClose(rs);
            statementClose(ps);
        }
        return blacklist;
    }

    private static void resultSetClose(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {

        }
    }

    private static void statementClose(PreparedStatement ps) {
        try {
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // 5. добавляем все сообщения в таблицу "messages" создаем Историю.
    public static int addRecordToDB(String client,String toNick, String message){
        int result=-1;
        String requestToAdd = String.format("INSERT INTO messages ('fromNick','toNick','message') VALUES ('%s','%s','%s');",client,toNick,message);
        try {
            result = statement.executeUpdate(requestToAdd);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getHistoryListByNickname(String nickname){
        List<String> historyList =new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs =null;
        try {
            ps = connection.prepareStatement("SELECT * FROM messages WHERE toNick=?");
            ps.setString(1,nickname);
            rs = ps.executeQuery();
            while(rs.next()){
                historyList.add(rs.getString("message"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            statementClose(ps);
            resultSetClose(rs);
        }
        return historyList;
    }
}

