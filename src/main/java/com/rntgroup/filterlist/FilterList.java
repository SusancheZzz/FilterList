package com.rntgroup.filterlist;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;


public class FilterList<E extends Serializable> extends AbstractList<E> implements List<E>, Serializable {

    private static final long serialVersionUID = 1L;
    private final List<E> objects = new ArrayList<>();
    private final Set<E> predicate;

    private String NULL_PARAM = "Param is null";
    private String NULL_COLLECTION = "Collection is null";


    public FilterList(Collection<E> objects, Collection<E> predicate) {
        Objects.requireNonNull(objects, "Objects are null");
        Objects.requireNonNull(predicate, "Predicate are null");

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
        Objects.requireNonNull(o, NULL_PARAM);
        return this.objects.contains(o);
    }

    public Iterator<E> iterator() {
        return new FilterListItr();
    }

    public Object[] toArray() {
        return objects.toArray(new Object[0]);
    }

    public boolean add(E e) {
        Objects.requireNonNull(e, NULL_PARAM);
        return !predicate.contains(e) ? objects.add(e) : false;
    }

    public boolean remove(Object o) {
        Objects.requireNonNull(o, NULL_PARAM);
        return !predicate.contains(o) ? objects.remove(o) : false;
    }

    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c, NULL_COLLECTION);
        if (objects.size() < c.size())
            return false;
        return objects.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c, NULL_COLLECTION);
        return this.addAll(objects.size() - 1, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c, NULL_COLLECTION);
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
        Objects.requireNonNull(c, NULL_COLLECTION);
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
        Objects.requireNonNull(element, NULL_PARAM);
        Objects.checkIndex(index, objects.size());

        return objects.set(index, element);
    }

    public void add(int index, E element) {
        Objects.requireNonNull(element, NULL_PARAM);
        Objects.checkIndex(index, objects.size());

        if (!predicate.contains(element))
            objects.add(index, element);
    }

    public E remove(int index) {
        Objects.checkIndex(index, objects.size());
        return !predicate.contains(objects.get(index)) ? objects.remove(index) : null;
    }

    public int indexOf(Object o) {
        Objects.requireNonNull(o, NULL_PARAM);
        return objects.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        Objects.requireNonNull(o, NULL_PARAM);
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (Objects.isNull(object) || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        FilterList<?> that = (FilterList<?>) object;
        return Objects.equals(objects, that.objects) && Objects.equals(predicate, that.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), objects, predicate);
    }
}
