package com.wsj.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class PaymentOrderSimulation extends Simulation {

  def hasText(str: String): Boolean = {
    !(str.isEmpty || str.length == 0)
  }

  val httpProtocol = http
    .baseUrl("http://10.244.6.160:8080/")
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/12.0 Mobile/15A372 Safari/604.1")


  val headers = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json;charset=utf-8",
    "Origin" -> "http://activity.pre.northlife.com.cn",
    "appChannel" -> "MzIwMDcwMDAwMDAwMA==",
    "mobileType" -> "h5"
  )

  def currentTime():Long = {
    System.currentTimeMillis()
  }


  /**
   * 支付
   *
   * @return
   */
  def pay() =
  repeat(1000,"n"){
    exec(http("prePay ${n}")
      .post("/payment/prePay")
      .headers(headers)
      .body(StringBody("{\"businCode\":\"0000\",\"goodsType\":\"0\",\"merchantCodeEnum\":\"EQUITY\",\"orderAmount\":0.1,\"orderId\":\"${n}\",\"paymentChannelEnum\":\"ALIPAY_APP_PAY\",\"paymentType\":\"NORMAL\",\"productId\":\"string\",\"returnUrl\":\"string\",\"subject\":\"我二舅\",\"timeoutExpress\":2}"))
      .check(jsonPath("$..code").is(expected = "1"))
      .check(bodyString.saveAs("prePayResponse"))
    ).exec {
      session =>
        println("prePayResponse:")
        println(session.attributes.get("prePayResponse").get)
        session
    }
  }

  /**
   * 闲逛用户：
   *
   * 打开页面
   * 立即购买
   */
  val browseUsers = scenario("浏览用户")
    .exec(pay())

  setUp(
    browseUsers.inject(rampUsers(1) during(1 minutes))
  ).protocols(httpProtocol)

}