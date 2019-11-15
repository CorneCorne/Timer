package models.formapp

import javax.inject.{Inject, Singleton}
import models.Dao
import play.api.db.slick.{DatabaseConfigProvider => DBConfigProvider}

import scala.concurrent.ExecutionContext

/**
  * account dao
  */
@Singleton
class Tasks @Inject()(dbcp: DBConfigProvider)(implicit ec: ExecutionContext) extends Dao(dbcp) {

  import profile.api._
  import utility.Await

  val table = "task"

  //case class Task(id: Int, account_id: String, task_name: String, status: String, createdAt: Timestamp)

  def list: Seq[Task] = Await.result(
    db.run(sql"SELECT id, task_id,account_id, title,description, is_done, created_At FROM #$table ORDER BY id".as[Task])
  )

  def getListByAccountId(account_id: String): Seq[Task] = Await.result(
    db.run(
      sql"SELECT id , task_id, account_id, title,description, is_done, created_At FROM #$table WHERE account_id='#$account_id' ORDER BY id"
        .as[Task]
    )
  )

  def getTaskByTaskId(task_id: String): Option[Task] = Await.result(
    db.run(
      sql"SELECT id,task_id, account_id, title,description, is_done, created_At FROM #$table WHERE task_id='#$task_id'"
        .as[Task]
        .headOption
    )
  )

  def findByID(id: Int): Option[Task] = Await.result(
    db.run(
      sql"SELECT id, account_id, title,description, is_done, created_At FROM #$table WHERE id=#$id".as[Task].headOption
    )
  )

  //登録のみ
  def save(task: Task): Int = task match {
    case Task(0, _, account_id, title, description, _, _) =>
      val task_id: String = scala.util.Random.alphanumeric.take(32).mkString
      Await.result(
        db.run(
          sqlu"INSERT INTO #$table (task_id,account_id, title,description, is_done) VALUES ('#$task_id','#$account_id', '#$title', '#$description',false)"
        )
      )
    case Task(id, _, _, title, description, is_done, _) =>
      Await.result(
        db.run(sqlu"UPDATE #$table SET title='#$title', description='#$description' ,is_done=#$is_done WHERE id = #$id")
      )
  }

  //ユーザの退会時、タスクも全て消す
  def delete(accoun_id: String) = Await.result(
    db.run(sql"DELETE FROM #$table WHERE account_id='#$account_id'")
  )

}
