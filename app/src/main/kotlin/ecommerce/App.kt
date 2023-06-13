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
    fun dataSource(): DataSource {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.postgresql.Driver")
        ds.url = "jdbc:postgresql://localhost:5433/postgres?currentSchema=helloworld_spring_jpa"
        ds.username = "postgres"
        ds.password = "mysecret"
        return ds
    }

    /**
     * We create a transaction manager bean based on an entity manager factory. Every interaction with the database
     * should occur within transaction boundaries and Spring Data needs a transaction manager bean.
     */
    @Bean
    fun transactionManager(emf: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(emf)
    }

    /**
     * Needed by JPA to interact with Hibernate.
     */
    @Bean
    fun jpaVendorAdapter(): JpaVendorAdapter {
        val adapter = object : HibernateJpaVendorAdapter() {
            override fun determineDatabaseDialectClass(database: Database): Class<*>? =
                when (database) {
                    Database.POSTGRESQL -> PostgreSQLDialect::class.java
                    else -> super.determineDatabaseDialectClass(database)
                }
        }
        adapter.setDatabase(Database.POSTGRESQL)
        adapter.setShowSql(true)
        return adapter
    }

    @Bean
    fun entityManagerFactory(ds: DataSource, vendor: JpaVendorAdapter): LocalContainerEntityManagerFactoryBean {
        val container = LocalContainerEntityManagerFactoryBean()
        container.dataSource = ds
        container.jpaVendorAdapter = vendor
        container.setPackagesToScan("ecommerce")
        val props = Properties()
        props["hibernate.hbm2ddl.auto"] = "create"
        container.setJpaProperties(props)
        return container
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
