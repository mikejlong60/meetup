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
  with CancelAfterFailure
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

    //    "insert" should {
    //      "insert the bank account as given" in propertyTest { videoFeed =>
    //        val x = for {
    //          newId <- repo.nextPublicId
    //          existingVF <- repo.find(videoFeed.publicId)
    //          insertedVF <- repo.create(existingVF.get.copy(publicId = newId))
    //          queriedVF <- repo.find(newId)
    //        } yield (insertedVF.right.get, queriedVF.get)
    //
    //        val (insertedVF, queriedVF) = x.futureValue
    //
    //        insertedVF should equal(queriedVF)
    //      }
    //    }

    //    "update" should {
    //      "update the video feed as given" in propertyTest { videoFeed =>
    //        def switchDisabledBy(currentFlags: Flags) = currentFlags.disabledBy match {
    //          case Some(FEED_DISABLER.VMC4) => currentFlags.copy(disabledBy = Some(FEED_DISABLER.ADMIN))
    //          case Some(FEED_DISABLER.ADMIN) => currentFlags.copy(disabledBy = None)
    //          case None => currentFlags.copy(disabledBy = Some(FEED_DISABLER.ADMIN))
    //        }
    //        val x = for {
    //          existingVF <- repo.find(videoFeed.publicId)
    //          updatedVF <- repo.update(existingVF.get.copy(name = "test", flags = switchDisabledBy(existingVF.get.flags)))
    //          queriedVF <- repo.find(videoFeed.publicId)
    //        } yield (updatedVF.get, queriedVF.get)
    //
    //        val (updatedVF, queriedVF) = x.futureValue
    //
    //        updatedVF should equal(queriedVF)
    //      }
    //    }
    //
    //    "delete" should {
    //      "remove the video feed" in propertyTest { videoFeed =>
    //        val x = for {
    //          newId <- repo.nextPublicId
    //          existingVF <- repo.find(videoFeed.publicId)
    //          insertedVF <- repo.create(existingVF.get.copy(publicId = newId))
    //          _ <- repo.delete(newId)
    //          deletedVF <- repo.find(newId)
    //        } yield deletedVF
    //
    //        val deletedVF = x.futureValue
    //
    //        deletedVF should not be 'defined
    //      }
    //
    //      "return true on successful remove of the video feed" in propertyTest { videoFeed =>
    //
    //        val x = for {
    //          newId <- repo.nextPublicId
    //          existingVF <- repo.find(videoFeed.publicId)
    //          insertedVF <- repo.create(existingVF.get.copy(publicId = newId))
    //          result <- repo.delete(newId)
    //        } yield result
    //        val delResult = x.futureValue
    //
    //        assert(delResult)
    //      }
    //    }

  }
}

class PostgresBankAccountDAOSpecs
  extends BankAccountDAOSpecs {

  val dao = new BankAccountDAO

  def genBankAccount =
    for {
      accountNum <- Gen.choose(0, 1000000)
      balance <- Gen.choose(0, 100000)
      active <- Gen.oneOf(true, false)
    } yield BankAccount(accountNum = accountNum, balance = balance, active = active)

  val feeds = Gen.listOfN(100, for {
    videoFeed ← genBankAccount
  } yield videoFeed).sample.get

  val bankAccounts = Table(
    ("bankAccounts"), feeds: _*)

  "A BankAccountDAO" should {
    behave like bankAccountDAOBehavior(dao, bankAccounts)
  }
}

