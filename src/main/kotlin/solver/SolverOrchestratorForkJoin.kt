package solver

import kdtree.KdPoint
import kdtree.KdTree
import java.util.concurrent.ForkJoinPool


class SolverOrchestratorForkJoin<T>(
    private val tree: KdTree<T>,
    private val pool: ForkJoinPool = ForkJoinPool.commonPool()
)
        where T : Number, T : Comparable<T> {

    fun findNearestPoints(inputPoints: List<KdPoint<T>>): List<KdPoint<T>> {
        return pool.invoke(SolverWorkerRecursive(tree, inputPoints))
    }
}