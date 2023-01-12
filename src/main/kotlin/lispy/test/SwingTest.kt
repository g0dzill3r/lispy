package lispy.test

import lispy.*
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
        val buf = StringBuffer ()
        try {
            val exprs = provider.parser.parseMany (it)
            exprs.forEach {
                try {
                    val (_, result, output) = interp.evalOne (it)
                    if (output.isNotEmpty()) {
                        buf.append(output.stripTrailingNewlines())
                        buf.append("\n")
                    }
                    when (result) {
                        NilValue -> Unit
                        is StringValue -> buf.append("=> \"$result\"\n")
                        else -> buf.append("=> $result\n")
                    }
                } catch (e: Exception) {
                    buf.append (e.toString () + "\n")
                }
            }
        } catch (e: Exception) {
            buf.append ("${e::class.simpleName}: ${e.message}")
        }
        buf.toString ()
    }
    return
}

class MinimalSwingApplication (val execute: (String) -> String) {
    val provider = ProviderFactory.getProvider()
    val input = JTextArea(10, 80)
    var output = JTextArea (10, 80)
    val status = JTextField ("")

    val isExpression: Boolean
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
        status.apply {
            if (isExpression) {
                text = "⏺ Valid"
                foreground = Color.DARK_GRAY
            } else {
                text = "⏺ Invalid"
                foreground = Color.RED
            }
        }
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
        val panel = JPanel().apply {
            setLayout (BoxLayout (this, BoxLayout.PAGE_AXIS))
        }

        val textFont = Font("Courier", Font.PLAIN, 14)

        // The input area

        input.apply {
            text = "(define example '(1 2 3))"
            font = textFont
            border = EmptyBorder(10, 10, 10, 10)
            addKeyListener(object : KeyAdapter() {
                override fun keyReleased(e: KeyEvent?) {
                    update()
                }

                override fun keyPressed(evt: KeyEvent) {
                    if (evt.isControlDown && evt.keyCode == 10) {
                        output.text = execute (text)
                    }
                }
            });
        }

        // The output area

        output.apply {
            isEditable = false
            font = textFont
            border = EmptyBorder(10, 10, 10, 10)
        }

        // Status area

        status.apply {
            background = Color(255, 255, 255)
            border = EmptyBorder(0, 20, 0, 0)
        }

        // set Orientation for slider

        val topPanel = JPanel ().apply {
            layout = BoxLayout (this, BoxLayout.PAGE_AXIS)
            add (input)
            add (status)
        }

        val splitPane = JSplitPane (SwingConstants.VERTICAL, topPanel, output).apply {
            border = BorderFactory.createCompoundBorder (EmptyBorder(10, 10, 10, 10), EtchedBorder())
            orientation = SwingConstants.HORIZONTAL
        }
        aFrame.contentPane.add (splitPane)

        return
    }
}
