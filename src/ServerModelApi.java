import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Defines a collection of methods that should be available in the {@link ServerModel} class. These
 * methods will be used by the {@link ServerBackend}, and should therefore conform to this
 * interface.
 *
 * You do not need to modify this file.
 */
public interface ServerModelApi {

    /**
     * Informs the model that a client has connected to the server with the given user ID. The model
     * should update its state so that it can identify this user during later interactions. The
     * newly connected user will not yet have had the chance to set a nickname, and so the model
     * should provide a default nickname for the user.
     * Any user who is registered with the server (without being later deregistered) should appear
     * in the output of {@link #getRegisteredUsers()}.
     *
     * @param userId The unique ID created by the backend to represent this user
     * @return A {@link Broadcast} to the user with their new nickname
     */
    Broadcast registerUser(int userId);

    /**
     * Informs the model that the client with the given user ID has disconnected from the server.
     * After a user ID is deregistered, the server backend is free to reassign this user ID to an
     * entirely different client; as such, the model should remove all state of the user associated
     * with the deregistered user ID. The behavior of this method if the given user ID is not
     * registered with the model is undefined.
     * Any user who is deregistered (without later being registered) should not appear in the output
     * of {@link #getRegisteredUsers()}.
     *
     * @param userId The unique ID of the user to deregister
     * @return A {@link Broadcast} instructing clients to remove the user from all channels
     */
    Broadcast deregisterUser(int userId);

    /**
     * Gets the user ID currently associated with the given nickname. The returned ID is -1 if the
     * nickname is not currently in use.
     *
     * @param nickname The nickname for which to get the associated user ID
     * @return The user ID of the user with the argued nickname if such a user exists, otherwise -1
     */
    int getUserId(String nickname);

    /**
     * Gets the nickname currently associated with the given user ID. The returned nickname is
     * null if the user ID is not currently in use.
     *
     * @param userId The user ID for which to get the associated nickname
     * @return The nickname of the user with the argued user ID if such a user exists, otherwise
     *          null
     */
    String getNickname(int userId);

    /**
     * Gets a collection of the nicknames of all users who are registered with the server. Changes
     * to the returned collection should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of registered user nicknames
     */
    Collection<String> getRegisteredUsers();

    /**
     * Gets a collection of the names of all the channels that are present on the server. Changes to
     * the returned collection should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of channel names
     */
    Collection<String> getChannels();

    /**
     * Gets a collection of the nicknames of all the users in a given channel. The collection is
     * empty if no channel with the given name exists. Modifications to the returned collection
     * should not affect the server state.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get member nicknames
     * @return The collection of user nicknames in the argued channel
     */
    Collection<String> getUsersInChannel(String channelName);

    /**
     * Gets the nickname of the owner of the given channel. The result is {@code null} if no
     * channel with the given name exists.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get the owner nickname
     * @return The nickname of the channel owner if such a channel exists, othewrise null
     */
    String getOwner(String channelName);
    /**
     * Gets the tmap field of the server model
     * @param None
     * @return tmap of server model
     * */
    Map<Integer, User> getTMap();
     
     /**
      * This function creates a list of all the users who are in channels with the
      * user that is mapped with the given user Id
      * @param int that is user ID
      * @return a string set of all the users who are in channels with the
      * user that is mapped with the given user Id
      * */
    Collection<String> getFriends(int userId);
     
     /**
      * Checks if a channel already exists in model 
      * @param String representing channel being searched for and 
      * the model that we are searching in 
      * @return boolean telling if the channel exists in the model
      * */
    boolean doesChannelExist(String channelName);
      
      /**
       * Deletes a channel from service in the event the owner leaves or deregisters
       * @param String name of channel to be deleted
       * @return none
       * */
    void deleteChannel(String channelName);
      
      /**
       * Checks if given nickname is already being used by a registered user
       * @param String nickname in question
       * @return boolean stating if the nickname is in use
       * */
    boolean nicknameInUse(String nickname);
      
      /** 
       * Updates the userName of the user with given ID to the nickname 
       * @param int ID of user, String nickname
       * @return void
       */
    void changeName(int iD, String nickname);
      /**
       *  Adds channel name to to the groupchats  field of the user with given ID
       *  @param int ID of user joining group, String name of the channel joined
       * @return void
       * */
    void addGroup(int iD, String channel);
      /**
       *  Adds channel name to to the BossSet  field of user with the given ID and
       *  Adds channel name to the inviteMap of the owner along with whether it is inviteOnly
       *  @param int ID of user that is the owner, string representing the channel name, 
       *  boolean inviteOnly
       *  showing if the channel is invite only
       * @return  void
       * */
    void addBoss(int iD, String channel, boolean inviteOnly);
    
      /**
       *  Removes channel name to to the groupchats  field of the user with given ID
       *  @param int ID of user joining group, String name of the channel left
       * @return void
       * */
    void removeGroup(int iD, String channel);
      
      /**
       * Checks if a channel is invite only
       * @param String that is the channel's name 
       * @return boolean showing if the channel is invite only
       * */
    boolean isInviteOnly(String channel);
}
