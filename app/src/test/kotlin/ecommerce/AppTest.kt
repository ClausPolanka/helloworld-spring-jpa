package ecommerce

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.*
import org.springframework.beans.factory.annotation.*
import org.springframework.test.context.*
import org.springframework.test.context.junit.jupiter.*

// This extension is used to integrate the Spring test context with the JUnit 5 Jupiter test.
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [SpringConfiguration::class]) // The Spring test context is configured using the beans defined in the listed SpringConfiguration.
class AppTest {

    @Autowired
    lateinit var repo: MessageRepo

    @Autowired
    lateinit var foo: Foo

    @Test
    fun appHasAGreeting() {
        val classUnderTest = App()
        assertEquals("Hello World!", classUnderTest.greeting, "app should have a greeting")
    }

    @Test
    fun storeLoadMessage() {
        val msg = Message(text = "before")
        repo.save(msg)
        val msgs = repo.findAll().toList()
        assertAll(
            { assertEquals(1, msgs.size) },
            { assertEquals("before", msgs[0].text) },
        )
        repo.deleteAll()
    }

    @Test
    fun foo() {
        val ex = assertThrows(RuntimeException::class.java) { foo.foo() }
        assertAll(
            { assertEquals("Kaboom", ex.message) },
            { assertEquals(0, repo.findAll().toList().size) }
        )
    }
}
