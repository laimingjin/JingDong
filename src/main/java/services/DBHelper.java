package services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by minlai on 2016/11/17.
 */
public class DBHelper {
    private String SSH_HOST = "";
    private String SSH_PORT = "";
    private String SSH_USERNAME = "";
    private String SSH_PASSWORD = "";
    private String REMOTE_HOST = "";
    private String REMOTE_PORT = "";
    private String DB_HOST = "";
    private String DB_PORT = "";
    private String DB_USERNAME = "";
    private String DB_PASSWORD = "";
    private String DB_DATABASE = "";
    public  Connection connection = null;

    public DBHelper() {
        try {
            this.connection = getConnection();
        } catch (Exception e) {
            System.out.println("error connection");

        }
    }

    private void getDBConfig() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/databaseConfig.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        String jsonContext = "";
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            if (!line.trim().equals("]")) {
                jsonContext += line;
            }
        }
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonContext).getAsJsonObject();
        SSH_HOST = change(jsonObject.get("ssh_host").toString());
        SSH_PORT = change(jsonObject.get("ssh_port").toString());
        SSH_USERNAME = change(jsonObject.get("ssh_username").toString());
        SSH_PASSWORD = change(jsonObject.get("ssh_password").toString());
        REMOTE_HOST = change(jsonObject.get("remote_host").toString());
        REMOTE_PORT = change(jsonObject.get("remote_port").toString());
        DB_HOST = change(jsonObject.get("db_host").toString());
        DB_PORT = change(jsonObject.get("db_port").toString());
        DB_USERNAME = change(jsonObject.get("db_username").toString());
        DB_PASSWORD = change(jsonObject.get("db_password").toString());
        DB_DATABASE = change(jsonObject.get("db_database").toString());

    }

    private String change(String s) {
        return s.substring(1, s.length() - 1);
    }

    private  void connectSSH()throws Exception{
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(SSH_USERNAME, SSH_HOST, Integer.parseInt(SSH_PORT));
            session.setPassword(SSH_PASSWORD);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.connect();
            //System.out.println("SSH connected");

            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();

            //System.out.println(session.getServerVersion());//这里打印SSH服务器版本信息
            int assinged_port = session.setPortForwardingL(Integer.parseInt(DB_PORT), REMOTE_HOST, Integer.parseInt(REMOTE_PORT));
            //System.out.println("localhost:" + assinged_port + " -> " + REMOTE_HOST + ":" + REMOTE_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Connection getConnection() throws Exception {
        getDBConfig();
        connectSSH();
        Connection connection = null;
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(DB_HOST);
            dataSource.setPortNumber(Integer.parseInt(DB_PORT));
            dataSource.setUser(DB_USERNAME);
            dataSource.setAllowMultiQueries(true);
            dataSource.setPassword(DB_PASSWORD);
            dataSource.setDatabaseName(DB_DATABASE);
            dataSource.setCharacterEncoding("utf8");
            connection = dataSource.getConnection();
            //System.out.print("Connection to server successful!:" + connection + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void closeConnection() {
        try {
            Connection connection = this.connection;
            if (connection != null && !connection.isClosed()) {
                //System.out.println("close connection");
                connection.close();
            }
        } catch (SQLException sqle) {
            System.out.println("Error while closing connection.");
        }
    }

    public ResultSet queryWithParams(Connection connection,String query, ArrayList<String> params) throws Exception {
        ResultSet rs = null;
        try {
            PreparedStatement prepStmt = connection.prepareStatement(query);
            int count = 1;
            for (String param : params) {
                prepStmt.setString(count, param);
                count++;
            }

            rs = prepStmt.executeQuery();
            if (rs.next()) {
                // System.out.println("data valid in DB");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
        return rs;
    }

    public void add(Connection connection,String sql, ArrayList<String> params)throws Exception{
        try {
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            int count = 1;
            for (String param : params) {
                prepStmt.setString(count, param);
                count++;
            }
            prepStmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }

    }
    public void update(Connection connection,InputStream inputStream,String sql){
        try {
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            prepStmt.setBinaryStream(1, inputStream,inputStream.available());
            prepStmt.executeUpdate();
            System.out.println(sql);
            closeConnection();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }
    }
    
    
    
//    private  String DB_HOST="localhost";
//    private  String DB_PORT="3306";
//    private  String DB_USERNAME="root";
//    private  String DB_PASSWORD="";
//    private  String DB_DATABASE="SLNG_EMAIL_AUTO_GEN";
//    public Connection connection=null;
//
//
//    public DBHelper() {
//        try {
//            this.connection = getConnection();
//        } catch (Exception e) {
//            System.out.println("error connection");
//
//        }
//    }
//    private Connection getConnection()throws Exception{
//        Connection connection=null;
//        try {
//            String driver = "com.mysql.jdbc.Driver";
//            String url = "jdbc:mysql://" + DB_HOST +":"+DB_PORT+ "/" + DB_DATABASE + "?user=" + DB_USERNAME + "&password=" + DB_PASSWORD;
//            System.out.println(url);
//            Class.forName(driver);
//            connection = DriverManager.getConnection(url);
//        }catch (SQLException sqle){
//            System.out.println("SQLException: Unable to open connection to db: "+sqle.getMessage());
//            throw sqle;
//        }catch (Exception e){
//            System.out.println("Exception: Unable to open connection to db: "+e.getMessage());
//            throw e;
//        }
//
//        return connection;
//    }
//    public void closeConnection() {
//        try {
//            Connection connection = this.connection;
//            if (connection != null && !connection.isClosed()) {
//                System.out.println("close connection");
//                connection.close();
//            }
//        } catch (SQLException sqle) {
//            System.out.println("Error while closing connection.");
//        }
//    }
//
//    public ResultSet queryWithParams(Connection connection, String query, ArrayList<String> params) throws Exception {
//
//        ResultSet rs = null;
//        try {
//            PreparedStatement prepStmt = connection.prepareStatement(query);
//            int count = 1;
//            for (String param : params) {
//                prepStmt.setString(count, param);
//                count++;
//            }
//
//            rs = prepStmt.executeQuery();
//            if (rs.next()) {
//                // System.out.println("data valid in DB");
//            }
//        } catch (Exception e) {
//            System.out.println("Error: " + e.getMessage());
//            throw e;
//        }
//        return rs;
//    }
}