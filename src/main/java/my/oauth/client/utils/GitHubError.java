package my.oauth.client.utils;

public class GitHubError {
	private String error;
	private String error_description;

	public GitHubError(String error, String error_description) {
		super();
		this.error = error;
		this.error_description = error_description;
	}

	public String getError() {
		return error;
	}

	public String getError_description() {
		return error_description;
	}

	@Override
	public String toString() {
		return "GitHubError [error=" + error + ", error_description=" + error_description + "]";
	}

}
