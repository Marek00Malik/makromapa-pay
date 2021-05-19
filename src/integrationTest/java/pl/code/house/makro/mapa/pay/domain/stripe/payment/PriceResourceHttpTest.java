package pl.code.house.makro.mapa.pay.domain.stripe.payment;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.webAppContextSetup;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static pl.code.house.makro.mapa.pay.AuthenticationToken.getAuthenticationHeader;

import java.time.Clock;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.context.WebApplicationContext;
import pl.code.house.makro.mapa.pay.MockOAuth2Admin;

@MockOAuth2Admin
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class PriceResourceHttpTest {

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  void setUp() {
    webAppContextSetup(context, springSecurity());
  }

  @Test
  @DisplayName("should find all prices")
  void shouldFindAllPrices() {
    //given
    given()
        .header(getAuthenticationHeader())
        .contentType(JSON)

        .when()
        .get("/api/v1/stripe/price")

        .then()
        .log().ifValidationFails()
        .status(OK)

        .body("$", hasSize(3))
        .body("id", everyItem(notNullValue()))
        .body("product", everyItem(notNullValue()))
        .body("currency", everyItem(equalTo("pln")))
        .body("type", everyItem(equalTo("licensed")))
        .body("product", hasSize(3))
        .body("interval", hasItems(equalTo("week"), equalTo("month")))
        .body("unitAmount", hasSize(3))
        .body("unitAmount", hasItems(greaterThanOrEqualTo(200), lessThanOrEqualTo(2500)))
        .body("$.billingScheme", everyItem(equalTo("per_unit")))
    ;
  }

  @Test
  @DisplayName("should find all prices linked to product by productName Key")
  void shouldFindAllPricesLinkedToProductByProductNameKey() {
    //given
    given()
        .header(getAuthenticationHeader())
        .contentType(JSON)
        .queryParam("productName", "DISABLE_ADS")

        .when()
        .get("/api/v1/stripe/price")

        .then()
        .log().ifValidationFails()
        .status(OK)

        .body("$", hasSize(2))
        .body("id", everyItem(notNullValue()))
        .body("product", everyItem(notNullValue()))
        .body("currency", everyItem(equalTo("pln")))
        .body("type", everyItem(equalTo("licensed")))
        .body("product", hasSize(2))
        .body("interval", hasItems(equalTo("week"), equalTo("month")))
        .body("unitAmount", hasSize(2))
        .body("unitAmount", hasItems(greaterThanOrEqualTo(200), lessThanOrEqualTo(2500)))
        .body("$.billingSchema", everyItem(equalTo("per_unit")))
    ;
  }
}