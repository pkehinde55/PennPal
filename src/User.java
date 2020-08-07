import java.util.*;
/**
 * Represents a user in the ServerModel once registered. Information concerning 
 * the user's nickname, channels the user is in, and channels the user owns and 
 * whether those channe's are inviteOnly or not
 * */
public class User {
    
    private String userName;//user nickname
    private Set<String> groupChats;//set of the names of channels that the user is a member of
    private Set<String> bossSet;// Set of the names of channels the user is the owner of 
    //Map that shows which channels in the User's bossSet are inviteOnly
    private Map<String, Boolean> inviteMap;
    public User(int iD) {
        this.userName = "";
        this.groupChats = new TreeSet<String>(); 
        this.bossSet = new TreeSet<String>();
        this.inviteMap = new TreeMap <String, Boolean>();
    }
    
    /** 
     * Updates the userName of the user to its selected nickname 
     * @param String nickname
     * @return void
     */
    public void assignName(String nickname) {
        userName = nickname;
    }
    /**
     * Accessor for userSame of the User.
     *@param none
     * @return the userName of the User
   */
    public String getUserName() {
        return userName;
    }
    /**
     * Accessor for the group chats field of the User
     * @param none
     * @return the group chats of the User
     */
    public Set<String> getGroupChats() {
        return groupChats;
    }
    /**
     * Accessor for the bossSet field of the User
     * @param none
     * @return the bossSet of the User
     */
    public Set<String> getBossSet() {
        return bossSet;
    }
    
    /**
     *  Adds channel name to to the groupchats  field
     *  @param String name of the new channel
     * @return void
     * */
    public void addGroupChat(String newChannel) {
        groupChats.add(newChannel); 
    }
    /**
     *  Adds channel name to to the bossSet  field
     *  @param string representing the channel name 
     * @return  void
     * */
    public void addCompany(String newChannel, boolean invite) {
        bossSet.add(newChannel); 
        groupChats.add(newChannel); 
        inviteMap.put(newChannel, invite);
    }
    /**
     *  Removes channel name to to the groupchats  field
     * @return void
     * */
    public void removeGroupChat(String newChannel) {
        groupChats.remove(newChannel); 
    }
    /**
     *  Removes channel name from the bossSet  field and inviteMap
     *  @param string representing the channel name 
     * @return  void
     * */
    public void removeCompany(String newChannel) {
        bossSet.remove(newChannel); 
        inviteMap.remove(newChannel);
        groupChats.remove(newChannel);
    }
    
    /**
     * Checks whether a user is a inside a certain channel by checking if the string of the 
     * channel name is in the groupChats field. For testing
     * return @ boolean of whether the channel name is in groupChats
     * */
    public boolean isAMemberof(String channelName) {
        return groupChats.contains(channelName);//Is this structural or referential equality 
    }
    /**
     * Checks whether a user is the owner of a channel by checking if the name of the given 
     * channel is in the Users bossSet. For testing
     * @return  boolean of whether the User is the owner of the channel*/
    public boolean isOwnerof(String channelName) {
        return bossSet.contains(channelName);
    }
    
    /**
     * Returns whether the given channel that the user is the owner of is inviteOnly
     * @param String channel of interest
     * @return boolean telling if that channel is invite only*/
    public boolean getInvite(String channel) {
        return inviteMap.get(channel);
    }
    /**
     * 
     * Returns inviteMap, testing
     * @param None
     * @return Map<String, Boolean> inviteMpap
     * */
    public Map<String, Boolean> getInviteMap() {
        return inviteMap;
    }
    



}
