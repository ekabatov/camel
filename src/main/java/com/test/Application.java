package com.test;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.jms.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        Connection con = factory.createConnection();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("remotingQueue");

        QueueBrowser browser = session.createBrowser(queue);
        con.start();  // don't forget this!

        Enumeration<Message> e = (Enumeration<Message>) browser.getEnumeration();
//        List<Message> list = new ArrayList<>(Collections.list(e));
//                list.stream()
//                .forEach(System.out::println);
        while (e.hasMoreElements()) {
            Message msg = e.nextElement();
//            Enumeration<String> props = (Enumeration<String>) msg.getText();
//            List<String> properties = Collections.list(props);
//            properties.forEach(System.out::println);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(((TextMessage) msg).getText().getBytes("UTF-8")));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
//            System.out.println("Found " + doc.getDocumentElement().getElementsByTagName("CoordinateMessage"));
            System.out.println("Found " + nodeList.item(11).getNodeName());
//            System.out.println("Found " + );
//            nodeList.item(1).setTextContent("login");
            System.out.println(msg.getJMSCorrelationID());
            toString(doc);
        }
        con.stop();
    }

    private static void toString(Document newDoc) throws Exception{
        DOMSource domSource = new DOMSource(newDoc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        System.out.println(sw.toString());
    }
}
