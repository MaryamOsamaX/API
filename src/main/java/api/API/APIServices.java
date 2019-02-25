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

}
