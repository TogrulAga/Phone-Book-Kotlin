package phonebook

import java.io.File
import kotlin.math.sqrt


fun main() {
    val phoneBook = "C:\\Users\\eaghtog\\Downloads\\directory.txt"
    val names = "C:\\Users\\eaghtog\\Downloads\\find.txt"

    PhoneBook.readFiles(phoneBook, names)

    PhoneBook.search()
}


object PhoneBook {
    private lateinit var directory: MutableList<String>
    private lateinit var find: List<String>
    private val patternName = """\d+\s+([a-zA-Z (),'-]+)""".toRegex()

    fun search() {
        println("Start searching (linear search)...")

        val startLinear = System.currentTimeMillis()
        val foundNames = linearSearch()
        val endLinear = System.currentTimeMillis()
        println("Found ${foundNames.size} / ${find.size} entries. Time taken ${elapsedTime(elapsedMillis = endLinear - startLinear)}\n")

        println("Start searching (bubble sort + jump search)...")
        val startBubbleSort = System.currentTimeMillis()
        val sortedDirectory = bubbleSort(directory.toMutableList(), timeToBeat = endLinear - startLinear)
        val endBubbleSort = System.currentTimeMillis()

        if (sortedDirectory == null) {
            val startLinear2 = System.currentTimeMillis()
            val foundNames2 = linearSearch()
            val endLinear2 = System.currentTimeMillis()
            println("Found ${foundNames2.size} / ${find.size} entries. Time taken ${elapsedTime(elapsedMillis = endLinear2 - startBubbleSort)}")
            println("Sorting time: ${elapsedTime(elapsedMillis = endBubbleSort - startBubbleSort)} - STOPPED, moved to linear search")
            println("Searching time: ${elapsedTime(elapsedMillis = endLinear2 - startLinear2)}\n")
        } else {
            val startJumpSearch = System.currentTimeMillis()
            val foundNames21 = jumpSearch(sortedDirectory)
            val endJumpSearch = System.currentTimeMillis()

            println("Found ${foundNames21.size} / ${find.size} entries. Time taken ${elapsedTime(elapsedMillis = endJumpSearch - startBubbleSort)}")
            println("Sorting time: ${elapsedTime(elapsedMillis = endBubbleSort - startBubbleSort)}")
            println("Searching time: ${elapsedTime(elapsedMillis = endJumpSearch - startJumpSearch)}\n")
        }

        println("Start searching (quick sort + binary search)...")
        val startQuickSort = System.currentTimeMillis()
        val sortedDirectory2 = directory.toMutableList()
        quickSort(sortedDirectory2, 0, directory.size - 1)
        val endQuickSort = System.currentTimeMillis()

        val startBinarySearch = System.currentTimeMillis()
        val foundNames3 = binarySearch(sortedDirectory2)
        val endBinarySearch = System.currentTimeMillis()

        println("Found ${foundNames3.size} / ${find.size} entries. Time taken ${elapsedTime(elapsedMillis = endBinarySearch - startQuickSort)}")
        println("Sorting time: ${elapsedTime(elapsedMillis = endQuickSort - startQuickSort)}")
        println("Searching time: ${elapsedTime(elapsedMillis = endBinarySearch - startBinarySearch)}\n")

        println("Start searching (hash table)...")
        val startCreating = System.currentTimeMillis()
        val hashTable = createHashTable()
        val endCreating = System.currentTimeMillis()

        val startHashSearch = System.currentTimeMillis()
        val foundNames4 = hashSearch(hashTable)
        val endHashSearch = System.currentTimeMillis()

        println("Found ${foundNames4.size} / ${find.size} entries. Time taken ${elapsedTime(elapsedMillis = endHashSearch - startCreating)}")
        println("Creating time: ${elapsedTime(elapsedMillis = endCreating - startCreating)}")
        println("Searching time: ${elapsedTime(elapsedMillis = endHashSearch - startHashSearch)}\n")
    }

    private fun hashSearch(hashTable: HashMap<String, String>): MutableList<String> {
        val foundNames = mutableListOf<String>()

        for (name in find) {
            foundNames.add(hashTable[name]!!)
        }

        return foundNames
    }

    private fun createHashTable(): HashMap<String, String> {
        val hashMap = hashMapOf<String, String>()
        for (number in directory) {
            val key = patternName.find(number)?.groups?.get(1)?.value
            try {
                hashMap[key!!] = number
            } catch (e: java.lang.NullPointerException) {
                println(number)
            }
        }
        return hashMap
    }

