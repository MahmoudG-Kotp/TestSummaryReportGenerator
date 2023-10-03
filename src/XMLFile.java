import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a utility class for parsing and working with XML files.
 */
public class XMLFile {
    private Document parsedXMLSourceFile; // The parsed XML document

    /**
     * Constructor for XMLFile.
     *
     * @param path The path to the XML file to be parsed.
     */
    public XMLFile(String path) {
        try {
            // Parsing the XML file
            parsedXMLSourceFile = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
            // Normalizing the text within the parsed XML document
            parsedXMLSourceFile.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of XML elements with a specified tag name from the parsed XML document.
     *
     * @param tagName The name of the XML elements to retrieve.
     * @return An ArrayList containing the retrieved XML elements.
     */
    public ArrayList<Element> getNodeElementsListByTagName(String tagName) {
        ArrayList<Element> elementsList = new ArrayList<>();
        NodeList nodeList = parsedXMLSourceFile.getElementsByTagName(tagName);

        for (int nodeCounter = 0; nodeCounter < nodeList.getLength(); nodeCounter++) {
            elementsList.add((Element) nodeList.item(nodeCounter));
        }

        return elementsList;
    }
}

