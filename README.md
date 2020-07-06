# Implement EventStore

## General

The implementation of class was maked using a **circular doubly linked list** to facilitate navigation between the elements.

Each method that changes or queries the List is using `synchronized` to prevent more than two threads calls at the same time

## Insert

The Iserts are made using the `event.timestemp()` as a reference to keep the list sorted. This slows down the new inserts, but increases performance on elements queries .

To reduce the query time of the element, it is always checked if `event.timestamp()` is closer to the beginning or end of the list using the method `delta()`. Then it will be decided whether the list will be scrolled at the beginning/end or at the end/beginning

## Query

In the case of search intervals, once the first item is found, simply scroll through the list, usually checking only if the types are the same or the `event.timestemp()` are bigger than endTime.

