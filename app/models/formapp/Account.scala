package models.formapp

import java.sql.Timestamp

/**
  * Domain model of enquete
  * @param id        ID
  * @param name      氏名
  * @param gender    性別
  * @param message   メッセージ
  * @param createdAt 作成日時
  */
case class Enquete(id: Int, name: String, gender: String, message: String, createdAt: Timestamp)


