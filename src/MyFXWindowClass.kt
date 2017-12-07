import com.sun.webkit.BackForwardList
import com.sun.webkit.BackForwardList.Entry
import com.sun.webkit.WebPage
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.web.WebEngine
import javafx.scene.web.WebHistory
import javafx.stage.Stage

class MyFXWindowClass : Application() {
    override fun start(primaryStage: Stage?) {
        createFXWindow(primaryStage)
    }

    private fun createFXWindow(primaryStage: Stage?){
        val btnLogin = Button()
        btnLogin.text = "Login"
        btnLogin.onAction = EventHandler<ActionEvent>{e -> run{
            println(e)
            println("Clicked")
        }}
        val root = StackPane()
        root.children.add(btnLogin)
        val scene = Scene(root, 300.0, 250.0)
        primaryStage!!.title = "JavaFX Demo"
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>){
            launch(MyFXWindowClass::class.java)
        }
    }

}