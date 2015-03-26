package com.esuta.fidm.gui.component;

import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  @author shood
 * */
public final class WebMiscUtil {

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
        xStream.alias("sharingPolicy", FederationSharingPolicyType.class);
        xStream.alias("sharingRule", FederationSharingRuleType.class);
        return xStream;
    }

    public static ObjectType xmlToObject(String xmlObject){
        XStream xStream = prepareXStream();

        return (ObjectType) xStream.fromXML(xmlObject);
    }

    public static String prepareObjectTypeInJson(ObjectType object){
        if(object == null){
            return "Result is NOT valid";
        }

        Gson gson = new Gson();

        String uglyJson = gson.toJson(object);

        gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJson);

        return gson.toJson(je);
    }

    public static ObjectType jsonToObject(String jsonObject, Class<? extends ObjectType> clazz){
        Gson gson = new Gson();

        if(clazz.equals(AccountType.class)){
            return gson.fromJson(jsonObject, AccountType.class);
        } else if(clazz.equals(OrgType.class)){
            return gson.fromJson(jsonObject, OrgType.class);
        } else if(clazz.equals(ResourceType.class)){
            return gson.fromJson(jsonObject, ResourceType.class);
        } else if(clazz.equals(RoleType.class)){
            return gson.fromJson(jsonObject, RoleType.class);
        } else if(clazz.equals(UserType.class)){
            return gson.fromJson(jsonObject, UserType.class);
        } else if(clazz.equals(SystemConfigurationType.class)){
            return gson.fromJson(jsonObject, SystemConfigurationType.class);
        } else if(clazz.equals(FederationMemberType.class)){
            return gson.fromJson(jsonObject, FederationMemberType.class);
        }

        return gson.fromJson(jsonObject, ObjectType.class);
    }

    public static boolean isOrgAttributeSingleValue(String attributeName){
        return attributeName.equals("name") || attributeName.equals("displayName") || attributeName.equals("locality");
    }

    public static String getSingleValueSharingRuleDescription(FederationSharingRuleType.SingleValueTolerance tolerance){
        if(FederationSharingRuleType.SingleValueTolerance.ENFORCE.equals(tolerance)){
            return "The value of this attribute is enforced by origin org. unit and can't be modified in any way";
        } else if(FederationSharingRuleType.SingleValueTolerance.ALLOW_OWN.equals(tolerance)){
            return "You are able to specify own value, but this will not be distributed in federation.";
        } else if(FederationSharingRuleType.SingleValueTolerance.ALLOW_CHANGE.equals(tolerance)){
            return "You can modify the value and it will be distributed in federation";
        }

        return null;
    }

    public static String getMultiValueSharingRuleDescription(FederationSharingRuleType.MultiValueTolerance tolerance){
        if(FederationSharingRuleType.MultiValueTolerance.ENFORCE.equals(tolerance)){
            return "Values enforced by origin org. unit. Can't modify in any way";
        } else if(FederationSharingRuleType.MultiValueTolerance.ALLOW_ADD_OWN.equals(tolerance)){
            return "You are able to add own values, but they are not distributed";
        } else if(FederationSharingRuleType.MultiValueTolerance.ALLOW_CHANGE_OWN.equals(tolerance)){
            return "You are able to add own values and modify existing, but only locally";
        } else if(FederationSharingRuleType.MultiValueTolerance.ALLOW_ADD.equals(tolerance)){
            return "You are able to add and change own values, added values will be distributed.";
        } else if(FederationSharingRuleType.MultiValueTolerance.ALLOW_CHANGE.equals(tolerance)){
            return "You are able to perform any modifications and they will be distributed";
        }

        return null;
    }
}
