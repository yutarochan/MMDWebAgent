import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Mitsuku {
	public static void main(String[] args) throws IOException {
		/*
		HttpServer server = HttpServer.create(new InetSocketAddress(1234), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
		*/
		System.out.println(talk(""));
	}

	public static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            Headers h = t.getResponseHeaders();
		    h.add("Content-Type", "application/jsonp; charset=UTF-8");
		    h.add("Access-Control-Allow-Origin","*");
		    h.add("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
		    h.add("Access-Control-Allow-Methods","POST, GET, OPTIONS");
        	
        	String response = talk(t.getRequestURI().toString().substring(1, t.getRequestURI().toString().length()));
        	System.out.println(response);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


	public static String talk(String input_data) {
		if (input_data.contains("language") || input_data.contains("dev") || input_data.contains("dev language"))
			return "Yo, its obviously going to be Node JS.";
		
		if (input_data.contains("hackathon"))
			return "The answer is always PennApps, there are no other better hackathons than PennApps!";

		String url = "http://fiddle.pandorabots.com/pandora/talk?botid=9fa364f2fe345a10";
		String param = "botcust2=db07a35ace1a0db2&message=" + input_data;

		String result = excutePost(url, param);

		int trim_length = result.substring(0,
				result.indexOf("<B> Mitsuku:</B>")).length();
		String data = result.substring(trim_length, result.length());
		data = data.substring("<B> Mitsuku:</B> ".length(),
				data.indexOf("<br>"));

		Document doc = Jsoup.parse(data);
		String txt_data = doc.text();

		String d = "";
		if (txt_data.contains("xgallery") || txt_data.contains("xloadswf3") || txt_data.contains("xlinks")) {
			String[] s = txt_data.split(" ");
			for (int i = 1; i < s.length; i++)
				d += s[i] + " ";
		} else
			d = txt_data;

		// d.replaceAll("Mitsuku", "Wanda");
		String j = "";
		String[] x = d.split(" ");
		for (int i = 0; i < x.length; i++) {
			if (x[i].equals("Mitsuku"))
				x[i] = "Wanda";
			else if (x[i].equals("Mitsuku."))
				x[i] = "Wanda.";

			j += x[i] + " ";
		}

		return j;
	}

	public static String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
}
