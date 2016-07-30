package com.jatis.mobile.whatsapptx;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

/** Json Simple Lib refer to https://code.google.com/archive/p/json-simple/ */
//import org.json.simple.JSONArray;  
import org.json.simple.JSONObject;  
import org.json.simple.parser.JSONParser;

 
public class WhatsappMQ {
	
	static String OS = System.getProperty("os.name").toLowerCase();
	private static String dirData, home=System.getProperty("user.home");
	private static String wadata="genymotion/bash/wadata";
	private static String jsonFile="checkMQ.json", resultOfJson="";
	private static String[] mData = new String[5];
	private static String MQhost = "tcp://localhost:61616", MQname="whatsapp";
	
    public static void main(String[] args) throws Exception {
        
    	CheckFile();
    	
    	
    	MQConsumer consumer = new MQConsumer();
    	Thread threadConsumer = new Thread(consumer);
        threadConsumer.start();
    	
    	
    	/*
    	MQProducer producer = new MQProducer(); 
        Thread threadProducer = new Thread(producer);
        threadProducer.start();
        */
 
    }
    
    public static void CheckFile(){
    	try{
    		if(OS.indexOf("win") >= 0){
    			dirData="C:\\wadata";
    		}else{
    			dirData=home + File.separator + wadata;
    		}
    		
    		//create new directory for whatsapp data
    		File mdir = new File(dirData);
    		if(mdir.exists()){
    			System.out.println(mdir + " already exists in your home");
    		}else if(mdir.mkdirs()){
    			System.out.println(mdir + " was created");
    		}else{
    			System.out.println(mdir + " could not created");
    		}
    		   		
    		File mFile = new File(dirData+File.separator+jsonFile);
    		if(mFile.exists()){
    			System.out.println(mFile + " already exist");
    		}else if(mFile.createNewFile()){
    			System.out.println(mdir + " was created");
    		}else{
    			System.out.println(mdir + " could not created");
    		}
    		
    		//System.out.println("length nya : " + mFile.length());
    		
    	}catch(Exception ex){
    		System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace();
    	}
    }
    
    public static void WriteJSON(String[] data){
    	
    	JSONObject checkMsgJson = new JSONObject();
    	checkMsgJson.put("stat", data[0]);
    	checkMsgJson.put("trxid", data[1]);
    	checkMsgJson.put("mdn", data[2]);
    	checkMsgJson.put("msg", data[3]);
    	try{
    		File mFile = new File(dirData+File.separator+jsonFile);
    		mFile.createNewFile();
    		FileWriter mFileWriter = new FileWriter(mFile);
    		System.out.println("JSON file :");
    		System.out.println(checkMsgJson);
    		
    		mFileWriter.write(checkMsgJson.toJSONString());
    		mFileWriter.flush();
    		mFileWriter.close();
    	}catch(Exception ex){
    		System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace();
    	}
    }
    
