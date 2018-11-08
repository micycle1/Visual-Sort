package App.javafx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import processing.javafx.PSurfaceFX

class JavaFxApp : Application() {

    companion object {
        var surface: PSurfaceFX? = null
    }

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("stage.fxml"))
        val root = loader.load<Parent>()
        val scene = Scene(root, 1280.0, 720.0)

        primaryStage.title = "Sorting-Algorithm Visualiser"
        primaryStage.scene = scene
        primaryStage.show()
        surface!!.stage = primaryStage
    }
}