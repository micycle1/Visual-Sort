package App

import App.javafx.Controller
import App.javafx.JavaFxApp
import javafx.application.Application
import processing.core.PApplet
import processing.core.PSurface
import processing.javafx.PSurfaceFX
import java.util.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.IntegerProperty
import javafx.scene.text.Text
import javafx.scene.canvas.Canvas


class Sort : PApplet() {

    private var len = 100
    private var off = 0
    internal var algorithmID = 0
    private var compare = 0
    private var acc = 0

    private val comparisons = SimpleIntegerProperty(this, "comparisons")
    fun comparisonsOut(): IntegerProperty {
        return comparisons
    }
    private val arrayAccessesOut = SimpleIntegerProperty(this, "comparisons")
    fun arrayAccesses(): IntegerProperty {
        return arrayAccessesOut
    }

    //GRAPH VARIABLES
    private var SIZE = 800 // width?
    private var current = -1
    private var check = -1
    private var WIDTH = SIZE / len // width of bars
    internal var type = 0

    //ARRAYS
    private var list: IntArray = intArrayOf(SIZE) // todo
    private var list2: MutableList<Int> = arrayListOf(type)

    internal var sorting = false
    private var shuffled = true
    private val algorithm = SortingAlgorithms()
    var r = Random()

    private var label1: Text? = null

    override fun initSurface(): PSurface {
        Controller.applet = this
        g = createPrimaryGraphics()
        val genericSurface = g.createSurface()
        val fxSurface = genericSurface as PSurfaceFX
        fxSurface.sketch = this
        App.javafx.JavaFxApp.surface = fxSurface // todo remove?
        App.javafx.Controller.surface = fxSurface

        Thread { Application.launch(JavaFxApp::class.java) }.start()

        while (fxSurface.stage == null) {
            try {
                Thread.sleep(5)
            } catch (e: InterruptedException) {
            }
        }

        this.surface = fxSurface
        return surface
    }

    override fun settings() {
        size(0, 0, FX2D)
    }

    override fun setup() {
        val canvas = surface.native as Canvas
        label1 = canvas.scene.lookup("#arrayAccesses") as Text

        frameRate(100f)
        createList()
        shuffleList()
        noLoop()
    }

    override fun draw() {
        background(0)

        for (i in 0 until len) {
            val barHeight = list[i] * WIDTH

            fill(255)
            if (current > -1 && i == current) {
                fill(0f, 255f, 0f)
            }
            if (check > -1 && i == check) {
                fill(255f, 0f, 0f)
            }

            if (type == 0) {
                rect(i * WIDTH.toFloat(), SIZE - barHeight.toFloat(), WIDTH.toFloat(), barHeight.toFloat())
//                kotlin.io.println(SIZE - barHeight.toFloat())
            } else {
                ellipse(i * WIDTH.toFloat(), SIZE - barHeight.toFloat(), 5f, 5f)
            }
        }
    }

    fun Update() {
        WIDTH = SIZE / len
    }

    private fun createList() {
        list = IntRange(0, len).step(1).toList().toIntArray()    //CREATES A LIST EQUAL TO THE LENGTH
    }

    fun shuffleList() { // todo change
        createList()
        for (a in 0..499) {    //SHUFFLE RUNS 500 TIMES
            for (i in 0 until len) {    //ACCESS EACH ELEMENT OF THE LIST
                val rand = r.nextInt(len)    //PICK A RANDOM NUM FROM 0-LEN
                val temp = list[i]            //SETS TEMP INT TO CURRENT ELEMENT
                list[i] = list[rand]        //SWAPS THE CURRENT INDEX WITH RANDOM INDEX
                list[rand] = temp            //SETS THE RANDOM INDEX TO THE TEMP
            }
        }
        sorting = false
        shuffled = true
        list2.shuffle() //todo
    }

    //RESET SOME VARIABLES
    fun reset() {
        sorting = false
        current = -1
        check = -1
        off = 0
        Update()
    }

    fun delay() {
        delay(1) //todo framerate/1
    }

    fun sort() {
        sorting = true
        thread("sorting")
    }

    /**
     * Public for processing threading
     */
    @Suppress("unused", "RedundantVisibilityModifier")
    public fun sorting() {
        if (sorting) {
            loop()
            try {
                when (algorithmID) {
                    0 -> algorithm.selectionSort()
                    1 -> algorithm.bubbleSort()
                    2 -> algorithm.cocktailSort()
                    3 -> algorithm.oddEvenSort()
                    4 -> algorithm.insertionSort(0, len - 1)
                    5 -> algorithm.timSort(len)
                    6 -> algorithm.quickSort(0, len - 1)
                    7 -> algorithm.heapSort()
                    8 -> algorithm.mergeSort(0, len - 1)
                    9 -> algorithm.pigeonholeSort()
                    10 -> algorithm.radixSort(len)
                    else -> algorithm.bogoSort()
                }
            } catch (e: IndexOutOfBoundsException) {
            }
        }
        reset()    //RESET (sorting = false)
        noLoop()
        redraw()
    }


