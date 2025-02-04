package com.rntgroup.filterlist;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


public class FilterListTest {

    private List<String> objects;

    private List<String> predicate;

    private List<String> filterList;

    @Before
    public void init() {
        objects = Arrays.asList("one", "two", "three", "four", "five");
        predicate = Arrays.asList("two", "three");
        filterList = new FilterList<>(objects, predicate);
    }

    @Test
    public void size() {
        assertEquals(5, filterList.size());
    }

    @Test
    public void isEmpty() {
        assertFalse(filterList.isEmpty());
    }

    @Test
    public void containsElementFromCollection() {
        assertTrue(filterList.contains("four"));
    }

    @Test
    public void containsElementNotFromCollection() {
        assertFalse(filterList.contains("nine"));
    }

    @Test
    public void containsAllWhenAllElementsFromCollection() {
        List<String> listForCheck = Arrays.asList("two", "five", "one");
        assertTrue(filterList.containsAll(listForCheck));
    }

    @Test
    public void containsAllWhenSomeElementsFromCollection() {
        List<String> listForCheck = Arrays.asList("two", "five", "zero");
        assertFalse(filterList.containsAll(listForCheck));
    }

    @Test
    public void toArray() {
        assertArrayEquals(new String[]{"one", "two", "three", "four", "five"}, filterList.toArray());
    }

    @Test
    public void clear() {
        filterList.clear();
        assertTrue(filterList.isEmpty());
    }

