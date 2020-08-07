import java.util.*;


/**
 * The {@code ServerModel} is the class responsible for tracking the
 * state of the server, including its current users and the channels
 * they are in.
 * This class is used by subclasses of {@link Command} to:
 *     1. handle commands from clients, and
 *     2. handle commands from {@link ServerBackend} to coordinate 
 *        client connection/disconnection. 
 */
public final class ServerModel implements ServerModelApi {
    //User ID is the key and the associated  User is the value
    private Map <Integer , User> tmap;

    /**
     * Constructs a {@code ServerModel} and initializes any
     * collections needed for modeling the server state.
     */
    public ServerModel() {
        tmap = new TreeMap<Integer, User>();
    }


    //==========================================================================
    // Client connection handlers
    //==========================================================================

    /**
     * Informs the model that a client has connected to the server
     * with the given user id. The model should update its state so
     * that it can identify this user during later interactions. The
     * newly connected user will not yet have had the chance to set a
     * nickname, and so the model should provide a default nickname
     * for the user.  Any user who is registered with the server
     * (without being later deregistered) should appear in the output
     * of {@link #getRegisteredUsers()}.
     *
     * @param userid The unique id created by the backend to represent this user
     * @return A {@link Broadcast} to the user with their new nickname
     */
    public Broadcast registerUser(int userid) {
        String nickname = generateUniqueNickname();
        User bob = new User(userid);
        bob.assignName(nickname);
        tmap.put(userid, bob);
        return Broadcast.connected(nickname);
    }

    /**
     * Generates a unique nickname of the form "UserX", where X is the
     * smallest non-negative integer that yields a unique nickname for a user.
     * @return the generated nickname
     */
    private String generateUniqueNickname() {
        int suffix = 0;
        String nickname;
        Collection<String> existingUsers = getRegisteredUsers();
        do {
            nickname = "User" + suffix++;
        } while (existingUsers != null && existingUsers.contains(nickname));
        return nickname;
    }

    /**
     * Determines if a given nickname is valid or invalid (contains at least
     * one alphanumeric character, and no non-alphanumeric characters).
     * @param name The channel or nickname string to validate
     * @return true if the string is a valid name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Informs the model that the client with the given user id has
     * disconnected from the server.  After a user id is deregistered,
     * the server backend is free to reassign this user id to an
     * entirely different client; as such, the model should remove all
     * state of the user associated with the deregistered user id. The
     * behavior of this method if the given user id is not registered
     * with the model is undefined.  Any user who is deregistered
     * (without later being registered) should not appear in the
     * output of {@link #getRegisteredUsers()}.
     *
     * @param userid The unique id of the user to deregister
     * @return A {@link Broadcast} instructing clients to remove the
     * user from all channels
     */
    public Broadcast deregisterUser(int userid) {
        Collection<String> friends = getFriends(userid);
        User pat = tmap.get(userid);
        List<String> removed = new LinkedList<String>();
        Iterator<String> passer = pat.getBossSet().iterator();
        while (passer.hasNext()) {
            removed.add(passer.next());
        }
        
        for (String channel : removed) {
            deleteChannel(channel);
        }
        tmap.remove(userid);//Removes user from the map
        return Broadcast.disconnected(pat.getUserName(), friends);
    }

    

    

    //==========================================================================
    // Server model queries
    // These functions provide helpful ways to test the state of your model.
    // You may also use them in your implementation.
    //==========================================================================

    /**
     * Gets the user id currently associated with the given
     * nickname. The returned id is -1 if the nickname is not
     * currently in use.
     *
     * @param nickname The nickname for which to get the associated user id
     * @return The user id of the user with the argued nickname if
     * such a user exists, otherwise -1
     */
    public int getUserId(String nickname) {
        int identity = -1;
        for (Map.Entry <Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            if (bob.getUserName().equals(nickname)) {
                identity = entry.getKey();
                break;
            }
        }
        return identity;
    }

    /**
     * Gets the nickname currently associated with the given user
     * id. The returned nickname is null if the user id is not
     * currently in use.
     *
     * @param userid The user id for which to get the associated
     *        nickname
     * @return The nickname of the user with the argued user id if
     *          such a user exists, otherwise null
     */
    public String getNickname(int userid) {
        User bob = tmap.get(userid);
        if (bob == null) {
            return null;
        }
        String nickname = bob.getUserName();
        return nickname;
    }

    /**
     * Gets a collection of the nicknames of all users who are
     * registered with the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of registered user nicknames
     */
    public Collection<String> getRegisteredUsers() {
        Set<String> setOfUsers = new TreeSet<String>();
        for (Map.Entry <Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            setOfUsers.add(bob.getUserName());
        }
        return setOfUsers;
    }

