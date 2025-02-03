package com.rntgroup.filterlist;

import java.io.Serializable;
import java.util.*;


public class FilterList<E> extends AbstractList<E> implements List<E>, Serializable {

    private static final long serialVersionUID = 1L;
    private final List<E> objects = new ArrayList<>();
    private final Set<E> predicate;


    public FilterList(Collection<E> objects, Collection<E> predicate) {
        if (objects == null || predicate == null)
            throw new NullPointerException("Objects or predicate are null");

        this.objects.addAll(objects);
        this.predicate = Set.copyOf(predicate); //immutable
    }

    public int size() {
        return this.objects.size();
    }

    public boolean isEmpty() {
        return this.objects.isEmpty();
    }

    public boolean contains(Object o) {
        Objects.requireNonNull(o, "Param is null");
        return this.objects.contains(o);
    }

    public Iterator<E> iterator() {
        return new FilterItr(objects.iterator());
    }

    public Object[] toArray() {
        return objects.toArray(new Object[0]);
    }

    public boolean add(E e) {
        Objects.requireNonNull(e, "Param is null");
        return !predicate.contains(e) ? objects.add(e) : false;
    }

    public boolean remove(Object o) {
        Objects.requireNonNull(o, "Param is null");
        return !predicate.contains(o) ? objects.remove(o) : false;
    }

    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c, "Collection is null");
        if (objects.size() < c.size())
            return false;
        return objects.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c, "Collection is null");
        return this.addAll(objects.size() - 1, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c, "Collection is null");
        Objects.checkIndex(index, objects.size());

        boolean wasAdded = false;
        for (E e : c) {
            if (!predicate.contains(e)) {
                objects.add(index++, e);
                wasAdded = true;
            }
        }
        return wasAdded;
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c, "Collection is null");
        return objects.removeIf(object -> c.contains(object) && !predicate.contains(object));
    }

    public void clear() {
        objects.clear();
    }

    public E get(int index) {
        Objects.checkIndex(index, objects.size());
        return objects.get(index);
    }

    public E set(int index, E element) {
        Objects.requireNonNull(element, "Param is null");
        Objects.checkIndex(index, objects.size());

        return objects.set(index, element);
    }

    public void add(int index, E element) {
        Objects.requireNonNull(element, "Param is null");
        Objects.checkIndex(index, objects.size());

        if (!predicate.contains(element))
            objects.add(index, element);
    }

    public E remove(int index) {
        Objects.checkIndex(index, objects.size());
        return !predicate.contains(objects.get(index)) ? objects.remove(index) : null;
    }

    public int indexOf(Object o) {
        Objects.requireNonNull(o, "Param is null");
        return objects.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        Objects.requireNonNull(o, "Param is null");
        return objects.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return new FilterListItr();
    }

    public ListIterator<E> listIterator(int index) {
        Objects.checkIndex(index, objects.size());
        return new FilterListItr(index);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return objects.subList(fromIndex, toIndex);
    }

    private class FilterItr implements Iterator<E> {
        private final Iterator<E> iterator;
        private E nextElement;
        private boolean hasNextComputed = false;
        private boolean canRemove = false;

        public FilterItr(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        private void findNext() {
            while (iterator.hasNext()) {
                E element = iterator.next();
                if (!predicate.contains(element)) {
                    nextElement = element;
                    hasNextComputed = true;
                    return;
                }
            }
            hasNextComputed = true;
            nextElement = null;
        }

        @Override
        public boolean hasNext() {
            if (!hasNextComputed) {
                findNext();
            }
            return nextElement != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            hasNextComputed = false;
            canRemove = true;
            return nextElement;
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("Cannot remove element before calling next()");
            }
            iterator.remove();
            canRemove = false; //Запрещаем повторный вызов remove()
        }
    }

    private class FilterListItr implements ListIterator<E> {
        private int cursor;
        private int lastReturnedIndex = -1;

        public FilterListItr() {
            moveToNextValid();
        }

        public FilterListItr(int startIndex) {
            this.cursor = startIndex;
            moveToNextValid();
        }

        private void moveToNextValid() {
            while (cursor < objects.size() && predicate.contains(objects.get(cursor))) {
                cursor++;
            }
        }

        private void moveToPrevValid() {
            while (cursor > 0 && predicate.contains(objects.get(cursor - 1))) {
                cursor--;
            }
        }

        @Override
        public boolean hasNext() {
            int tempCursor = cursor;
            while (tempCursor < objects.size() && predicate.contains(objects.get(tempCursor))) {
                tempCursor++;
            }
            return tempCursor < objects.size();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturnedIndex = cursor;
            E element = objects.get(cursor++);
            moveToNextValid();
            return element;
        }

        @Override
        public boolean hasPrevious() {
            int tempCursor = cursor;
            while (tempCursor > 0 && predicate.contains(objects.get(tempCursor - 1))) {
                tempCursor--;
            }
            return tempCursor > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            moveToPrevValid();
            lastReturnedIndex = --cursor;
            return objects.get(cursor);
        }

        @Override
        public int nextIndex() {
            int tempCursor = cursor;
            while (tempCursor < objects.size() && predicate.contains(objects.get(tempCursor))) {
                tempCursor++;
            }
            return tempCursor;
        }

        @Override
        public int previousIndex() {
            int tempCursor = cursor;
            while (tempCursor > 0 && predicate.contains(objects.get(tempCursor - 1))) {
                tempCursor--;
            }
            return tempCursor - 1;
        }

        @Override
        public void remove() {
            if (lastReturnedIndex == -1) {
                throw new NoSuchElementException("You cannot remove an element before calling next() or previous()");
            }
            if (predicate.contains(objects.get(lastReturnedIndex))) {
                throw new IllegalStateException("Cannot remove an element that is part of a predicate");
            }

            objects.remove(lastReturnedIndex);
            if (lastReturnedIndex < cursor) {
                cursor--;
            }
            lastReturnedIndex = -1; // Запрещаем повторный remove() без next() или previous()
        }

        @Override
        public void set(E e) {
            if (lastReturnedIndex == -1 || predicate.contains(e)) {
                throw new IllegalStateException();
            }
            objects.set(lastReturnedIndex, e);
        }

        @Override
        public void add(E e) {
            if (predicate.contains(e)) {
                throw new IllegalStateException("The predicate contains this element");
            }
            objects.add(cursor++, e);
            lastReturnedIndex = -1; // Запрещаем remove() сразу после add()
        }
    }

}
