package de.svenkubiak.embeddedmongodb;

import static org.awaitility.Awaitility.await;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Version.Main;

class EmbeddedMongoDBTests {

    @Test
    void testStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();
        
        // then
        assertTrue(embeddedMongoDB.isActive());
        
        // when
        embeddedMongoDB.stop();
        
        // then
        assertFalse(embeddedMongoDB.isActive());
    }

    @Test
    void testExistingStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();

        // then
        assertTrue(embeddedMongoDB.isActive());

        // when
        embeddedMongoDB.start();

        // then
        assertTrue(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.createAndStart();
        
        // then
        assertTrue(embeddedMongoDB.isActive());
        
        // when
        embeddedMongoDB.stop();
        
        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testStop() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();
        
        // when
        embeddedMongoDB.stop();
        
        // then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(embeddedMongoDB.isActive(), equalTo(false)));
    }
    
    @Test
    void testDefaultValues() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create();
        
        // then
        assertFalse(embeddedMongoDB.isActive());
        assertEquals("localhost", embeddedMongoDB.getHost());
        assertEquals(29019, embeddedMongoDB.getPort());
        assertEquals(Version.Main.V7_0, embeddedMongoDB.getVersion());
        embeddedMongoDB.stop();
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateWithHostAndPort() {
        // given
        String host = "myhost";
        int port = 30131;
        
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create(host, port);
        
        // then
        assertEquals(host, embeddedMongoDB.getHost());
        assertEquals(port, embeddedMongoDB.getPort());

        // when
        embeddedMongoDB.stop();

        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateWithPort() {
        // given
        int port = 30131;
        
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withPort(port);
        
        // then
        assertEquals(port, embeddedMongoDB.getPort());

        // when
        embeddedMongoDB.stop();

        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateWithHost() {
        // given
        String host = "myhost";
        
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withHost(host);
        
        // then
        assertEquals(host, embeddedMongoDB.getHost());

        // when
        embeddedMongoDB.stop();

        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateWithIPv6Enabled() {
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().enableIPv6();
        
        // then
        assertEquals(Boolean.TRUE, embeddedMongoDB.isIPv6());

        // when
        embeddedMongoDB.stop();

        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testCreateWithVersion() {
        //given
        Main version = Main.V6_0;
        
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withVersion(version);
        
        // then
        assertEquals(version, embeddedMongoDB.getVersion());

        // when
        embeddedMongoDB.stop();

        // then
        assertFalse(embeddedMongoDB.isActive());
    }
    
    @Test
    void testInvalidPort() {
        //given
        String expectedMessage = "Port needs to be greater than 1024";
        int port = 555;
        
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            EmbeddedMongoDB.create().withPort(port);
        });

        assertTrue(exception.getMessage().contains(expectedMessage));
    }
    
	@Test
	void testMongoDB() {
	    // given
	    EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();

        var codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder()
                .conventions(List.of(ANNOTATION_CONVENTION))
                .automatic(true)
                .build();

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://" + embeddedMongoDB.getHost() + ":" + embeddedMongoDB.getPort()))
                .codecRegistry(fromRegistries(codecRegistry, fromProviders(pojoCodecProvider)))
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

	    // then
	    assertNotNull(mongoClient);
		
	    // given
		MongoDatabase db = mongoClient.getDatabase("embeddedTestDB"); 
		
		// then
		assertNotNull(db);
		
		// given
		MongoCollection<Document> collection = db.getCollection("testCollection");
		
		// then
		assertNotNull(collection);
		
		// when
		for (int i=0; i < 100; i++) {
			collection.insertOne(new Document("i", i));
		}
		
		// then
		assertEquals(100, collection.countDocuments());
        embeddedMongoDB.stop();
        assertFalse(embeddedMongoDB.isActive());
	}
	
	@Test
    void testInUse() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().start();

        // when
        Method privateMethod = embeddedMongoDB.getClass().getDeclaredMethod("inUse", int.class);
        privateMethod.setAccessible(true); //NOSONAR
        boolean inUse = (boolean) privateMethod.invoke(embeddedMongoDB, 29019);
        
        // then
        assertTrue(inUse);
        embeddedMongoDB.stop();
        assertFalse(embeddedMongoDB.isActive());
    }
}