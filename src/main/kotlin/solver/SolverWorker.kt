package solver

import kdtree.KdPoint
import kdtree.KdTree
import java.util.*


class SolverWorker<T>(tree: KdTree<T>, inputPoints: List<KdPoint<T>>) where T : Number, T : Comparable<T> {
    private val thread: Thread
    private val solver = Solver(tree)

    private val resultPoints: MutableList<KdPoint<T>> = ArrayList(inputPoints.size)

    init {
        this.thread = Thread {
            for (point in inputPoints) {
                resultPoints.add(solver.findNearestPoint(point))
            }
        }
    }

    fun start() {
        this.thread.start()
    }

    /**
     * Waits for this worker thread to finish, then returns the list of result
     * points to the caller. The index of each result point corresponds to the index
     * of the input points when constructing this worker.
     */
    fun getResultPoints(): List<KdPoint<T>> {
        this.thread.join()
        return resultPoints
    }
}