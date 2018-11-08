package app.javafx

import app.Sort
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import processing.javafx.PSurfaceFX
import java.net.URL
import java.util.*

class Controller : Initializable {
    companion object {
        var surface: PSurfaceFX? = null
    }

    // @formatter:off
    @FXML var processing: StackPane? = null
    @FXML var algorithmDropdown: ComboBox<Any>? = null
    @FXML var graphDropdown: ComboBox<Any>? = null
    @FXML var sort: Button? = null
    @FXML var shuffle: Button? = null
    @FXML var size: Text? = null
    @FXML var delay: Text? = null
    @FXML var comparisons: Text? = null
    @FXML var arrayAccesses: Text? = null
    @FXML var sizeSlider: Slider? = null
    @FXML var delaySlider: Slider? = null
    // @formatter:on

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val canvas = surface!!.native as Canvas
        surface!!.fx.context = canvas.graphicsContext2D
        processing!!.children.add(canvas)
        canvas.widthProperty().bind(processing!!.widthProperty())
        canvas.heightProperty().bind(processing!!.heightProperty())

        algorithmDropdown!!.items.addAll("Selection Sort",
                "Bubble Sort",
                "Cocktail Sort",
                "Odd Even Sort",
                "Insertion Sort",
                "Tim Sort",
                "Quick Sort",
                "Heap Sort",
                "Merge Sort",
                "Pigeonhole Sort",
                "Radix Sort(MSB)",
                "Bogo Sort")
        algorithmDropdown!!.selectionModel.selectFirst()

        graphDropdown!!.items.addAll("Bar Graph", "Dot Plot")
        graphDropdown!!.selectionModel.selectFirst()

        size!!.textProperty().bind(sizeSlider!!.valueProperty().asString())
        delay!!.textProperty().bind(delaySlider!!.valueProperty().asString())
    }

    @FXML
    private fun sort() {
        Sort.applet!!.sort()
    }

    @FXML
    private fun shuffle() {
        Sort.applet!!.shuffleList()
        Sort.applet!!.redraw()
    }

    @FXML
    private fun selectAlgorithm() {
        Sort.applet!!.algorithmID = algorithmDropdown!!.selectionModel.selectedIndex
    }

    @FXML
    private fun selectGraphType() {
        Sort.applet!!.graphType = graphDropdown!!.selectionModel.selectedIndex
        Sort.applet!!.redraw()
    }

    @FXML
    private fun changeSize() {
        Sort.applet!!.len = sizeSlider!!.value.toInt()
    }

    @FXML
    private fun changeDelay() {
        Sort.applet!!.delay = delaySlider!!.value.toInt()
    }

    @FXML
    private fun quit() {
        Sort.applet!!.exit()
    }
}