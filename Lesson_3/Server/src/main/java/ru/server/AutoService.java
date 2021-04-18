package ru.server;


import java.sql.*;

public class AutoService {
    private static Connection connection;
    private static Statement statement;

    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");                                           // подключаем к проекту драйвер необход.  для соед. с БД
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");        // для соед. с БД
            statement = connection.createStatement();                                   // для созд. запросов
        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getNicknameByLoginAndPassword (String login, String password){
        String query = String.format("select nickname from \"main\".users where login='%s' and password='%s'",login,password);
        try {
            ResultSet rs = statement.executeQuery(query);                              // вернет выборку т.е. нашу команду (select)
            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  null;
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}