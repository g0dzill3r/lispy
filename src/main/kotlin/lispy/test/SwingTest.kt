package lispy.test

import lispy.Interpreter
import lispy.ProviderFactory
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.EtchedBorder


fun main() {
    val provider = ProviderFactory.getProvider()
    val interp = Interpreter (provider)

    val app = MinimalSwingApplication {
        val output = try {
            val result = interp.eval (it)
            "-> $result"
        } catch (e: Exception) {
            e.message ?: ""
        }
        println (output)
        output
    }
    return
}


class MinimalSwingApplication (val execute: (String) -> String) {
    var output = JTextArea (10, 80)

    init {
        buildAndDisplayGui()
    }

    // PRIVATE
    fun buildAndDisplayGui () {
        val frame = JFrame("Lispy Interpreter")
        buildContent(frame)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
        return
    }

    fun setOutput (string: String) {
        output.setText (string)
    }

    private fun buildContent(aFrame: JFrame) {
        val panel = JPanel()
        panel.setLayout(BoxLayout(panel, BoxLayout.PAGE_AXIS))
        panel.setBorder (EmptyBorder (10, 10, 10, 10))

        val textArea = JTextArea(10, 80)
        textArea.text = "(define example '(1 2 3))"
        textArea.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(evt: KeyEvent) {
                if (evt.isControlDown && evt.keyCode == 10) {
                    setOutput (execute (textArea.text))
                }
            }
        });

        textArea.setBorder(BorderFactory.createCompoundBorder(EmptyBorder(10, 10, 10, 10), EtchedBorder()))
        panel.add (textArea)

        output.isEditable = false
        output.setBorder(BorderFactory.createCompoundBorder(EmptyBorder(10, 10, 10, 10), EtchedBorder()))
        panel.add (output)

        aFrame.contentPane.add(panel)
    }
}
