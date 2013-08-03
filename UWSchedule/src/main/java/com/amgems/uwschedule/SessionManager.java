/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   UWSchedule student class and registration sharing interface
 *   Copyright (C) 2013 Sherman Pay, Jeremy Teo, Zachary Iqbal
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.amgems.uwschedule;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;


/**
 * This class maintains session cookies between
 * the UW and a given client. Makes a request
 * to login and captures hidden fields required for
 * validation checks.
 */

public class SessionManager {

    private static SessionManager mSessionManager;
    
    /* HTTP Session Variables */
    private DefaultHttpClient mClient;
    private boolean sessionEstablised;
    private static final String userAgent = "Mozilla/5.0";
    
    private SessionManager() {
        mClient = new DefaultHttpClient();
        mClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
    }

    /**
     *  Static factory method ensures class is a singleton.
     */
    public static SessionManager initialize() {
        return (mSessionManager == null) ? new SessionManager() : mSessionManager;
    }



    /*    public static void main(String[] args) throws IOException{

	  HttpGet httpget = new HttpGet("https://www.facebook.com/");
	  httpget.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
	  HttpResponse response = httpClient.execute(httpget);
	  HttpEntity entity = response.getEntity();

	  System.out.println("Login form: " + response.getStatusLine());
	  if (entity != null) {
	  entity.consumeContent();
	  }
	  System.out.println("Initial cookies: ");
	  List<Cookie> cookies = httpClient.getCookieStore().getCookies();
	  if(cookies.isEmpty()) {
	  System.out.println("None");
	  } else {
	  for (int i = 0; i < cookies.size(); i++) {
	  System.out.println(i + ") " + cookies.get(i).toString());
	  }
	  }

	  HttpPost httpPost = new HttpPost("https://www.facebook.com/login.php");

	  List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	  nvps.add(new BasicNameValuePair("email", "ninjaxp@hotmail.com"));
	  nvps.add(new BasicNameValuePair("pass", ""));

	  httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	
	  response = httpClient.execute(httpPost);
	  entity = response.getEntity();

	  System.out.println("Post-Logon cookies: ");
	  cookies = httpClient.getCookieStore().getCookies();
	  if(cookies.isEmpty()) {
	  System.out.println("None");
	  } else {
	  for (int i = 0; i < cookies.size(); i++) {
	  System.out.println(i + ") " + cookies.get(i).toString());
	  }
	  }
	
	  if (entity != null) {
	  entity.consumeContent();
	  }
	
	  httpget = new HttpGet("https://www.facebook.com/home.php");	
	  response = httpClient.execute(httpget);
	  entity = response.getEntity();

	  if(response != null) {
	  InputStream instream = entity.getContent();
	  Scanner inReader = new Scanner(new InputStreamReader(instream));
	  try {
	  while(inReader.hasNextLine()) {
	  System.out.println(inReader.nextLine());
	  }
	  } finally {
	  instream.close();
	  System.out.println();
	  }

	  }
	  }
    */

}
