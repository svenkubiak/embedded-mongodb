package de.svenkubiak.embeddedmongodb;

import static org.junit.Assert.*;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import de.svenkubiak.embeddedmongodb.EmbeddedMongoDB;

public class EmbeddedMongoDBTest {
	@Before
	public void init() {
		EmbeddedMongoDB.getInstance();
	}
	
	@Test
	public void testMongoDB() {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(EmbeddedMongoDB.getHost(), EmbeddedMongoDB.getPort());
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
		EmbeddedMongoDB.shutdown();
	}
}