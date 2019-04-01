package solver

import kdtree.KdPoint
import kdtree.KdTree

class SolverCoroutineOrchestrator<T>(
    private val tree: KdTree<T>,
    private val courutineCount: Int =
        Runtime.getRuntime().availableProcessors()
)
        where T : Number, T : Comparable<T> {

    suspend fun findNearestPoints(inputPoints: List<KdPoint<T>>): List<KdPoint<T>> {
        val workers = ArrayList<SolverCoroutineWorker<T>>(courutineCount)

        val batchSize = inputPoints.size / courutineCount

        var startIndex = 0

        for (i in 0 until courutineCount) {
            val endIndex = if (i < courutineCount - 1) {
                Math.min(startIndex + batchSize, inputPoints.size)
            } else {
                inputPoints.size
            }

            val subset = inputPoints.subList(startIndex, endIndex)

            startIndex += batchSize
            val worker = SolverCoroutineWorker(tree, subset)
            workers.add(worker)
        }

        return workers
            .map { it.getResultPointsAsync() }
            .asSequence()
            .fold(ArrayList(inputPoints.size)) { acc, deferred ->
                acc.addAll(deferred.await())
                acc
            }
    }
}