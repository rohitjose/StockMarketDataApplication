package au.edu.unsw.soacourse.stockmarketdata;

import java.util.List;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StockMarketDataSOAPInInterceptor extends AbstractSoapInterceptor {

	public StockMarketDataSOAPInInterceptor() {
		super(Phase.USER_PROTOCOL);
	}

	public void traverseElement(Node node) {
		NodeList nodes = node.getChildNodes();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (nodes.getLength() == 1) {

				System.err
						.println(node.getNamespaceURI() + ":" + node.getLocalName() + " has " + node.getTextContent());
			} else {
				System.err.println(node.getNamespaceURI() + ":" + node.getLocalName());
				for (int i = 0; i < nodes.getLength(); i++) {
					Node currentNode = nodes.item(i);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						traverseElement(currentNode);
					}
				}
			}
		}

	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {

		List<Header> headers = message.getHeaders();
		if (!headers.isEmpty()) {
			System.err.println("A message received with the following headers:");
			for (Header header : headers) {
				Node nodeElement = (Node) header.getObject();
				traverseElement(nodeElement);

			}
		} else {
			System.err.println("A message received with no SOAP headers");
		}
	}
}
