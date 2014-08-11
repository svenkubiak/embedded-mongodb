package de.svenkubiak.embeddedmongo;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class EmbeddedMongoTest {
	@Before
	public void init() {
		EmbeddedMongo.getInstance();
	}
	
	@Test
	public void testMongoDB() {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(EmbeddedMongo.host, EmbeddedMongo.port);
		} catch (UnknownHostException e) {
			fail("Failed to created MongoClient");
		}
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
	
	@After
	public void shutdown() {
		EmbeddedMongo.shutdown();
	}
}