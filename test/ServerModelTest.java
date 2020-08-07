import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
public class ServerModelTest {
    private ServerModel model;
    private User bob;
    private User pat;
    private User sam;

    /**
     * Before each test, we initialize model to be 
     * a new ServerModel (with all new, empty state)
     */
    @BeforeEach
    public void setUp() {
        // We initialize a fresh ServerModel for each test
        model = new ServerModel();
    }
    /**
     * Used to register a few clients into the model
     * */
    public void enlistUsers() {
        model.registerUser(0);
        bob = model.getTMap().get(0);
        model.registerUser(1);
        pat = model.getTMap().get(1);
        model.registerUser(2);
        sam = model.getTMap().get(2);
        
    }
    /**
     * Creates a public disney channel and gives it 2 users
     * */
    public void createDisney() {
        Command create = new CreateCommand(0,"User0","disney", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "disney");
        join.updateServerModel(model);
    }
    /**
     * Creates a private MTV channel
     * */
    public void createMTV() {
        Command create = new CreateCommand(0,"User0","MTV", true);
        create.updateServerModel(model);
    }
    

    /** 
     * Here is an example test that checks the functionality of your 
     * changeNickname error handling. Each line has commentary directly above
     * it which you can use as a framework for the remainder of your tests.
     */
    @Test
    public void testInvalidNickname() {
        // A user must be registered before their nickname can be changed, 
        // so we first register a user with an arbitrarily chosen id of 0.
        model.registerUser(0);

        // We manually create a Command that appropriately tests the case 
        // we are checking. In this case, we create a NicknameCommand whose 
        // new Nickname is invalid.
        Command command = new NicknameCommand(0, "User0", "!nv@l!d!");

        // We manually create the expected Broadcast using the Broadcast 
        // factory methods. In this case, we create an error Broadcast with 
        // our command and an INVALID_NAME error.
        Broadcast expected = Broadcast.error(
            command, ServerResponse.INVALID_NAME
        );

        // We then get the actual Broadcast returned by the method we are 
        // trying to test. In this case, we use the updateServerModel method 
        // of the NicknameCommand.
        Broadcast actual = command.updateServerModel(model);

        // The first assertEquals call tests whether the method returns 
        // the appropriate Broadcast.
        assertEquals(expected, actual, "Broadcast");

        // We also want to test whether the state has been correctly
        // changed.In this case, the state that would be affected is 
        // the user's Collection.
        Collection<String> users = model.getRegisteredUsers();

        // We now check to see if our command updated the state 
        // appropriately. In this case, we first ensure that no 
        // additional users have been added.
        assertEquals(1, users.size(), "Number of registered users");

        // We then check if the username was updated to an invalid value 
        // (it should not have been).
        assertTrue(users.contains("User0"), "Old nickname still registered");

        // Finally, we check that the id 0 is still associated with the old, 
        // unchanged nickname.
        assertEquals(
            "User0", model.getNickname(0), 
            "User with id 0 nickname unchanged"
        );
    }

    /*
     * Your TAs will be manually grading the tests you write in this file.
     * Don't forget to test both the public methods you have added to your
     * ServerModel class as well as the behavior of the server in different
     * scenarios.
     * You might find it helpful to take a look at the tests we have already
     * provided you with in ChannelsMessagesTest, ConnectionNicknamesTest,
     * and InviteOnlyTest.
     */

    @Test
    public void testUserConstructor() {
        User bob = new User(1);
        assertEquals("", bob.getUserName(), "Makes a user");
    }
    
    @Test
    public void testUserConstructor2() {
        User bob = new User(1);
        assertEquals(new TreeSet<String>(), bob.getGroupChats(), "Empty group chat");
    }
    
    @Test
    public void testUserConstructor3() {
        User bob = new User(1);
        assertEquals(new TreeSet<String>(), bob.getBossSet(),"Empty BossSet");
    }
    
    @Test
    public void testAssignName() {
        User bob = new User(1);
        bob.assignName("bobby");
        assertEquals("bobby", bob.getUserName(), "Name assigned");
    }
    
    @Test
    public void testAddGroupChat() {
        User bob = new User(1);
        bob.addGroupChat("CIS 120");
        assertTrue(bob.isAMemberof("CIS 120"), "Check membership");
    }
    
    @Test
    public void testAddCompany() {
        User bob = new User(1);
        bob.addCompany("CIS 120", false);
        assertTrue(bob.isOwnerof("CIS 120"), "Get the boss's name");
    }
    