    /** 
     * Gets a collection of the names of all the channels that are
     * present on the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of channel names
     */
    public Collection<String> getChannels() {
        Set<String> channels = new TreeSet<String>();
        
        for (Map.Entry<Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            Iterator<String> itr = bob.getBossSet().iterator();
            while (itr.hasNext()) {
                channels.add(itr.next());
            }
        }
        return channels;
    }

    /**
     * Gets a collection of the nicknames of all the users in a given
     * channel. The collection is empty if no channel with the given
     * name exists. Modifications to the returned collection should
     * not affect the server state.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get member nicknames
     * @return The collection of user nicknames in the argued channel
     */
    public Collection<String> getUsersInChannel(String channelName) {
        Set<String> channelMembers = new TreeSet<String>();
        
        for (Map.Entry<Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            if (bob.getGroupChats().contains(channelName)) {
                channelMembers.add(bob.getUserName());
            }
        }
        return channelMembers;
    }

    /**
     * Gets the nickname of the owner of the given channel. The result
     * is {@code null} if no channel with the given name exists.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get the owner nickname
     * @return The nickname of the channel owner if such a channel
     * exists, othewrise null
     */
    public String getOwner(String channelName) {
        String boss = null;
        for (Map.Entry<Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            if (bob.getBossSet().contains(channelName)) {
                boss = bob.getUserName();
                break;
            }
        }
        return boss;
    }
    
   /**
    * Gets the tmap field of the server model, for testing
    * @param None
    * @return tmap of server model
    * */
    public Map<Integer, User> getTMap() {
        return this.tmap;
    }
   /**
    * This function creates a list of all the users who are in channels with the
    * user that is mapped with the given user id
    * @param int that is user id
    * @return a string set of all the users who are in channels with the
    * user that is mapped with the given user id
    * */
    
    public Collection<String> getFriends(int userId) {
        User bob = tmap.get(userId);
        Set<String> friends = new TreeSet<String>();
        Iterator<String> itr = bob.getGroupChats().iterator();
       
        while (itr.hasNext()) {
            Collection<String> associates = getUsersInChannel(itr.next());
           
            Iterator<String> passer = associates.iterator();
            while (passer.hasNext()) {
                friends.add(passer.next());
            }
        }
        friends.remove(bob.getUserName());
        return friends;
    }
   /**
    * Checks if a channel already exists in model 
    * @param String representing channel being searched for and 
    * the model that we are searching in 
    * @return boolean telling if the channel exists in the model
    * */
    public boolean doesChannelExist(String channelName) {
        boolean contains = false;
        contains = getChannels().contains(channelName);
        return contains;
    }
    
   /**
    * Deletes a channel from server in the event the owner leaves or deregisters
    * @param String name of channel to be deleted
    * @return none
    * */
    public void deleteChannel(String channelName) {
        for (Map.Entry<Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            bob.removeCompany(channelName);
        }
    }
    
   /**Checks if given nickname is already being used by a registered user
    * @param String nickname in question
    * @return boolean stating if the nickname is in use
    * */
    public boolean nicknameInUse(String nickname) {
        for (Map.Entry <Integer, User> entry : tmap.entrySet()) {
            User bob = entry.getValue();
            if (bob.getUserName().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
   /** 
    * Updates the userName of the user with given id to the nickname 
    * @param int id of user, String nickname
    * @return void
    */
    public void changeName(int id, String nickname) {
        tmap.get(id).assignName(nickname);
    }
   /**
    *  Adds channel name to to the groupchats  field of the user with given id
    *  @param int id of user joining group, String name of the channel joined
    * @return void
    * */
    public void addGroup(int id, String channel) {
        tmap.get(id).addGroupChat(channel);
    }
   /**
    *  Adds channel name to to the bossSet  field of user with the given id and
    *  Adds channel name to the inviteMap of the owner along with whether it is invite Only
    *  @param int id of user that is the owner, string representing the channel name, 
    *  boolean inviteOnly
    *  showing if the channel is invite only
    * @return  void
    * */
    public void addBoss(int id, String channel, boolean inviteOnly) {
        tmap.get(id).addCompany(channel,inviteOnly);
       
    }
   /**
    *  Removes channel name to to the groupchats  field of the user with given id
    *  @param int id of user joining group, String name of the channel left
    * @return void
    * */
    public void removeGroup(int id, String channel) {
        tmap.get(id).removeGroupChat(channel);
    }
   /**
    * Checks if a channel is invite only
    * @param String that is the channel's name 
    * @return boolean showing if the channel is invite only*/
    public boolean isInviteOnly(String channel) {
        String bossName = getOwner(channel);
        int id = getUserId(bossName);
        return tmap.get(id).getInvite(channel);
    }
   
   
   
}
