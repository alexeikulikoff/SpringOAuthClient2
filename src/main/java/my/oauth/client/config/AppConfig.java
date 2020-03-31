package my.oauth.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class AppConfig {
	@Value("${app.client_id}")
	private String client_id;

	@Value("${app.client_secret}")
	private String client_secret;

	@Value("${app.redirect_uri}")
	private String redirect_uri;

	@Value("${app.authorize_url}")
	private String authorize_url;

	@Value("${app.access_token_url}")
	private String access_token_url;

	@Value("${app.get_user_url}")
	private String get_user_url;

	public String getClient_id() {
		return client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public String getRedirect_uri() {
		return redirect_uri;
	}

	public String getAuthorize_url() {
		return authorize_url;
	}

	public String getAccess_token_url() {
		return access_token_url;
	}

	public String getGet_user_url() {
		return get_user_url;
	}

}
