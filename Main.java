import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;


    public static void main(String[] args) {
        try {
            connect();
            readFile();

            selectExRange();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void rollbackEx() throws SQLException {
        stmt.executeUpdate("INSERT INTO goodsTable (prodid, title, cost) VALUES ('101101', 'car', 50000);");
        Savepoint sp1 = connection.setSavepoint();
        stmt.executeUpdate("INSERT INTO goodsTable (prodid, title, cost) VALUES ('102102', 'car', 50000);");
        connection.rollback(sp1);
        stmt.executeUpdate("INSERT INTO goodsTable (prodid, title, cost) VALUES ('103103', 'car', 50000);");
        connection.setAutoCommit(true);
    }


    private static void transactionEx() throws SQLException {
        connection.setAutoCommit(false);
        long t = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            stmt.executeUpdate("INSERT INTO goodsTable (prodid, title, cost) VALUES (" + i + ", 'car', 100);");
        }
        System.out.println(System.currentTimeMillis() - t);
        connection.setAutoCommit(true);
    }

    private static void createTableEx() throws SQLException {

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS goodsTable (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    prodid  TEXT UNIQUE,\n" +
                "    title TEXT,\n" +
                "    cost INTEGER\n" +
                ");");
    }

    private static void dropTableEx() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS goodsTable;");
    }


    private static void clearTableEx() throws SQLException {
        stmt.executeUpdate("DELETE FROM goodsTable;");
    }

    private static void deleteOneEntryEx() throws SQLException {
        stmt.executeUpdate("DELETE FROM goodsTable WHERE id = 5;");
    }

    private static void updateEx() throws SQLException {
        System.out.println("изменяем");
        Scanner scanner = new Scanner(System.in);
        String cost = scanner.nextLine();
        String id = scanner.nextLine();
        String sql = String.format("UPDATE goodsTable SET cost = '%s' WHERE id = '%s';", cost, id);
        stmt.executeUpdate(sql);
    }

    private static void insertEx() throws SQLException {
        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob4', 100);");
    }
   private static void selectEx() throws SQLException {
        System.out.println("ищем");
        Scanner scanner = new Scanner(System.in);
        String res = scanner.nextLine();
        String sql = String.format("SELECT cost FROM goodsTable where prodid = '%s';", res);
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("cost"));
        }
    }

    
    private static void selectExRange() throws SQLException {
        System.out.println("ищем в диапазоне");
        Scanner scanner = new Scanner(System.in);
        String low = scanner.nextLine();
        String hight = scanner.nextLine();
        String sql = String.format("SELECT prodid FROM goodsTable where cost BETWEEN '%s' AND '%s' ORDER BY  prodid;", low, hight);
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString("prodid"));
        }
    }

    public static void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readFile() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("update.txt");
        Scanner scanner = new Scanner(fileInputStream);

        while (scanner.hasNext()) {
            String[] mass = scanner.nextLine().split(" ");
            try {
                updateDB(mass[0], mass[1]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateDB(String id, String newValue) throws SQLException {
        String sql = String.format("UPDATE students SET score = %s where id = %s", newValue, id);
        stmt.executeUpdate(sql);
    }
}
