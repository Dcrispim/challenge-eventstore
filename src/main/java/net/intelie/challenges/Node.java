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
public class Node {
    private Event event = null;
    private Node next=null, previous=null;

    public Node() {
        
    };
    public Node(Event event) {
        this.event=event;
    }

    
    
    public synchronized  Event getEvent() {
        return event;
    }

    public synchronized  void setEvent(Event event) {
        this.event = event;
    }

    public synchronized  Node getNext() {
        return next;
    }

    public synchronized  void setNext(Node next) {
        this.next = next;
    }

    public synchronized  Node getPrevious() {
        return previous;
    }

    public synchronized  void setPrevious(Node previous) {
        this.previous = previous;
    }



            
}
