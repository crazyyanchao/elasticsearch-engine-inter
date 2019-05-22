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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import casia.isi.elasticsearch.common.HttpHeader;
/**
 * http
 * @author wzy
 *
 */
public class HttpRequest2 {
	private Logger logger = Logger.getLogger(HttpRequest2.class);
	private CloseableHttpClient httpClient = null;
	
	
	public HttpRequest2() {
		httpClient=HttpClients.createDefault();
	} 
	public synchronized String httpGet( String url ){
		try {
			HttpGet httpGet=new HttpGet( url ); 
			
			CloseableHttpResponse httpResponse = this.httpClient.execute(httpGet);
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
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public synchronized String httpPost( String url , String query ){
		try {
            HttpPost httpPost=new HttpPost( url ); 
            StringEntity input = new StringEntity(query,  HttpHeader.Encoding_UTF_8 );
            input.setContentType("application/json");
//            input.setContentEncoding("gzip");
            httpPost.setEntity(input);
			CloseableHttpResponse httpResponse = this.httpClient.execute( httpPost );
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
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 处理可能出现的压缩格式
	 * 
	 * @param method
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
	public static void main(String[] args) {
		HttpRequest2 httpRequest = new HttpRequest2();
	}
}
