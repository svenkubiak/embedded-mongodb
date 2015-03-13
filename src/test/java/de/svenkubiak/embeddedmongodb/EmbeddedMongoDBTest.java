package de.svenkubiak.embeddedmongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

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
		
		DB db = mongoClient.getDB("embeddedTestDB");
		assertNotNull(db);
		
		DBCollection collection = db.getCollection("testCollection");
		assertNotNull(collection);
		
		for (int i=0; i < 100; i++) {
			collection.insert(new BasicDBObject("i", i));
		}
		
		assertEquals(100, collection.count());
	}
}