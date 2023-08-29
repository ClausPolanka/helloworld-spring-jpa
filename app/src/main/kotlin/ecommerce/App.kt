package ecommerce

import jakarta.persistence.*
import org.hibernate.dialect.PostgreSQLDialect
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.*
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.*
import javax.sql.DataSource

class App {
    val greeting: String
        get() {
            return "Hello World!"
        }
}

fun main() {
    println(App().greeting)
}

@EnableJpaRepositories
class SpringConfiguration {

    @Bean
    fun dataSource(): DataSource =
        DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            url = "jdbc:postgresql://localhost:5433/postgres?currentSchema=helloworld_spring_jpa"
            username = "postgres"
            password = "mysecret"
        }

    /**
     * We create a transaction manager bean based on an entity manager factory. Every interaction with the database
     * should occur within transaction boundaries and Spring Data needs a transaction manager bean.
     */
    @Bean
    fun transactionManager(emf: EntityManagerFactory) =
        JpaTransactionManager(emf)

    /**
     * Needed by JPA to interact with Hibernate.
     */
    @Bean
    fun jpaVendorAdapter(): JpaVendorAdapter =
        object : HibernateJpaVendorAdapter() {
            override fun determineDatabaseDialectClass(database: Database): Class<*>? =
                when (database) {
                    Database.POSTGRESQL -> PostgreSQLDialect::class.java
                    else -> super.determineDatabaseDialectClass(database)
                }
        }.apply {
            setDatabase(Database.POSTGRESQL)
            setShowSql(true)
        }

    @Bean
    fun entityManagerFactory(ds: DataSource, vendor: JpaVendorAdapter): LocalContainerEntityManagerFactoryBean =
        LocalContainerEntityManagerFactoryBean().apply {
            dataSource = ds
            jpaVendorAdapter = vendor
            setPackagesToScan("ecommerce")
            val props = Properties()
            props["hibernate.hbm2ddl.auto"] = "create"
            setJpaProperties(props)
        }
}

@Entity
class Message(
    @Id
    @GeneratedValue
    val id: Long? = null,
    var text: String,
) {
    override fun toString(): String {
        return "Message(id=$id, text='$text')"
    }
}

interface MessageRepo : CrudRepository<Message, Long>