    @Test
    public void testRemoveCompany() {
        User bob = new User(1);
        bob.addCompany("CIS120", false);
        bob.removeCompany("CIS120");
        assertFalse(bob.isAMemberof("CIS120"));
    }
    @Test
    public void testRemoveCompany2() {
        User bob = new User(1);
        bob.addCompany("CIS120", false);
        bob.removeCompany("CIS120");
        assertFalse(bob.isOwnerof("CIS120"));
    }
    @Test
    public void testAddCompanyInvite() {
        User bob = new User(1);
        bob.addCompany("CIS 120", false);
        assertFalse(bob.getInvite("CIS 120"));
    }
    
    @Test
    public void testRegisterUserInMap1() {
        model.registerUser(0);
        User bob = model.getTMap().get(0);
        assertTrue(bob.equals(model.getTMap().get(0)), "Checks if user is in map");
    }
    
    @Test
    public void testRegisterUserInMap2() {
        model.registerUser(1);
        assertTrue(model.getTMap().containsKey(1), "Checks if ID is in map");
    }
    
    @Test
    public void testRegisterUserNameChange() {
        model.registerUser(0);
        User bob = model.getTMap().get(0);
        assertTrue(bob.getUserName().equals("User0"), "Automatic name assignement");
    }
    @Test
    public void testRegisterUserBroadcast() {
        assertEquals(Broadcast.connected("User0"), model.registerUser(0),"Deregister Broadcast");
    }
    @Test
    public void testDeregisterUserGone() {
        model.registerUser(1);
        model.deregisterUser(1);
        assertFalse(model.getTMap().containsKey(1), "Key is removed");
    }
    
    @Test
    public void testDeregisterUserBroadcast() {
        model.registerUser(0);
        model.registerUser(1);
        Command create = new CreateCommand(0,"User0","disney", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "disney");
        join.updateServerModel(model);
        Broadcast expected = Broadcast.disconnected("User1", Collections.singleton("User0"));
        assertEquals(expected, model.deregisterUser(1));
    }
    @Test
    public void degisterUserOwnerofAllChannels() {
        enlistUsers();
        createDisney();
        createMTV();
        model.deregisterUser(0);
        assertTrue(model.getChannels().isEmpty());
    }
    
    @Test
    public void degisterUserOwnerofisNowNull() {
        enlistUsers();
        createDisney();
        createMTV();
        model.deregisterUser(0);
        assertNull(model.getOwner("disney"));
    }
  
    @Test
    public void degisterUserOwnerChannelsEmpty() {
        enlistUsers();
        createDisney();
        createMTV();
        model.deregisterUser(0);
        boolean empty = model.getUsersInChannel("disney").isEmpty()
                && model.getUsersInChannel("MTV").isEmpty();
        assertTrue(empty);
    }
    @Test
    public void testGetUserID() {
        enlistUsers();
        assertEquals(0, model.getUserId("User0"),"Collects user Id");
    }
    @Test
    public void testGetUserIDNicknameNotInUse() {
        enlistUsers();
        assertEquals(-1, model.getUserId("User4"),"Collects user Id");
    }
    @Test
    public void testGetNickname() {
        enlistUsers();
        assertEquals("User0", model.getNickname(0), "Collects correct nickname");
        
    }
    
    @Test
    public void testGetNicknameIDNotInUse() {
        enlistUsers();
        assertNull(model.getNickname(4));
    }
    @Test
    public void testGetRegisteredUsers() {
        enlistUsers();
        Collection<String> clients = model.getRegisteredUsers();
        boolean expected = clients.contains("User0")
                && clients.contains("User1") && clients.contains("User2");
        assertTrue(expected, "All users are in list");
    }
    
    @Test
    public void testRegisteredUsersEncapsulation() {
        enlistUsers();
        Collection<String> clients = model.getRegisteredUsers();
        clients.add("User 100");
        assertFalse(clients == model.getRegisteredUsers());
    }
    
    @Test
    public void testGetUsersInChannel() {
        enlistUsers();
        Collection<String> clients = model.getRegisteredUsers();
        Command create = new CreateCommand(0,"User0","disney", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "disney");
        join.updateServerModel(model);
        Command join2 = new JoinCommand(2, "User2", "disney");
        join2.updateServerModel(model);
        Collection<String> channelUsers = model.getUsersInChannel("disney");
        assertEquals(clients, channelUsers);
    }
    
    @Test
    public void testGetChannels() {
        enlistUsers();
        createDisney();
        createMTV();
        Collection<String> channels = new TreeSet<String>();
        channels.add("disney");
        channels.add("MTV");
        assertEquals(channels, model.getChannels());
    }
    
    @Test
    public void testGetChannelsEncapsulation() {
        enlistUsers();
        createDisney();
        createMTV();
        Collection<String> channels = model.getChannels();
        channels.add("espn");
        assertFalse(channels.equals(model.getChannels()));
    }
    