    @Test
    public void get() {
        assertEquals("four", filterList.get(3));
        assertEquals("two", filterList.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithNegativeIndex() {
        filterList.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithIncorrectIndex() {
        filterList.get(150);
    }

    @Test
    public void set() {
        filterList.set(3, "zero");
        assertEquals("zero", filterList.get(3));
        assertEquals(5, filterList.size());
    }

    @Test
    public void setNewElementInsteadElementWhichPredicateContains() {
        filterList.set(1, "zero");
        assertEquals("zero", filterList.get(1));
        assertEquals(5, filterList.size());
    }

    @Test(expected = NullPointerException.class)
    public void setNullElement() {
        filterList.set(2, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setWithNegativeIndex() {
        filterList.set(-1, "one");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void setWithIncorrectIndex() {
        filterList.set(150, "one");
    }

    @Test
    public void indexOf() {
        objects = Arrays.asList("one", "two", "three", "one", "five");
        predicate = Arrays.asList("two", "three");
        filterList = new FilterList<>(objects, predicate);
        assertEquals(0, filterList.indexOf("one"));
    }

    @Test
    public void lastIndexOf() {
        objects = Arrays.asList("one", "two", "three", "one", "five");
        predicate = Arrays.asList("two", "three");
        filterList = new FilterList<>(objects, predicate);
        assertEquals(3, filterList.lastIndexOf("one"));
    }

    @Test(expected = NullPointerException.class)
    public void indexOfNullElement() {
        filterList.indexOf(null);
    }

    @Test(expected = NullPointerException.class)
    public void lastIndexOfNullElement() {
        filterList.lastIndexOf(null);
    }


    @Test
    public void subList() {
        List<String> expectedList = Arrays.asList("two", "three", "four");
        assertEquals(expectedList, filterList.subList(1, 4));
    }

    @Test
    public void addElementNotFromPredicate() {
        assertTrue(filterList.add("nine"));
        assertEquals(6, filterList.size());
    }

    @Test
    public void addElementNotFromPredicateByIndex() {
        filterList.add(3, "nine");
        assertEquals(6, filterList.size());
        assertEquals("nine", filterList.get(3));
    }

    @Test
    public void addElementFromPredicate() {
        assertFalse(filterList.add("two"));
        assertEquals(5, filterList.size());
    }

    @Test
    public void addElementFromPredicateByIndex() {
        filterList.add(3, "two");
        assertEquals(5, filterList.size());
    }

    @Test(expected = NullPointerException.class)
    public void addNullElement() {
        filterList.add(null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addElementWithNegativeIndex() {
        filterList.add(-1, "nine");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addElementWithIncorrectIndex() {
        filterList.add(150, "nine");
    }

    @Test
    public void addAll() {
        List<String> listForAddition = Arrays.asList("one", "two", "six");
        assertTrue(filterList.addAll(listForAddition));
        assertEquals(7, filterList.size());
    }

    @Test
    public void addAllWithIndex() {
        List<String> listForAddition = Arrays.asList("one", "two", "six");
        assertTrue(filterList.addAll(3, listForAddition));
        assertEquals(listForAddition.get(0), filterList.get(3));
        assertEquals(listForAddition.get(2), filterList.get(4));
    }

    @Test
    public void remove() {
        assertTrue(filterList.remove("four"));
        assertEquals(4, filterList.size());
    }

    @Test
    public void removeElementNotFromCollection() {
        assertFalse(filterList.remove("nine"));
        assertEquals(5, filterList.size());
    }

    @Test
    public void removeElementFromPredicate() {
        assertFalse(filterList.remove("two"));
        assertEquals(5, filterList.size());
    }

    @Test
    public void removeElementFromPredicateByIndex() {
        filterList.remove(1);
        assertEquals(5, filterList.size());
    }

    @Test
    public void removeElementNotFromPredicateByIndex() {
        filterList.remove(3);
        assertEquals(4, filterList.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeByNegativeIndex() {
        filterList.remove(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeByIncorrectIndex() {
        filterList.remove(150);
    }

    @Test(expected = NullPointerException.class)
    public void removeNullElement() {
        filterList.remove(null);
    }

    @Test
    public void removeAll() {
        List<String> listForRemoving = Arrays.asList("four", "two", "six");
        filterList.removeAll(listForRemoving);
        assertEquals(4, filterList.size());
    }

    @Test
    public void iteratorHasNext() {
        Iterator<String> iterator = filterList.iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    public void iteratorHasNextAtTheEnd() {
        Iterator<String> iterator = filterList.iterator();
        for (int i = 0; i < (filterList.size() - predicate.size()); i++) {
            iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void iteration() {
        Iterator<String> iterator = filterList.iterator();
        assertEquals("one", iterator.next());
        assertEquals("four", iterator.next());
        assertEquals("five", iterator.next());
    }

    @Test
    public void iteratorRemove() {
        Iterator<String> iterator = filterList.iterator();
        iterator.next();
        iterator.remove();
        assertEquals(4, filterList.size());
        assertEquals("two", filterList.get(0));
        assertEquals("three", filterList.get(1));
        assertEquals("four", filterList.get(2));
        assertEquals("five", filterList.get(3));

        iterator.next();
        iterator.remove();
        assertEquals(3, filterList.size());
        assertEquals("two", filterList.get(0));
        assertEquals("three", filterList.get(1));
        assertEquals("five", filterList.get(2));
    }

    @Test
    public void hasPrevious() {
        ListIterator<String> iterator = filterList.listIterator();
        assertFalse(iterator.hasPrevious());
        iterator.next();
        assertTrue(iterator.hasPrevious());
    }

    @Test
    public void previous() {
        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        String previousElement = iterator.previous();
        assertEquals("one", previousElement);
    }

    @Test
    public void indexes() {
        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        assertEquals(3, iterator.nextIndex());
        assertEquals(0, iterator.previousIndex());
    }

    @Test
    public void setByIterator() {
        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        iterator.set("zero");
        assertEquals("zero", filterList.get(0));
    }

    @Test
    public void addByIterator() {
        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        iterator.add("zero");
        assertEquals(6, filterList.size());
        assertEquals("zero", filterList.get(3));
    }

    @Test
    public void hasNextWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        Iterator<String> iterator = filterList.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void nextWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        Iterator<String> iterator = filterList.iterator();
        iterator.next();
    }

    @Test
    public void hasPreviousWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        ListIterator<String> iterator = filterList.listIterator();
        assertFalse(iterator.hasPrevious());
    }

    @Test(expected = NoSuchElementException.class)
    public void previousWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        ListIterator<String> iterator = filterList.listIterator();
        iterator.previous();
    }

    @Test
    public void nextIndexWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        ListIterator<String> iterator = filterList.listIterator();
        assertEquals(3, iterator.nextIndex());
    }

    @Test
    public void previousIndexWhenCollectionEqualsPredicate() {
        initFilterListWithSameObjectsAndPredicate();
        ListIterator<String> iterator = filterList.listIterator();
        assertEquals(-1, iterator.previousIndex());
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorBehaviourInLastPosition() {
        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        iterator.next();
        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(5, iterator.nextIndex());
        assertEquals(4, iterator.previousIndex());

        assertEquals("five", iterator.previous());
        iterator.next();
        iterator.add("six");
        assertEquals(6, filterList.size());
        assertEquals("six", filterList.get(5));
        iterator.remove();
        assertEquals(5, filterList.size());

        iterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void iterationWhenLastElementOfCollectionFromPredicate() {
        objects = Arrays.asList("one", "two", "three");
        predicate = Arrays.asList("three", "four");
        filterList = new FilterList<>(objects, predicate);

        ListIterator<String> iterator = filterList.listIterator();
        iterator.next();
        iterator.next();
        assertFalse(iterator.hasNext());
        iterator.next();
    }

    private void initFilterListWithSameObjectsAndPredicate() {
        objects = Arrays.asList("one", "two", "three");
        predicate = Arrays.asList("one", "two", "three");
        filterList = new FilterList<>(objects, predicate);
    }
}
