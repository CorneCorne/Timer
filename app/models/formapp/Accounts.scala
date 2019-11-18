package models.formapp

import javax.inject.{Inject, Singleton}
import models.Dao
import play.api.db.slick.{DatabaseConfigProvider => DBConfigProvider}

import scala.concurrent.ExecutionContext

/**
  * account dao
  */
@Singleton
class Accounts @Inject()(dbcp: DBConfigProvider)(implicit ec: ExecutionContext) extends Dao(dbcp) {

  import profile.api._
  import utility.Await

  val table = "account"

  def list: Seq[Account] = Await.result(
    db.run(sql"SELECT id, account_id, user_name, account_password FROM #$table ORDER BY id".as[Account])
  )

  def findByID(id: Int): Option[Account] = Await.result(
    db.run(sql"SELECT id, account_id, user_name, account_password FROM #$table WHERE id=#$id".as[Account].headOption)
  )

  def getPassByAccountId(account_id: String) = Await.result(
    db.run(sql"SELECT account_password FROM #$table WHERE account_id='#$account_id'".as[String].headOption)
  )

  //登録のみ
  def save(account: Account): Int = account match {
    case Account(_, account_id, user_name, account_password, _, _) =>
      Await.result(
        db.run(
          sqlu"INSERT INTO #$table (account_id, user_name, account_password) VALUES ('#$account_id', '#$user_name', '#$account_password')"
        )
      )
  }

  //発行されたsession_idをデータベースに更新
  def updateSessionID(account_id: String, session_id: String) {
    val date                          = new java.util.Date()
    val timestamp: java.sql.Timestamp = new java.sql.Timestamp(new org.joda.time.DateTime(date).getMillis)
    Await.result(
      db.run(
        sqlu"UPDATE #$table SET session_id='#$session_id', session_timestamp='#${timestamp}' WHERE account_id='#$account_id'"
      )
    )
  }

  def getAccountIdBySessionId(session_id: String) = Await.result(
    db.run(sql"SELECT account_id FROM #$table WHERE session_id='#$session_id'".as[String].headOption)
  )

  def getAccountBySessionId(session_id: String): Option[Account] = Await.result(
    db.run(
      sql"SELECT id, account_id, user_name, account_password,session_id ,session_timestamp FROM #$table WHERE session_id='#$session_id'"
        .as[Account]
        .headOption
    )
  )

  //パスワードの変更
  def updatePass(account: Account): Int = account match {
    case Account(_, account_id, _, account_password, _, _) =>
      Await.result(
        db.run(sqlu"UPDATE #$table SET account_password='#$account_password' WHERE account_id = '#$account_id'")
      )
  }

  //ユーザー名の変更
  def updateUserName(account: Account): Int = account match {
    case Account(_, account_id, user_name, _, _, _) =>
      Await.result(
        db.run(sqlu"UPDATE #$table SET user_name=#$user_name WHERE account_id = #$account_id")
      )
  }

  //ユーザの退会
  def delete(account_id: String) = Await.result(
    db.run(sqlu"DELETE FROM #$table WHERE account_id='#$account_id'")
  )

}
