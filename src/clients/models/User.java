package clients.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class User {
	public String id;
	public String name;
	public String fullname;
	public String password;
	public String email;
	public ArrayList<User> friends = new ArrayList<>();
	private boolean isAdmin;
	private boolean isLocked;
	private String history;
	private boolean isOnline;
	public boolean chatWithU;
	private ArrayList<String> blockList = new ArrayList<>();
	private ArrayList<User> onlList = new ArrayList<>();
	public ArrayList<groupChat> groupList = new ArrayList<>();
	public static String hashPassword(String pw)
	{
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(pw.getBytes());

			// Convert byte array to a hexadecimal string
			StringBuilder hexString = new StringBuilder();
			for (byte hashedByte : hashedBytes) {
				String hex = Integer.toHexString(0xff & hashedByte);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			String hashedPW = hexString.toString();
			return hashedPW;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Hashing algorithm not found");
			e.printStackTrace();
			return null;
		}
	}

	//For login purpose
	public User(String e,String p) {
		this.email = e;
		this.password = p;
	}

	//	For register purpose
	public User(String _id, String n, boolean locked, boolean isOnl, ArrayList<String> blocks) {
		this.id = _id;
		this.name = n;
		this.isLocked = locked;
		this.isOnline = isOnl;
		this.blockList = blocks;
	}

	// For General purpose
	public User(String id,String n,String f,String e,Boolean p) {
		this.id = id;
		this.name = n;
		this.email = e;
		this.isAdmin = p;
		this.fullname = f;
	}

	public User(String id,String n,boolean isOnline) {
		this.id = id;
		this.name = n;
		this.isOnline = isOnline;
	}
	
	public User(String id,String n,boolean isOnline,boolean isAdmin) {
		this.id = id;
		this.name = n;
		this.isAdmin = isAdmin;
		this.isOnline = isOnline;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getBlockList() {
		return blockList;
	}

	public ArrayList<User> getFriends() {
		return friends;
	}

	public String getEmail() {
		return email;
	}

	public String getHistory() {
		return history;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public void setBlockList(ArrayList<String> blockList) {
		this.blockList = blockList;
	}

	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public void setLocked(boolean locked) {
		isLocked = locked;
	}

	public void setOnline(boolean online) {
		isOnline = online;
	}

	public ArrayList<User> getOnlineList() {
		return onlList;
	}

	public ArrayList<groupChat> getGroupList() {
		return groupList;
	}

	public void setOnlineList(ArrayList<User> chatList) {
		this.onlList = chatList;
	}

	public void setGroupList(ArrayList<groupChat> groupList) {
		this.groupList = groupList;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void updateFriend(String _id) {
		for (User friend : friends) {
			if(friend.id.equals(_id)) {
				friend.chatWithU = true;
			}
		}
	}
}