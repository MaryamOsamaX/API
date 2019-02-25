package api.API;

import java.util.*;
/*
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
*/

import org.json.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;



import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;



import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

@Controller
public class APIServices {
    
	@RequestMapping(method = RequestMethod.GET, value = "/cityweather")
	public String getweatherview(Model model)
	{   data  x=new data();
		model.addAttribute("data" , x);
		return "weather";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/cityweather")
	public String getWeather(Model model,@ModelAttribute("data") data x) {
	 
		String country=x.getCountry();
		RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?APPID=9c81052dfd3e0256818521f463e98e86&q="+country+"&units=metric"
	    		, String.class);
	    //System.out.println(result);
	    JSONObject obj=new JSONObject(result);
		JSONArray arr = obj.getJSONArray("weather");

		JSONObject obj2 = arr.getJSONObject(0);
		String description = obj2.getString("description");

		JSONObject obj3 = obj.getJSONObject("main");
		double temp = obj3.getDouble("temp");
		double temp_min = obj3.getDouble("temp_min");
		double temp_max = obj3.getDouble("temp_max");
		model.addAttribute("desc" ,description );
		model.addAttribute("temp" ,temp+" ْc" );
		model.addAttribute("min" ,temp_min+" ْc" );
		model.addAttribute("max" ,temp_max+" ْc" );
		return "weather";
	}
	@RequestMapping(method = RequestMethod.GET, value = "/citynews")
	public String getnewsView(Model model)
	{   data  x=new data();
		model.addAttribute("data" , x);
		return "news";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/citynews")
	public String getNews(Model model,@ModelAttribute("data") data x) {
		List <Article> article=new ArrayList <Article>();
		String country=x.getCountry();
		
		
		RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.getForObject("https://newsapi.org/v2/everything?apikey=e4d5462b31484f4091d259103188b15f&q="+country
	    		, String.class);
	    JSONObject post=new JSONObject(result);
	    JSONArray articles = post.getJSONArray("articles");
	    for(int i=0 ; i< articles.length(); i++)
	    { 
	    	Article a=new Article();
	    	JSONObject obj2 = articles.getJSONObject(i);
	    	a.setTitle(obj2.getString("title"));
	    	
	    	a.setUrl(obj2.getString("url"));
	    	article.add(a);
	    	
	    }
	    model.addAttribute("articles", article);
	   
	 
		
		return "news";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/cityinfo")
	public String getInfoView(Model model)
	{   data  x=new data();
		model.addAttribute("data" , x);
		return "info";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/cityinfo")
	public String getInfo(Model model,@ModelAttribute("data") data x) {
	  
		String country=x.getCountry();
		RestTemplate restTemplate = new RestTemplate();
	    String result = restTemplate.getForObject("https://restcountries.eu/rest/v2/name/"+country, String.class);
	   // System.out.println(result);
	    String subres=result.substring(1, result.length()-1);
	   JSONObject res=new JSONObject(subres); 
	  
	   	String name=res.getString("name");
	    String capital=res.getString("capital");
	    String subregion=res.getString("subregion");
	    int population=res.getInt("population");
	    double area=res.getDouble("area");
	  
	    JSONArray arr = res.getJSONArray("languages");

		JSONObject obj2 = arr.getJSONObject(0);
		String language = obj2.getString("name");
		
        model.addAttribute("name" , name);
        model.addAttribute("cap", capital);
        model.addAttribute("sub", subregion);
        model.addAttribute("pop", population);
        model.addAttribute("area", area);
        model.addAttribute("lan", language);
	  
		
		return "info";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/home")
	public String home(Model model) {
		data x=new data();
		model.addAttribute("data",x );
		return "home";
	}
	
	
	private final static Log logger = LogFactory.getLog(APIServices.class);
	private static final String APPLICATION_NAME = "";
	private static HttpTransport httpTransport;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static com.google.api.services.calendar.Calendar client;

	GoogleClientSecrets clientSecrets;
	GoogleAuthorizationCodeFlow flow;
	Credential credential;

	@Value("${google.client.client-id}")
	private String clientId;
	@Value("${google.client.client-secret}")
	private String clientSecret;
	@Value("${google.client.redirectUri}")
	private String redirectURI;

	private Set<Event> events = new HashSet<>();

	final DateTime date1 = new DateTime("2000-05-05T16:30:00.000+05:30");
	final DateTime date2 = new DateTime("2200-05-05T16:30:00.000+05:30");

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(authorize());
	}

	@RequestMapping(value = "/login/google", method = RequestMethod.GET, params = "code")
	public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
		com.google.api.services.calendar.model.Events eventList;
		String message;
		try {
			TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
			credential = flow.createAndStoreCredential(response, "userID");
			client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
			Events events = client.events();
			eventList = events.list("primary").setTimeMin(date1).setTimeMax(date2).execute();
			message = eventList.getItems().toString();
			System.out.println("My:" + eventList.getItems());
			
		} catch (Exception e) {
			logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.");
			message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
					+ " Redirecting to google connection status page.";
		}

		System.out.println("cal message:" + message);
		return new ResponseEntity<>(message, HttpStatus.OK );
	}

	public Set<Event> getEvents() throws IOException {
		return this.events;
	}

	private String authorize() throws Exception {
		AuthorizationCodeRequestUrl authorizationUrl;
		if (flow == null) {
			Details web = new Details();
			web.setClientId(clientId);
			web.setClientSecret(clientSecret);
			clientSecrets = new GoogleClientSecrets().setWeb(web);
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
					Collections.singleton(CalendarScopes.CALENDAR)).build();
		}
		authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
		System.out.println("cal authorizationUrl->" + authorizationUrl);
		return authorizationUrl.build();
	}
	
}
