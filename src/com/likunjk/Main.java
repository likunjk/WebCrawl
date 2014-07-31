package com.likunjk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Main {

	public static String domai_url_1 = "http://pat.zju.edu.cn/users/sign_in";
	public static String domai_url_2 = "http://pat.zju.edu.cn";
	//http://pat.zju.edu.cn/submissions/554840/source
	public static String domai_url_3 = "http://pat.zju.edu.cn/submissions/";
	
	public static String user_name = "likunjk2";
	public static String pass_word = "xxxxxxxx";
	
	public static String char_set = "UTF-8";
	public static String prefix = "Download/";
	
	public static void main(String[] args) {

//		testFunction();
		
		// 1 ����httpclient
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		// 2 ����postMethod
		PostMethod postMethod = new PostMethod(domai_url_1);
		
		NameValuePair username = new NameValuePair("user[handle]", user_name);
		NameValuePair password = new NameValuePair("user[password]", pass_word);
		postMethod.setRequestBody(new  NameValuePair[] {username, password});
		
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 50000);
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		// 3 ִ��Http����
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			/* 4 �жϷ��ʵ�״̬�� */
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + postMethod.getStatusLine());
			}

			/* 5 ���� HTTP ��Ӧ���� */
//			showHeader( postMethod.getResponseHeaders() );

			// ��ȡΪ InputStream������ҳ������������ʱ���Ƽ�ʹ��
//			showContent(postMethod.getResponseBodyAsStream());
			
			//����¼�ɹ���������ҳ��ת��
	        Cookie[] cookies = httpClient.getState().getCookies();  
	        httpClient.getState().addCookies(cookies);  
	        
	        GetMethod  getMethod = new GetMethod(domai_url_2);
	        getMethod.setRequestHeader("Cookie" , cookies.toString());
	        
	        int statusCode2 = httpClient.executeMethod(getMethod);
			if (statusCode2 != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine());
			}
	        
//			showHeader( getMethod.getResponseHeaders() );
//			showContent( getMethod.getResponseBodyAsStream() );
			getMethod.releaseConnection();
			
			helper(httpClient, cookies);
			
		} catch (HttpException e) {
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			e.printStackTrace();
		} finally {
			/* �ͷ����� */
			postMethod.releaseConnection();
		}
	}
	
	public static void showHeader(Header[] headers){
		
		System.out.println("------------------------�����Ƿ���Httpͷ��Ϣ--------------------------------");
		for (Header h : headers) {
			System.out.println(h.getName() + " " + h.getValue());
		}
	}
	
	public static void showContent(InputStream inputStream){
		
		System.out.println("------------------------�����Ƿ���Http������Ϣ--------------------------------");
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, "ISO-8859-1"));

			StringBuffer stringBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = bufferedReader.readLine()) != null) {
				stringBuffer.append(resTemp + "\n");
			}
			String responseString = stringBuffer.toString();
			System.out.println(new String(
					responseString.getBytes("ISO-8859-1"), char_set));
		} catch (HttpException e) {
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			e.printStackTrace();
		}
	}

	public static void helper(HttpClient httpClient, Cookie[] cookies){
		
		//��ʼץȡԴ������ҳ
		for(int i=554800; i<554900; ++i){
			
			String url = domai_url_3+i+"/source";
			System.out.println(url);
			
	        GetMethod  getMethod = new GetMethod(url);
	        getMethod.setRequestHeader("Cookie" , cookies.toString());
	        
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: " + getMethod.getStatusLine());
					continue;
				}
				
//				showHeader(getMethod.getResponseHeaders());
//				showContent( getMethod.getResponseBodyAsStream() );
				saveToFile(getMethod.getResponseBodyAsStream(), i);
				getMethod.releaseConnection();
				
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
	}

	public static void saveToFile(InputStream inputStream, int index){
		
		System.out.println("------------------------���潫������Ϣ���浽�ļ�--------------------------------");
		
		try {
			
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream, char_set));

			StringBuffer stringBuffer = new StringBuffer();
			String resTemp = "";
			while ((resTemp = bufferedReader.readLine()) != null) {
				if(resTemp.contains("/s/")){
					resTemp = resTemp.replace("/s/", "");
//					System.out.println(resTemp);
				}
				stringBuffer.append(resTemp+"\n");
			}
			String responseString = stringBuffer.toString();
			
			File file = new File(prefix+index+".html");
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), char_set); 
			BufferedWriter writer = new BufferedWriter(write);   
			writer.write(responseString);
			writer.flush();
			writer.close();
			
		} catch (HttpException e) {
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("Please check your provided http address!");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			e.printStackTrace();
		}
		
	}

	public static void testFunction(){

		Scanner scan = new Scanner(System.in);
		while(scan.hasNext()){
			
			String resTemp = scan.next();
			if(resTemp.contains("/s/")){
				resTemp.replace("/s/", "ww");
				System.out.println(resTemp);
			}
		}
		
	}
	
}
