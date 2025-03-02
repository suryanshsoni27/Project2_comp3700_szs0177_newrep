import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class ProductServer {
    static String dbfile = "C:\\Users\\hsoni\\Desktop\\comp3700\\Project1_data.db";

    public static void main(String[] args) {

        int port = 6000;

        if (args.length > 0) {
            System.out.println( "Running arguments: " );
            for (String arg : args)
                System.out.println( arg );
            port = Integer.parseInt( args[0] );
            dbfile = args[1];
        }

        try {
            ServerSocket server = new ServerSocket( port );

            System.out.println( "Server is listening at port = " + port );

            while (true) {
                Socket pipe = server.accept();
                PrintWriter out = new PrintWriter( pipe.getOutputStream(), true );
                Scanner in = new Scanner( pipe.getInputStream() );

                String command = in.nextLine();
                if (command.equals( "GET" )) {
                    String str = in.nextLine();
                    System.out.println( "GET product with id = " + str );
                    int productID = Integer.parseInt( str );

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection( url );

                        String sql = "SELECT * FROM Products WHERE Productid = " + productID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery( sql );

                        if (rs.next()) {
                            out.println(rs.getString( "supplier" ));//send back supplier name
                            out.println( rs.getString( "Name" ) ); // send back product name!
                            out.println( rs.getDouble( "Price" ) ); // send back product price!
                            out.println( rs.getDouble( "Quantity" ) ); // send back product quantity!
                        } else
                            out.println( "null" );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }

                if (command.equals( "PUT" )) {
                    String id = in.nextLine();
                    String supplier = in.nextLine();// read all information from client
                    String name = in.nextLine();
                    String price = in.nextLine();
                    String quantity = in.nextLine();

                    System.out.println( "PUT command with Productid = " + id );

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection( url );

                        String sql = "SELECT * FROM Products WHERE Productid = " + id;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery( sql );

                        if (rs.next()) {
                            rs.close();
                            stmt.execute( "DELETE FROM Products WHERE Productid = " + id );
                        }

                        sql = "INSERT INTO Products VALUES (" + id + ",\""+ supplier +"\"," + "\"" + quantity + "\"," + price + "," + name + ")";
                        System.out.println( "SQL for PUT: " + sql );
                        stmt.execute( sql );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();


                } else {
                    out.println( 0 ); // logout unsuccessful!
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


