package models.formapp

import java.sql.Timestamp

import models.DomainModel

/**
  * Domain model of task
  * @param id        ID
  * @param task_id   タスクid、IDによるid管理はダメ
  * @param account_id      アカウント名（ログイン時に要求）
  * @param title    	タスク名
  * @param description					説明
  *  @param is_done					状態
  * @param createdAt				セッションid
  */
case class Task(
    id: Int,
    task_id: String,
    account_id: String,
    title: String,
    description: String,
    is_done: Boolean,
    createdAt: Timestamp
)

object Task extends DomainModel[Task] {
  import slick.jdbc.GetResult
  override implicit def getResult: GetResult[Task] = GetResult(
    r => Task(r.nextInt, r.nextString, r.nextString, r.nextString, r.nextString, r.nextBoolean, r.nextTimestamp)
  )

  def apply(title: String, description: String, is_done: Boolean): Task =
    Task(0, null, null, title, description, is_done, null)

  def apply(account_id: String, title: String, description: String, is_done: Boolean): Task =
    Task(0, null, account_id, title, description, is_done, null)
  def apply(id: Int, title: String, description: String, is_done: Boolean): Task =
    Task(id, null, null, title, description, is_done, null)
}
