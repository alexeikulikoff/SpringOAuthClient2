package my.oauth.client.utils;

public class GitHubToken {
	private String access_token;
	private String token_type;
	private String scope;

	public GitHubToken(String access_token, String token_type, String scope) {
		super();
		this.access_token = access_token;
		this.token_type = token_type;
		this.scope = scope;
	}

	public String getAccess_token() {
		return access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public String getScope() {
		return scope;
	}

	@Override
	public String toString() {
		return "GitHubToken [access_token=" + access_token + ", token_type=" + token_type + ", scope=" + scope + "]";
	}

}
