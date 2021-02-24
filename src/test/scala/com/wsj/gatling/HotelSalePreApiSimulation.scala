package com.wsj.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/**
 * 遛娃卡活动2.0
 *
 * 活动买卡
 * 登陆
 * 提交订单
 * 查询订单
 */
class HotelSalePreApiSimulation extends Simulation {

  def hasText(str: String): Boolean = {
    !(str.isEmpty || str.length == 0)
  }

  val userPhoneFeeder = csv("com/yofish/test/load/userPhoneNumberList.csv").random

  val httpProtocol = http
    .baseUrl("http://frontend.pre.northlife.com.cn")
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/12.0 Mobile/15A372 Safari/604.1")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "If-Modified-Since" -> "Tue, 24 Nov 2020 07:36:34 GMT",
    "If-None-Match" -> """W/"5fbcb802-2b813"""")

  val headers_2 = Map(
    "If-Modified-Since" -> "Wed, 25 Nov 2020 02:59:26 GMT",
    "If-None-Match" -> """W/"5fbdc88e-9625d"""")

  val headers_3 = Map(
    "Access-Control-Request-Headers" -> "appchannel,appid,mobiletype,token,userid",
    "Access-Control-Request-Method" -> "GET",
    "Origin" -> "http://activity.pre.northlife.com.cn")

  val headers_4 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Origin" -> "http://activity.pre.northlife.com.cn",
    "appChannel" -> "MzIwMDcwMDAwMDAwMA==",
    "appId" -> "",
    "mobileType" -> "h5",
    "token" -> "",
    "userId" -> "")

  val headers_11 = Map("Accept" -> "image/webp,*/*")

  val headers_15 = Map(
    "Access-Control-Request-Headers" -> "appchannel,appid,content-type,mobiletype,token,userid",
    "Access-Control-Request-Method" -> "POST",
    "Origin" -> "http://activity.pre.northlife.com.cn")

