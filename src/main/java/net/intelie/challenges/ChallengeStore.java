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
public class ChallengeStore implements EventStore {

    private Node head = null;
    

    public synchronized Node getHead() {
        return head;
    }

    public ChallengeStore() {
    }

    private long delta(long a, long b) {
        long diff = a - b;
        if (diff < 0) {
            diff = diff * -1;
        }

        return diff;

    }

    @Override
    public synchronized void insert(Event event) {

        Node newNode = new Node(event);
        long newTimestemp = event.timestamp();
        if (head == null) {
            head = newNode;
            head.setNext(head);
            head.setPrevious(head);
        } else {

            if (newTimestemp <= head.getEvent().timestamp()) {
                Node _aux = head;
                Node _prevAux = _aux.getPrevious();

                _prevAux.setNext(newNode);
                newNode.setPrevious(_prevAux);
                newNode.setNext(_aux);
                _aux.setPrevious(newNode);

                head = newNode;

            } else if (newTimestemp > head.getPrevious().getEvent().timestamp()) {
                Node _aux = head;
                newNode.setPrevious(_aux.getPrevious());
                _aux.getPrevious().setNext(newNode);
                newNode.setNext(_aux);

                _aux.setPrevious(newNode);

            } else if (delta(newTimestemp, head.getEvent().timestamp()) <= delta(newTimestemp, head.getPrevious().getEvent().timestamp())) {

                Node _aux = head;

                while ((_aux.getNext() != head && newTimestemp > _aux.getNext().getEvent().timestamp())) {

                    _aux = _aux.getNext();
                }

                _aux.getNext().setPrevious(newNode);
                newNode.setNext(_aux.getNext());

                _aux.setNext(newNode);
                newNode.setPrevious(_aux);

            } else if (delta(newTimestemp, head.getEvent().timestamp()) > delta(newTimestemp, head.getPrevious().getEvent().timestamp())) {
                Node _aux = head;

                while (_aux.getPrevious() != head
                        && (newTimestemp < _aux.getPrevious().getEvent().timestamp())) {

                    _aux = _aux.getPrevious();
                }

                _aux.getPrevious().setNext(newNode);
                newNode.setPrevious(_aux.getPrevious());

                _aux.setPrevious(newNode);
                newNode.setNext(_aux);

            }
        }

    }

    @Override
    public synchronized void removeAll(String type) {
        Node _aux = head;
        if (head != null) {
            while (_aux.getNext() != head) {

                if (_aux.getEvent().type().equals(type)) {
                    Node _prev = _aux.getPrevious(), _next = _aux.getNext();

                    if (_aux == head) {
                        head = _next;
                        head.setPrevious(_prev);
                        _prev.setNext(head);
                        _aux = head;

                    } else {
                        _prev.setNext(_next);
                        _next.setPrevious(_prev);

                    }
                }

                _aux = _aux.getNext();
            }
            if (_aux.getEvent().type().equals(type)) {
                Node _prev = _aux.getPrevious(), _next = _aux.getNext();
                _prev.setNext(_next);
                _next.setPrevious(_prev);
            }
        }
    }

    @Override
    public synchronized EventIterator query(String type, long startTime, long endTime) {

        ChallengeStore _auxList = new ChallengeStore();

        if (head.getEvent().timestamp() == startTime && head.getPrevious().getEvent().timestamp() == endTime) {
            Node _aux = head;
            while (_aux.getNext() != head) {
                if (_aux.getEvent().type().equals(type)) {
                    _auxList.insert(_aux.getEvent());

                }
                _aux = _aux.getNext();
            }
            if (_aux.getEvent().type().equals(type)) {
                _auxList.insert(_aux.getEvent());

            }

        } else {
            Node _aux = head;
            if (delta(head.getEvent().timestamp(), startTime) <= delta(head.getPrevious().getEvent().timestamp(), startTime)) {

                while (_aux.getNext() != head && _aux.getEvent().timestamp() < startTime) {
                    _aux = _aux.getNext();
                }

            } else {
                _aux = _aux.getPrevious();

                while (_aux.getPrevious() != head && _aux.getEvent().timestamp() > startTime) {

                    _aux = _aux.getPrevious();
                }

            }

            if (_aux.getEvent().timestamp() >= startTime && _aux.getEvent().timestamp() < endTime && _aux.getEvent().type().equals(type)) {
                _auxList.insert(_aux.getEvent());
                _aux = _aux.getNext();

            }

            while ((_aux.getEvent().timestamp() >= startTime && _aux.getEvent().timestamp() < endTime)) {
                if (_aux.getEvent().type().equals(type)) {

                    _auxList.insert(_aux.getEvent());

                }

                _aux = _aux.getNext();

            }
        }
        return new ChallengeIterator(_auxList);
    }

}
