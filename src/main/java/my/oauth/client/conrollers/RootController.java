package my.oauth.client.conrollers;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ResponseBody;

import my.oauth.client.entity.UserEntity;
import my.oauth.client.repository.UserRepository;
import my.oauth.client.service.UsersDetails;

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

	@RequestMapping("/oauth")
	public @ResponseBody String LoginWith(HttpServletRequest req, HttpServletResponse resp, Model model)
			throws IOException {
		BCryptPasswordEncoder crypt = new BCryptPasswordEncoder(4);
		UserEntity user = new UserEntity();
		String userName = UUID.randomUUID().toString().substring(0, 8);
		user.setName(userName);
		user.setPassword(crypt.encode("12345"));
		user.setRole("user");
		userRepository.save(user);
		UsersDetails usersDetails = new UsersDetails(user);

		Authentication auth = new UsernamePasswordAuthenticationToken(usersDetails, null,
				usersDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect";

		/*
		 * HttpClient httpclient = HttpClients.createDefault(); HttpPost httppost = new
		 * HttpPost("http://localhost:8080/login"); List<NameValuePair> params = new
		 * ArrayList<>(); params.add(new BasicNameValuePair("username", userName));
		 * params.add(new BasicNameValuePair("password", "12345"));
		 * 
		 * httppost.setEntity(new UrlEncodedFormEntity(params)); //
		 * httppost.setHeader("Content-Type", "text/html; charset=ISO-8859-4"); //
		 * httppost.setHeader("Accept", "text/plain; q=0.5, text/html, text/x-dvi; //
		 * q=0.8, text/x-c");
		 * 
		 * // httppost.setHeader("Authorization", "Basic " + Client.getClient_id() + ":"
		 * // +
		 * Base64.getEncoder().encodeToString(Client.getClient_secret().getBytes()));
		 * 
		 * CloseableHttpResponse response = (CloseableHttpResponse)
		 * httpclient.execute(httppost); HttpEntity entity = response.getEntity();
		 * System.out.println(response);
		 * 
		 * // resp.sendRedirect("http://localhost:8080/login?username=" + userName +
		 * "&password=12345");
		 * 
		 * /* RestTemplate restTemplate = new RestTemplate();
		 * 
		 * HttpHeaders headers = new HttpHeaders();
		 * headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		 * 
		 * MultiValueMap<String, String> map = new LinkedMultiValueMap<String,
		 * String>();
		 * 
		 * map.add("username", userName); map.add("password", "12345");
		 * 
		 * HttpEntity<MultiValueMap<String, String>> request = new
		 * HttpEntity<MultiValueMap<String, String>>(map, headers);
		 * 
		 * ResponseEntity<String> response =
		 * restTemplate.postForEntity("http://localhost:8080/login", request,
		 * String.class);
		 * 
		 * System.out.println(response);
		 */

	}
}
