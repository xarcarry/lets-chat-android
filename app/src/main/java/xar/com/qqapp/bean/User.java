package xar.com.qqapp.bean;

public class User {
	private long userId;
	private String username;
	private String password;

	public User(long userId, String password) {
		super();
		this.userId = userId;
		this.password = password;
	}

	public User(long userId, String username, String password) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