    private fun linearSearch(): MutableList<String> {
        val foundNames = mutableListOf<String>()
        find.forEach {
            for (number in directory) {
                if (it in number) {
                    foundNames.add(number)
                    break
                }
            } }

        return foundNames
    }

    private fun jumpSearch(sortedDirectory: MutableList<String>): MutableList<String> {
        val foundNames = mutableListOf<String>()

        for (name in find) {
            foundNames.add(jumpSearchSingle(sortedDirectory, name))
        }

        return foundNames
    }

    private fun jumpSearchSingle(array: MutableList<String>, value: String): String {
        val step = kotlin.math.floor(sqrt(array.size.toDouble())).toInt()
        var curr = 1
        while (curr <= array.size) {
            var name = patternName.find(array[curr])?.groups?.get(1)?.value
            if (name == value){
                return array[curr]
            } else if (array[curr] > value) {
                var ind = curr - 1

                while (ind > curr - step && ind >= 1) {
                    name = patternName.find(array[ind])?.groups?.get(1)?.value
                    if (name == value) {
                        return array[ind]
                    }
                    ind -= 1
                }
                return "Not found"
            }

            curr += step
        }

        var ind = array.size - 1

        while (ind > curr - step) {
            val name = patternName.find(array[ind])?.groups?.get(1)?.value
            if (name == value) {
                return array[ind]
            }
            ind -= 1
        }
        return "Not found"
    }

    private fun bubbleSort(sortedDirectory: MutableList<String>, timeToBeat: Long): MutableList<String>? {
        val startTime = System.currentTimeMillis()

        for (i in 0 until sortedDirectory.size - 1) {
            for (j in 0 until sortedDirectory.size - i - 1) {
                val left = patternName.find(sortedDirectory[j])?.groups?.get(1)?.value
                val right = patternName.find(sortedDirectory[j + 1])?.groups?.get(1)?.value
                if (left.toString() > right.toString()) {
                    val temp = sortedDirectory[j]
                    sortedDirectory[j] = sortedDirectory[j + 1]
                    sortedDirectory[j + 1] = temp
                }
            }
            if ((System.currentTimeMillis() - startTime) > timeToBeat * 10) {
                return null
            }
        }

        return sortedDirectory
    }

    private fun binarySearch(directory: MutableList<String>): MutableList<String> {
        val foundNames = mutableListOf<String>()

        for (name in find) {
            foundNames.add(foundBinary(directory, name))
        }

        return foundNames
    }

    private fun foundBinary(array: MutableList<String>, value: String): String {
        var left = 0
        var right = array.size - 1

        while (left <= right) {
            val middle = left + (right - left) / 2
            val match = patternName.find(array[middle])?.groups?.get(1)?.value
            if (match == value) {
                return array[middle]
            } else if (match!! > value) {
                right = middle - 1
            } else {
                left = middle + 1
            }
        }

        return "not found"
    }

    private fun quickSort(directory: MutableList<String>, start: Int, end: Int) {
        if (start < end) {
            val pi = partition(directory, start, end)

            quickSort(directory, start, pi - 1)

            quickSort(directory, pi + 1, end)
        }
    }

    private fun partition(directory: MutableList<String>, start: Int, end: Int): Int {
        val pivot = directory[end]

        val right = patternName.find(pivot)?.groups?.get(1)?.value
        var i = start - 1

        for (j in start until end){
            val left = patternName.find(directory[j])?.groups?.get(1)?.value
            if (left.toString() < right.toString()){
                i++
                val temp = directory[j]
                directory[j] = directory[i]
                directory[i] = temp
            }
        }

        val temp = directory[end]
        directory[end] = directory[i + 1]
        directory[i + 1] = temp

        return i + 1
    }

    private fun elapsedTime(elapsedMillis: Long): String {
        val minutes = elapsedMillis / 60000
        val seconds = elapsedMillis % 60000 / 1000
        val millis = elapsedMillis % 60000 % 1000

        return "$minutes min. $seconds sec. $millis ms."
    }

    fun readFiles(phoneBook: String, names: String) {
        directory = File(phoneBook).readLines().distinct().toMutableList()
        find = File(names).readLines()
    }
}