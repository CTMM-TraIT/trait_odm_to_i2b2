/**
 * Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
 * This class uses RESTful web service API to get data set from Redcap.
 * @author: Alex Wu
 * @date: September 14, 2011
 */

package com.recomdata.redcap.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**This class is a RESTful web service client that get Redcap data fron all the API
*  defined by CHB
*  The class can call REST WebService with any parameters (as Map) and use a proxy per call.
*  The code also takes care of right encoding of parameters. The result is a json/xml string,
*/
public class GetRedcapService {
	/**Create a web query for RESTful web service
	 * @throws UnsupportedEncodingException 
	 */
	private String buildWebQuery(Map<String, String> params) throws UnsupportedEncodingException {
		String key = "";
		String value = "";
		StringBuilder sb = new StringBuilder();

		for(Map.Entry<String, String> entry : params.entrySet()) {
			key = URLEncoder.encode(entry.getKey(), "UTF-8");
			value = URLEncoder.encode(entry.getValue(), "UTF-8");
			sb.append(key).append("=").append(value).append("&");
		}

		return sb.toString().substring(0, sb.length() - 1);
	}
	
	public BufferedReader readRedcapWebServiceData(String endpoint, Map<String, String> params, String proxy, int port) throws IOException {
		Proxy proxyObject = null;
		if (StringUtils.isNotBlank(proxy) && port > 0) {
			InetSocketAddress proxyAddress = new InetSocketAddress(proxy, port);
			proxyObject = new Proxy(Proxy.Type.HTTP, proxyAddress);
		}

		URL url = new URL(endpoint);

		//make post mode connection
		URLConnection urlc = null;
		if (proxyObject == null) {
			urlc = url.openConnection();
		}
		else {
			urlc = url.openConnection(proxyObject);
		}
		urlc.setDoOutput(true);
		urlc.setAllowUserInteraction(false);

		//send query
		if (params != null) {
			String query = buildWebQuery(params);
			PrintStream ps = new PrintStream(urlc.getOutputStream());
			ps.print(query);
			ps.close();
		}

		//retrieve result
		return new BufferedReader(new InputStreamReader(urlc.getInputStream(), "UTF-8"));		
	}

	/**
	 * This web service method looks up the redcap metadata info for a project
	 * (study) and returns an xml response as String. It also works per Proxy per web service call
	 *
	 * @param HashMap <String, String> params
	 * @param String endpoint
	 * @return XML in String format
	 * @throws Exception
	 */
	public String getRedcapWebServiceData(String endpoint, Map<String, String> params, String proxy, int port) throws Exception
	{
		BufferedReader br = readRedcapWebServiceData(endpoint, params, proxy, port);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		br.close();
		return sb.toString();
	}

	/**Overloading method without parameters for Proxy server
	*/
	public String getRedcapWebServicedata(String endpoint, Map<String, String> params) throws Exception
	{
		return getRedcapWebServiceData(endpoint, params, null, 0);
	}
}