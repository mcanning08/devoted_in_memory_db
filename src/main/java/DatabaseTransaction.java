import java.util.*;

/**
 * Simple class to store the DB transactions. Because we're saving memory, instead of storing the Hashmap state, we're
 * just going to store the reversion statements.
 */
class DatabaseTransaction {
    private Stack<String> rollback_statements = new Stack<>();
    public void add_rollback_command(String rollback_command) {
        rollback_statements.push(rollback_command);
    }
    public Stack<String> get_rollback_commands() {
        return rollback_statements;
    }
}