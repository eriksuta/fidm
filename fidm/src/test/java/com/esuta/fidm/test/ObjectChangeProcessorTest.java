package com.esuta.fidm.test;

import com.esuta.fidm.model.ObjectChangeProcessor;
import com.esuta.fidm.model.util.JsonUtil;
import com.esuta.fidm.repository.schema.core.ModificationType;
import com.esuta.fidm.repository.schema.core.OrgType;
import com.esuta.fidm.repository.schema.support.AttributeModificationType;
import com.esuta.fidm.repository.schema.support.ObjectModificationType;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *  TODO - add tests to test objectChangeProcessor work with ObjectReferences and Inducements
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
}
