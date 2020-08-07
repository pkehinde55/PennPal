import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * These tests are provided for testing the server's handling of 
 * channels and messages. You can and should use these tests as a model 
 * for your own testing, but write your tests in ServerModelTest.java.
 *
 * Note that "assert" commands used for testing can have a String as 
 * their last argument, denoting what the "assert" command is testing.
 * 
 * Remember to check:
 * https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html 
 * for assert method documention!
 */
public class ChannelsMessagesTest {
    private ServerModel model;

    /**
     * Before each test, we initialize model to be 
     * a new ServerModel (with all new, empty state)
     */
    @BeforeEach
    public void setUp() {
        model = new ServerModel();
    }

    @Test
    public void testCreateNewChannel() {
        model.registerUser(0);
        Command create = new CreateCommand(0, "User0", "java", false);
        Broadcast expected = 
            Broadcast.okay(create, Collections.singleton("User0"));
        assertEquals(expected, create.updateServerModel(model), "broadcast");

        assertTrue(model.getChannels().contains("java"), "channel exists");
        assertTrue(
            model.getUsersInChannel("java").contains("User0"), 
            "channel has creator"
        );
        assertEquals("User0", model.getOwner("java"), "channel has owner");
    }

    @Test
    public void testJoinChannelExistsNotMember() {
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0, "User0", "java", false);
        create.updateServerModel(model);

        Command join = new JoinCommand(1, "User1", "java");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("User0");
        Broadcast expected = Broadcast.names(join, recipients, "User0");
        assertEquals(expected, join.updateServerModel(model), "broadcast");

        assertTrue(
            model.getUsersInChannel("java").contains("User0"), 
            "User0 in channel"
        );
        assertTrue(
            model.getUsersInChannel("java").contains("User1"), 
            "User1 in channel"
        );
        assertEquals(
            2, model.getUsersInChannel("java").size(), 
            "num. users in channel"
        );
    }

    @Test
    public void testNickBroadcastsToChannelWhereMember() {
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0, "User0", "java", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "java");
        join.updateServerModel(model);

        Command nick = new NicknameCommand(0, "User0", "Duke");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("Duke");
        Broadcast expected = Broadcast.okay(nick, recipients);
        assertEquals(expected, nick.updateServerModel(model), "broadcast");

        assertFalse(
            model.getUsersInChannel("java").contains("User0"), 
            "old nick not in channel"
        );
        assertTrue(
            model.getUsersInChannel("java").contains("Duke"), 
            "new nick is in channel"
        );
        assertTrue(
            model.getUsersInChannel("java").contains("User1"), 
            "unaffected user still in channel"
        );
    }

    @Test
    public void testLeaveChannelExistsMember() {
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0, "User0", "java", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "java");
        join.updateServerModel(model);

        Command leave = new LeaveCommand(1, "User1", "java");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("User0");
        Broadcast expected = Broadcast.okay(leave, recipients);
        assertEquals(expected, leave.updateServerModel(model), "broadcast");

        assertTrue(
            model.getUsersInChannel("java").contains("User0"), 
            "User0 in channel"
        );
        assertFalse(
            model.getUsersInChannel("java").contains("User1"), 
            "User1 not in channel"
        );
        assertEquals(
            1, model.getUsersInChannel("java").size(), 
            "num. users in channel"
        );
    }

    @Test
    public void testDeregisterSendsDisconnectedWhereMember() {
        model.registerUser(0);
        model.registerUser(1);//Register 2 clients
        Command create = new CreateCommand(0, "User0", "java", false);
        create.updateServerModel(model);//Creates class java with User0 as owner
        Command join = new JoinCommand(1, "User1", "java");
        join.updateServerModel(model);//User1 joins java

        Broadcast expected = 
            Broadcast.disconnected("User1", Collections.singleton("User0"));
        assertEquals(expected, model.deregisterUser(1), "broadcast");

        assertTrue(
            model.getChannels().contains("java"), 
            "channel still exists"
        );
        assertEquals(
            1, model.getUsersInChannel("java").size(), 
            "num. users in channel"
        );
        assertTrue(
            model.getUsersInChannel("java").contains("User0"), 
            "unaffected user still in channel"
        );
    }

    @Test
    public void testMesgChannelExistsMember() {
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0, "User0", "java", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "java");
        join.updateServerModel(model);

        Command mesg = 
            new MessageCommand(0, "User0", "java", "hey whats up hello");
        Set<String> recipients = new TreeSet<>();
        recipients.add("User1");
        recipients.add("User0");
        Broadcast expected = Broadcast.okay(mesg, recipients);
        assertEquals(expected, mesg.updateServerModel(model), "broadcast");
    }

}

