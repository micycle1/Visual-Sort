package app

import app.javafx.JavaFxApp
import javafx.application.Application
import javafx.scene.canvas.Canvas
import javafx.scene.text.Text
import processing.core.PApplet
import processing.core.PSurface
import processing.javafx.PSurfaceFX
import java.util.*


class Sort : PApplet() {

    companion object {
        internal var applet: Sort? = null
    }

    internal var len = 275
    internal var delay = 1 // in ms
    internal var algorithmID = 0
    internal var graphType = 0
    internal var sorting = false

    private var off = 0
    private var comparisons = 0
    private var comparisonsLabel: Text? = null
    private var arrayAccesses = 0
    private var aAccessesLabel: Text? = null

    private var current = -1
    private var check = -1

    private var list: MutableList<Int> = IntRange(1, len).step(1).toList().toMutableList()

    private val algorithm = SortingAlgorithms()

    override fun initSurface(): PSurface {
        g = createPrimaryGraphics()
        val genericSurface = g.createSurface()
        val fxSurface = genericSurface as PSurfaceFX
        fxSurface.sketch = this
        app.javafx.JavaFxApp.surface = fxSurface // todo remove?
        app.javafx.Controller.surface = fxSurface

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
        applet = this
        size(0, 0, FX2D)
    }

    override fun setup() {
        val canvas = surface.native as Canvas
        comparisonsLabel = canvas.scene.lookup("#comparisons") as Text
        aAccessesLabel = canvas.scene.lookup("#arrayAccesses") as Text
        frameRate(60f)
        colorMode(HSB, 360f, 1f, 1f)
        shuffleList()
        noLoop()
    }

    override fun draw() {
        background(0)
        comparisonsLabel!!.textProperty().set(comparisons.toString())
        aAccessesLabel!!.textProperty().set(arrayAccesses.toString())
        val w = width / len.toFloat()

        for ((i, n) in list.withIndex()) {
            fill(list[i].toFloat() / len * 360, 1f, 1f)
            if (current > -1 && i == current) {
                fill(120f, 1f, 0.5f) // g
            }
            if (check > -1 && i == check) {
                fill(0f, 1f, 1f) // r
            }

            if (graphType == 0) {
                rect(i * w, height.toFloat(), w, -(n.toFloat() / len) * height)
            } else {
                ellipse(i * w, height -(n.toFloat() / len) * height, 5f, 5f)
            }
        }
    }

    fun shuffleList() {
        list = IntRange(1, len).step(1).toList().toMutableList()
        list.shuffle()
    }

    fun delay() {
        delay(delay)
    }

    fun sort() {
        if (!sorting) {
            sorting = true
            thread("sorting")
        }
    }

    /**
     * Public for processing threading
     */
    @Suppress("unused", "RedundantVisibilityModifier")
    public fun sorting() {
        comparisons = 0
        arrayAccesses = 0
        loop()
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
        sorting = false
        current = -1
        check = -1
        off = 0
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
                    comparisons++
                    arrayAccesses += 2
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
                    comparisons++
                    arrayAccesses += 2
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
                    comparisons++
                    arrayAccesses += 2
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
                    comparisons++
                    arrayAccesses += 2

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
                if (off == 1) {
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
                    comparisons++
                    arrayAccesses += 2

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
                delay()
            }
        }

        private fun heapify(n: Int) {
            var start = iParent(n - 1)
            while (start >= 0 && sorting) {
                siftDown(start, n - 1)
                start--
                delay()
            }
        }

        private fun siftDown(start: Int, end: Int) {
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
                comparisons += 3
                arrayAccesses += 4
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
            arrayAccesses++
            var i = lo - 1
            for (j in lo until hi) {
                check = j
                if (!sorting)
                    break
                if (list[j] < pivot) {
                    i++
                    swap(i, j)
                }
                comparisons++
                arrayAccesses++
                delay()
            }
            swap(i + 1, hi)

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
                arrayAccesses++
            }
            for (j in 0 until n2) {
                R[j] = list[m + 1 + j]
                arrayAccesses++
            }
            var i = 0
            var j = 0

            var k = l
            while (i < n1 && j < n2 && sorting) {
                check = k
                if (L[i] <= R[j]) {
                    list[k] = L[i]
                    arrayAccesses++
                    i++
                } else {
                    list[k] = R[j]
                    arrayAccesses++
                    j++
                }
                comparisons++
                delay()
                k++
            }

            while (i < n1 && sorting) {
                list[k] = L[i]
                arrayAccesses++
                i++
                k++

                delay()
            }

            while (j < n2 && sorting) {
                list[k] = R[j]
                arrayAccesses++
                j++
                k++
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
                    arrayAccesses++
                    i++
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
                delay()
                exp *= 10
            }
        }

        private fun countSort(n: Int, exp: Int) {
            val output = IntArray(n)
            var i: Int = 0
            val count = IntArray(10)
            Arrays.fill(count, 0)

            while (i < n) {
                count[list[i] / exp % 10]++
                arrayAccesses++
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
                arrayAccesses++
                count[list[i] / exp % 10]--
                arrayAccesses++
                i--
            }
            i = 0
            while (i < n) {
                if (!sorting)
                    break
                check = i
                list[i] = output[i]
                arrayAccesses++
                delay()
                i++
            }
        }

        private fun getMax(n: Int): Int {
            var mx = list[0]
            for (i in 1 until n) {
                if (list[i] > mx)
                    mx = list[i]
                comparisons++
                arrayAccesses++
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
                    val rand = Random().nextInt(len)
                    check = rand
                    val temp = list[i]
                    arrayAccesses++
                    list[i] = list[rand]
                    arrayAccesses += 2
                    list[rand] = temp
                    arrayAccesses++
                    delay()
                }
            }
        }

        private fun swap(i1: Int, i2: Int) {
            val temp = list[i1]
            arrayAccesses++
            list[i1] = list[i2]
            arrayAccesses += 2
            list[i2] = temp
            arrayAccesses++
        }

        private fun checkSorted(): Boolean {
            for (i in 0 until len - 1) {
                if (list[i] > list[i + 1]) {
                    arrayAccesses += 2
                    return false
                }
            }
            return true
        }
    }
}

