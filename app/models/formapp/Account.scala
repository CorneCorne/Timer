package models.formapp

import java.sql.Timestamp

import models.DomainModel
//import org.joda.time.{DateTime, DateTimeZone}

/**
  * Domain model of enquete
  * @param id        ID
  * @param account_id      アカウント名（ログイン時に要求）
  * @param user_name    	ユーザー名（ログイン時に要求されない）
  * @param account_password   パスワード
  * @param session_id					セッションid
  * @param session_timestamp  セッション期限
  */
case class Account(
    id: Int,
    account_id: String,
    user_name: String,
    account_password: String,
    session_id: String,
    session_timestamp: Timestamp
)

object Account extends DomainModel[Account] {
  import slick.jdbc.GetResult
  override implicit def getResult: GetResult[Account] = GetResult(
    r => Account(r.nextInt, r.nextString, r.nextString, r.nextString, r.nextString, r.nextTimestamp)
  )

  def apply(account_id: String, user_name: String, account_password: String): Account =
    Account(0, account_id, user_name, account_password, null, null)
}
