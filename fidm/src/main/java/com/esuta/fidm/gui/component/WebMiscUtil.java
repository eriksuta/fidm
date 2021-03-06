package com.esuta.fidm.gui.component;

import com.esuta.fidm.gui.page.PageBase;
import com.esuta.fidm.infra.exception.DatabaseCommunicationException;
import com.esuta.fidm.model.ModelService;
import com.esuta.fidm.model.federation.client.RestFederationServiceClient;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.log4j.Logger;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  @author shood
 * */
public final class WebMiscUtil {

    private static final Logger LOGGER = Logger.getLogger(WebMiscUtil.class);

    public static <T extends Enum> IModel<List<T>> createReadonlyModelFromEnum(final Class<T> type) {
        return new AbstractReadOnlyModel<List<T>>() {

            @Override
            public List<T> getObject() {
                List<T> list = new ArrayList<>();
                Collections.addAll(list, type.getEnumConstants());

                return list;
            }
        };
    }

    public static String prepareObjectTypeInXml(ObjectType object) throws ParserConfigurationException,
            IOException, SAXException, XPathExpressionException, TransformerException {

        if(object == null){
            return "Result is NOT valid";
        }

        XStream xStream = prepareXStream();
        String uglyXml = xStream.toXML(object);

        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new ByteArrayInputStream(uglyXml.getBytes("utf-8"))));

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                document,
                XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            node.getParentNode().removeChild(node);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);

        transformer.transform(new DOMSource(document), streamResult);

        return  stringWriter.toString();
    }

    private static XStream prepareXStream(){
        XStream xStream = new XStream(new StaxDriver());
        xStream.alias("account", AccountType.class);
        xStream.alias("orgUnit", OrgType.class);
        xStream.alias("resource", ResourceType.class);
        xStream.alias("role", RoleType.class);
        xStream.alias("user", UserType.class);
        xStream.alias("federationMember", FederationMemberType.class);
        xStream.alias("systemConfiguration", SystemConfigurationType.class);
        xStream.alias("assignment", AssignmentType.class);
        xStream.alias("inducement", InducementType.class);
        xStream.alias("objectRef", ObjectReferenceType.class);
        xStream.alias("federationID", FederationIdentifierType.class);
        xStream.alias("sharingPolicy", SharingPolicyType.class);
        xStream.alias("sharingRule", SharingRuleType.class);
        xStream.alias("provisioningPolicy", ProvisioningPolicyType.class);
        xStream.alias("provisioningRule", ProvisioningRuleType.class);
        return xStream;
    }

    public static ObjectType xmlToObject(String xmlObject){
        XStream xStream = prepareXStream();

        return (ObjectType) xStream.fromXML(xmlObject);
    }

    public static boolean isOrgAttributeSingleValue(String attributeName){
        return attributeName.equals("name") ||
                attributeName.equals("displayName") ||
                attributeName.equals("locality");
    }

    public static boolean isOrgAttributeMultiValue(String attributeName){
        return attributeName.equals("orgType") ||
                attributeName.equals("parentOrgUnits") ||
                attributeName.equals("governors") ||
                attributeName.equals("resourceInducements") ||
                attributeName.equals("roleInducements");
    }

    public static String getSingleValueSharingRuleDescription(SingleValueTolerance tolerance){
        if(SingleValueTolerance.ENFORCE.equals(tolerance)){
            return "The value of this attribute is enforced by origin org. unit and can't be modified in any way";
        } else if(SingleValueTolerance.ALLOW_OWN.equals(tolerance)){
            return "You are able to specify own value, but this will not be distributed in federation.";
        } else if(SingleValueTolerance.ALLOW_MODIFY.equals(tolerance)){
            return "You can modify the value and it will be distributed in federation";
        }

        return null;
    }

    public static String getMultiValueSharingRuleDescription(MultiValueTolerance tolerance){
        if(MultiValueTolerance.ENFORCE.equals(tolerance)){
            return "Values enforced by origin org. unit. Can't modify in any way";
        } else if(MultiValueTolerance.ALLOW_ADD_OWN.equals(tolerance)){
            return "You are able to add own values, but they are not distributed";
        } else if(MultiValueTolerance.ALLOW_MODIFY_OWN.equals(tolerance)){
            return "You are able to add own values and modify existing, but only locally";
        } else if(MultiValueTolerance.ALLOW_ADD.equals(tolerance)){
            return "You are able to add and change own values, added values will be distributed.";
        } else if(MultiValueTolerance.ALLOW_MODIFY.equals(tolerance)){
            return "You are able to perform any modifications and they will be distributed";
        }

        return null;
    }

    public static SharingRuleType getRuleByAttributeName(SharingPolicyType policy, String attributeName){
        if(policy == null || attributeName == null){
            return null;
        }

        for(SharingRuleType rule: policy.getRules()){
            if(attributeName.equals(rule.getAttributeName())){
                return rule;
            }
        }

        return null;
    }

    public static List<String> createOrgAttributeList(){
        List<String> attributeList = new ArrayList<>();
        attributeList.add("name");
        attributeList.add("displayName");
        attributeList.add("orgType");
        attributeList.add("locality");
        attributeList.add("parentOrgUnits");
        attributeList.add("governors");
        attributeList.add("resourceInducements");
        attributeList.add("roleInducements");
        return attributeList;
    }

    public static String getUniqueAttributeValue(ObjectType object, String uniqueAttributeName) throws NoSuchFieldException, IllegalAccessException {
        String attributeValue;

        Field attribute = object.getClass().getDeclaredField(uniqueAttributeName);
        attribute.setAccessible(true);
        attributeValue = (String)attribute.get(object);
        return attributeValue;
    }

    public static FederationMemberType getFederationMemberByName(String federationMemberName){
        FederationMemberType memberToRetrieve = null;

        try {
            List<FederationMemberType> allMembers = ModelService.getInstance().getAllObjectsOfType(FederationMemberType.class);

            for(FederationMemberType member: allMembers){
                if(member.getFederationMemberName().equals(federationMemberName)){
                    memberToRetrieve = member;
                }
            }
        } catch (DatabaseCommunicationException e) {
            LOGGER.error("Could not retrieve all federation member from the repository.", e);
        }

        return memberToRetrieve;
    }

    public static String getLocalFederationMemberIdentifier() throws DatabaseCommunicationException {
        SystemConfigurationType systemConfiguration = ModelService.getInstance().readObject(SystemConfigurationType.class, PageBase.SYSTEM_CONFIG_UID);
        return systemConfiguration.getIdentityProviderIdentifier();
    }
}
