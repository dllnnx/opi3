import entity.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultTest {
    private static Result result;

    @BeforeAll
    static void initAll() {
        result = new Result();
    }

    @Test
    public void testIsInRectangleTrue() {
        assertTrue(result.checkHit(2, 2, 3),
                "Point (2, 2) should be inside the rectangle if radius equals 3");
    }

    @Test
    public void testIsInCircleTrue() {
        assertTrue(result.checkHit(-1, 1, 5),
                "Point (-1, 1) should be inside the circle if radius equals 5");
    }

    @Test
    public void testIsInTriangleTrue() {
        assertTrue(result.checkHit(1, -1, 3),
                "Point (1, -1) should be inside the triangle if radius equals 3");
    }

    @Test
    public void testIsInRectangleFalse() {
        assertFalse(result.checkHit(3, 3, 2),
                "Point (3, 3) should be outside the rectangle if radius equals 2");
    }

    @Test
    public void testIsInCircleFalse() {
        assertFalse(result.checkHit(-2, 2, 2),
                "Point (-2, 2) should be outside the circle if radius equals 2");
    }

    @Test
    public void testIsInTriangleFalse() {
        assertFalse(result.checkHit(2, -2, 3),
                "Point (2, -2) should be outside the triangle if radius equals 3");
    }
}
