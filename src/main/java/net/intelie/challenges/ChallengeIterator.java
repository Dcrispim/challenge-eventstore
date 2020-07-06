/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.intelie.challenges;

/**
 *
 * @author diego
 */
public class ChallengeIterator implements EventIterator {

    private Node headNode = null;
    private Node currentNode = null;
    private boolean end = false;

    ChallengeIterator(ChallengeStore store) {
        this.headNode = store.getHead();

    }

    @Override
    public boolean moveNext() {
        if(end){
            throw new IllegalStateException("Itarator alredy ended");
        }
        if (currentNode == null) {
            currentNode = headNode;
            return true;
        } else if (headNode == null || currentNode.getNext() == headNode) {
            end=true;
            return false;
        } else {
            currentNode = currentNode.getNext();

            return true;
        }

    }

    @Override
    public Event current() {
        if (currentNode == null) {
            throw new IllegalStateException("moveNext() was never called");
        }
        return currentNode.getEvent();
    }

    @Override
    public void remove() {
        if (currentNode == null) {
            throw new IllegalStateException("moveNext() was never called");
        }
        if (end) {
            throw new IllegalStateException("last event of Iterator");
        }
        
        Node _auxPrev = currentNode.getPrevious();
        Node _auxNext = currentNode.getNext();
        _auxPrev.setNext(_auxNext);
        _auxNext.setPrevious(_auxPrev);
        currentNode = _auxPrev;

    }

    @Override
    public void close() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
