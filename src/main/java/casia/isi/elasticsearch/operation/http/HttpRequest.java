package casia.isi.elasticsearch.operation.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import casia.isi.elasticsearch.common.HttpHeader;

/**
 * http
 * @author wzy
 * @version elasticsearch - 5.6.3
 */
public class HttpRequest {
	private Logger logger = Logger.getLogger(HttpRequest.class);
	private HttpClient httpClient = null;
	
	@SuppressWarnings("deprecation")
	public HttpRequest() {
		httpClient = new DefaultHttpClient();
		if (httpClient != null) {
			httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HttpHeader.Encoding_UTF_8 );
		}
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2 * 60 * 1000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", true);
	} 
	
	public synchronized String httpGet( String url){
		HttpGet httpGet=new HttpGet( url ); 
		try {
			httpGet.setHeader( "User-Agent" , HttpHeader.User_Agent_Firefox ); 
			HttpResponse httpResponse = this.httpClient.execute(httpGet);
			int requestStatus = httpResponse.getStatusLine().getStatusCode();
			
			if (requestStatus == HttpStatus.SC_OK) {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				return html;
			} else {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				
				logger.info(requestStatus + "\t" + url);
				logger.error(html);
				
				return html;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} finally {
			try {
				httpGet.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public synchronized String httpPost( String url , String query ){
		HttpPost httpPost=new HttpPost( url ); 
		try {
			
            StringEntity input = new StringEntity(query,  HttpHeader.Encoding_UTF_8 );
            input.setContentType("application/json");
            httpPost.setEntity(input);
            
            HttpResponse httpResponse = this.httpClient.execute( httpPost );
			int requestStatus = httpResponse.getStatusLine().getStatusCode();
			
			if (requestStatus == HttpStatus.SC_OK) {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				return html;
			} else {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				
				logger.info(requestStatus + "\t" + url);
				logger.error(html);
				
				return html;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} finally {
			try {
				httpPost.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public synchronized String httpPut( String url , String query ){
		HttpPut httpPut=new HttpPut( url ); 
		try {
			if( query != null ){
			    StringEntity input = new StringEntity(query,  HttpHeader.Encoding_UTF_8 );
	            input.setContentType("application/json");
	            httpPut.setEntity(input);
			}
            
            HttpResponse httpResponse = this.httpClient.execute( httpPut );
			int requestStatus = httpResponse.getStatusLine().getStatusCode();
			
			if (requestStatus == HttpStatus.SC_OK) {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				return html;
			} else {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				
				logger.info(requestStatus + "\t" + url);
				logger.error(html);
				
				return html;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("error",e);
		} finally {
			try {
				httpPut.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public synchronized String postDeleteRequest( String url , String query ) {
		HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
		try {
			if( query!=null && !query.equals("") ){
				StringEntity input = new StringEntity( query, HttpHeader.Encoding_UTF_8 );
				input.setContentType("application/json");
				httpDeleteWithBody.setEntity(input);
			}
			HttpResponse httpResponse = this.httpClient.execute(httpDeleteWithBody);
			int requestStatus = httpResponse.getStatusLine().getStatusCode();
			if (requestStatus == HttpStatus.SC_OK) {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				return html;
			} else {
				byte[] temp = getResponseBody(httpResponse);
				String html = new String(temp, HttpHeader.Encoding_UTF_8 );
				logger.info(requestStatus + "\t" + url);
				logger.error(html);
				System.out.println(requestStatus + "\t" + url);
				System.out.println(html);
				
				return html;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error",e);
		} finally {
			try {
				httpDeleteWithBody.abort();
			} catch (Exception e) {
			}
		}
		return null;
	}
	/**
	 * 处理可能出现的压缩格式
	 * 
	 * @param //method
	 * @return byte[]
	 */
	public synchronized byte[] getResponseBody(HttpResponse response) {
		try {
			Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");
			HttpEntity entity = response.getEntity();
			if (contentEncodingHeader != null) {
				String contentEncoding = contentEncodingHeader.getValue();
				if (contentEncoding.toLowerCase(Locale.US).indexOf("gzip") != -1) {
					GZIPInputStream gzipInput = null;
					try {
						gzipInput = new GZIPInputStream(entity.getContent());
					} catch (EOFException e) {
						logger.error("read gzip inputstream eof exception!");
					}
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buffer = new byte[256];
					int n;
					while ((n = gzipInput.read(buffer)) >= 0) {
						out.write(buffer, 0, n);
					}
					return out.toByteArray();
				}
			}
			return EntityUtils.toByteArray(entity);
		} catch (Exception e) {
			logger.error("read response body exception! ", e);
		}

		return null;
	}
}
