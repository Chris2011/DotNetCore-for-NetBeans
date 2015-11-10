package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author chrl
 */
class ReferencesChildFactory extends ChildFactory<String> {

    @Override
    protected boolean createKeys(List<String> listToPopulate) {
        String[] objs = new String[5];
        CSharpProjFileParser sxp = new CSharpProjFileParser();
        sxp.parseXmlDocument("info.xml");
        NodeList resources = sxp.getResources();

        for (int i = 0; i < objs.length; i++) {
            String referenceNode = resources.item(i).getNodeName();
            objs[i] = referenceNode;
        }

        listToPopulate.addAll(Arrays.asList(objs));

        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        AbstractNode result = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        result.setDisplayName(key);
        result.setIconBaseWithExtension("org/chrisle/netbeans/plugins/csharp4netbeans/resources/references.png");

        return result;
    }
    
    private class CSharpProjFileParser {
        private Document _parsedXmlDocument;

        public void parseXmlDocument(String xmlDocument) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                this._parsedXmlDocument = db.parse(new File(xmlDocument));
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        public Document getParsedDocument() {
            return this._parsedXmlDocument;
        }
        
        public NodeList getResources() {
            Element docElem = this._parsedXmlDocument.getDocumentElement();
            NodeList itemGroups = docElem.getElementsByTagName("ItemGroup");
            
            for (int i = 0; i < itemGroups.getLength(); i++) {
                Element itemGroupElem = (Element)itemGroups.item(i);

                NodeList references = itemGroupElem.getElementsByTagName("Reference");
                
                if (references.getLength() > 0) {
                    for (int j = 0; j < references.getLength(); j++) {
                        Element referenceElem = (Element)references.item(j);

                        referenceElem.getAttribute("Include");
                    }
                }
                
            }
            
            return null;
        }
    }
}
