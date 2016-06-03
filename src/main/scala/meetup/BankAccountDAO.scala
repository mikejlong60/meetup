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

  private implicit val executionContext = db.ioExecutionContext

  val bankAccounts = TableQuery[BankAccounts]

  def filterQuery(accountNum: Int): Query[BankAccounts, BankAccount, Seq] =
    bankAccounts.filter(acct => acct.accountNum === accountNum)

  def find(accountNum: Int): Future[Option[BankAccount]] =
    db.run(filterQuery(accountNum).result.headOption)

  def insert(bankAccount: BankAccount): Future[Int] =
    db.run(bankAccounts += bankAccount)

  def update(accountNum: Int, bankAccount: BankAccount): Future[Int] =
    db.run(filterQuery(accountNum).update(bankAccount))

  def delete(accountNum: Int): Future[Int] =
    db.run(filterQuery(accountNum).delete)
}