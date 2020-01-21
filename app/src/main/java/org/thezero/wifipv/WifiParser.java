package org.thezero.wifipv;

import android.text.TextUtils;

import org.thezero.wifipv.data.Password;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class WifiParser {
    private static final String ns = null;
    private List<Password> networks = new ArrayList<>();

    WifiParser(List<String> content) {
        String document = TextUtils.join("\n", content);
        final InputStream stream = new ByteArrayInputStream(document.getBytes());
        try {
            parse(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void parse(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        NodeList n1 = doc.getElementsByTagName("WifiConfiguration");
        String ssid = null, psk = null, kt = null, idt = null;

        for (int i = 0; i < n1.getLength(); i++) {
            Element e = (Element) n1.item(i);
            NodeList children = e.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeName().equalsIgnoreCase("string")) {
                    switch(((Element)child).getAttribute("name")) {
                        case "ConfigKey":
                            kt = child.getTextContent().split("\"")[2];
                            break;
                        case "SSID":
                            ssid = child.getTextContent().replace("\"", "");
                            break;
                        case "PreSharedKey":
                            psk = child.getTextContent().replace("\"", "");
                            break;
                        default:
                            break;
                    }
                }
            }
            networks.add(new Password(ssid, psk, kt, idt));
        }
    }

    List<Password> getPasswords() {
        return networks;
    }
}
