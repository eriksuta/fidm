package com.esuta.fidm.model.util;

import com.esuta.fidm.repository.schema.core.AssignmentType;
import com.esuta.fidm.repository.schema.core.InducementType;
import com.esuta.fidm.repository.schema.core.ObjectReferenceType;
import com.esuta.fidm.repository.schema.support.FederationIdentifierType;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 *  @author shood
 * */
public class JsonUtil {

    public static String objectToJson(Object object){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ObjectReferenceType.class, new ObjectReferenceAdapter())
                .registerTypeAdapter(AssignmentType.class, new AssignmentAdapter())
                .registerTypeAdapter(InducementType.class, new InducementAdapter())
                .create();
        return gson.toJson(object);
    }

    /**
     *  JSON adapter for ObjectReferenceType objects
     * */
    private static class ObjectReferenceAdapter implements JsonSerializer<ObjectReferenceType>, JsonDeserializer<ObjectReferenceType> {

        @Override
        public ObjectReferenceType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ObjectReferenceType ref = new ObjectReferenceType();

            JsonElement uid = object.get("uid");
            ref.setUid((String)context.deserialize(uid, String.class));

            JsonElement federationIdentifier = object.get("federationIdentifier");
            ref.setFederationIdentifier((FederationIdentifierType)context.deserialize(federationIdentifier, FederationIdentifierType.class));

            JsonElement sharedInFederation = object.get("sharedInFederation");
            ref.setSharedInFederation((Boolean)context.deserialize(sharedInFederation, Boolean.class));
            return ref;
        }

        @Override
        public JsonElement serialize(ObjectReferenceType reference, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("uid", context.serialize(reference.getUid(), String.class));
            result.add("federationIdentifier", context.serialize(reference.getFederationIdentifier(), FederationIdentifierType.class));
            result.add("sharedInFederation", context.serialize(reference.isSharedInFederation(), Boolean.class));
            return result;
        }
    }

    /**
     *  JSON adapter for AssignmentType objects
     * */
    private static class AssignmentAdapter implements JsonSerializer<AssignmentType>, JsonDeserializer<AssignmentType> {

        @Override
        public AssignmentType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            AssignmentType assignment = new AssignmentType();

            JsonElement uid = object.get("uid");
            assignment.setUid((String) context.deserialize(uid, String.class));

            JsonElement federationIdentifier = object.get("federationIdentifier");
            assignment.setFederationIdentifier((FederationIdentifierType) context.deserialize(federationIdentifier, FederationIdentifierType.class));

            JsonElement sharedInFederation = object.get("sharedInFederation");
            assignment.setSharedInFederation((Boolean) context.deserialize(sharedInFederation, Boolean.class));
            return assignment;
        }

        @Override
        public JsonElement serialize(AssignmentType reference, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("uid", context.serialize(reference.getUid(), String.class));
            result.add("federationIdentifier", context.serialize(reference.getFederationIdentifier(), FederationIdentifierType.class));
            result.add("sharedInFederation", context.serialize(reference.isSharedInFederation(), Boolean.class));
            return result;
        }
    }

    /**
     *  JSON adapter for InducementType objects
     * */
    private static class InducementAdapter implements JsonSerializer<InducementType>, JsonDeserializer<InducementType> {

        @Override
        public InducementType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            InducementType inducement = new InducementType();

            JsonElement uid = object.get("uid");
            inducement.setUid((String) context.deserialize(uid, String.class));

            JsonElement federationIdentifier = object.get("federationIdentifier");
            inducement.setFederationIdentifier((FederationIdentifierType) context.deserialize(federationIdentifier, FederationIdentifierType.class));

            JsonElement sharedInFederation = object.get("sharedInFederation");
            inducement.setSharedInFederation((Boolean) context.deserialize(sharedInFederation, Boolean.class));
            return inducement;
        }

        @Override
        public JsonElement serialize(InducementType reference, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("uid", context.serialize(reference.getUid(), String.class));
            result.add("federationIdentifier", context.serialize(reference.getFederationIdentifier(), FederationIdentifierType.class));
            result.add("sharedInFederation", context.serialize(reference.isSharedInFederation(), Boolean.class));
            return result;
        }
    }
}
