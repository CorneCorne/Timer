package models.formapp

//import java.sql.Timestamp

/**
  * Domain model of enquete
  * @param id        ID
  * @param account_id      アカウント名（ログイン時に要求）
  * @param user_name    	ユーザー名（ログイン時に要求されない）
  * @param account_password   パスワード
  * @param session_id					セッションid
  */
case class Account(id: Int, account_id: String, user_name: String, account_password: String, session_id: String)


