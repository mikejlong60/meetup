package meetup


import com.typesafe.config.Config
import slick.jdbc.JdbcBackend.Database
import com.typesafe.config.ConfigFactory

trait DatabaseProvider {
  def db: Database
}

trait ConfigDatabaseProvider extends DatabaseProvider {
  def db: Database = ConfigDatabase.db
}

object ConfigDatabase extends ConfigurationProvider {
  lazy val db = Database.forConfig("meetup.slick.meetup", config)
}


trait IConfigurationProvider {
  val config: Config
}

trait ConfigurationProvider extends IConfigurationProvider {
  override lazy val config = ConfigFactory.load()

}