    @Test
    public void testGetUsersInChannelEncapsulation() {
        enlistUsers();
        Command create = new CreateCommand(0,"User0","disney", false);
        create.updateServerModel(model);
        Command join = new JoinCommand(1, "User1", "disney");
        join.updateServerModel(model);
        Command join2 = new JoinCommand(2, "User2", "disney");
        join2.updateServerModel(model);
        Collection<String> channelUsers = model.getUsersInChannel("disney");
        channelUsers.add("User90");
        assertFalse(channelUsers.equals(model.getUsersInChannel("disney")));
    }
    
    @Test
    public void testGetOwner() {
        enlistUsers();
        Command create = new CreateCommand(0,"User0","disney", false);
        create.updateServerModel(model);
        assertTrue("User0".equals(model.getOwner("disney")));
    }
    
    @Test
    public void testGetOwnerNull() {
        enlistUsers();
        assertNull(model.getOwner("disney"));
    }
    @Test
    public void testGetFriends() {
        enlistUsers();
        createDisney();
        assertEquals(Collections.singleton("User0"), model.getFriends(1));
        
    }
    
    @Test
    public void testDoesChannelExistTrue() {
        enlistUsers();
        createDisney();
        assertTrue(model.doesChannelExist("disney"));
    }
    
    @Test
    public void testDoesChannelExistFalse() {
        enlistUsers();
        createDisney();
        assertFalse(model.doesChannelExist("espn"));
    }
    
    @Test
    public void testDeleteChannelDestroyDisney() {
        enlistUsers();
        createDisney();
        model.deleteChannel("disney");
        assertTrue(bob.getGroupChats().isEmpty() && pat.getGroupChats().isEmpty());
    }
    
    @Test
    public void testDeleteChannelDIsneyNotInList() {
        enlistUsers();
        createDisney();
        model.deleteChannel("disney");
        assertFalse(model.getChannels().contains("disney"));
    }
    
    
    @Test
    public void testNickNameInUseTrue() {
        enlistUsers();
        assertTrue(model.nicknameInUse("User0"));
    }
    
    @Test
    public void testNickNameInUseFalse() {
        enlistUsers();
        assertFalse(model.nicknameInUse("User3"));
    }
    
    @Test 
    public void testChangeName() {
        enlistUsers();
        model.changeName(0,"Johnny");
        assertTrue(bob.getUserName().equals("Johnny"));
    }
    
    @Test
    public void testAddGroup() {
        enlistUsers();
        model.addGroup(0, "disney");
        assertEquals(Collections.singleton("disney"), bob.getGroupChats());
    }
    
    @Test
    public void testAddBoss() {
        enlistUsers();
        model.addBoss(0, "disney", false);
        assertEquals(Collections.singleton("disney"), bob.getBossSet());
    }
    @Test
    public void testAddBossInvite() {
        enlistUsers();
        model.addBoss(0, "disney", true);
        assertTrue(bob.getInvite("disney"));
    }
    @Test
    public void testRemoveGroup() {
        enlistUsers();
        model.addGroup(0, "disney");
        model.removeGroup(0, "disney");
        assertTrue(bob.getGroupChats().isEmpty());
    }
    
    @Test
    public void testIsInviteOnly() {
        enlistUsers();
        model.addBoss(0, "disney", true);
        assertTrue(model.isInviteOnly("disney"));
    }
    
    @Test
    public void testNicknameCommandInvalidName() {
        enlistUsers();
        Command command = new NicknameCommand(0, "User0", "=+3");
        Broadcast expected = Broadcast.error(command, ServerResponse.INVALID_NAME);
        assertEquals(expected, command.updateServerModel(model));
    }
    @Test
    public void testNicknameCommandNameInUse() {
        enlistUsers();
        Command command = new NicknameCommand(0, "User0", "User1");
        Broadcast expected = Broadcast.error(command, ServerResponse.NAME_ALREADY_IN_USE);
        assertEquals(expected, command.updateServerModel(model));
    }
    
    @Test
    public void testNicknameCommandChange() {
        enlistUsers();
        Command command = new NicknameCommand(0, "User0", "Johnny");
        Collection<String> recipients = model.getFriends(0);
        recipients.add("Johnny");
        Broadcast expected = Broadcast.okay(command, recipients);
        assertEquals(expected, command.updateServerModel(model));
    }
    
    @Test
    public void testCreateCommandInValidName() {
        enlistUsers();
        Command command = new CreateCommand(0, "User0", "%^7", false);
        Broadcast expected = Broadcast.error(command, ServerResponse.INVALID_NAME);
        assertEquals(expected, command.updateServerModel(model));
    }
    
