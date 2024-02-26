import org.junit.Assert;
import org.junit.Test;

public class StatefulDatabaseTest {
    @Test
    public void test_set_and_get() {
        StatefulDatabase stateful_database = new StatefulDatabase();
        stateful_database.execute_command("SET a foo",false);
        stateful_database.execute_command("SET b foo",false);
        Assert.assertEquals("foo", stateful_database.get_value_by_name("b"));
        Assert.assertEquals(2, stateful_database.count_by_value("foo"));
        stateful_database.execute_command("END",false);
    }

    @Test
    public void test_example_one() {
        StatefulDatabase stateful_database = new StatefulDatabase();
        stateful_database.execute_command("SET a foo",false);
        stateful_database.execute_command("SET b foo",false);
        Assert.assertEquals(2, stateful_database.count_by_value("foo"));
        stateful_database.execute_command("DELETE a",false);
        Assert.assertEquals(1, stateful_database.count_by_value("foo"));
        stateful_database.execute_command("SET b baz",false);
        Assert.assertEquals(0, stateful_database.count_by_value("foo"));
        Assert.assertEquals("baz", stateful_database.get_value_by_name("b"));
        Assert.assertEquals(null, stateful_database.get_value_by_name("B"));
        stateful_database.execute_command("END",false);
    }

    @Test
    public void test_example_two() {
        StatefulDatabase stateful_database = new StatefulDatabase();
        stateful_database.execute_command("SET a foo",false);
        stateful_database.execute_command("SET a foo",false);
        Assert.assertEquals(1, stateful_database.count_by_value("foo"));
        Assert.assertEquals("foo", stateful_database.get_value_by_name("a"));
        stateful_database.execute_command("DELETE a",false);
        Assert.assertEquals(null, stateful_database.get_value_by_name("a"));
        Assert.assertEquals(0, stateful_database.count_by_value("foo"));
        stateful_database.execute_command("END",false);
    }

    @Test
    public void test_example_three() {
        StatefulDatabase stateful_database = new StatefulDatabase();
        stateful_database.execute_command("BEGIN",false);
        stateful_database.execute_command("SET a foo",false);
        Assert.assertEquals("foo", stateful_database.get_value_by_name("a"));
        stateful_database.execute_command("BEGIN",false);
        stateful_database.execute_command("SET a bar",false);
        Assert.assertEquals("bar", stateful_database.get_value_by_name("a"));
        stateful_database.execute_command("SET a baz",false);
        stateful_database.execute_command("ROLLBACK",false);
        Assert.assertEquals("foo", stateful_database.get_value_by_name("a"));
        stateful_database.execute_command("ROLLBACK",false);
        Assert.assertEquals(null, stateful_database.get_value_by_name("a"));
    }
    @Test
    public void test_example_four() {
        StatefulDatabase stateful_database = new StatefulDatabase();
        stateful_database.execute_command("SET a foo",false);
        stateful_database.execute_command("SET b baz",false);
        stateful_database.execute_command("BEGIN",false);
        Assert.assertEquals("foo", stateful_database.get_value_by_name("a"));
        stateful_database.execute_command("SET a bar",false);
        Assert.assertEquals(1, stateful_database.count_by_value("bar"));
        stateful_database.execute_command("BEGIN",false);
        Assert.assertEquals(1, stateful_database.count_by_value("bar"));
        stateful_database.execute_command("DELETE a",false);
        Assert.assertEquals(null, stateful_database.get_value_by_name("a"));
        Assert.assertEquals(0, stateful_database.count_by_value("bar"));
        stateful_database.execute_command("ROLLBACK",false);
        Assert.assertEquals("bar", stateful_database.get_value_by_name("a"));
        Assert.assertEquals(1, stateful_database.count_by_value("bar"));
        stateful_database.execute_command("COMMIT",false);
        Assert.assertEquals("bar", stateful_database.get_value_by_name("a"));
        Assert.assertEquals("baz", stateful_database.get_value_by_name("b"));
        stateful_database.execute_command("END",false);
    }
}