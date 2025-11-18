package ecommerce

import jakarta.persistence.*
import org.springframework.beans.factory.annotation.*
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.*
import org.springframework.data.repository.*
import org.springframework.jdbc.datasource.*
import org.springframework.orm.jpa.*
import org.springframework.orm.jpa.vendor.*
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.*
import java.util.*
import javax.sql.*

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
@EnableTransactionManagement
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
        HibernateJpaVendorAdapter().apply {
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

    @Bean
    fun foo(repo: MessageRepo) = Foo(repo)
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

@Component
open class Foo(
    private val messageRepo: MessageRepo
) {

    @Transactional
    open fun foo() {
        messageRepo.save(Message(text = "foo"))
        throw RuntimeException("Kaboom")
    }
}