package com.esuta.fidm.test;

import com.esuta.fidm.model.ObjectChangeProcessor;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.*;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 *  @author shood
 * */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ObjectChangeProcessorTest {

    private static final Logger LOGGER = Logger.getLogger(ObjectChangeProcessorTest.class);

    private static ObjectChangeProcessor changeProcessor;

    public static void printTestName(String testName){
        LOGGER.info("Starting test: " + testName);
    }

    @BeforeClass
    public static void init(){
        LOGGER.info("Starting 'ObjectChangeProcessor' test suite.");
        changeProcessor = ObjectChangeProcessor.getInstance();
    }

    @AfterClass
    public static void end(){
        changeProcessor = null;
        LOGGER.info("Finishing 'ObjectChangeProcessor' test suite.");
    }

    @Test
    public void test_01_noModificationsTest(){
        printTestName("test_01_noModificationsTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("Old Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be NO modifications after change processing in this test.",
                true, modificationObject.getModificationList().isEmpty());
    }

    @Test
    public void test_02_singleValueAddTest(){
        printTestName("test_02_singleValueAddTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are adding 'displayName' attribute
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("Old Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType displayNameModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(displayNameModification);
        Assert.assertEquals("'displayName' attribute should be modified.", "displayName", displayNameModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'ADD'", ModificationType.ADD, displayNameModification.getModificationType());
        Assert.assertEquals("Old value should be 'null'", null,
                JsonUtil.jsonToObject(displayNameModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'Old Organization Unit'", "Old Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getNewValue(), String.class));
    }

    @Test
    public void test_03_singleValueModifyTest(){
        printTestName("test_03_singleValueModifyTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are modifying 'displayName' attribute
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("New Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType displayNameModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(displayNameModification);
        Assert.assertEquals("'displayName' attribute should be modified.", "displayName", displayNameModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, displayNameModification.getModificationType());
        Assert.assertEquals("Old value should be 'Old Organization Unit'", "Old Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Organization Unit'", "New Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getNewValue(), String.class));
    }

    @Test
    public void test_04_singleValueDeleteTest(){
        printTestName("test_04_singleValueDeleteTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are deleting 'displayName' attribute
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName(null);
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType displayNameModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(displayNameModification);
        Assert.assertEquals("'displayName' attribute should be modified.", "displayName", displayNameModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.DELETE, displayNameModification.getModificationType());
        Assert.assertEquals("Old value should be 'Old Organization Unit'", "Old Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'null'", null,
                JsonUtil.jsonToObject(displayNameModification.getNewValue(), String.class));
    }

    @Test
    public void test_05_multiValueAddTest(){
        printTestName("test_05_multiValueAddTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are adding a value to 'orgType' list
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("Old Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));
        newOrg.getOrgType().add("Type Four");

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType orgTypeModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(orgTypeModification);
        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'ADD'", ModificationType.ADD, orgTypeModification.getModificationType());
        Assert.assertEquals("Old value should be 'null'", null,
                JsonUtil.jsonToObject(orgTypeModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'Type Four'", "Type Four",
                JsonUtil.jsonToObject(orgTypeModification.getNewValue(), String.class));
    }

    @Test
    public void test_06_multiValueModifyTest(){
        printTestName("test_06_multiValueModifyTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are modifying a value in 'orgType' list
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("Old Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","New Type Two","Type Three")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType orgTypeModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(orgTypeModification);
        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, orgTypeModification.getModificationType());
        Assert.assertEquals("Old value should be 'Type Two'", "Type Two",
                JsonUtil.jsonToObject(orgTypeModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Type Two'", "New Type Two",
                JsonUtil.jsonToObject(orgTypeModification.getNewValue(), String.class));
    }

    @Test
    public void test_07_multiValueDeleteTest(){
        printTestName("test_07_multiValueDeleteTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Old org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two","Type Three")));

        //Init new org. unit - we are removing a value in 'orgType' list
        OrgType newOrg = new OrgType();
        newOrg.setName("Old Org");
        newOrg.setDisplayName("Old Organization Unit");
        newOrg.setDescription("Old org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY one change", 1, modificationObject.getModificationList().size());

        AttributeModificationType orgTypeModification = modificationObject.getModificationList().get(0);

        Assert.assertNotNull(orgTypeModification);
        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'DELETE'", ModificationType.DELETE, orgTypeModification.getModificationType());
        Assert.assertEquals("Old value should be 'Type Three'", "Type Three",
                JsonUtil.jsonToObject(orgTypeModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'null'", null,
                JsonUtil.jsonToObject(orgTypeModification.getNewValue(), String.class));
    }

    @Test
    public void test_08_complexModificationTest(){
        printTestName("test_08_complexModificationTest");

        //Init old org. unit
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Old Org");
        oldOrg.setDisplayName("Old Organization Unit");
        oldOrg.setDescription("Org. unit description");
        oldOrg.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two")));

        //Init new org. unit - we are removing a value in 'orgType' list
        OrgType newOrg = new OrgType();
        newOrg.setName("New Org");
        newOrg.setDisplayName("New Organization Unit");
        newOrg.setDescription("Org. unit description");
        newOrg.setOrgType(new ArrayList<>(Arrays.asList("New Type One","New Type Two", "Type Three", "Type Four")));

        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be EXACTLY seven change", 6, modificationObject.getModificationList().size());

        AttributeModificationType nameModification = modificationObject.getModificationList().get(0);
        AttributeModificationType displayNameModification = modificationObject.getModificationList().get(1);
        AttributeModificationType orgTypeModificationOne = modificationObject.getModificationList().get(2);
        AttributeModificationType orgTypeModificationTwo = modificationObject.getModificationList().get(3);
        AttributeModificationType orgTypeModificationThree = modificationObject.getModificationList().get(4);
        AttributeModificationType orgTypeModificationFour = modificationObject.getModificationList().get(5);

        Assert.assertNotNull(nameModification);
        Assert.assertNotNull(displayNameModification);
        Assert.assertNotNull(orgTypeModificationOne);
        Assert.assertNotNull(orgTypeModificationTwo);
        Assert.assertNotNull(orgTypeModificationThree);
        Assert.assertNotNull(orgTypeModificationFour);

        //Check name modification
        Assert.assertEquals("'name' attribute should be modified.", "name", nameModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, nameModification.getModificationType());
        Assert.assertEquals("Old value should be 'Old Org'", "Old Org",
                JsonUtil.jsonToObject(nameModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Org'", "New Org",
                JsonUtil.jsonToObject(nameModification.getNewValue(), String.class));

        //Check displayName modification
        Assert.assertEquals("'displayName' attribute should be modified.", "displayName", displayNameModification.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, displayNameModification.getModificationType());
        Assert.assertEquals("Old value should be 'Old Organization Unit'", "Old Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Organization Unit'", "New Organization Unit",
                JsonUtil.jsonToObject(displayNameModification.getNewValue(), String.class));

        //Check changes in orgTypes list
        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModificationOne.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, orgTypeModificationOne.getModificationType());
        Assert.assertEquals("Old value should be 'Type One'", "Type One",
                JsonUtil.jsonToObject(orgTypeModificationOne.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Type One'", "New Type One",
                JsonUtil.jsonToObject(orgTypeModificationOne.getNewValue(), String.class));

        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModificationTwo.getAttribute());
        Assert.assertEquals("ChangeType should be 'MODIFY'", ModificationType.MODIFY, orgTypeModificationTwo.getModificationType());
        Assert.assertEquals("Old value should be 'Type Two'", "Type Two",
                JsonUtil.jsonToObject(orgTypeModificationTwo.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'New Type Two'", "New Type Two",
                JsonUtil.jsonToObject(orgTypeModificationTwo.getNewValue(), String.class));

        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModificationThree.getAttribute());
        Assert.assertEquals("ChangeType should be 'ADD'", ModificationType.ADD, orgTypeModificationThree.getModificationType());
        Assert.assertEquals("Old value should be 'null'", null,
                JsonUtil.jsonToObject(orgTypeModificationThree.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'Type Three'", "Type Three",
                JsonUtil.jsonToObject(orgTypeModificationThree.getNewValue(), String.class));

        Assert.assertEquals("'orgType' attribute should be modified.", "orgType", orgTypeModificationFour.getAttribute());
        Assert.assertEquals("ChangeType should be 'ADD'", ModificationType.ADD, orgTypeModificationFour.getModificationType());
        Assert.assertEquals("Old value should be 'null'", null,
                JsonUtil.jsonToObject(orgTypeModificationFour.getOldValue(), String.class));
        Assert.assertEquals("New value should be 'Type Four'", "Type Four",
                JsonUtil.jsonToObject(orgTypeModificationFour.getNewValue(), String.class));
    }

    @Test
     public void test_09_governorAdditionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_09_governorAdditionTest");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");
        ObjectReferenceType<UserType> governor = new ObjectReferenceType<>();
        governor.setSharedInFederation(false);
        governor.setUid("uid");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");
        governor.setFederationIdentifier(federationIdentifier);
        newOrg.getGovernors().add(governor);

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'ADD'.", ModificationType.ADD, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "governors", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertNotNull(governorRef.getFederationIdentifier());
        Assert.assertEquals("Governor reference should have uid with value 'uid'.", "uid", governorRef.getUid());
        Assert.assertEquals("Governor federation identifier should have member 'member'.",
                "member", governorRef.getFederationIdentifier().getFederationMemberId());
        Assert.assertEquals("Governor federation identifier should have unique value 'value'.",
                "value", governorRef.getFederationIdentifier().getUniqueAttributeValue());

        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("Old org object (oldOrg) and org object after modification application should equal.", oldOrg, newOrg);
    }

    @Test
    public void test_10_governorModificationTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_10_governorModificationTest");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");
        ObjectReferenceType<UserType> oldGovernor = new ObjectReferenceType<>();
        oldGovernor.setSharedInFederation(false);
        oldGovernor.setUid("uid");
        oldGovernor.setFederationIdentifier(federationIdentifier);
        oldOrg.getGovernors().add(oldGovernor);

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");
        ObjectReferenceType<UserType> newGovernor = new ObjectReferenceType<>();
        newGovernor.setSharedInFederation(false);
        newGovernor.setUid("new uid");
        newGovernor.setFederationIdentifier(federationIdentifier);
        newOrg.getGovernors().add(newGovernor);

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'MODIFY'.", ModificationType.MODIFY, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "governors", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertNotNull(governorRef.getFederationIdentifier());
        Assert.assertEquals("Governor reference should have uid with value 'new uid'.", "new uid", governorRef.getUid());
        Assert.assertEquals("Governor federation identifier should have member 'member'.",
                "member", governorRef.getFederationIdentifier().getFederationMemberId());
        Assert.assertEquals("Governor federation identifier should have unique value 'value'.",
                "value", governorRef.getFederationIdentifier().getUniqueAttributeValue());

        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("Old org object (oldOrg) and org object after modification application should equal.", oldOrg, newOrg);
    }

    @Test
    public void test_11_governorDeletionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_11_governorDeletionTest");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");
        ObjectReferenceType<UserType> oldGovernor = new ObjectReferenceType<>();
        oldGovernor.setSharedInFederation(false);
        oldGovernor.setUid("uid");
        oldGovernor.setFederationIdentifier(federationIdentifier);
        oldOrg.getGovernors().add(oldGovernor);

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'MODIFY'.", ModificationType.DELETE, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "governors", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertEquals("governorRef should be null", null, governorRef);
        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("New org object (newOrg) should have 0 governors.", 0, newOrg.getGovernors().size());
    }

    @Test
    public void test_12_inducementAdditionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_12_inducementAdditionTest");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");
        InducementType<RoleType> roleInducement = new InducementType<>();
        roleInducement.setSharedInFederation(false);
        roleInducement.setUid("uid");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");
        roleInducement.setFederationIdentifier(federationIdentifier);
        newOrg.getRoleInducements().add(roleInducement);

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'ADD'.", ModificationType.ADD, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "roleInducements", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertNotNull(governorRef.getFederationIdentifier());
        Assert.assertEquals("Governor reference should have uid with value 'uid'.", "uid", governorRef.getUid());
        Assert.assertEquals("Governor federation identifier should have member 'member'.",
                "member", governorRef.getFederationIdentifier().getFederationMemberId());
        Assert.assertEquals("Governor federation identifier should have unique value 'value'.",
                "value", governorRef.getFederationIdentifier().getUniqueAttributeValue());

        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("Old org object (oldOrg) and org object after modification application should equal.", oldOrg, newOrg);
    }

    @Test
    public void test_13_inducementModificationTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_13_inducementModificationTest");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");
        InducementType<RoleType> oldRroleInducement = new InducementType<>();
        oldRroleInducement.setSharedInFederation(false);
        oldRroleInducement.setUid("uid");
        oldRroleInducement.setFederationIdentifier(federationIdentifier);
        oldOrg.getRoleInducements().add(oldRroleInducement);

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");
        InducementType<RoleType> newRoleInducement = new InducementType<>();
        newRoleInducement.setSharedInFederation(false);
        newRoleInducement.setUid("new uid");
        newRoleInducement.setFederationIdentifier(federationIdentifier);
        newOrg.getRoleInducements().add(newRoleInducement);

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'MODIFY'.", ModificationType.MODIFY, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "roleInducements", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertNotNull(governorRef.getFederationIdentifier());
        Assert.assertEquals("Governor reference should have uid with value 'new uid'.", "new uid", governorRef.getUid());
        Assert.assertEquals("Governor federation identifier should have member 'member'.",
                "member", governorRef.getFederationIdentifier().getFederationMemberId());
        Assert.assertEquals("Governor federation identifier should have unique value 'value'.",
                "value", governorRef.getFederationIdentifier().getUniqueAttributeValue());

        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("Old org object (oldOrg) and org object after modification application should equal.", oldOrg, newOrg);
    }

    @Test
    public void test_14_inducementDeletionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_14_inducementDeletionTest");

        FederationIdentifierType federationIdentifier = new FederationIdentifierType();
        federationIdentifier.setFederationMemberId("member");
        federationIdentifier.setUniqueAttributeValue("value");

        //Initialize org. units
        OrgType oldOrg = new OrgType();
        oldOrg.setName("Org");
        InducementType<RoleType> roleInducement = new InducementType<>();
        roleInducement.setSharedInFederation(false);
        roleInducement.setUid("uid");
        roleInducement.setFederationIdentifier(federationIdentifier);
        oldOrg.getRoleInducements().add(roleInducement);

        OrgType newOrg = new OrgType();
        newOrg.setName("Org");

        //Try to get change object
        ObjectModificationType modificationObject = changeProcessor.getOrgModifications(oldOrg, newOrg);

        //Check created modification objects
        Assert.assertNotNull(modificationObject);
        Assert.assertEquals("There should be exactly 1 modification.", 1, modificationObject.getModificationList().size());

        AttributeModificationType modification = modificationObject.getModificationList().get(0);
        Assert.assertEquals("Modification should be 'MODIFY'.", ModificationType.DELETE, modification.getModificationType());
        Assert.assertEquals("'governors' attribute should be modified.", "roleInducements", modification.getAttribute());

        ObjectReferenceType governorRef = (ObjectReferenceType) JsonUtil.jsonToObject(modification.getNewValue(), ObjectReferenceType.class);
        Assert.assertEquals("governorRef should be null", null, governorRef);
        //Try to apply the changes on the old org.
        changeProcessor.applyModificationsOnOrg(oldOrg, modificationObject);

        //Old org should be equal to new org after modification application.
        Assert.assertEquals("New org object (newOrg) should have 0 governors.", 0, newOrg.getRoleInducements().size());
    }

    @Test
    public void test_15_localChangesSingleValueDefaultTest(){
        printTestName("test_11_localChangesSingleValueDefaultTest");

        //Prepare policies and change objects
        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ENFORCE);

        FederationSharingPolicyType allowOwnPolicy = new FederationSharingPolicyType();
        allowOwnPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareLocalChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowOwnPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareLocalChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowOwnModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_OWN' default SV policy is set, the modification should NOT be filtered.",
                1, allowOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                0, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_16_localChangesSingleValueSpecificRuleTest(){
        printTestName("test_12_localChangesSingleValueSpecificRuleTest");

        //Prepare policies and change objects
        FederationSharingRuleType enforceRule = new FederationSharingRuleType();
        enforceRule.setAttributeName("displayName");
        enforceRule.setSingleValueTolerance(SingleValueTolerance.ENFORCE);

        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ENFORCE);
        enforcePolicy.getRules().add(enforceRule);

        FederationSharingRuleType allowOwnRule = new FederationSharingRuleType();
        allowOwnRule.setAttributeName("displayName");
        allowOwnRule.setSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);

        FederationSharingPolicyType allowOwnPolicy = new FederationSharingPolicyType();
        allowOwnPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);
        allowOwnPolicy.getRules().add(allowOwnRule);

        FederationSharingRuleType allowAllRule = new FederationSharingRuleType();
        allowAllRule.setAttributeName("displayName");
        allowAllRule.setSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);
        allowAllPolicy.getRules().add(allowAllRule);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareLocalChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowOwnPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareLocalChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowOwnModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_OWN' default SV policy is set, the modification should NOT be filtered.",
                1, allowOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                0, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_17_localChangesMultiValueDefaultTest(){
        printTestName("test_13_localChangesMultiValueDefaultTest");

        //Prepare policies and change objects
        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ENFORCE);

        FederationSharingPolicyType allowAddOwnPolicy = new FederationSharingPolicyType();
        allowAddOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);

        FederationSharingPolicyType allowChangeOwnPolicy = new FederationSharingPolicyType();
        allowChangeOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);

        FederationSharingPolicyType allowAddPolicy = new FederationSharingPolicyType();
        allowAddPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareLocalChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowAddOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowAddOwnPolicy);
        ObjectModificationType allowChangeOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowChangeOwnPolicy);
        ObjectModificationType allowAddModification = changeProcessor.prepareLocalChanges(modificationObject, allowAddPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareLocalChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowAddOwnModification);
        Assert.assertNotNull(allowChangeOwnModification);
        Assert.assertNotNull(allowAddModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD_OWN' default SV policy is set and change type is NOT addition, the modification should be filtered.",
                0, allowAddOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE_OWN' default SV policy is set, the modification should NOT be filtered.",
                1, allowChangeOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD' default SV policy is set, the modification should be filtered.",
                0, allowAddModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should be filtered.",
                0, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_18_localChangesMultiValueSpecificRuleTest(){
        printTestName("test_14_localChangesMultiValueSpecificRuleTest");

        //Prepare policies and change objects
        FederationSharingRuleType enforceRule = new FederationSharingRuleType();
        enforceRule.setAttributeName("orgType");
        enforceRule.setMultiValueTolerance(MultiValueTolerance.ENFORCE);

        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ENFORCE);
        enforcePolicy.getRules().add(enforceRule);

        FederationSharingRuleType allowAddOwnRule = new FederationSharingRuleType();
        allowAddOwnRule.setAttributeName("orgType");
        allowAddOwnRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);

        FederationSharingPolicyType allowAddOwnPolicy = new FederationSharingPolicyType();
        allowAddOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);
        allowAddOwnPolicy.getRules().add(allowAddOwnRule);

        FederationSharingRuleType allowChangeOwnRule = new FederationSharingRuleType();
        allowChangeOwnRule.setAttributeName("orgType");
        allowChangeOwnRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);

        FederationSharingPolicyType allowChangeOwnPolicy = new FederationSharingPolicyType();
        allowChangeOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);
        allowChangeOwnPolicy.getRules().add(allowChangeOwnRule);

        FederationSharingRuleType allowAddRule = new FederationSharingRuleType();
        allowAddRule.setAttributeName("orgType");
        allowAddRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);

        FederationSharingPolicyType allowAddPolicy = new FederationSharingPolicyType();
        allowAddPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);
        allowAddPolicy.getRules().add(allowAddRule);

        FederationSharingRuleType allowAllRule = new FederationSharingRuleType();
        allowAllRule.setAttributeName("orgType");
        allowAllRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);
        allowAllPolicy.getRules().add(allowAllRule);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareLocalChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowAddOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowAddOwnPolicy);
        ObjectModificationType allowChangeOwnModification = changeProcessor.prepareLocalChanges(modificationObject, allowChangeOwnPolicy);
        ObjectModificationType allowAddModification = changeProcessor.prepareLocalChanges(modificationObject, allowAddPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareLocalChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowAddOwnModification);
        Assert.assertNotNull(allowChangeOwnModification);
        Assert.assertNotNull(allowAddModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD_OWN' default SV policy is set and change type is NOT addition, the modification should be filtered.",
                0, allowAddOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE_OWN' default SV policy is set, the modification should NOT be filtered.",
                1, allowChangeOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD' default SV policy is set, the modification should be filtered.",
                0, allowAddModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should be filtered.",
                0, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_19_distributedChangesSingleValueDefaultTest(){
        printTestName("test_15_distributedChangesSingleValueDefaultTest");

        //Prepare policies and change objects
        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ENFORCE);

        FederationSharingPolicyType allowOwnPolicy = new FederationSharingPolicyType();
        allowOwnPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareDistributedChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowOwnPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowOwnModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_OWN' default SV policy is set, the modification should be filtered.",
                0, allowOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                1, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_20_distributedChangesSingleValueSpecificRuleTest(){
        printTestName("test_16_distributedChangesSingleValueSpecificRuleTest");

        //Prepare policies and change objects
        FederationSharingRuleType enforceRule = new FederationSharingRuleType();
        enforceRule.setAttributeName("displayName");
        enforceRule.setSingleValueTolerance(SingleValueTolerance.ENFORCE);

        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ENFORCE);
        enforcePolicy.getRules().add(enforceRule);

        FederationSharingRuleType allowOwnRule = new FederationSharingRuleType();
        allowOwnRule.setAttributeName("displayName");
        allowOwnRule.setSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);

        FederationSharingPolicyType allowOwnPolicy = new FederationSharingPolicyType();
        allowOwnPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_OWN);
        allowOwnPolicy.getRules().add(allowOwnRule);

        FederationSharingRuleType allowAllRule = new FederationSharingRuleType();
        allowAllRule.setAttributeName("displayName");
        allowAllRule.setSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultSingleValueTolerance(SingleValueTolerance.ALLOW_CHANGE);
        allowAllPolicy.getRules().add(allowAllRule);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareDistributedChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowOwnPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowOwnModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_OWN' default SV policy is set, the modification should be filtered.",
                0, allowOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                1, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_21_distributedChangesMultiValueDefaultTest(){
        printTestName("test_17_distributedChangesMultiValueDefaultTest");

        //Prepare policies and change objects
        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ENFORCE);

        FederationSharingPolicyType allowAddOwnPolicy = new FederationSharingPolicyType();
        allowAddOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);

        FederationSharingPolicyType allowChangeOwnPolicy = new FederationSharingPolicyType();
        allowChangeOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);

        FederationSharingPolicyType allowAddPolicy = new FederationSharingPolicyType();
        allowAddPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareDistributedChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowAddOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAddOwnPolicy);
        ObjectModificationType allowChangeOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowChangeOwnPolicy);
        ObjectModificationType allowAddModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAddPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowAddOwnModification);
        Assert.assertNotNull(allowChangeOwnModification);
        Assert.assertNotNull(allowAddModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD_OWN' default SV policy is set and change type is addition, the modification should be filtered.",
                0, allowAddOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE_OWN' default SV policy is set, the modification should be filtered.",
                0, allowChangeOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD' default SV policy is set, the modification should be filtered.",
                0, allowAddModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                1, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_22_distributedChangesMultiValueSpecificRuleTest(){
        printTestName("test_14_localChangesMultiValueSpecificRuleTest");

        //Prepare policies and change objects
        FederationSharingRuleType enforceRule = new FederationSharingRuleType();
        enforceRule.setAttributeName("orgType");
        enforceRule.setMultiValueTolerance(MultiValueTolerance.ENFORCE);

        FederationSharingPolicyType enforcePolicy = new FederationSharingPolicyType();
        enforcePolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ENFORCE);
        enforcePolicy.getRules().add(enforceRule);

        FederationSharingRuleType allowAddOwnRule = new FederationSharingRuleType();
        allowAddOwnRule.setAttributeName("orgType");
        allowAddOwnRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);

        FederationSharingPolicyType allowAddOwnPolicy = new FederationSharingPolicyType();
        allowAddOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD_OWN);
        allowAddOwnPolicy.getRules().add(allowAddOwnRule);

        FederationSharingRuleType allowChangeOwnRule = new FederationSharingRuleType();
        allowChangeOwnRule.setAttributeName("orgType");
        allowChangeOwnRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);

        FederationSharingPolicyType allowChangeOwnPolicy = new FederationSharingPolicyType();
        allowChangeOwnPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE_OWN);
        allowChangeOwnPolicy.getRules().add(allowChangeOwnRule);

        FederationSharingRuleType allowAddRule = new FederationSharingRuleType();
        allowAddRule.setAttributeName("orgType");
        allowAddRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);

        FederationSharingPolicyType allowAddPolicy = new FederationSharingPolicyType();
        allowAddPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_ADD);
        allowAddPolicy.getRules().add(allowAddRule);

        FederationSharingRuleType allowAllRule = new FederationSharingRuleType();
        allowAllRule.setAttributeName("orgType");
        allowAllRule.setMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);

        FederationSharingPolicyType allowAllPolicy = new FederationSharingPolicyType();
        allowAllPolicy.setDefaultMultiValueTolerance(MultiValueTolerance.ALLOW_CHANGE);
        allowAllPolicy.getRules().add(allowAllRule);

        AttributeModificationType modification = new AttributeModificationType();
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.MODIFY);
        modification.setOldValue(JsonUtil.objectToJson("old"));
        modification.setNewValue(JsonUtil.objectToJson("new"));

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Perform filtering
        ObjectModificationType enforceModification = changeProcessor.prepareDistributedChanges(modificationObject, enforcePolicy);
        ObjectModificationType allowAddOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAddOwnPolicy);
        ObjectModificationType allowChangeOwnModification = changeProcessor.prepareDistributedChanges(modificationObject, allowChangeOwnPolicy);
        ObjectModificationType allowAddModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAddPolicy);
        ObjectModificationType allowAllModification = changeProcessor.prepareDistributedChanges(modificationObject, allowAllPolicy);

        //Check the results
        Assert.assertNotNull(enforceModification);
        Assert.assertNotNull(allowAddOwnModification);
        Assert.assertNotNull(allowChangeOwnModification);
        Assert.assertNotNull(allowAddModification);
        Assert.assertNotNull(allowAllModification);

        Assert.assertEquals("Since 'ENFORCE' default SV policy is set, the modification should be filtered.",
                0, enforceModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD_OWN' default SV policy is set and change type is addition, the modification should be filtered.",
                0, allowAddOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE_OWN' default SV policy is set, the modification should be filtered.",
                0, allowChangeOwnModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_ADD' default SV policy is set, the modification should be filtered.",
                0, allowAddModification.getModificationList().size());
        Assert.assertEquals("Since 'ALLOW_CHANGE' default SV policy is set, the modification should NOT be filtered.",
                1, allowAllModification.getModificationList().size());
    }

    @Test
    public void test_23_applySingleValueAdditionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_19_applySingleValueAdditionTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(null);
        modification.setNewValue(JsonUtil.objectToJson("Org Display Name"));
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.ADD);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Display name should be set to 'Org Display Name'", "Org Display Name", newOrg.getDisplayName());
    }

    @Test
    public void test_24_applySingleValueModificationTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_20_applySingleValueModificationTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setDisplayName("Old Display Name");
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(JsonUtil.objectToJson("Old Display Name"));
        modification.setNewValue(JsonUtil.objectToJson("New Display Name"));
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.MODIFY);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Display name should be set to 'New Display Name'", "New Display Name", newOrg.getDisplayName());
    }

    @Test
    public void test_25_applySingleValueDeletionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_21_applySingleValueDeletionTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setDisplayName("Old Display Name");
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(JsonUtil.objectToJson("Old Display Name"));
        modification.setNewValue(JsonUtil.objectToJson(null));
        modification.setAttribute("displayName");
        modification.setModificationType(ModificationType.DELETE);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Display name should be set to 'null'", null, newOrg.getDisplayName());
    }

    @Test
    public void test_26_applyMultiValueAdditionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_22_applyMultiValueAdditionTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two")));
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(null);
        modification.setNewValue(JsonUtil.objectToJson("Type Three"));
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.ADD);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Org. unit should have 3 org types.", 3, newOrg.getOrgType().size());
        Assert.assertEquals("Org should have new value 'Type Three' as 3rd value of orgType", "Type Three", newOrg.getOrgType().get(2));
    }

    @Test
    public void test_27_applyMultiValueModificationTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_23_applyMultiValueModificationTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two", "Type Three")));
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(JsonUtil.objectToJson("Type Three"));
        modification.setNewValue(JsonUtil.objectToJson("New Type Three"));
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.MODIFY);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Org. unit should have 3 org types.", 3, newOrg.getOrgType().size());
        Assert.assertEquals("Org should have new value 'New Type Three' as 3rd value of orgType",
                "New Type Three", newOrg.getOrgType().get(2));
    }

    @Test
    public void test_28_applyMultiValueDeletionTest() throws NoSuchFieldException, IllegalAccessException {
        printTestName("test_24_applyMultiValueDeletionTest");

        //Prepare org. and modification objects
        OrgType org = new OrgType();
        org.setOrgType(new ArrayList<>(Arrays.asList("Type One","Type Two", "Type Three")));
        org.setName("org");

        AttributeModificationType modification = new AttributeModificationType();
        modification.setOldValue(JsonUtil.objectToJson("Type Three"));
        modification.setNewValue(null);
        modification.setAttribute("orgType");
        modification.setModificationType(ModificationType.DELETE);

        ObjectModificationType modificationObject = new ObjectModificationType();
        modificationObject.getModificationList().add(modification);

        //Apply prepared modifications
        OrgType newOrg = changeProcessor.applyModificationsOnOrg(org, modificationObject);

        //Check the results
        Assert.assertNotNull(newOrg);
        Assert.assertEquals("Org. unit should have 2 org types.", 2, newOrg.getOrgType().size());
        Assert.assertEquals("Org should have new value 'Type One' as 1st value of orgType",
                "Type One", newOrg.getOrgType().get(0));
        Assert.assertEquals("Org should have new value 'Type Two' as 2nd value of orgType",
                "Type Two", newOrg.getOrgType().get(1));
    }
}
