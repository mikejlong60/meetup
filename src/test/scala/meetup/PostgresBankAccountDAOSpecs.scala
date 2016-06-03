package meetup

import org.scalacheck.Gen
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.{ PropertyChecks, TableFor1 }
import org.scalatest._
import scala.concurrent.duration._

trait BankAccountDAOSpecs
  extends WordSpecLike
  with PropertyChecks
  with Matchers
  with ScalaFutures {

  override implicit val patienceConfig = PatienceConfig(3.seconds)

  def bankAccountDAOBehavior(dao: BankAccountDAO, bankAccounts: TableFor1[BankAccount]) = {

    def propertyTest(f: (BankAccount) => Unit): Unit = {
      forAll(bankAccounts) { (bankAccount: BankAccount) =>
        f(bankAccount)
      }
    }

    "find" should {

      "return a bank account with the given account number when it exists." in propertyTest { bankAccount =>
        whenReady(dao.insert(bankAccount)) { cr =>
          dao.find(bankAccount.accountNum).futureValue should be('defined)
        }
      }

      "return a video feed having the correct values." in propertyTest { bankAccount =>
        val possibleBankAccount: Option[BankAccount] = dao.find(bankAccount.accountNum).futureValue
        val foundAccount = possibleBankAccount.get

        foundAccount should equal(bankAccount)
      }

      "return None when the public Id does not exist." in forAll { (fakeAccountNum: Int) =>
        val possibleBankAccount: Option[BankAccount] = dao.find(fakeAccountNum).futureValue

        possibleBankAccount should not be 'defined
      }
    }

    "withdraw" should {

      import scala.concurrent.ExecutionContext.Implicits.global

      "allow you to withdraw if you have enough money in the account" in propertyTest { bankAccount =>
        val withdrawal = for {
          _ <- dao.update(bankAccount.copy(active = true))
          account <- dao.find(bankAccount.accountNum)
          good <- dao.withdraw(bankAccount, account.get.balance - 1)
        } yield good
        withdrawal.futureValue should equal(1)
      }

      "not allow you to withdraw more than your balance" in propertyTest { bankAccount =>
        val attemptedOverdraft = for {
          _ <- dao.update(bankAccount.copy(active = true))
          account <- dao.find(bankAccount.accountNum)
          overdraft <- dao.withdraw(bankAccount, account.get.balance + 1)
        } yield overdraft
        attemptedOverdraft.futureValue should equal(0)
      }

      "not allow you to withdraw from a closed account" in propertyTest { bankAccount =>
        val attemptedClosed = for {
          newId <- dao.update(bankAccount.copy(active = false))
          good <- dao.withdraw(bankAccount, bankAccount.balance - 1)
        } yield good
        attemptedClosed.futureValue should equal(0)
      }
    }
  }
}

class PostgresBankAccountDAOSpecs
  extends BankAccountDAOSpecs {

  val dao = new BankAccountDAO

  def genBankAccount =
    for {
      accountNum <- Gen.choose(0, 1000000)
      balance <- Gen.choose(0, 100)
      active <- Gen.oneOf(true, false)
    } yield BankAccount(accountNum = accountNum, balance = balance, active = active)

  val genedAccounts = Gen.listOfN(100, for {
    bankAccount ← genBankAccount
  } yield bankAccount).sample.get

  val bankAccounts = Table(
    ("bankAccounts"), genedAccounts: _*)

  "A BankAccountDAO" should {
    behave like bankAccountDAOBehavior(dao, bankAccounts)
  }
}

