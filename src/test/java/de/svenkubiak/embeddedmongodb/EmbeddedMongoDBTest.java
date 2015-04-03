package de.svenkubiak.embeddedmongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.Document;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * @author svenkubiak
 *
 */
public class EmbeddedMongoDBTest {
	
	@Test
	public void testMongoDBThreaded() {
	    EmbeddedMongo.DB.start();
	    
	    MongoClient mongoClient = EmbeddedMongo.DB.getMongoClient();
		assertNotNull(mongoClient);
		
		MongoDatabase db = mongoClient.getDatabase("embeddedTestDB"); 
		assertNotNull(db);
		
		MongoCollection<Document> collection = db.getCollection("testCollection");
		assertNotNull(collection);
		
		for (int i=0; i < 100; i++) {
			collection.insertOne(new Document("i", i));
		}
		
		assertEquals(100, collection.count());
	}
}