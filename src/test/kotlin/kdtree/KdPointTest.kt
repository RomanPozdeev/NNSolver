package kdtree

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class KdPointTest {

    @Test
    fun testKdPointFromArray() {
        // Create a 5-dimensional point from an array of values.
        val point = KdPoint(1, 2, 3, 4, 5)
        assertThat(point.dimensions).isEqualTo(5)
    }

    @Test
    fun testKdPointFromList() {
        // Create a 5-dimensional point from a list of values.
        val values = listOf(1, 2, 3, 5, 5)
        val point = KdPoint(values)
        assertThat(point.dimensions).isEqualTo(5)
    }

    @Test
    fun testKdPointFromSourcePoint() {
        val values = listOf(1, 2, 3, 5, 5)
        val point = KdPoint(values)
        val result = KdPoint(point)
        assertThat(result.dimensions).isEqualTo(5)
    }

    @Test
    fun testToString() {
        val point = KdPoint(1, 2, 3, 4, 5)
        assertThat(point.toString()).isEqualTo("[1, 2, 3, 4, 5]")
    }

    @Test
    fun testGetDistanceSquared() {
        testDistanceSquaredOfPoints(KdPoint(0.0, 0.0), KdPoint(2.0, 2.0), 8.0)
        testDistanceSquaredOfPoints(KdPoint(2.0, 2.0), KdPoint(4.0, 4.0), 8.0)
        testDistanceSquaredOfPoints(KdPoint(0.0, 0.0), KdPoint(4.0, 4.0), 32.0)
        testDistanceSquaredOfPoints(KdPoint(4.0, -4.0), KdPoint(-4.0, 4.0), 128.0)
        testDistanceSquaredOfPoints(KdPoint(0.0, 0.0), KdPoint(-4.0, -4.0), 32.0)
    }

    private fun testDistanceSquaredOfPoints(
        originPoint: KdPoint<Double>,
        targetPoint: KdPoint<Double>,
        expectedDistance: Double
    ) {
        assertThat(expectedDistance).isEqualTo(originPoint.getDistanceSquared(targetPoint))
    }
}