    public static String ReadJSON(String read){
    	String result="";
    	
    	JSONParser parser = new JSONParser();
    	try{
    		Object obj = parser.parse(new FileReader(dirData+File.separator+jsonFile));
    		JSONObject jObject = (JSONObject) obj;
    		
    		if(read == "stat"){
	    		result=(String) jObject.get("stat");
    		}else if(read == "trxid"){
	    		result=(String) jObject.get("trxid");
    		}else if(read == "mdn"){
	    		result=(String) jObject.get("mdn");
    		}else if(read == "msg"){
	    		result=(String) jObject.get("msg");
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	return result;
    }
 
    public static class MQProducer implements Runnable {
        String msgID, mdn, message;
    	public void run() {
        	//while(true){
	            try {
	            	msgID = UUID.randomUUID().toString();
	            	mdn="81910173445";
	            	message="Hi Sobat Alumni 6596 !" + "\n\n" +
						"Pastikan kamu hadir pada acara Reuni 20th 6596 di :" + "\n" +
						"Restoran Istana Nelayan Tangerang," + "\n" +
						"Sabtu 3 Sept 2016 pkl 10:00" + "\n\n" +
						"Disana kita akan beri penghargaan kepada para bapak ibu guru yg pernah membimbing kita dulu." + "\n" +
						
						"Acaranya apa lagi ? ada doorprize, live music, games dan yg pasti temu kangen donk.";
	                // Create a ConnectionFactory
	                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
	                		MQhost);
	 
	                // Create a Connection
	                Connection connection = connectionFactory.createConnection();
	                connection.start();
	 
	                // Create a Session
	                Session session = connection.createSession(false,
	                        Session.AUTO_ACKNOWLEDGE);
	 
	                // Create the destination (Topic or Queue)
	                Destination destination = session.createQueue(MQname);
	 
	                // Create a MessageProducer from the Session to the Topic or
	                // Queue
	                MessageProducer producer = session.createProducer(destination);
	                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	 
	                // Create a messages
	                String text = msgID
	                        + "&___" + mdn 
	                        + "&___" + message;
	                TextMessage message = session.createTextMessage(text);
	 
	                // Tell the producer to send the message
	                System.out.println("Sent message: " + message.hashCode()
	                        + " : " + Thread.currentThread().getName());
	                producer.send(message);
	 
	                // Clean up
	                session.close();
	                connection.close();
	                Thread.sleep(2000);
	            } catch (Exception e) {
	                System.out.println("Caught: " + e);
	                e.printStackTrace();
	            }
        	//}
        }
    }
 
    public static class MQConsumer implements Runnable,
            ExceptionListener {
        public void run() {
            while(true){
	        	try {
	 
	                // Create a ConnectionFactory
	                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
	                		MQhost);
	 
	                // Create a Connection
	                Connection connection = connectionFactory.createConnection();
	                connection.start();
	 
	                connection.setExceptionListener(this);
	 
	                // Create a Session
	                Session session = connection.createSession(false,
	                        Session.AUTO_ACKNOWLEDGE);
	 
	                // Create the destination (Topic or Queue)
	                Destination destination = session.createQueue(MQname);
	 
	                // Create a MessageConsumer from the Session to the Topic or
	                // Queue
	                MessageConsumer consumer = session.createConsumer(destination);
	 
	                // Wait for a message
	                Message message = consumer.receive(1000);
	 
	                if (message instanceof TextMessage) {
	                    TextMessage textMessage = (TextMessage) message;
	                    String rawData = textMessage.getText();
	                    String[] text = rawData.split("&___");
	                    
	                    boolean isTaken=false, isNewFile=false;
	                    File mFile = new File(dirData+File.separator+jsonFile);
	                    if(mFile.length() < 2){
	                    	//JSON for stat
                    		mData[0] = "false";
                    		//JSON for trxid
                    		mData[1] = text[0];
                    		//JSON for stat
                    		mData[2] = text[1];
                    		//JSON for messages
                    		mData[3] = text[2];
                    		WriteJSON(mData);
                    		isNewFile=true;
	            		}
	                    
	                    while(isTaken == false && isNewFile == false){
	                    	resultOfJson = ReadJSON("stat");
	                    	//if(resultOfJson == "true"){// || resultOfJson == "True" || resultOfJson == "TRUE" ){
	                    	switch(resultOfJson){
	                    	case "true":
	                    		//JSON for stat
	                    		mData[0] = "false";
	                    		//JSON for trxid
	                    		mData[1] = text[0];
	                    		//JSON for stat
	                    		mData[2] = text[1];
	                    		//JSON for messages
	                    		mData[3] = text[2];
	                    		WriteJSON(mData);
	                    		System.out.println("JSON File already taken by whatsapp transmitter");
	                    		isTaken = true;
	                    	}
	                    }
	                }else{
	                	System.out.println("Currently, there is not any messages received -- messages: "+message );
	                }
	                consumer.close();
	                session.close();
	                connection.close();
	                //Thread.sleep(4000);
	            } catch (Exception e) {
	                System.out.println("Caught: " + e);
	                e.printStackTrace();
	            }
            }
        }
 
        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }
}
