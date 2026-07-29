package de.svenkubiak.embeddedmongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version.Main;
import org.bson.Document;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmbeddedMongoDBTests {

    private final List<EmbeddedMongoDB> managedInstances = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (int i = managedInstances.size() - 1; i >= 0; i--) {
            managedInstances.get(i).stop();
        }
        managedInstances.clear();
    }

    private EmbeddedMongoDB manage(EmbeddedMongoDB instance) {
        managedInstances.add(instance);
        return instance;
    }

    private EmbeddedMongoDB createStarted() {
        return manage(EmbeddedMongoDB.create().start());
    }

    @Test
    void testStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = createStarted();
        
        // then
        assertThat(embeddedMongoDB.isActive()).isTrue();

        // when
        embeddedMongoDB.stop();
        
        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }

    @Test
    void testExistingStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = createStarted();

        // then
        assertThat(embeddedMongoDB.isActive()).isTrue();

        // when
        embeddedMongoDB.start();

        // then
        assertThat(embeddedMongoDB.isActive()).isTrue();
    }
    
    @Test
    void testCreateStart() {
        // given
        EmbeddedMongoDB embeddedMongoDB = manage(EmbeddedMongoDB.createAndStart());
        
        // then
        assertThat(embeddedMongoDB.isActive()).isTrue();
        
        // when
        embeddedMongoDB.stop();
        
        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testStop() {
        // given
        EmbeddedMongoDB embeddedMongoDB = createStarted();
        
        // when
        embeddedMongoDB.stop();
        
        // then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> assertThat(embeddedMongoDB.isActive()).isFalse());
    }
    
    @Test
    void testDefaultValues() {
        // given
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create();
        
        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
        assertThat(embeddedMongoDB.getHost()).isEqualTo("localhost");
        assertThat(embeddedMongoDB.getPort()).isEqualTo(29019);
        assertThat(embeddedMongoDB.getVersion()).isEqualTo(Main.V7_0);
        embeddedMongoDB.stop();
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testCreateWithHostAndPort() {
        // given
        String host = "myhost";
        int port = 30131;
        
        //  when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create(host, port);
        
        // then
        assertThat(embeddedMongoDB.getHost()).isEqualTo(host);
        assertThat(embeddedMongoDB.getPort()).isEqualTo(port);

        // when
        embeddedMongoDB.stop();

        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testCreateWithPort() {
        // given
        int port = 30131;
        
        // when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withPort(port);
        
        // then
        assertThat(embeddedMongoDB.getPort()).isEqualTo(port);

        // when
        embeddedMongoDB.stop();

        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }

    @Test
    void testCreateWithPortLowerBound() {
        // given
        int port = 1023;

        // when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create();

        // then
        assertThatThrownBy(() -> embeddedMongoDB.withPort(port))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCreateWithPortUpperBound() {
        // given
        int port = 65536;

        // when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create();

        // then
        assertThatThrownBy(() -> embeddedMongoDB.withPort(port))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void testCreateWithHost() {
        // given
        String host = "myhost";
        
        // when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withHost(host);
        
        // then
        assertThat(embeddedMongoDB.getHost()).isEqualTo(host);

        // when
        embeddedMongoDB.stop();

        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testCreateWithIPv6Enabled() {
        //when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create();
        
        // then
        assertThat(embeddedMongoDB).isNotNull();
        assertThat(embeddedMongoDB.isIPv6()).isFalse();

        // when
        embeddedMongoDB = embeddedMongoDB.enableIPv6();
        assertThat(embeddedMongoDB).isNotNull();

        // then
        assertThat(embeddedMongoDB.isIPv6()).isTrue();

        // when
        embeddedMongoDB.stop();

        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testCreateWithVersion() {
        //given
        Main version = Main.V8_1;
        
        // when
        EmbeddedMongoDB embeddedMongoDB = EmbeddedMongoDB.create().withVersion(version);
        
        // then
        assertThat(embeddedMongoDB.getVersion()).isEqualTo(version);

        // when
        embeddedMongoDB.stop();

        // then
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
    
    @Test
    void testInvalidPort() {
        // given
        String expectedMessage = "Port needs to be between 1024 and 65535";
        int port = 555;
        
        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            EmbeddedMongoDB.create().withPort(port);
        });

        // then
        assertThat(exception.getMessage().contains(expectedMessage)).isTrue();
    }
    
	@Test
	void testMongoDB() {
	    // given
	    EmbeddedMongoDB embeddedMongoDB = createStarted();

        var codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        var pojoCodecProvider = PojoCodecProvider.builder()
                .conventions(List.of(ANNOTATION_CONVENTION))
                .automatic(true)
                .build();

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://" + embeddedMongoDB.getHost() + ":" + embeddedMongoDB.getPort()))
                .codecRegistry(fromRegistries(codecRegistry, fromProviders(pojoCodecProvider)))
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            // then
            assertThat(mongoClient).isNotNull();

            // given
            MongoDatabase db = mongoClient.getDatabase("embeddedTestDB");

            // then
            assertThat(db).isNotNull();

            // given
            MongoCollection<Document> collection = db.getCollection("testCollection");

            // then
            assertThat(collection).isNotNull();

            // when
            for (int i = 0; i < 100; i++) {
                collection.insertOne(new Document("i", i));
            }

            // then
            assertThat(collection.countDocuments()).isEqualTo(100);
        }

        embeddedMongoDB.stop();
        assertThat(embeddedMongoDB.isActive()).isFalse();
	}
	
	@Test
    void testInUse() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // given
        EmbeddedMongoDB embeddedMongoDB = createStarted();

        // when
        Method privateMethod = embeddedMongoDB.getClass().getDeclaredMethod("inUse", int.class);
        privateMethod.setAccessible(true); //NOSONAR
        boolean inUse = (boolean) privateMethod.invoke(embeddedMongoDB, 29019);
        
        // then
        assertThat(inUse).isTrue();
        embeddedMongoDB.stop();
        assertThat(embeddedMongoDB.isActive()).isFalse();
    }
}
