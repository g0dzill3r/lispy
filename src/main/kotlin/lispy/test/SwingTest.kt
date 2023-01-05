package lispy.test

import lispy.Interpreter
import lispy.ProviderFactory
import java.awt.Color
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.EtchedBorder


fun main() {
    val provider = ProviderFactory.getProvider()
    val interp = Interpreter (provider)

    MinimalSwingApplication {
        val output = try {
            val result = interp.eval (it)
            result.map { "-> $it" }.joinToString ("\n")
        } catch (e: Exception) {
            e.message ?: ""
        }
        println (output)
        output
    }
    return
}

class MinimalSwingApplication (val execute: (String) -> String) {
    val provider = ProviderFactory.getProvider()
    val input = JTextArea(10, 80)
    var output = JTextArea (10, 80)
    val status = JTextField ("")

    val isValid: Boolean
        get() {
            return try {
                provider.parser.parseMany (input.text)
                true
            }
            catch (e: Exception) {
                false
            }
        }

    init {
        buildAndDisplayGui()
        update ()
    }

    private fun update () {
        status.text = if (isValid) "Valid" else "Invalid"
    }

    // PRIVATE
    fun buildAndDisplayGui () {
        val frame = JFrame("Lispy Interpreter, v0.1")
        buildContent(frame)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
        return
    }

    private fun buildContent(aFrame: JFrame) {
        val panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.PAGE_AXIS))
        panel.setBorder (EmptyBorder (10, 10, 10, 10))

        val font = Font("Courier", Font.PLAIN, 14)

        input.text = "(define example '(1 2 3))"
        input.font = font
        input.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                update ()
            }
            override fun keyPressed(evt: KeyEvent) {
                if (evt.isControlDown && evt.keyCode == 10) {
                    output.text = execute (input.text)
                }
            }
        });

        input.border = BorderFactory.createCompoundBorder(EmptyBorder(10, 10, 10, 10), EtchedBorder())
        panel.add (input)
        panel.font = font

        status.background = Color (255, 255, 255)
        status.border = EmptyBorder (0, 20, 0, 0)
        panel.add (status)

        output.isEditable = false
        output.border = BorderFactory.createCompoundBorder(EmptyBorder(10, 10, 10, 10), EtchedBorder())
        panel.add (output)

        aFrame.contentPane.add(panel)
        return
    }
}
