package randomizedtest;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> expected = new AListNoResizing<>();
        BuggyAList<Integer> test = new BuggyAList<>();

        test.addLast(1);
        test.addLast(2);
        test.addLast(3);
        expected.addLast(1);
        expected.addLast(2);
        expected.addLast(3);
        assertEquals(expected.size(), test.size());
        assertEquals(expected.removeLast(), test.removeLast());
        assertEquals(expected.removeLast(), test.removeLast());
        assertEquals(expected.removeLast(), test.removeLast());
    }

    @Test
    public void test2() {
        AListNoResizing<Integer> expected = new AListNoResizing<>();
        BuggyAList<Integer> test = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                expected.addLast(randVal);
                test.addLast(randVal);
                assertEquals(expected.size(), test.size());
            } else if (operationNumber == 1) {
                // size
                if (expected.size() == 0) {
                    continue;
                }
                Integer i1 = expected.removeLast();
                Integer i2 = test.removeLast();
                assertEquals(i1, i2);
                assertEquals(expected.size(), test.size());
            }
        }
    }
}
