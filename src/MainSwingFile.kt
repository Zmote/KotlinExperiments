import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

var base:Int = 500

fun main(vararg args: String){
    createWindow()
}

fun createWindow(){
    val frame = JFrame("Frame Demo")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val button = JButton("Click Me!")
    button.addActionListener({createLogWindow("Clicked!")})
    frame.contentPane.add(button, BorderLayout.CENTER)
    frame.setLocation(860,490)
    frame.minimumSize = Dimension(200,100)
    frame.pack()
    frame.isVisible = true
}


fun createLogWindow(logText:String){
    val frame = JFrame("Log Window")
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    val label = JLabel(logText, SwingConstants.CENTER)
    frame.contentPane.add(label, BorderLayout.CENTER)
    frame.setLocation(base, base)
    base += 50
    frame.minimumSize = Dimension(200,100)
    frame.pack()
    frame.isVisible = true
}
