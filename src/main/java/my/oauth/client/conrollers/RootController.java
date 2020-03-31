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

import my.oauth.client.entity.UserEntity;
import my.oauth.client.repository.UserRepository;
import my.oauth.client.service.UsersDetails;
import my.oauth.client.utils.GitHubError;
import my.oauth.client.utils.GitHubToken;
import my.oauth.client.utils.GithubUser;

@Controller
public class RootController {

	@Autowired
	private UserRepository userRepository;

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

	@RequestMapping("/callback")
	public String callback(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state,
			Model model) throws IOException {

		System.out.println(code);
		System.out.println(state);

		HttpClient httpclient = HttpClients.createDefault();

		HttpPost httppost = new HttpPost("https://github.com/login/oauth/access_token");
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("client_id", "611f43c030371f5d7297"));
		params.add(new BasicNameValuePair("client_secret", "0d013731236e856bf283698a4cfd1d7ed2ed9439"));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/callback"));
		params.add(new BasicNameValuePair("state", state));

		httppost.setEntity(new UrlEncodedFormEntity(params));
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.setHeader("Accept", "application/json");

		StringBuilder sb = new StringBuilder();
		try (CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httppost)) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (InputStream instream = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(instream));) {
					String s;
					while ((s = reader.readLine()) != null) {
						System.out.println(s);
						sb.append(s);
					}
				}
			}
		}

		GitHubToken gitToken = new Gson().fromJson(sb.toString(), GitHubToken.class);

		if (gitToken.getAccess_token() == null) {
			GitHubError gitError = new Gson().fromJson(sb.toString(), GitHubError.class);
			model.addAttribute("error", gitError.getError());
			model.addAttribute("error_description", gitError.getError_description());
			return "error";
		}

		HttpClient httpclient2 = HttpClients.createDefault();

		HttpGet httppost2 = new HttpGet("https://api.github.com/user");

		httppost2.setHeader("Authorization", "token " + gitToken.getAccess_token());

		StringBuilder sb2 = new StringBuilder();
		try (CloseableHttpResponse response2 = (CloseableHttpResponse) httpclient2.execute(httppost2)) {
			HttpEntity entity2 = response2.getEntity();
			if (entity2 != null) {
				try (InputStream instream = entity2.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(instream));) {
					String s;
					while ((s = reader.readLine()) != null) {
						sb2.append(s);
					}
				}
			}
		}

		GithubUser githubUser = new Gson().fromJson(sb2.toString(), GithubUser.class);
		System.out.println(githubUser);

		BCryptPasswordEncoder crypt = new BCryptPasswordEncoder(4);
		UserEntity user = new UserEntity();

		user.setName(githubUser.getLogin());
		user.setPassword(crypt.encode("12345"));
		user.setRole("user");

		UserEntity tempUsr = userRepository.findByName(githubUser.getLogin());
		if (tempUsr == null) {
			userRepository.save(user);
		}
		UsersDetails usersDetails = new UsersDetails(user);
		Authentication auth = new UsernamePasswordAuthenticationToken(usersDetails, null,
				usersDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:hello";

	}

	@RequestMapping("/oauth")
	public void Auth(HttpServletRequest req, HttpServletResponse resp, Model model) throws IOException {
		String response_type = "code";

		String client_id = "611f43c030371f5d7297";
		String redirect_uri = "http://localhost:8080/callback";

		String state = UUID.randomUUID().toString().replace("-", "");

		String auth_url = "https://github.com/login/oauth/authorize/";

		String scope = "read:user";
		String url_params = "response_type=" + response_type + "&scope=" + scope + "&client_id=" + client_id
				+ "&redirect_uri=" + redirect_uri + "&state=" + state;
		System.out.println("hello");

		resp.sendRedirect(auth_url + "?" + url_params);

	}
	/*
	 * @RequestMapping("/oauth") public @ResponseBody String
	 * LoginWith(HttpServletRequest req, HttpServletResponse resp, Model model)
	 * throws IOException {
	 * 
	 * 
	 * BCryptPasswordEncoder crypt = new BCryptPasswordEncoder(4); UserEntity user =
	 * new UserEntity(); String userName = UUID.randomUUID().toString().substring(0,
	 * 8); user.setName(userName); user.setPassword(crypt.encode("12345"));
	 * user.setRole("user"); userRepository.save(user); UsersDetails usersDetails =
	 * new UsersDetails(user);
	 * 
	 * Authentication auth = new UsernamePasswordAuthenticationToken(usersDetails,
	 * null, usersDetails.getAuthorities());
	 * 
	 * SecurityContextHolder.getContext().setAuthentication(auth); return
	 * "redirect";
	 * 
	 * 
	 * }
	 */
}
