import javafx.application.Application
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.concurrent.Worker.State
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Hyperlink
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import javafx.stage.Stage
import javafx.util.Callback
import netscape.javascript.JSObject
import java.io.File
import java.io.InputStream

//Adapted to Kotlin, from : https://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
class WebViewSample : Application() {

    private var scene: Scene? = null
    private var browser:Browser? = null;

    override fun start(stage: Stage) {
        // create scene
        stage.title = "Web View"
        browser = Browser();
        scene = Scene(browser, 750.0, 500.0, Color.web("#666970"))
        stage.scene = scene
        // apply CSS style
        scene!!.stylesheets.add("webviewsample/BrowserToolbar.css")
        // show stage
        stage.show()
        loadHistory()
    }

    override fun stop() {
        super.stop()
        saveHistory()
    }

    private fun saveHistory(){
        File("browserHistory.txt").printWriter().use { out ->
            for(item in browser!!.getWebEngine()!!.history.entries)
                out.println(item);
        }
    }

    private fun loadHistory(){
        val inputStream:InputStream = File("browserHistory.txt").inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() };
        println(inputString);
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            launch(WebViewSample::class.java);
        }
    }
}

internal class Browser : Region() {
    private val imageFiles = arrayOf("product.png", "blog.png", "documentation.png", "partners.png", "help.png")
    private val captions = arrayOf("Products", "Blogs", "Documentation", "Partners", "Help")
    private val urls = arrayOf("http://www.oracle.com/products/index.html", "http://blogs.oracle.com/", "http://docs.oracle.com/javase/index.html", "http://www.oracle.com/partners/index.html", "https://docs.oracle.com/en/")

    private val toolBar: HBox
    private val browser = WebView()

    init {

        val selectedImage = ImageView()
        val hpls = arrayOfNulls<Hyperlink>(captions.size)
        val images = arrayOfNulls<Image>(imageFiles.size)
        val webEngine = browser.engine
        val showPrevDoc = Button("Toggle Previous Docs")
        val smallView = WebView()
        val comboBox = ComboBox<String>()
        var needDocumentationButton = false
        //apply the styles
        styleClass.add("browser")

        for (i in captions.indices) {
            // create hyperlinks
            hpls[i] = Hyperlink(captions[i])
            val hpl = hpls[i]
            images[i] = Image(javaClass.getResourceAsStream(imageFiles[i]))
            val image = images[i]
            hpl!!.graphic = ImageView(image)
            val url = urls[i]
            val addButton = hpl.text == "Documentation"

            // process event
            hpl.onAction = EventHandler {
                needDocumentationButton = addButton
                webEngine.load(url)
            }
        }

        comboBox.prefWidth = 60.0

        // create the toolbar
        toolBar = HBox()
        toolBar.alignment = Pos.CENTER
        toolBar.styleClass.add("browser-toolbar")
        toolBar.children.add(comboBox)
        toolBar.children.addAll(*hpls)
        toolBar.children.add(createSpacer())

        //set action for the button
        showPrevDoc.onAction = EventHandler<ActionEvent> { webEngine.executeScript("toggleDisplay('PrevRel')") }

        smallView.setPrefSize(120.0, 80.0)

        //handle popup windows
        webEngine.createPopupHandler = Callback {
            smallView.fontScale = 0.8
            if (!toolBar.children.contains(smallView)) {
                toolBar.children.add(smallView)
            }
            smallView.engine
        }

        //process history
        val history = webEngine.history
        history.entries.addListener(ListChangeListener { c ->
            c.next()
            for (e in c.removed) {
                comboBox.items.remove(e.url)
            }
            for (e in c.addedSubList) {
                comboBox.items.add(e.url)
            }
        })

        //set the behavior for the history combobox
        comboBox.onAction = EventHandler<ActionEvent> {
            val offset = comboBox.selectionModel.selectedIndex - history.currentIndex
            history.go(offset)
        }


        // process page loading
        webEngine.loadWorker.stateProperty().addListener { ov, oldState, newState ->
            toolBar.children.remove(showPrevDoc)
            if (newState == State.SUCCEEDED) {
                val win = webEngine.executeScript("window") as JSObject
                win.setMember("app", JavaApp())
                if (needDocumentationButton) {
                    toolBar.children.add(showPrevDoc)
                }
            }
        }

        // load the home page
        webEngine.load("http://www.oracle.com/products/index.html")

        //add components
        children.add(toolBar)
        children.add(browser)
    }

    // JavaScript interface object
    inner class JavaApp {
        fun exit() {
            Platform.exit()
        }
    }

    private fun createSpacer(): Node {
        val spacer = Region()
        HBox.setHgrow(spacer, Priority.ALWAYS)
        return spacer
    }

    fun getWebEngine(): WebEngine? {
        return browser.engine
    }

    override fun layoutChildren() {
        val w = width
        val h = height
        val tbHeight = toolBar.prefHeight(w)
        layoutInArea(browser, 0.0, 0.0, w, h - tbHeight, 0.0, HPos.CENTER, VPos.CENTER)
        layoutInArea(toolBar, 0.0, h - tbHeight, w, tbHeight, 0.0, HPos.CENTER, VPos.CENTER)
    }

    override fun computePrefWidth(height: Double): Double {
        return 750.0
    }

    override fun computePrefHeight(width: Double): Double {
        return 600.0
    }
}