    @Test
    public void testCreateCommandChannelAlreadyExists() {
        enlistUsers();
        createDisney();
        Command command = new CreateCommand(0, "User0", "disney", false);
        Broadcast expected = Broadcast.error(command, ServerResponse.CHANNEL_ALREADY_EXISTS);
        assertEquals(expected, command.updateServerModel(model));
    }
    
    @Test
    public void testCreateCommandSuccess() {
        enlistUsers();
        createDisney();
        Command command = new CreateCommand(0, "User0", "espn", false);
        Set<String> recipients = new TreeSet<String>();
        recipients.add("User0");
        Broadcast expected = Broadcast.okay(command, recipients);
        assertEquals(expected, command.updateServerModel(model));
    }
    
    @Test
    public void testCreateCommandSuccessBoss() {
        enlistUsers();
        createDisney();
        Command command = new CreateCommand(0, "User0", "espn", false);
        command.updateServerModel(model);
        assertTrue(bob.getBossSet().contains("espn"));
    }
    
    @Test
    public void testCreateCommandSuccessGroup() {
        enlistUsers();
        createDisney();
        Command command = new CreateCommand(0, "User0", "espn", false);
        command.updateServerModel(model);
        assertTrue(bob.getGroupChats().contains("espn"));
    }
    
    @Test
    public void testJoinCommandChannelDoesNotExist() {
        enlistUsers();
        createDisney();
        Command join = new JoinCommand(0, "User0", "espn");
        Broadcast expected = Broadcast.error(join, ServerResponse.NO_SUCH_CHANNEL);
        assertEquals(expected, join.updateServerModel(model));
    }
    
    @Test
    public void testJoinCommandPrivateChannel() {
        enlistUsers();
        createMTV();
        Command join = new JoinCommand(0, "User1", "MTV");
        Broadcast expected = Broadcast.error(join, ServerResponse.JOIN_PRIVATE_CHANNEL);
        assertEquals(expected, join.updateServerModel(model));
    }
    @Test
    public void testJoinCommandSuccess() {
        enlistUsers();
        createDisney();
        Command join = new JoinCommand(2, "User2", "disney");
        Collection <String> recipients = model.getUsersInChannel("disney");
        recipients.add("User2");
        Broadcast expected = Broadcast.names(join, recipients, "User0");
        assertEquals(expected, join.updateServerModel(model));
    }
    
    @Test
    public void testJoinCommandSuccess2() {
        enlistUsers();
        createDisney();
        Command join = new JoinCommand(2, "User2", "disney");
        join.updateServerModel(model);
        Collection <String> members = model.getUsersInChannel("disney");
        assertTrue(members.contains("User2"));
    }
    
    @Test
    public void testMessageCommandChannelDoesNotExist() {
        enlistUsers();
        Command message = new MessageCommand(0, "User0", "espn", "Hi");
        Broadcast expected = Broadcast.error(message, ServerResponse.NO_SUCH_CHANNEL);
        assertEquals(expected, message.updateServerModel(model));
    }
    
    @Test
    public void testMessageCommandUserNotinChannel() {
        enlistUsers();
        createDisney();
        Command message = new MessageCommand(2, "User2", "disney", "Hi");
        Broadcast expected = Broadcast.error(message, ServerResponse.USER_NOT_IN_CHANNEL);
        assertEquals(expected, message.updateServerModel(model));
    }
    
    @Test
    public void testMessageCommandSuccess() {
        enlistUsers();
        createDisney();
        Command message = new MessageCommand(1, "User1", "disney", "Hi");
        Collection<String> recipients = new TreeSet<String>();
        recipients.add("User0");
        recipients.add("User1");
        Broadcast expected = Broadcast.okay(message, recipients);
        assertEquals(expected, message.updateServerModel(model));
    }
    
    @Test
    public void testLeaveCommandChannelDoesNotExist() {
        enlistUsers();
        Command exit = new LeaveCommand(1, "User1", "espn");
        Broadcast expected = Broadcast.error(exit, ServerResponse.NO_SUCH_CHANNEL);
        assertEquals(expected, exit.updateServerModel(model));
    }
    
