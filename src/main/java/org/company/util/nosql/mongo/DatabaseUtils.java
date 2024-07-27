package org.company.util.nosql.mongo;

import com.mongodb.client.*;
import org.bson.Document;
import org.company.util.FileUtils;
import org.company.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {

    private static MongoClient mongoClient;

    public static boolean isDatabaseUpAndRunning() {
        try {
            return mongoClient.getClusterDescription() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static MongoClient getDatabaseConnection() {
        var noSqlDbUrl = FileUtils.getPropertyValue("noSqlDbUrl");
        var noSqlDbUsername = FileUtils.getPropertyValue("noSqlDbUsername");
        var noSqlDbPassword = FileUtils.getPropertyValue("noSqlDbPassword");
        try {
            mongoClient = MongoClients.create(noSqlDbUrl);
            LogUtils.debug("NoSql Database connection established");
        } catch (Exception e) {
            LogUtils.error("Unable to establish nosql db connection", e);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase(String databaseName) {
        return mongoClient.getDatabase(databaseName);
    }

    public static MongoCollection<Document> getCollection( String databaseName, String collectionName) {
        return getDatabase(databaseName).getCollection(collectionName);
    }

    public static void createCollectionFromDatabase(String collectionName, String databaseName) {
        getDatabase(databaseName).createCollection(collectionName);
    }

    public static ListCollectionsIterable<Document> listAllCollectionsInDatabase(String databaseName) {
        return getDatabase(databaseName).listCollections();
    }

    public static List<String> listAllCollectionNamesInDatabase( String databaseName) {
        var collectionNames = new ArrayList<String>();
        var mongoCursor = getDatabase(databaseName).listCollectionNames().iterator();
        while(mongoCursor.hasNext()){
            collectionNames.add(mongoCursor.next());
        }
        return collectionNames;
    }

    public static List<Document> getDocumentsFromCollection(String databaseName, String collectionName, Map<String,Object> documentMap) {
        Document searchQuery = new Document();
        documentMap.entrySet().stream().forEach(es -> searchQuery.put(es.getKey(), es.getValue()));
        var mongoCursor = getCollection(databaseName, collectionName).find(searchQuery).cursor();
        var documents = new ArrayList<Document>();
        while(mongoCursor.hasNext()){
            documents.add(mongoCursor.next());
        }
        return documents;
    }

    public static String insertDocumentToCollection(String databaseName, String collectionName, Map<String,Object> documentMap) {
        Document document = new Document();
        documentMap.entrySet().stream().forEach(es -> document.put(es.getKey(), es.getValue()));
        var result = getCollection(databaseName, collectionName).insertOne(document);
        return result.getInsertedId().toString();
    }

    public static String getValueOfFieldInDocumentFromCollection(String field, Document document) {
        var value = document.get(field);
        if(value instanceof Document) {
            return ((Document)value).toJson();
        }
        return value.toString();
    }
}
