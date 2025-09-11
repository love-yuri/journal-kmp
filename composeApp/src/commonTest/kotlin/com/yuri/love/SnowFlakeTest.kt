import com.yuri.love.utils.algorithm.SnowFlake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.system.measureTimeMillis
import kotlin.test.Test


class SnowFlakeTest {

    @Test
    fun main() = runBlocking {
        val set: MutableSet<Long> = ConcurrentSkipListSet()

        val threadCount = 400
        val idsPerThread = 10_000

        val timeTaken = measureTimeMillis {
            val jobs = List(threadCount) {
                launch(Dispatchers.Default) {
                    repeat(idsPerThread) {
                        val id = SnowFlake.nextId()
                        set.add(id)
                    }
                }
            }
            jobs.joinAll() // 等待所有协程完成
        }

        println("总 ID 数量: ${set.size}")
        println("耗时: $timeTaken ms")
    }
}
