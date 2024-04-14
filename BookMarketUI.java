package acm;


import java.sql.*;
import java.util.*;

public class BookMarketUI {
    // JDBC URL, username, and password of MySQL server
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/acm";
    static final String USERNAME = "root";
    static final String PASSWORD = "root26";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Initialize the BookMarket with the database connection
            BookMarket market = new BookMarket(conn);
            Scanner scan = new Scanner(System.in);

            while (true) {
                System.out.println("\nWelcome to the Book Market!");
                System.out.println("1. Show available domains");
                System.out.println("2. Show books in a domain");
                System.out.println("3. Buy a book");
                System.out.println("4. Sell a book");
                System.out.println("5. Rent a book");
                System.out.println("6. Exit");

                System.out.print("Enter your choice: ");
                String choice = scan.nextLine();

                switch (choice) {
                    case "1":
                        market.showAvailableDomains();
                        break;
                    case "2":
                        market.showBooksByDomain(scan);
                        break;
                    case "3":
                        market.buyBook(scan);
                        break;
                    case "4":
                        market.sellBook(scan);
                        break;
                    case "5":
                        market.rentBook(scan);
                        break;
                    case "6":
                        System.out.println("Thank you for visiting. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class BookMarket {
    private Connection connection;

    public BookMarket(Connection connection) {
        this.connection = connection;
    }

    public void showAvailableDomains() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT  domain FROM books GROUP BY domain");
            System.out.println("Available Domains:");
            while (rs.next()) {
                System.out.println(rs.getString("domain"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showBooksByDomain(Scanner scanner) {
        System.out.print("Enter the domain to show books: ");
        String domain = scanner.nextLine();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT  title,author,domain,price FROM books WHERE domain = ?")) {
            stmt.setString(1, domain);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No books available in this domain.");
                System.exit(0);
            } else {
                System.out.println("Books Available in " + domain + ":");
                while (rs.next()) {
                    System.out.println("Title: " + rs.getString("title") + ", Author: " + rs.getString("author") + ", Selling Price: $" + rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void buyBook(Scanner scanner) {
        System.out.print("Enter title of book: ");
        String title = scanner.nextLine();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT  title,author,domain,price FROM books WHERE title = ?")) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Book is not available.");
                System.exit(0);
            } else {
                while (rs.next()) {
                    System.out.println("Title: " + rs.getString("title") + ", Author: " + rs.getString("author") + ", Selling Price: $" + rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Do you want to buy this book (yes/no):");
        String resp = scanner.next().toLowerCase();       
        if (resp.equals("yes")) {
            System.out.println("Enter Customer name");
            String name = scanner.next();

            System.out.println("Enter Customer number");
            String cno = scanner.next();

            System.out.println("Enter Customer Address");
            String address = scanner.next();

            String query = "INSERT INTO customers(cno, cname, cadd, title) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, cno);
                pstmt.setString(2, name);
                pstmt.setString(3, address);
                pstmt.setString(4, title);
                pstmt.executeUpdate();

                System.out.println("Book will be delivered to you shortly. You have to pay the book price using UPI id (abc@sbi.up)");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Thank you for visiting!");
            System.exit(0);
        }
        
        String deleteQuery = "DELETE FROM books WHERE title = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, title);
            deleteStmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    } 
 
   

    public void sellBook(Scanner scanner) {
        System.out.println("Note: Half the cost price of the book will be paid!!");
        System.out.println("Enter seller name:");
        String sname = scanner.next();

        System.out.println("Enter seller number:");
        String sno = scanner.next();

        System.out.println("Enter seller Address:");
        String saddress = scanner.next();

        System.out.println("Enter seller upi id:");
        String upid = scanner.next();

        System.out.println("Enter book title");
        String btitle = scanner.next();

        System.out.println("Enter books author:");
        String author = scanner.next();

        System.out.println("Enter book domain:");
        String bdomain = scanner.next();

        System.out.println("Enter price of book:");
        int bprice = scanner.nextInt();

        int price = bprice / 2;

        String query = "INSERT INTO books(title, author, domain, price, sellername, selleradd, sellerno, sellerupid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, btitle);
            pstmt.setString(2, author);
            pstmt.setString(3, bdomain);
            pstmt.setInt(4, bprice);
            pstmt.setString(5, sname);
            pstmt.setString(6, saddress);
            pstmt.setString(7, sno);
            pstmt.setString(8, upid);
            pstmt.executeUpdate();

            String orgaddress = "Shop no.32, 4th floor, KK Market, Dhankawadi, Pune 43";
            System.out.println("Deliver the books to the address: " + orgaddress + " by courier or manually. After book reception, payment will be done.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rentBook(Scanner scanner) {
        System.out.print("Enter title of book: ");
        String title = scanner.nextLine();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT  title,author,domain,price FROM books WHERE title = ?")) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("Book is not available.");
                System.exit(0);
            } else {
                while (rs.next()) {
                    System.out.println("Title: " + rs.getString("title") + ", Author: " + rs.getString("author") + ", Selling Price: $" + rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Do you want to borrow this book (yes/no):");
        String resp = scanner.next();
        if (resp.equals("yes")) {
            System.out.println("Enter Customer name");
            String rname = scanner.next();

            System.out.println("Enter Customer number");
            String rno = scanner.next();

            System.out.println("Enter Customer Address");
            String raddress = scanner.next();

            System.out.println("Enter today's date (dd/mm/yyyy)");
            String date = scanner.next();

            String query = "INSERT INTO rent(rno, rname, radd, title, date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, rno);
                pstmt.setString(2, rname);
                pstmt.setString(3, raddress);
                pstmt.setString(4, title);
                pstmt.setString(5, date);
                pstmt.executeUpdate();

                System.out.println("Book will be delivered to you shortly. You have to pay Rs.20 using UPI id (abc@sbi.up)and have to return the book within a month.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Thank you for visiting!");
            System.exit(0);
        }
    }
}