  val headers_16 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json;charset=utf-8",
    "Origin" -> "http://activity.pre.northlife.com.cn",
    "appChannel" -> "MzIwMDcwMDAwMDAwMA==",
    "appId" -> "",
    "mobileType" -> "h5",
    "token" -> "",
    "userId" -> "")

  val headers_22 = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json;charset=utf-8",
    "Origin" -> "http://activity.pre.northlife.com.cn",
    "appChannel" -> "MzIwMDcwMDAwMDAwMA==",
    "mobileType" -> "h5"
  )

  val uri2 = "http://activity.pre.northlife.com.cn"

  /**
   * 打开页面
   *
   * @return
   */
  def openPage() = exec(http("打开页面_0")
    .get(uri2 + "/hotelsale")
    .headers(headers_0))
    .pause(24 milliseconds)
    .exec(http("umi.css_1")
      .get(uri2 + "/umi.css")
      .headers(headers_1))
    .exec(http("umi.js_2")
      .get(uri2 + "/umi.js")
      .headers(headers_2))
    .pause(57 milliseconds)
    .exec(http("查询活动_3")
      .options("/marketing/activities/serial-number/sixtymillion1")
      .headers(headers_3)
    )
    .exec(http("查询活动1_4")
      .get("/marketing/activities/serial-number/sixtymillion1")
      .headers(headers_4)
      .check(jsonPath("$..code").is(expected = "1"))
      .check(jsonPath("$..data.id").saveAs("activityId"))
      .check(jsonPath("$..data.activityTargetId").saveAs("activityCardId"))
    ).exec {
    session => {
      print("activityId:")
      println(session("activityId").as[String])
      print("activityCardId:")
      println(session("activityCardId").as[String])
    }
      session
  }
    .exec(http("酒店数据1_5")
      .options("/marketing/activities/home/sixtymillion1")
      .headers(headers_3))
    .exec(http("酒店数据2_6")
      .options("/marketing/activities/home/sixtymillion2")
      .headers(headers_3))
    .exec(http("酒店数据3_7")
      .options("/marketing/activities/home/sixtymillion3")
      .headers(headers_3))
    .exec(http("酒店数据1_8")
      .get("/marketing/activities/home/sixtymillion1")
      .headers(headers_4))
    .exec(http("酒店数据3_9")
      .get("/marketing/activities/home/sixtymillion3")
      .headers(headers_4))
    .exec(http("酒店数据2_10")
      .get("/marketing/activities/home/sixtymillion2")
      .headers(headers_4))
    .pause(14)

  /**
   * 立即购买
   *
   * @return
   */
  def buyNow() = doIf(session => hasText(session("activityId").as[String])) {

    exec(http("png_11")
      .get(uri2 + "/static/vipBg.1338bdbc.png")
      .headers(headers_11))
      .exec(http("卡详情_12")
        .options("/card/cardId/${activityCardId}")
        .headers(headers_3))
      .exec(http("static_13")
        .get(uri2 + "/static/bg2.a68733e0.gif")
        .headers(headers_11))
      .exec(http("卡详情_14")
        .get("/card/cardId/${activityCardId}")
        .headers(headers_4)
      )
      .pause(17)
  }

  /**
   * 购买
   *
   * @return
   */
  def buy() = doIf(session => hasText(session("activityId").as[String])) {
    exec(http("创建订单_15")
      .options("/cardorder/create")
      .headers(headers_15))
      .exec(http("创建订单_16")
        .post("/cardorder/create")
        .headers(headers_16)
        .body(StringBody("""{"activityId":$activityId","cardId":${activityCardId}}"""))
        .check(bodyString.saveAs("cardOrderResp"))
      ).exec {
      session => {
        print("创建订单:")
        println(session.attributes.get("cardOrderResp").get)
      }
        session
    }
      .pause(23)
  }

  /**
   * 登陆
   *
   * @return
   */
  def login() = exec(http("发送验证码_17")
    .options("/noLogin/sendSms")
    .headers(headers_15))
    .feed(userPhoneFeeder)
    .exec(http("发送验证码_18")
      .post("/noLogin/sendSms")
      .headers(headers_16)
      .body(StringBody("""{"imageCode":"","imgType":"DEFAULT","userPhone":${phoneNumber},"yzmType":"REGISTER"}"""))
      .check(bodyString.saveAs("sendSmsResp"))
    ).exec {
    session =>
      println("发送验证码:")
      println(session.attributes.get("sendSmsResp").get)
      session
  }
    .pause(7)
    .exec(http("快速登陆_19")
      .options("/noLogin/fastLogin")
      .headers(headers_15))
    .exec(http("快速登陆_20")
      .post("/noLogin/fastLogin")
      .headers(headers_16)
      .body(StringBody("""{"phone":${phoneNumber},"yzm":"123123","yzmType":"REGISTER","imageCode":""}"""))
      .check(status.is(200))
      .check(jsonPath("$..code").is(expected = "1"))
      .check(jsonPath("$..data.accessToken").saveAs("accessToken"))
      .check(jsonPath("$..data.userId").saveAs("userId"))
      .check(jsonPath("$..data.appId").saveAs("appId"))
      .check(bodyString.saveAs("fastLoginResp"))
    ).exec {
    session =>
      print("快速登陆:")
      println(session.attributes.get("fastLoginResp").get)
      session
  }
    .pause(10)

  /**
   * 继续购买
   *
   * @return
   */
  def keepBuying() = doIf(session => hasText(session("accessToken").as[String])) {

    exec(http("继续购买-创建订单_21")
      .options("/cardorder/create")
      .headers(headers_15))
      .exec(http("继续购买-创建订单_22")
        .post("/cardorder/create")
        .headers(headers_22.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
        .body(StringBody("""{"activityId":${activityId},"cardId":${activityCardId}}"""))
        .check(bodyString.saveAs("createOrderResp"))
        .check(jsonPath("$..code").is(expected = "1"))
        .check(jsonPath("$..data.orderNum").saveAs("orderNum"))
      ).exec {
      session =>
        print("继续购买-创建订单:")
        println(session.attributes.get("createOrderResp").get)
        session
    }.doIf(session => hasText(session("orderNum").as[String])) {
      exec(http("查询订单详情_23")
        .options("/cardorder/query/deatil")
        .headers(headers_15))
        .exec(http("查询订单详情_24")
          .post("/cardorder/query/deatil")
          .headers(headers_22.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
          .body(StringBody("""{"orderNum":"${orderNum}"}"""))
          .check(bodyString.saveAs("cardOrderQueryResp"))
        ).exec {
        session =>
          print("查询订单详情:")
          println(session.attributes.get("cardOrderQueryResp").get)
          session
      }
    }
      .pause(10)
  }

  /**
   * 支付
   *
   * @return
   */
  def pay() = doIf(session => hasText(session("orderNum").as[String])) {
    exec(http("查询订单详情_25")
      .options("/cardorder/query/deatil")
      .headers(headers_15))
      .exec(http("查询订单详情_26")
        .post("/cardorder/query/deatil")
        .headers(headers_22.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
        .body(StringBody("""{"orderNum":${orderNum}""")))
      .exec(http("支付_27")
        .options("/cardorder/pay")
        .headers(headers_15))
      .exec(http("支付_28")
        .post("/cardorder/pay")
        .headers(headers_22.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
        .body(StringBody("""{"orderNum":"${orderNum}","payWay":"WECHAT_MOBILE_WEB_PAY","returnUrl":"http%3A%2F%2Factivity.pre.northlife.com.cn%2Fcashier%3Fo%3D5120201125050000104%26pb%3D1%26%26returnuri%3D%252Fcdetail%252F3200700000000%253Fcid%253D2115532%2526aid%253D11"}""")))
  }


  /**
   * 闲逛用户：
   *
   * 打开页面
   * 立即购买
   */
  val browseUsers = scenario("浏览用户")
    .exec(openPage(), buyNow())

  /**
   * 买卡用户：
   *
   * 打开页面
   * 立即购买
   * 购买
   * 登陆
   * 继续购买
   * 支付
   */
  val cardUsers = scenario("买卡用户")
    .exec(openPage(), buyNow(), buy(), login(), keepBuying(), pay())

  //用户模拟  https://blog.csdn.net/like4501/article/details/90611326
  //报告解读  https://blog.csdn.net/qunyaoaiziji/article/details/105854603
  /**
   * //10分钟内，用户并发率从1到10
   * rampUsersPerSec(1) to 10 during(10 minutes)
   *
   * //10分钟内，用户并发率从1到10 用户将被随机间隔注入
   * rampUsersPerSec(1) to 10 during(10 minutes ) randomized
   *
   * //10秒钟请求30个用户，等待10秒，循环，直到满足3000个用户
   * splitUsers(3000) into( rampUsers(30) during(10 seconds)) separatedBy(10 seconds)
   *
   * //10秒注入30个用户，立即注入30个用户，这两个方案循环，直到满足300个用户
   * splitUsers(3000) into(rampUsers(30) during(10 seconds)) separatedBy atOnceUsers(30)
   *
   * //500个用户在30秒内的heaviside阶梯函数曲线
   * heavisideUsers( 500 ) during( 30 seconds)
   *
   * //1分钟内等速率运行300个用户
   * rampUsers(300) during( 1 minutes)
   *
   * //每秒10个用户的并发率运行3分钟
   * constantUsersPerSec(10) during(3 minutes)
   *
   *
   */
  setUp(
    browseUsers.inject(rampUsersPerSec(1) to 100 during(10 seconds ) randomized),
    cardUsers.inject(heavisideUsers(20) during (10 seconds))
  ).protocols(httpProtocol)

  //这个模拟将以10秒的上升速率达到10次/秒，然后保持1分钟的吞吐量，跳到
  //5次/秒，最后保持2分钟的吞吐量
  /*setUp(
    browseUsers.inject(
      constantUsersPerSec(10) during( 1 minutes)
    )
  ).throttle(
    //reachRps(target) in (duration)：在给定的持续时间内以一个斜坡为目标的吞吐量
    reachRps(10) in (10 seconds),
    //holdFor(duration)：在给定的持续时间内保持当前的吞吐量
    holdFor(1 minute),
    //jumpToRps(target)：立即跳转到给定的目标吞吐量
    jumpToRps(5),
    //    以当前吞吐量持续运行
    holdFor(2 minute)
  ).protocols( httpProtocol )*/
}