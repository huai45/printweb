package com.huai.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpPostUtils {

	private static final Logger log = Logger.getLogger(HttpPostUtils.class);

	public static String doPost(String url, Map<String, String> param) {
		/*����HTTPost����*/
		String strResult = null;
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * NameValuePairʵ����������ķ�װ
		 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : param.entrySet()) {
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			/* �������������������*/
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/*�������󲢵ȴ���Ӧ*/
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/*��״̬��Ϊ200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/*����������*/
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				log.error("Error Response: " + httpResponse.getStatusLine().toString());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}
}