    internal inner class SortingAlgorithms {

        fun selectionSort() {
            var c = 0
            while (c < len && sorting) {
                var sm = c
                current = c
                for (i in c + 1 until len) {
                    if (!sorting)
                        break
                    if (list[i] < list[sm]) {
                        sm = i
                    }
                    check = i
                    compare++
                    comparisons.value = comparisons.value + 1
                    acc += 2
                    arrayAccessesOut.value = arrayAccessesOut.value + 2
                    Update()
                    delay()
                }
                if (c != sm)
                    swap(c, sm)
                c++
            }
        }

        fun insertionSort(start: Int, end: Int) {
            for (i in start + 1..end) {
                current = i
                var j = i
                while (list[j] < list[j - 1] && sorting) {
                    swap(j, j - 1)
                    check = j
                    compare++
                    acc += 2
                    Update()
                    delay()
                    if (j > start + 1)
                        j--
                }
            }
        }

        fun bubbleSort() {
            var c = 1
            var noswaps = false
            while (!noswaps && sorting) {
                current = len - c
                noswaps = true
                for (i in 0 until len - c) {
                    if (!sorting)
                        break
                    if (list[i + 1] < list[i]) {
                        noswaps = false
                        swap(i, i + 1)
                    }
                    check = i + 1
                    compare++
                    acc += 2
                    Update()
                    delay()
                }
                c++
            }
        }

        fun oddEvenSort() {
            var c = 0
            var noswaps = false
            while (!noswaps && sorting) {
                noswaps = true
                var i = 0 + off
                while (i < len - 1) {
                    if (!sorting)
                        break
                    if (list[i + 1] < list[i]) {
                        noswaps = false
                        swap(i, i + 1)
                    }
                    current = i
                    check = i + 1
                    compare++
                    acc += 2
                    Update()
                    delay()
                    i += 2
                }
                off = 1 - off
                c++
            }
        }

        fun cocktailSort() {
            var noswaps = false
            var c = 0
            while (!noswaps && sorting) {
                noswaps = true
                var i: Int
                val target: Int
                val inc: Int
                if (off === 1) {
                    i = len - 2 - c
                    target = c - 1
                    inc = -1
                } else {
                    i = c
                    target = len - 2 - c
                    inc = 1
                }
                current = target + 1
                while (i != target && sorting) {
                    if (list[i] > list[i + 1]) {
                        noswaps = false
                        swap(i, i + 1)
                    }
                    check = i + 1 - off
                    compare++
                    acc += 2
                    Update()
                    delay()
                    i += inc
                }
                if (off == 1)
                    c++
                off = 1 - off
            }
        }

        fun heapSort() {
            heapify(len)
            var end = len - 1
            while (end > 0 && sorting) {
                current = end
                swap(end, 0)
                end--
                siftDown(0, end)
                Update()
                delay()
            }
        }

        fun heapify(n: Int) {
            var start = iParent(n - 1)
            while (start >= 0 && sorting) {
                siftDown(start, n - 1)
                start--
                Update()
                delay()
            }
        }

        fun siftDown(start: Int, end: Int) {
            var root = start
            while (iLeftChild(root) <= end && sorting) {
                val child = iLeftChild(root)
                var swap = root
                check = root
                if (list[swap] < list[child]) {
                    swap = child
                }
                if (child + 1 <= end && list[swap] < list[child + 1]) {
                    swap = child + 1
                }
                if (swap == root) {
                    return
                } else {
                    swap(root, swap)
                    check = root
                    root = swap
                }
                compare += 3
                acc += 4
                Update()
                delay()
            }
        }

        private fun iParent(i: Int): Int {
            return (i - 1) / 2
        }

        private fun iLeftChild(i: Int): Int {
            return 2 * i + 1
        }

        fun quickSort(lo: Int, hi: Int) {
            if (!sorting)
                return
            current = hi
            if (lo < hi) {
                val p = partition(lo, hi)
                quickSort(lo, p - 1)
                quickSort(p + 1, hi)
            }
        }

        private fun partition(lo: Int, hi: Int): Int {
            val pivot = list[hi]
            acc++
            var i = lo - 1
            for (j in lo until hi) {
                check = j
                if (!sorting)
                    break
                if (list[j] < pivot) {
                    i++
                    swap(i, j)
                }
                compare++
                acc++
                Update()
                delay()
            }
            swap(i + 1, hi)
            Update()
            delay()
            return i + 1
        }

        private fun merge(l: Int, m: Int, r: Int) {
            val n1 = m - l + 1
            val n2 = r - m

            val L = IntArray(n1)
            val R = IntArray(n2)

            for (i in 0 until n1) {
                L[i] = list[l + i]
                acc++
            }
            for (j in 0 until n2) {
                R[j] = list[m + 1 + j]
                acc++
            }
            var i = 0
            var j = 0

            var k = l
            while (i < n1 && j < n2 && sorting) {
                check = k
                if (L[i] <= R[j]) {
                    list[k] = L[i]
                    acc++
                    i++
                } else {
                    list[k] = R[j]
                    acc++
                    j++
                }
                compare++
                Update()
                delay()
                k++
            }

            while (i < n1 && sorting) {
                list[k] = L[i]
                acc++
                i++
                k++
                Update()
                delay()
            }

            while (j < n2 && sorting) {
                list[k] = R[j]
                acc++
                j++
                k++
                Update()
                delay()
            }
        }

        fun mergeSort(l: Int, r: Int) {
            if (l < r) {
                val m = (l + r) / 2
                current = r
                mergeSort(l, m)
                mergeSort(m + 1, r)

                merge(l, m, r)
            }
        }

        fun pigeonholeSort() {
            val mi = 0
            val size = len - mi + 1
            val holes = IntArray(size)
            for (x in list) {
                holes[x - mi] += 1
            }
            var i = 0
            for (count in 0 until size) {
                while (holes[count] > 0 && sorting) {
                    holes[count]--
                    check = i
                    list[i] = count + mi
                    acc++
                    i++
                    Update()
                    delay()
                }
            }
        }

        fun radixSort(n: Int) {
            val m = getMax(n)
            var exp = 1
            while (m / exp > 0) {
                if (!sorting)
                    break
                countSort(n, exp)
                Update()
                delay()
                exp *= 10
            }
        }

        private fun countSort(n: Int, exp: Int) {
            val output = IntArray(n)
            var i: Int
            val count = IntArray(10)
            Arrays.fill(count, 0)

            i = 0
            while (i < n) {
                count[list[i] / exp % 10]++
                acc++
                i++
            }

            i = 1
            while (i < 10) {
                count[i] += count[i - 1]
                i++
            }

            i = n - 1
            while (i >= 0) {
                output[count[list[i] / exp % 10] - 1] = list[i]
                acc++
                count[list[i] / exp % 10]--
                acc++
                i--
            }
            i = 0
            while (i < n) {
                if (!sorting)
                    break
                check = i
                list[i] = output[i]
                acc++
                Update()
                delay()
                i++
            }
        }

        private fun getMax(n: Int): Int {
            var mx = list[0]
            for (i in 1 until n) {
                if (list[i] > mx)
                    mx = list[i]
                compare++
                acc++
            }
            return mx
        }

        fun timSort(n: Int) {
            var RUN = 64
            if (len > 64) {
                while (len.toDouble() / RUN % 2 != 0.0)
                    RUN--
            }
            var i = 0
            while (i < n) {
                insertionSort(i, Math.min(i + RUN - 1, n - 1))
                i += RUN
            }

            var size = RUN
            while (size < n) {
                var left = 0
                while (left < n) {
                    val mid = left + size - 1
                    val right = Math.min(left + 2 * size - 1, n - 1)

                    merge(left, mid, right)
                    left += 2 * size
                }
                if (!sorting)
                    break
                size *= 2
            }
        }

        fun bogoSort() {
            while (!checkSorted() && sorting) {
                for (i in 0 until len) {
                    if (!sorting)
                        break
                    current = i
                    val rand = r.nextInt(len)
                    check = rand
                    val temp = list[i]
                    acc++
                    list[i] = list[rand]
                    acc += 2
                    list[rand] = temp
                    acc++
                    Update()
                    delay()
                }
            }
        }

        private fun swap(i1: Int, i2: Int) {
            val temp = list[i1]
            acc++
            list[i1] = list[i2]
            acc += 2
            list[i2] = temp
            acc++
        }

        private fun checkSorted(): Boolean {
            for (i in 0 until len - 1) {
                if (list[i] > list[i + 1]) {
                    acc += 2
                    return false
                }
            }
            return true
        }
    }
}

