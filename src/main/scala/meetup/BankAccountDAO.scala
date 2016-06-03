package meetup


import slick.driver.PostgresDriver.api._
import scala.concurrent._
//import ExecutionContext.Implicits.global._

case class BankAccount(accountNum: Int, balance: Int, active: Boolean)

class BankAccounts(tag: Tag) extends Table[BankAccount](tag, Some("meetup"), "bank_accounts") {
  def accountNum = column[Int]("account_num", O.PrimaryKey) // This is the primary key column
  def balance = column[Int]("balance")
  def active = column[Boolean]("active")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (accountNum, balance, active) <> (BankAccount.tupled, BankAccount.unapply _)
}

class BankAccountDAO extends ConfigDatabaseProvider {


//  import slick.lifted.{ColumnOrdered, Compiled, Rep}
//  import slick.jdbc.JdbcBackend
//  import scala.concurrent.ExecutionContext.Implicits.global

  private implicit val executionContext = db.ioExecutionContext

  val bankAccounts = TableQuery[BankAccounts]

  def filterQuery(accountNum: Int): Query[BankAccounts, BankAccount, Seq] =
    bankAccounts.filter(x => x.accountNum === accountNum)

  def find(accountNum: Int): Future[Option[BankAccount]] =
    try db.run(filterQuery(accountNum).result.headOption)//.head)
    finally db.close

  def insert(person: BankAccount): Future[Int] =
    try db.run(bankAccounts += person)
    finally db.close

  def update(id: Int, person: BankAccount): Future[Int] =
    try db.run(filterQuery(id).update(person))
    finally db.close

  def delete(id: Int): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close
}