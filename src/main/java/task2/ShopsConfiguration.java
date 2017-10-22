package task2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vavr.jackson.datatype.VavrModule;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.KEBAB_CASE;

@Configuration
public class ShopsConfiguration {

  @Bean
  ObjectMapper createObjectMapper() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(KEBAB_CASE)
        .registerModule(new VavrModule());
  }

  @Bean
  MongoCollection<Document> createShopsCollections() {
    MongoClient mongoClient = new MongoClient("localhost", 27017);
    MongoDatabase mongoDatabase = mongoClient.getDatabase("shops");
    return mongoDatabase.getCollection("shops");
  }

  @Bean
  ShopDao createShopDao(MongoCollection<Document> shopCollection, ObjectMapper objectMapper) {
    return new ShopDao(shopCollection, objectMapper);
  }

}
