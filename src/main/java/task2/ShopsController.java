package task2;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ShopsController {

  private final ShopDao shopDao;

  @Autowired
  public ShopsController(ShopDao shopDao) {
    this.shopDao = shopDao;
  }

  @RequestMapping(value = "/shops/{productId}", method = GET)
  public List<Shop> executionPlans(@PathVariable String productId) {
    return shopDao.readShopsWithProduct(productId);
  }
}
