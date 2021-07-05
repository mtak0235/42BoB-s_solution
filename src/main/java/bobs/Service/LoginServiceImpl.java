package bobs.Service;

import bobs.Dto.SessionDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;

@Service
public class LoginServiceImpl implements LoginService {

	@Override
	public String getOAuthToken(String code) {

		System.out.println("Authorization Code======>{}" + code);

		String uid = "60a61213eaaed1e7b4a7ffc8c029f35c5ee4ccd28c0882363e1e3b9b195dd30a";
		String secret = "8fb6ed99b91e9243100263350f0e9bc6d5e866e6c90be2098b07fb5de43c2d6e";
		String redirect_uri = "http://leeworld9.ipdisk.co.kr:58080/42OAuth";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> response = null;

		String accessTokenUrl = "https://api.intra.42.fr/oauth/token"
								+ "?grant_type=authorization_code"
		                        + "&client_id=" + uid
		                        + "&client_secret=" +secret
								+ "&code=" + code
								+ "&redirect_uri=" + redirect_uri;

		response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request, String.class);

		//parse를 쓰지 않고, dto로 넘길수도 있다는 모양. (추후 받아올 데이터가 늘어나면 변경예정)
		JSONParser parser = new JSONParser();
		String access_token = null;
		try{
			Object obj = parser.parse(response.getBody());
			JSONObject jsonObj = (JSONObject) obj;
			access_token = (String)jsonObj.get("access_token");
		}catch (Exception e)
		{
			e.printStackTrace();
		}

		//System.out.println("Access Token Response======>{}" + response.getBody());
		//System.out.println("Access Token======>{}" + access_token);

		return access_token;
	}

	@Override
	public SessionDto getUserInfo(String token) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+ token);
		headers.add("Content-Type", "application/json; charset=utf-8");
		HttpEntity<String> request = new HttpEntity<String>(headers);
		ResponseEntity<String> response = null;

		String getInfoUrl = "https://api.intra.42.fr/v2/me";

		response = restTemplate.exchange(getInfoUrl, HttpMethod.GET, request, String.class);

		System.out.println("INFO Response======>{}" + response.getBody());

		SessionDto sessionDto = new SessionDto();
		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(response.getBody());
			JSONObject jsonObj = (JSONObject) obj;
			sessionDto.setUser_id((String)jsonObj.get("login"));
			sessionDto.setEmail((String)jsonObj.get("email"));
			System.out.println("login : "+ sessionDto.getUser_id());
			System.out.println("email : "+ sessionDto.getEmail());
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return sessionDto;
	}

	@Override
	public HttpSession getSession(HttpServletRequest req, SessionDto sessionDto) {

		HttpSession session = req.getSession();

		if ((SessionDto)session.getAttribute("session") == null)
			session.setAttribute("session", sessionDto);
		return session;
	}

	@Override
	public void destorySession(HttpSession httpSession) {
		httpSession.invalidate();
	}
}
