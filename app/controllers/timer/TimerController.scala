package controllers.timer

import java.util.UUID
//import scala.util.Random

import java.math.BigInteger
import java.security.MessageDigest

import javax.inject.{Inject, Singleton}
import models.formapp._
import play.api.mvc.{AbstractController, ControllerComponents, Cookie, Result}

/**
  * 前半課題 formapp の Play Framework 上での実装
  */
@Singleton
class TimerController @Inject()(enquetes: Enquetes)(accounts: Accounts)(tasks: Tasks)(cc: ControllerComponents)
    extends AbstractController(cc) {

  /**
    *
    * @return
    */
  def home = Action { request =>
    Ok(views.html.timer.index("時計"))
  }

}
