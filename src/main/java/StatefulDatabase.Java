import java.util.*;
/**
 * Main class for interacting with the in-memory database. This has all the public functions for interaction with the
 * in-memory DB
 */
class StatefulDatabase {
    private HashMap<String,String> database_contents = new HashMap<>();
    private Stack<DatabaseTransaction> transaction_list = new Stack<>();
    private DatabaseTransaction current_transaction = null;

    /**
     * Main function for executing commands. Takes in the string input to be executed, as well as whether the command
     * is for a rollback. If the command is a rollback, we don't want to add rollback statements on top.
     */
    public boolean execute_command(String input_string, Boolean is_rollback) {
        String input_command = input_string.split(" ")[0];
        String input_name = null;
        String input_value = null;
        //Check if the inputs are there
        if (input_string.split(" ").length > 1) {
            input_name = input_string.split(" ")[1];
        }
        if (input_string.split(" ").length > 2) {
            input_value = input_string.split(" ")[2];
        }
        //If there's too many inputs, don't proceed
        if (input_string.split(" ").length > 3) {
            System.out.println("Too many arguments");
        } else {
            if (sanitize_input(input_command, input_name, input_value)) {
                if (input_command.equalsIgnoreCase("END")) {
                    System.out.println("Exiting DB.");
                    return false;
                } else if (input_command.equalsIgnoreCase("SET")) {
                    set_value(input_name, input_value,is_rollback);
                } else if (input_command.equalsIgnoreCase("GET")) {
                    System.out.println(get_value_by_name(input_name));
                } else if (input_command.equalsIgnoreCase("DELETE")) {
                    delete_value_by_name(input_name,is_rollback);
                } else if (input_command.equalsIgnoreCase("COUNT")) {
                    //nit: the input as named is just using the "name" as the "value".
                    System.out.println(count_by_value(input_name));
                } else if (input_command.equalsIgnoreCase("BEGIN")) {
                    begin_transaction();
                } else if (input_command.equalsIgnoreCase("ROLLBACK")) {
                    rollback_current_transaction();
                } else if (input_command.equalsIgnoreCase("COMMIT")) {
                    commit_all_transactions();
                } else {
                    System.out.println("Unknown command, try again");
                }
            }
        }
        return true;
    }

    /**
     * Set the value of the named input. If it's a rollback, see if the previous value is null, if so, add a command to delete
     * the value. If the value is not null, set it to the previous value.
     */
    public void set_value(String input_name,String input_value,Boolean is_rollback) {
        //Add a rollback for this if we're in a transaction
        if((current_transaction!=null) && !is_rollback) {
            if(database_contents.get(input_name) == null) {
                current_transaction.add_rollback_command("DELETE " + input_name);
            } else {
                current_transaction.add_rollback_command("SET " + input_name + " " + database_contents.get(input_name));
            }
        }
        database_contents.put(input_name,input_value);
    }

    public String get_value_by_name(String input_name) {
        String database_value = database_contents.get(input_name);
        return database_value;
    }

    /**
     * Delete the input name. If this isn't for a rollback, add a statement to set the value back to what it was.
     */
    public void delete_value_by_name(String input_name,Boolean is_rollback) {
        if (database_contents.get(input_name) == null) {
            System.out.println(database_contents.get(input_name));
        } else {
            //Add a rollback for this if we're in a transaction
            if((current_transaction!=null) && !is_rollback) {
                if(database_contents.get(input_name) != null) {
                    current_transaction.add_rollback_command("SET " + input_name + " " + database_contents.get(input_name));
                }
            }
            database_contents.remove(input_name);
        }
    }

    /**
     * Count the keys in the Hashmap that have a value equal to the input. This is an O(log n) but could be made faster
     * by storing a reverse index lookup.
     */
    public int count_by_value(String input_value) {
        //To-Do: Optimize for speed by setting up a bi-directional hashmap
        Integer value_counter = 0;
        Iterator<HashMap.Entry<String, String>> iterator = database_contents.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<String, String> database_row = iterator.next();
            if (database_row.getValue().toString().equals(input_value)) {
                value_counter++;
            }
        }
        return value_counter;
    }

    /**
     * add a new DB transaction to the transaction stack
     */
    public void begin_transaction() {
        DatabaseTransaction database_transaction = new DatabaseTransaction();
        current_transaction = database_transaction;
        transaction_list.push(database_transaction);
    }

    /**
     * rolls back the current transaction in the list of transactions and pops it off the top of the Stack.
     */
    public void rollback_current_transaction() {
        if (current_transaction == null) {
            System.out.println("TRANSACTION NOT FOUND");
        } else {
            //Iterate through the transactions and revert them
            Stack<String> rollback_commands = current_transaction.get_rollback_commands();
            while(!rollback_commands.empty()) {
                execute_command(rollback_commands.peek(),true);
                rollback_commands.pop();
            }
            transaction_list.pop();

            //Check if there's a transaction on top of the stack
            if (!transaction_list.empty()) {
                current_transaction = transaction_list.peek();
            }
        }
    }
    /**
     * "Commits" all transactions by just removing the rollbacks list
     */
    public void commit_all_transactions() {
        current_transaction = null;
        transaction_list = null;
    }

    /**
     * Validate that the inputs are valid for the specified command
     */
    public static boolean sanitize_input(String input_command,String input_name,String input_value) {
        if ((input_command.equalsIgnoreCase("set") || input_command.equalsIgnoreCase("get") || input_command.equalsIgnoreCase("count") || input_command.equalsIgnoreCase("delete")) && input_name == null) {
            System.out.println(input_command + " command requires a name");
            return false;
        }
        if ((input_command.equalsIgnoreCase("set")) && (input_value == null)) {
            System.out.println(input_command + " command requires a value");
            return false;
        }
        return true;
    }
}