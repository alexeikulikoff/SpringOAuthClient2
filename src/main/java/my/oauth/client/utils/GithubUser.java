package my.oauth.client.utils;

public class GithubUser {
	private String login;
	private String id;
	private String created_at;

	public GithubUser(String login, String id, String created_at) {
		super();
		this.login = login;
		this.id = id;
		this.created_at = created_at;
	}

	public String getLogin() {
		return login;
	}

	public String getId() {
		return id;
	}

	public String getCreated_at() {
		return created_at;
	}

	@Override
	public String toString() {
		return "GithubUser [login=" + login + ", id=" + id + ", created_at=" + created_at + "]";
	}

}
