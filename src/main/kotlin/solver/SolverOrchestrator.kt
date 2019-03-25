package solver

import kdtree.KdPoint
import kdtree.KdTree
import java.util.*


class SolverOrchestrator<T>(
    private val tree: KdTree<T>,
    private val workerThreadsCount: Int =
        Runtime.getRuntime().availableProcessors()
)
        where T : Number, T : Comparable<T> {

    /**
     *
     * @param inputPoints
     * @return
     * @throws SolverInterruptedException
     * if the calling thread was interrupted during the async operation.
     */
    fun findNearestPoints(inputPoints: List<KdPoint<T>>): List<KdPoint<T>> {
        val workers = ArrayList<SolverWorker<T>>(workerThreadsCount)

        val batchSize = inputPoints.size / workerThreadsCount

        var startIndex = 0

        for (i in 0 until workerThreadsCount) {
            val endIndex = if (i < workerThreadsCount - 1) {
                Math.min(startIndex + batchSize, inputPoints.size)
            } else {
                inputPoints.size
            }

            val subset = inputPoints.subList(startIndex, endIndex)

            startIndex += batchSize

            val worker = SolverWorker(tree, subset)

            workers.add(worker)

            worker.start()
        }

        // Join the individual workers and append their data.

        val resultPoints = ArrayList<KdPoint<T>>(inputPoints.size)

        for (worker in workers) {
            try {
                resultPoints.addAll(worker.getResultPoints())
            } catch (e: InterruptedException) {
                // Somebody decided to interrupt this orchestrator call.
                throw SolverInterruptedException(e)
            }

        }

        return resultPoints
    }
}