package task2

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.vavr.collection.List
import io.vavr.jackson.datatype.VavrModule
import org.bson.Document
import spock.lang.Specification
import spock.lang.Subject

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.KEBAB_CASE
import static io.vavr.API.List

class ShopDaoSpec extends Specification {

  public static final String PRODUCT_ID = "PRODUCT_ID"

  MongoClient mongoClient = new MongoClient("localhost", 27017)
  MongoDatabase mongoDatabase = mongoClient.getDatabase("shops")
  MongoCollection<Document> shopCollection = mongoDatabase.getCollection("shops")

  ObjectMapper objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .setPropertyNamingStrategy(KEBAB_CASE)
      .registerModule(new VavrModule())

  @Subject
  ShopDao shopDao = new ShopDao(shopCollection, objectMapper)

  def setup() {
    shopCollection.deleteMany(new Document())
  }

  def "should insert and read shops"() {
    given:
    def shops = List(
        new Shop("shop1", List(PRODUCT_ID, "b")),
        new Shop("shop2", List("c")),
        new Shop("shop3", List("c", PRODUCT_ID))
    )
    and:
    def expectedShops = List(
        new Shop("shop1", List(PRODUCT_ID, "b")),
        new Shop("shop3", List("c", PRODUCT_ID))
    )

    when:
    shops.forEach(shopDao.&insert)
    def actualShops = shopDao.readShopsWithProduct(PRODUCT_ID)

    then:
    actualShops.toSet() == expectedShops.toSet()
  }

  def "should insert shops"() {
    given:
    def shops = List(
        new Shop("shop1", List("a", "b")),
        new Shop("shop2", List("c")),
        new Shop("shop3", List("c", "a"))
    )

    when:
    shops.forEach(shopDao.&insert)
    def actualShops = readShops()

    then:
    actualShops.toSet() == shops.toSet()
  }

  def "should read shops with products"() {
    given:
    def shops = List(
        new Shop("shop1", List(PRODUCT_ID, "b")),
        new Shop("shop2", List("c")),
        new Shop("shop3", List("c", PRODUCT_ID))
    )
    and:
    def expectedShops = List(
        new Shop("shop1", List(PRODUCT_ID, "b")),
        new Shop("shop3", List("c", PRODUCT_ID))
    )

    when:
    shops.forEach(this.&insertShop)
    def actualShops = shopDao.readShopsWithProduct(PRODUCT_ID)

    then:
    actualShops.toSet() == expectedShops.toSet()
  }

  void insertShop(Shop shop) {
    def json = objectMapper.writeValueAsString(shop)
    def bson = Document.parse(json)
    shopCollection.insertOne(bson)
  }

  List<Shop> readShops() {
    return List.ofAll(shopCollection.find())
        .map({ it.toJson() })
        .map({ json -> objectMapper.readValue(json, Shop.class) })
  }
}
