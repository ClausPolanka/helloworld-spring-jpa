package ecommerce

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

// This extension is used to integrate the Spring test context with the JUnit 5 Jupiter test.
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [SpringConfiguration::class]) // The Spring test context is configured using the beans defined in the listed SpringConfiguration.
class AppTest {

    @Autowired
    lateinit var repo: MessageRepo

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
    }
}
