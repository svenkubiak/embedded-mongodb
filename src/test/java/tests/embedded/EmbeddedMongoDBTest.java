package tests.embedded;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

/**
 * 
 * @author svenkubiak
 *
 */
public class EmbeddedMongoDBTest {
    
    @BeforeClass
    public static void startup() {
        EmbeddedMongo.DB.start();        
    }
	
	@Test
	public void testMongoDBThreaded() {
	    MongoClient mongoClient = EmbeddedMongo.DB.getMongoClient();
		assertNotNull(mongoClient);
		
		MongoDatabase db = mongoClient.getDatabase("embeddedTestDB"); 
		assertNotNull(db);
		
		MongoCollection<Document> collection = db.getCollection("testCollection");
		assertNotNull(collection);
		
		for (int i=0; i < 100; i++) {
			collection.insertOne(new Document("i", i));
		}
		
		assertEquals(100, collection.countDocuments());
	}
	
    @AfterClass
    public static void shutdown() {
        EmbeddedMongo.DB.stop();        
    }
}