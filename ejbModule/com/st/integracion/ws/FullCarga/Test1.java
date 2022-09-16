package com.st.integracion.ws.FullCarga;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;

public class Test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String trama = "<parametro><contrapartida>123</contrapartida><recontrapartida>123</recontrapartida></parametro>";
		
		try 
		{
			InputSource is=new InputSource(new StringReader(trama));
			DocumentBuilder builder;
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root=builder.parse(is);
			Element ele = (Element) root.getElementsByTagName("recontrapartida").item(0);
			System.out.println(ele.getTextContent());

			Element newElement = root.createElement("test"); // Element to be inserted 
			newElement.setAttribute("name", "holas");
			newElement.setTextContent("prueba");
			ele.getParentNode().insertBefore(newElement, ele.getNextSibling());

			Element eleNew = (Element) root.getElementsByTagName("parametro").item(0);
			System.out.println(eleNew.getTextContent());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
