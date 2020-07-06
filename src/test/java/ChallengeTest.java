
import java.util.logging.Level;
import java.util.logging.Logger;
import net.intelie.challenges.ChallengeStore;
import net.intelie.challenges.Event;
import net.intelie.challenges.EventIterator;
import net.intelie.challenges.Node;
import org.junit.Assert;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author diego
 */
public class ChallengeTest {

    @Test
    public void testToCriateOrderedList() {
        long[] timestemps = {8, 1, 5, 7, 2, 4, 3, 6, 0, 10, 9};
        ChallengeStore store = new ChallengeStore();
        int i = 0;
        do {
            store.insert(new Event("data", timestemps[i]));
            i++;
        } while (i < 11);

        Assert.assertTrue("The store doesn't begins with lower timestemp", store.getHead().getEvent().timestamp() == 0);
        Assert.assertTrue("The store doesn't begins with higher timestemp", store.getHead().getPrevious().getEvent().timestamp() == 10);

    }

    @Test
    public void testToFindEvensWithQuery() {

        ChallengeStore store = new ChallengeStore();
        int i = 0;
        do {
            String type;
            if (i % 2 == 0) {
                type = "data";
            } else {
                type = "data1";
            }
            store.insert(new Event(type, i));
            i++;
        } while (i < 13);

        EventIterator query1 = store.query("data1", 2, 9);
        query1.moveNext();
        Assert.assertTrue("The query response is wrong:\n current event timestemp should be 3, but is " + query1.current().timestamp(),
                query1.current().timestamp() == 3);

        EventIterator query2 = store.query("data", 0, 10);
        query2.moveNext();
        query2.moveNext();

        Assert.assertTrue("The query with 1 move step response is wrong:\n current event timestemp should be 2, but is " + query2.current().timestamp(),
                query2.current().timestamp() == 2);

        EventIterator query3 = store.query("data", 10, 11);
        query3.moveNext();
        Assert.assertTrue("The query with 1 element response is wrong:\n current event timestemp should be 10, but is " + query3.current().timestamp(),
                query3.current().timestamp() == 10);

        Assert.assertFalse("The query with 1 element and 1 move step response is wrong:\n current event should be null",
                query3.moveNext());

    }

    @Test
    public void testToRemoveEvensByType() {

        ChallengeStore store = new ChallengeStore();
        int i = 0;
        do {
            String type;
            if (i % 2 == 0) {
                type = "data";
            } else {
                type = "data1";
            }
            store.insert(new Event(type, i));
            i++;
        } while (i < 11);

        store.removeAll("data");
        Assert.assertTrue("The first timestemp event should be 1, but is " + store.getHead().getEvent().timestamp(),
                store.getHead().getEvent().timestamp() == 1);

        Assert.assertTrue("The first timestemp event should be 9, but is " + store.getHead().getPrevious().getEvent().timestamp(),
                store.getHead().getPrevious().getEvent().timestamp() == 9);

        String times = "";
        Node _aux = store.getHead();
        while (_aux.getNext() != store.getHead()) {
            times = times + _aux.getEvent().timestamp();
            _aux = _aux.getNext();
        }
        times = times + _aux.getEvent().timestamp();
        Assert.assertEquals("13579", times);

    }

    @Test
    public void testThreadOprations() throws InterruptedException {
        ChallengeStore store = new ChallengeStore();
        long limit = 10000;
        TestThreadInsert Insert = new TestThreadInsert(store, "type1", limit);
        TestThreadInsert Insert2 = new TestThreadInsert(store, "type2", limit);
        TestThreadInsert Insert3 = new TestThreadInsert(store, "type3", limit);
        Insert.run();
        Insert2.run();
        Insert3.run();

        TestThreadRemove Remove = new TestThreadRemove(store, "type2");
        Remove.run();

        Insert.join();
        Insert2.join();
        Insert3.run();
        Remove.join();
        
        EventIterator query = store.query("type1", 0, limit);
        String times = "";
        for (int i = 0; i <= limit; i++) {
            times = times + ",type1-" + i;
        }
        String queryTime = "";
        int j = 0;
        while (query.moveNext()) {
            j++;
            queryTime = queryTime+","+query.current().type()+"-"+query.current().timestamp();
        }
        
        Assert.assertEquals(times, queryTime);

    }

}

class TestThreadInsert extends Thread {

    ChallengeStore store = null;
    String name = "";
    long limit = 10;

    public TestThreadInsert(ChallengeStore store, String name, long limit) {
        this.store = store;
        this.name = name;
        this.limit = limit;
    }

    public void run() {

        for (long i = 0; i <= limit; i++) {
            store.insert(new Event(name, i));
        }

    }
}

class TestThreadRemove extends Thread {

    ChallengeStore store = null;
    String name = "";

    public TestThreadRemove(ChallengeStore store, String name) {
        this.store = store;
        this.name = name;

    }

    public void run() {

        store.removeAll(name);

    }
}

class TestThreadQuery extends Thread {

    ChallengeStore store = null;
    String name = "";
    long limit = 10;

    public TestThreadQuery(ChallengeStore store, String name, long limit) {
        this.store = store;
        this.name = name;
        this.limit = limit;
    }

    public void run() {
        EventIterator query = store.query(name, 0, limit);
        System.out.println(query.current().type() + " " + query.current().timestamp());
        while (query.moveNext()) {
            System.out.println(query.current().type() + " " + query.current().timestamp());
        }

    }
}
