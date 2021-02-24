package com.wsj.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef.jdbcFeeder

import scala.concurrent.duration.DurationInt

/**
 *
 *
 * @author WangSongJun
 * @date 2021-01-22
 */
class UserCardOrderSimulation extends Simulation {

  def hasText(str: String): Boolean = {
    !(str.isEmpty || str.length == 0)
  }

  val datasource_url = "jdbc:mysql://192.168.1.202:3306/equity_user?useUnicode=true&characterEncoding=UTF-8"
  val datasource_username = "equity_user"
  val datasource_password = "AVw1rcAx9Yue!ywT"
  val selectUserSql = "SELECT phone as 'phoneNumber' from user_info ORDER BY RAND() LIMIT 10;"

  val userPhoneFromDB = jdbcFeeder(datasource_url, datasource_username, datasource_password, selectUserSql).circular

  val httpProtocol = http
    .baseUrl("http://frontend.pre.northlife.com.cn")
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/12.0 Mobile/15A372 Safari/604.1")

  val headers_anonymous = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Content-Type" -> "application/json;charset=utf-8",
    "Origin" -> "http://activity.pre.northlife.com.cn",
    "appChannel" -> "MzIwMDcwMDAwMDAwMA==",
    "appId" -> "",
    "mobileType" -> "h5",
    "token" -> "",
    "userId" -> ""
  )

  /**
   * 登陆
   *
   * @return
   */
  def login() = exec(http("发送验证码")
    .post("/noLogin/sendSms")
    .headers(headers_anonymous)
    .body(StringBody("""{"imageCode":"","imgType":"DEFAULT","userPhone":${phoneNumber},"yzmType":"REGISTER"}"""))
    .check(bodyString.saveAs("sendSmsResp"))
  ).exec {
    session =>
      println("发送验证码:")
      println(session.attributes.get("sendSmsResp").get)
      session
  }
    .pause(1)
    .exec(http("快速登陆")
      .post("/noLogin/fastLogin")
      .headers(headers_anonymous)
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


  /**
   * 查询卡列表
   *
   * @return
   */
  def cardListByPage() = exec(http("查询卡列表")
    .get("/card/page?page=1&size=20&isVip=true")
    .headers(headers_anonymous.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
    .check(status.is(200))
    .check(bodyString.saveAs("cardListByPageResp"))
    .check(jsonPath("$..code").is(expected = "1"))
    .check(jsonPath("$..data.rows").saveAs("cardList"))
//    .check(jsonPath("$..data.rows.flatMap(x=>x.id)").saveAs("cardIds"))
  ).exec {
    session =>
      println("查询卡列表:")
      println(session.attributes.get("cardListByPageResp").get)
      println("卡列表:")
      println(session.attributes.get("cardList").get)
      // fetch project from  session
      session("cardList").flatMap { project =>
        println(project)
      }
      session
  }


  def createCardOrder() = doIf("${cardIds.exists()}"){
    exec(http("创建卡订单")
      .post("/cardorder/create")
      .headers(headers_anonymous.+("token" -> "${accessToken}", "userId" -> "${userId}", "appId" -> "${appId}"))
      //    .body(StringBody(s"""{"cardId":${cardList.random.id}}"""))
      .formParam("cardId","${cardIds.random()}")

      .check(status.is(200))
      .check(bodyString.saveAs("cardOrderCreateResp"))
      .check(jsonPath("$..code").is(expected = "1"))
      //    .check(jsonPath("$..data.rows").saveAs("cardList"))
      //    .check(jsonPath("$..data.rows").ofType[Seq.type].saveAs("cardList"))
    )
      .exec {
        session =>
          println("创建卡订单:")
          println(session.attributes.get("cardOrderCreateResp").get)
          session
      }
  }




  val cardUsers = scenario("用户买卡")
    .feed(userPhoneFromDB)
    .exec(
      login(),
      cardListByPage(),
      createCardOrder()
    )

  setUp(
    cardUsers.inject(heavisideUsers(100) during (10 seconds))
  ).protocols(httpProtocol)


}
