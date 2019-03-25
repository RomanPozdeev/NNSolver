package kdtree

class KdNode<T>(
    val point: KdPoint<T>,
    val depth: Int,
    val axisIndex: Int
) where T : Number, T : Comparable<T> {

    var parentNode: KdNode<T>? = null
    var leftNode: KdNode<T>? = null
    var rightNode: KdNode<T>? = null

    fun hasParentNode(): Boolean {
        return parentNode != null
    }

    fun hasLeftNode(): Boolean {
        return leftNode != null
    }

    fun hasRightNode(): Boolean {
        return rightNode != null
    }

    fun hasChildren(): Boolean {
        return leftNode != null || rightNode != null
    }

    fun numberOfChildren(): Int {
        return (if (leftNode != null) 1 else 0) + if (rightNode != null) 1 else 0
    }
}