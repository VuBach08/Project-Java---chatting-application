package clients.models;

import java.util.ArrayList;

public class groupChat {
    private String groupID;
    private ArrayList<User> admins;
    private String groupName;
    private ArrayList<User> users;
    private String content;
    
    public groupChat(String id,String n) {
		this.groupID = id;
		this.groupName = n;
	}
    
    public ArrayList<User> getAdmins() {
        return admins;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public String getContent() {
        return content;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setAdmins(ArrayList<User> admins) {
        this.admins = admins;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public groupChat(String _id, ArrayList<User> ads, String name, ArrayList<User> _users, String _content)
    {
        this.groupID = _id;
        this.admins = ads;
        this.groupName = name;
        this.users = _users;
        this.content = _content;
    }

}
