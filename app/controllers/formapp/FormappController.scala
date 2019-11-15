package controllers.formapp

import java.util.UUID
//import scala.util.Random

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, Cookie, Result}
import models.formapp.{Account, Accounts, Enquete, Enquetes, Task, Tasks}

import java.security.MessageDigest
import java.math.BigInteger

/**
  * 前半課題 formapp の Play Framework 上での実装
  */
@Singleton
class FormappController @Inject()(enquetes: Enquetes)(accounts: Accounts)(tasks: Tasks)(cc: ControllerComponents)
    extends AbstractController(cc) {

  /**
    *
    * @return
    */
  def list = Action { request =>
    val entries = enquetes.list
    Ok(views.html.formapp.list(entries))
  }

  def entry(id: Int) = Action {
    enquetes.findByID(id) match {
      case Some(e) => Ok(views.html.formapp.entry(e))
      case None    => NotFound(s"No entry for id=${id}")
    }
  }

  def startRegistration = Action { request =>
    Ok(views.html.formapp.nameForm(request)).withNewSession
  }

  def registerName = Action { request =>
    (for {
      param <- request.body.asFormUrlEncoded
      name  <- param.get("name").flatMap(_.headOption)
    } yield {
      Ok(views.html.formapp.genderForm(request)).withSession(request.session + ("formapp::name" -> name))
    }).getOrElse[Result](Redirect("/formapp/register"))
  }

  def registerGender = Action { request =>
    (for {
      _      <- request.session.get("formapp::name")
      param  <- request.body.asFormUrlEncoded
      gender <- param.get("gender").flatMap(_.headOption)
    } yield {
      Ok(views.html.formapp.messageForm(request)).withSession(request.session + ("formapp::gender" -> gender))
    }).getOrElse[Result](Redirect("/formapp/register"))
  }

  def registerMessage = Action { request =>
    (for {
      name    <- request.session.get("formapp::name")
      gender  <- request.session.get("formapp::gender")
      param   <- request.body.asFormUrlEncoded
      message <- param.get("message").flatMap(_.headOption)
    } yield {
      val enquete = Enquete(name, gender, message)
      Ok(views.html.formapp.confirm(enquete)(request)).withSession(request.session + ("formapp::message" -> message))
    }).getOrElse[Result](Redirect("/formapp/register"))
  }

  def confirm = Action { request =>
    (for {
      name    <- request.session.get("formapp::name")
      gender  <- request.session.get("formapp::gender")
      message <- request.session.get("formapp::message")
    } yield {
      enquetes.save(Enquete(name, gender, message))
      Redirect("/formapp/messages").withNewSession
    }).getOrElse[Result](Redirect("/formapp/register"))
  }

  def registerAccount = Action { request =>
    (for {
      param <- request.body.asFormUrlEncoded
      name  <- param.get("name").flatMap(_.headOption)
      id    <- param.get("id").flatMap(_.headOption)
      pass  <- param.get("password").flatMap(_.headOption)
    } yield {
      val pass_res4: String = new String(MessageDigest.getInstance("SHA-256").digest(pass.getBytes("UTF-8")))
      val account           = Account(id, name, pass_res4)
      accounts.getPassByAccountId(id) match {
        case Some(e) => Ok(views.html.formapp.login(request)) //存在すると困る
        case None => {
          accounts.save(account)

          //ここまできたらセッションキーを発行する
          val cookie: String = UUID.randomUUID().toString
          val entries        = tasks.getListByAccountId(id)
          accounts.updateSessionID(id, cookie)
          Ok(views.html.formapp.room(entries)).withCookies(Cookie("session-id", cookie))
        }
      }
      //Ok(views.html.formapp.registerAccount(request)).withSession(request.session)
    }).getOrElse[Result](Redirect("/formapp/login"))
  }

  def tryLogin = Action { request =>
    (for {
      param <- request.body.asFormUrlEncoded
      id    <- param.get("id").flatMap(_.headOption)
      pass  <- param.get("password").flatMap(_.headOption)
    } yield {
      val pass_res4: String = new String(MessageDigest.getInstance("SHA-256").digest(pass.getBytes("UTF-8")))
      accounts.getPassByAccountId(id) match {
        case Some(e) => {
          if (pass_res4.equals(e)) {
            //パスワードが正しいので
            //セッションキーを発行する
            val cookie: String = UUID.randomUUID().toString
            val entries        = tasks.getListByAccountId(id)
            accounts.updateSessionID(id, cookie)
            Ok(views.html.formapp.room(entries)).withCookies(Cookie("session-id", cookie))
          } else {
            //idは正しいがパスワードが違う
            Ok(views.html.formapp.login(request))
          }
        }
        case None => {
          //idがない
          Ok(views.html.formapp.login(request))
        }
      }
      //Ok(views.html.formapp.registerAccount(request)).withSession(request.session)
    }).getOrElse[Result](Redirect("/formapp/login"))
  }

  def room = Action { request =>
    val session_id: String = request.cookies.get("session-id").map(_.value).getOrElse("")
    accounts.getAccountIdBySessionId(session_id) match {
      case None => Ok(views.html.formapp.login(request))
      case Some(value) => {
        println("せっしょんあいでー")
        val entries = tasks.getListByAccountId(value)
        Ok(views.html.formapp.room(entries))

      }
    }
  //Ok(views.html.formapp.login(request))
  }

  def login = Action { request =>
    Ok(views.html.formapp.login(request)).withSession(request.session)
  }

  def taskRegister = Action { request =>
    Ok(views.html.formapp.task_register(request)).withSession(request.session)
  }

  def taskConfirm = Action { request =>
    (for {
      param <- request.body.asFormUrlEncoded
      title <- param.get("title").flatMap(_.headOption)
      desc  <- param.get("description").flatMap(_.headOption)
      done  <- param.get("is_done").flatMap(_.headOption)
    } yield {
      val is_done: Boolean = done.equals("done")
      val task: Task       = Task(title, desc, is_done)
      //val entries          = tasks.list
      Ok(views.html.formapp.task_confirm(task)(request)).withSession(
        request.session
        + ("formapp::title"       -> title)
        + ("formapp::description" -> desc)
        + ("formapp::is_done"     -> done)
      )
    }).getOrElse[Result](Redirect("/formapp/room"))
  }

  def confirmed = Action { request =>
    (for {
      title       <- request.session.get("formapp::title")
      description <- request.session.get("formapp::description")
      is_done     <- request.session.get("formapp::is_done")
    } yield {
      val session_id: String = request.cookies.get("session-id").map(_.value).getOrElse("")
      accounts.getAccountIdBySessionId(session_id) match {
        case None => {
          println("失敗")
          Ok(views.html.formapp.login(request))
        }
        case Some(value) => {
          tasks.save(Task(value, title, description, is_done.equals("done")))
          val entries = tasks.getListByAccountId(value)
          Ok(views.html.formapp.room(entries))
        }
      }

    }).getOrElse[Result](Redirect("/formapp/room"))
  }

  def taskEntry(task_id: String) = Action { request =>
    {
      var task = tasks.getTaskByTaskId(task_id).getOrElse(null)
      if (task.==(null)) {
        NotFound(s"No entry for id=${task_id}")

      } else {
        val session_id: String = request.cookies.get("session-id").map(_.value).getOrElse("")
        accounts.getAccountIdBySessionId(session_id) match {
          case None => {
            println("失敗")
            Ok(views.html.formapp.login(request))
          }
          case Some(value) => {
            //タスクを正に取得した
            if (task.account_id.equals(value)) {
              Ok(views.html.formapp.task_entry(task))
            } else {
              //不正なタスク取得
              NotFound(s"No entry for id=${task_id}")
            }
          }
        }
      }
    }
  }

  def changeTaskState(task_id: String) = Action { request =>
    {
      var task = tasks.getTaskByTaskId(task_id).getOrElse(null)
      if (task == null) {
        NotFound(s"No entry for id=${task_id}")
      } else {
        val session_id: String = request.cookies.get("session-id").map(_.value).getOrElse("")
        accounts.getAccountIdBySessionId(session_id) match {
          case None => {
            println("失敗")
            Ok(views.html.formapp.login(request))
          }
          case Some(value) => {
            //タスクを正に取得した
            if (task.account_id.equals(value)) {
              var new_state_task: Task = Task(task.id, task.title, task.description, !task.is_done)
              tasks.save(new_state_task)
              val entries = tasks.getListByAccountId(value)
              Ok(views.html.formapp.room(entries))
            } else {
              //不正なタスク取得
              NotFound(s"No entry for id=${task_id}")
            }
          }
        }
      }
    }
  }

  def changeTaskState(task_id: String) = Action { request =>
    {
      val session_id: String = request.cookies.get("session-id").map(_.value).getOrElse("")
      accounts.getAccountIdBySessionId(session_id) match {
        case None        => Ok(views.html.formapp.login(request))
        case Some(value) =>
        {
          accounts.delete(account_id)
          tasks.delete(account_id)
        }
      }
    }
  }

}
