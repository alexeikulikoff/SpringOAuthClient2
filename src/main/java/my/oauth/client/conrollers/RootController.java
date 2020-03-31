package my.oauth.client.conrollers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

import my.oauth.client.config.AppConfig;
import my.oauth.client.entity.StateEntity;
import my.oauth.client.entity.UserEntity;
import my.oauth.client.repository.StateRepository;
import my.oauth.client.repository.UserRepository;
import my.oauth.client.service.UsersDetails;
import my.oauth.client.utils.GitHubError;
import my.oauth.client.utils.GitHubToken;
import my.oauth.client.utils.GithubUser;

@Controller
public class RootController {

	private static final String CLIENT_ID = "client_id";
	private static final String CLIENT_SECRET = "client_secret";
	private static final String CODE = "code";
	private static final String REDIRECT_URI = "redirect_uri";
	private static final String STATE = "state";
	private static final String WRONG_STATE_ERROR = "wrong state error!";

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StateRepository stateRepository;

	@Autowired
	private AppConfig appConfig;

	@RequestMapping("/callback")
	public String callback(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state,
			Model model) throws IOException {

		StateEntity checkState = stateRepository.findByState(state);

		if (checkState == null) {
			model.addAttribute("error", WRONG_STATE_ERROR);
			return "error";
		}

		HttpClient gitTokenClient = HttpClients.createDefault();

		HttpPost gitTokenPost = new HttpPost(appConfig.getAccess_token_url());
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(CLIENT_ID, appConfig.getClient_id()));
		params.add(new BasicNameValuePair(CLIENT_SECRET, appConfig.getClient_secret()));
		params.add(new BasicNameValuePair(CODE, code));
		params.add(new BasicNameValuePair(REDIRECT_URI, appConfig.getRedirect_uri()));
		params.add(new BasicNameValuePair(STATE, state));

		gitTokenPost.setEntity(new UrlEncodedFormEntity(params));
		gitTokenPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		gitTokenPost.setHeader("Accept", "application/json");

		StringBuilder gitTokenBuilder = new StringBuilder();
		try (CloseableHttpResponse response = (CloseableHttpResponse) gitTokenClient.execute(gitTokenPost)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (InputStream instream = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(instream));) {
					String s;
					while ((s = reader.readLine()) != null) {
						gitTokenBuilder.append(s);
					}
				}
			}
		}

		GitHubToken gitToken = new Gson().fromJson(gitTokenBuilder.toString(), GitHubToken.class);

		if (gitToken.getAccess_token() == null) {
			GitHubError gitError = new Gson().fromJson(gitTokenBuilder.toString(), GitHubError.class);
			model.addAttribute("error", gitError.getError());
			model.addAttribute("error_description", gitError.getError_description());
			return "error";
		}

		HttpClient githubUserClient = HttpClients.createDefault();

		HttpGet gitHubUserPost = new HttpGet(appConfig.getGet_user_url());

		gitHubUserPost.setHeader("Authorization", "token " + gitToken.getAccess_token());

		StringBuilder githubUserBuilder = new StringBuilder();
		try (CloseableHttpResponse response2 = (CloseableHttpResponse) githubUserClient.execute(gitHubUserPost)) {
			HttpEntity entity2 = response2.getEntity();
			if (entity2 != null) {
				try (InputStream instream = entity2.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(instream));) {
					String s;
					while ((s = reader.readLine()) != null) {
						githubUserBuilder.append(s);
					}
				}
			}
		}

		GithubUser githubUser = new Gson().fromJson(githubUserBuilder.toString(), GithubUser.class);
		BCryptPasswordEncoder crypt = new BCryptPasswordEncoder(4);

		UserEntity user = new UserEntity();
		user.setName(githubUser.getLogin());
		user.setPassword(crypt.encode("*****"));
		user.setRole("user");

		UserEntity tempUsr = userRepository.findByName(githubUser.getLogin());

		if (tempUsr == null) {
			userRepository.save(user);
		}
		UsersDetails usersDetails = new UsersDetails(user);
		Authentication auth = new UsernamePasswordAuthenticationToken(usersDetails, null,
				usersDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:profile";

	}

	@RequestMapping("/oauth")
	public void Auth(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {

		String response_type = "code";
		String client_id = appConfig.getClient_id();
		String redirect_uri = appConfig.getRedirect_uri();
		String state = UUID.randomUUID().toString().replace("-", "");
		String auth_url = appConfig.getAuthorize_url();
		String scope = "read:user";
		String url_params = "response_type=" + response_type + "&scope=" + scope + "&client_id=" + client_id
				+ "&redirect_uri=" + redirect_uri + "&state=" + state;

		StateEntity stateEntity = new StateEntity();
		stateEntity.setState(state);
		StateEntity result = stateRepository.save(stateEntity);
		if (result != null) {
			resp.sendRedirect(auth_url + "?" + url_params);
		} else {
			resp.sendRedirect("/login");
		}

	}

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

	@RequestMapping("/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