    @Test
    public void testLeaveCommandUserNotInChannel() {
        enlistUsers();
        createDisney();
        Command exit = new LeaveCommand(2, "User2", "disney");
        Broadcast expected = Broadcast.error(exit, ServerResponse.USER_NOT_IN_CHANNEL);
        assertEquals(expected, exit.updateServerModel(model));
    }
    @Test
    public void testLeaveCommandSuceess() {
        enlistUsers();
        createDisney();
        Command exit = new LeaveCommand(1, "User1", "disney");
        Collection<String> recipients = new TreeSet<String>();
        recipients.add("User0");
        recipients.add("User1");
        Broadcast expected = Broadcast.okay(exit, recipients);
        assertEquals(expected, exit.updateServerModel(model));
    }
    @Test
    public void testLeaveCommandRemoveGroupChat() {
        enlistUsers();
        createDisney();
        Command exit = new LeaveCommand(1, "User1", "disney");
        exit.updateServerModel(model);
        assertFalse(pat.getGroupChats().contains("disney"));
    }
     
    @Test
    public void testLeaveCommandBossLeavesDestroyChat() {
        enlistUsers();
        createDisney();
        Command exit = new LeaveCommand(0, "User0", "disney");
        exit.updateServerModel(model);
        Collection<String> channelList = model.getChannels();
        assertFalse(channelList.contains("disney"));
    }
    
    @Test
    public void testInviteCommandUserDoesNotExist() {
        enlistUsers();
        createMTV();
        Command invite = new InviteCommand(0, "User0", "MTV", "User3");
        Broadcast expected = Broadcast.error(invite, ServerResponse.NO_SUCH_USER);
        assertEquals(expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteCommandUserPublicChannel() {
        enlistUsers();
        createDisney();
        Command invite = new InviteCommand(0, "User0", "disney", "User2");
        Broadcast expected = Broadcast.error(invite, ServerResponse.INVITE_TO_PUBLIC_CHANNEL);
        assertEquals(expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteCommandChannelDoesNotExist() {
        enlistUsers();
        Command invite = new InviteCommand(0, "User0", "espn", "User2");
        Broadcast expected = Broadcast.error(invite, ServerResponse.NO_SUCH_CHANNEL);
        assertEquals(expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteCommandNotOwner() {
        enlistUsers();
        createMTV();
        Command invite = new InviteCommand(1, "User1", "MTV", "User2");
        Broadcast expected = Broadcast.error(invite, ServerResponse.USER_NOT_OWNER);
        assertEquals(expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteCommandSuccess() {
        enlistUsers();
        createMTV();
        Command invite = new InviteCommand(0, "User0", "MTV", "User1");
        Collection<String> recipients = new TreeSet<String>();
        recipients.add("User0");
        recipients.add("User1");
        Broadcast expected = Broadcast.names(invite, recipients, "User0");
        assertEquals(expected, invite.updateServerModel(model));
    }
    
    @Test
    public void testInviteCommandUsersinChannel() {
        enlistUsers();
        createMTV();
        Command invite = new InviteCommand(0, "User0", "MTV", "User1");
        invite.updateServerModel(model);
        assertTrue(model.getUsersInChannel("MTV").contains("User1"));
    }
    
    @Test
    public void testKickCommandUserDoesNotExist() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(0, "User0", "disney", "User3");
        Broadcast expected = Broadcast.error(kick, ServerResponse.NO_SUCH_USER);
        assertEquals(expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickCommandChannelDoesNotExist() {
        enlistUsers();
        Command kick = new KickCommand(0, "User0", "espn", "User1");
        Broadcast expected = Broadcast.error(kick, ServerResponse.NO_SUCH_CHANNEL);
        assertEquals(expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickCommandNotOwner() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(1, "User1", "disney", "User0");
        Broadcast expected = Broadcast.error(kick, ServerResponse.USER_NOT_OWNER);
        assertEquals(expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickCommandUserNotInChannel() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(0, "User0", "disney", "User2");
        Broadcast expected = Broadcast.error(kick, ServerResponse.USER_NOT_IN_CHANNEL);
        assertEquals(expected, kick.updateServerModel(model));
    }
    
    @Test
    public void testKickCommandSuccess() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(0, "User0", "disney", "User1");
        Collection<String> recipients = new TreeSet<String>();
        recipients.add("User0");
        recipients.add("User1");
        Broadcast expected = Broadcast.okay(kick, recipients);
        assertEquals(expected, kick.updateServerModel(model));
    }
    @Test
    public void testKickCommandMemberisKicked() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(0, "User0", "disney", "User1");
        kick.updateServerModel(model);
        assertFalse(model.getUsersInChannel("disney").contains("User1"));
    }
    
    @Test
    public void testKickCommandOwnerisKicked() {
        enlistUsers();
        createDisney();
        Command kick = new KickCommand(0, "User0", "disney", "User0");
        kick.updateServerModel(model);
        assertFalse(model.getChannels().contains("disney"));
    }
    
  
    
    
    
    
    
    
    
    
    
    
    
}
