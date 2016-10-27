package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.api.project.Project;
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
    
    private final Project _project;

    public ReferencesChildFactory(Project project) {
        this._project = project;
    }

    @Override
    protected boolean createKeys(List<String> listToPopulate) {
        CSharpProjFileParser sxp = new CSharpProjFileParser();
        
        sxp.parseXmlDocument(this._project.getProjectDirectory().getFileObject(this._project.getProjectDirectory().getName() + ".csproj").getPath());
        listToPopulate.addAll(sxp.getResources());

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
        
        public List<String> getResources() {
            Element docElem = this._parsedXmlDocument.getDocumentElement();
            NodeList itemGroupsElem = docElem.getElementsByTagName("ItemGroup");
            List<String> references = new ArrayList<>();
            
            for (int i = 0; i < itemGroupsElem.getLength(); i++) {
                Element itemGroupElem = (Element)itemGroupsElem.item(i);

                NodeList referencesElem = itemGroupElem.getElementsByTagName("Reference");
                
                if (referencesElem.getLength() > 0) {
                    for (int j = 0; j < referencesElem.getLength(); j++) {
                        Element referenceElem = (Element)referencesElem.item(j);

                        references.add(referenceElem.getAttribute("Include"));
                    }
                }
            }
            
            return references;
        }
    }
}
