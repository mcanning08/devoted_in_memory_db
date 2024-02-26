import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner input_scanner = new Scanner(System.in);
        StatefulDatabase stateful_database = new StatefulDatabase();

        System.out.println("Enter a Database Command (values are case-sensitive, but commands are not): ");
        System.out.println("To Set a Value: SET [name] [value]");
        System.out.println("To Get a Value: GET [name]");
        System.out.println("To Delete a Value: DELETE [name]");
        System.out.println("To count a Value: COUNT [value] ");
        System.out.println("To exit: END");
        System.out.println("To begin a transaction: BEGIN");
        System.out.println("To rollback a transaction: ROLLBACK");
        System.out.println("To commit all transactions: COMMIT");

        //Listen to the user inputs
        while (true) {
            String input_string = input_scanner.nextLine();
            if (!stateful_database.execute_command(input_string, false)) {
                break;
            }
        }
        input_scanner.close();
    }
}


