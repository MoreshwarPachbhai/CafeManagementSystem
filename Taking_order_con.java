package CafeManagementSystemFolder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Taking_order_con {
    Scanner sc = new Scanner(System.in);
    static Connection con;

    // Static block runs once when class loads
    static {
        try {
            con = Build_Connection_db.buildConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();  // shows the error in console
        }
    }

    // main method
    public static void main(String[] args) throws SQLException, ClassNotFoundException{
        Taking_order_con obj = new Taking_order_con();
        obj.login();
    }

    // Login method
    void login() {
        System.out.println("********** Login Page **********");
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM staff WHERE username='" + username + "' AND password='" + password + "'");
            if (rs.next()) {
                System.out.println("✅ Login successful! Welcome, " + rs.getString("name") + ".");
                showMenu();
                takeOrder(); // take order first
            } else {
                System.out.println("❌ Invalid username or password!");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Show menu
    void showMenu() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items");
            System.out.println("---- MENU ITEMS ----");
            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                System.out.println(id + " | " + name + " | ₹" + price + " | " + category);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Take order method (now also inserts into on_process)
    void takeOrder() {
        try {
            System.out.print("\nEnter Staff ID: ");
            int staffId = sc.nextInt();

            System.out.print("Enter Table Number: ");
            int tableNo = sc.nextInt();

            boolean ordering = true;
            int currentOrderId = 0;

            while (ordering) {
                System.out.print("Enter Item ID to order: ");
                int itemId = sc.nextInt();
                System.out.print("Enter Quantity: ");
                int qty = sc.nextInt();

                Statement stmt = con.createStatement();
                ResultSet rsItem = stmt.executeQuery("SELECT name, price FROM menu_items WHERE item_id=" + itemId);
                if (!rsItem.next()) {
                    System.out.println("Invalid Item ID!");
                    rsItem.close();
                    stmt.close();
                    continue;
                }
                String itemName = rsItem.getString("name");
                double price = rsItem.getDouble("price");
                double total = price * qty;
                rsItem.close();

                // Insert into orders only for the first item
                if (currentOrderId == 0) {
                    String insertOrder = "INSERT INTO orders (staff_id, table_no, total_price) VALUES ("
                            + staffId + ", " + tableNo + ", " + total + ")";
                    stmt.executeUpdate(insertOrder, Statement.RETURN_GENERATED_KEYS);
                    ResultSet rsOrder = stmt.getGeneratedKeys();
                    if (rsOrder.next())
                        currentOrderId = rsOrder.getInt(1);
                    rsOrder.close();

                    // ✅ NEW: Insert this order into on_process table
                    String insertProcess = "INSERT INTO on_process (order_id, table_no, total_price) VALUES ("
                            + currentOrderId + ", " + tableNo + ", " + total + ")";
                    stmt.executeUpdate(insertProcess);

                } else {
                    // update total_price in orders
                    String updateOrder = "UPDATE orders SET total_price = total_price + " + total
                            + " WHERE order_id=" + currentOrderId;
                    stmt.executeUpdate(updateOrder);

                    // ✅ Also update on_process total_price for this order
                    String updateProcess = "UPDATE on_process SET total_price = total_price + " + total
                            + " WHERE order_id=" + currentOrderId;
                    stmt.executeUpdate(updateProcess);
                }

                // Insert into order_items
                String insertItem = "INSERT INTO order_items (order_id, staff_id, item_id, quantity, total_price) VALUES ("
                        + currentOrderId + ", " + staffId + ", " + itemId + ", " + qty + ", " + total + ")";
                stmt.executeUpdate(insertItem);

                // Insert into order_summary
                String insertSummary = "INSERT INTO order_summary (order_id, item_name, quantity, total_price) VALUES ("
                        + currentOrderId + ", '" + itemName + "', " + qty + ", " + total + ")";
                stmt.executeUpdate(insertSummary);

                System.out.println("✅ Added: " + itemName + " | Qty: " + qty + " | Total: ₹" + total);
                System.out.println("Order ID: " + currentOrderId);
                System.out.println("******************** Order placed successfully ! ********************");

                int more = -1;
                while (more != 0 && more != 1) {
                    System.out.print("Do you want to order more items? (1=Yes, 0=No): ");
                    more = sc.nextInt();
                }
                if (more == 0)
                    ordering = false;

                stmt.close();
            }

            processOrder(currentOrderId);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Process order summary
    void processOrder(int orderId) {
        try {
            Statement stmt = con.createStatement();

            ResultSet rsItems = stmt.executeQuery(
                    "SELECT item_name, quantity, total_price FROM order_summary WHERE order_id=" + orderId);
            System.out.println("\n---- ORDER SUMMARY ----");
            while (rsItems.next()) {
                System.out.println(rsItems.getString("item_name") + " | Qty: " + rsItems.getInt("quantity") +
                        " | Total: ₹" + rsItems.getDouble("total_price"));
            }
            rsItems.close();

            ResultSet rsTotal = stmt.executeQuery(
                    "SELECT SUM(total_price) AS total_amount, " +
                            "ROUND(SUM(total_price)*0.05,2) AS tax, " +
                            "ROUND(SUM(total_price) + (SUM(total_price)*0.05),2) AS final_total " +
                            "FROM order_summary WHERE order_id=" + orderId);
            if (rsTotal.next()) {
                System.out.println("Total Amount: ₹" + rsTotal.getDouble("total_amount"));
                System.out.println("Tax (5%): ₹" + rsTotal.getDouble("tax"));
                System.out.println("Final Total: ₹" + rsTotal.getDouble("final_total"));
            }
            rsTotal.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
