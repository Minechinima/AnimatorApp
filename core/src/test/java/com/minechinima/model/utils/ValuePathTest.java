package com.minechinima.model.utils;

import com.minechinima.model.exceptions.PathParsingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ValuePathTest {
    public static Stream<Arguments> providePathFormatsWithConcat() {
        List<Arguments> enclosures = new ArrayList<>();

        for (ValuePath.Format value : ValuePath.Format.values()) {
            if (value.getMode() == ValuePath.Format.Mode.CONCAT) enclosures.add(Arguments.of(value));
        }

        return enclosures.stream();
    }

    public static Stream<Arguments> providePathFormatsWithEnclose() {
        List<Arguments> enclosures = new ArrayList<>();

        for (ValuePath.Format value : ValuePath.Format.values()) {
            if (value.getMode() == ValuePath.Format.Mode.ENCLOSE) enclosures.add(Arguments.of(value));
        }

        return enclosures.stream();
    }

    public static Stream<Arguments> providePathFormats() {
        List<Arguments> enclosures = new ArrayList<>();

        for (ValuePath.Format value : ValuePath.Format.values()) {
            enclosures.add(Arguments.of(value));
        }

        return enclosures.stream();
    }

    public static Stream<Arguments> provideInvalidPathFormatsExamples() {
        return Stream.of(Arguments.of(ValuePath.Format.ARRAY, List.of("\"", "/", "A", "", "-1")));
    }

    public static ID randomID() {
        return new ID("test" + (int) (Math.random() * 100));
    }

    public static ID randomIDwithUUID() {
        return new ID("test" + (int) (Math.random() * 100), UUID.randomUUID());
    }

    public static Stream<Arguments> provideTestPathsCombinations() {
        List<Arguments> paths = new ArrayList<>();

        //TODO generate all possible path combinations
        ValuePath p1 = new ValuePath();
        p1.append(ValuePath.Format.PATH, randomIDwithUUID());
        p1.append(ValuePath.Format.PATH, new ID("test2"));
        p1.append(ValuePath.Format.PATH, new ID("test3", UUID.randomUUID()));
        paths.add(Arguments.of(p1));

        ValuePath p1EMPTIES = new ValuePath();
        p1EMPTIES.append(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        p1EMPTIES.append(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        p1EMPTIES.append(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        paths.add(Arguments.of(p1EMPTIES));

        ValuePath p2 = new ValuePath();
        p2.append(ValuePath.Format.MAP, new ID("test1", UUID.randomUUID()));
        p2.append(ValuePath.Format.MAP, new ID("test2"));
        p2.append(ValuePath.Format.MAP, new ID("test3"));
        paths.add(Arguments.of(p2));

        ValuePath p2EMPTIES = new ValuePath();
        p2EMPTIES.append(ValuePath.Format.MAP, new ID("", UUID.randomUUID()));
        p2EMPTIES.append(ValuePath.Format.MAP, new ID(""));
        p2EMPTIES.append(ValuePath.Format.MAP, new ID(""));
        paths.add(Arguments.of(p2EMPTIES));

        ValuePath p3 = new ValuePath();
        p3.append(ValuePath.Format.MAP, new ID("test1", UUID.randomUUID()));
        p3.append(ValuePath.Format.PATH, new ID("test2"));
        p3.append(ValuePath.Format.MAP, new ID("test3"));
        paths.add(Arguments.of(p3));

        ValuePath p3EMPTIES = new ValuePath();
        p3EMPTIES.append(ValuePath.Format.MAP, new ID(""));
        p3EMPTIES.append(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        p3EMPTIES.append(ValuePath.Format.MAP, new ID(""));
        paths.add(Arguments.of(p3EMPTIES));

        ValuePath p4 = new ValuePath();
        p4.append(ValuePath.Format.ARRAY, new ID("0"));
        p4.append(ValuePath.Format.PATH, new ID("test", UUID.randomUUID()));
        p4.append(ValuePath.Format.MAP, new ID("test", UUID.randomUUID()));
        paths.add(Arguments.of(p4));

        ValuePath p5 = new ValuePath();
        p5.append(new ID("0"));
        p5.append(ValuePath.Format.ARRAY, new ID("1"));
        p5.append(ValuePath.Format.ARRAY, new ID("2"));
        paths.add(Arguments.of(p5));

        ValuePath p6 = new ValuePath();
        p6.append(new ID("0", UUID.randomUUID()));
        p6.append(ValuePath.Format.MAP, new ID("1", UUID.randomUUID()));
        p6.append(ValuePath.Format.PATH, new ID("2", UUID.randomUUID()));
        paths.add(Arguments.of(p6));

        ValuePath p11 = new ValuePath();
        p11.append(ValuePath.Format.PATH, randomIDwithUUID());
        p11.append(ValuePath.Format.PATH, new ID(""));
        p11.append(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        paths.add(Arguments.of(p11));

        ValuePath p12 = new ValuePath();
        p12.append(ValuePath.Format.PATH, new ID(""));
        paths.add(Arguments.of(p12));

        ValuePath p13 = new ValuePath();
        p13.append(ValuePath.Format.MAP, new ID(""));
        paths.add(Arguments.of(p13));

        return paths.stream();
    }

    @ParameterizedTest
    @MethodSource("provideTestPathsCombinations")
    public void testParsing(ValuePath test) throws PathParsingException {
        String uniquePathTest = test.toUniquePath();
        ValuePath testParse = new ValuePath(uniquePathTest);

        Assertions.assertEquals(uniquePathTest, testParse.toUniquePath());

        String valuePathTest = test.toValuePath();
        testParse = new ValuePath(valuePathTest);
        Assertions.assertEquals(valuePathTest, testParse.toValuePath());
    }

    @Test
    public void testParsingSyntaxErrors() throws PathParsingException {
        ValuePath testParse = new ValuePath("");
        Assertions.assertNull(testParse.getStart());

        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("."));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath(".[0]"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[]"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[-1]"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[0]a"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[0\"]"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[0"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("["));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[\"0\".a"));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("[\"0]\""));
        Assertions.assertThrows(PathParsingException.class, ()-> new ValuePath("..."));
        Assertions.assertThrows(PathParsingException.class, ()->  new ValuePath("..a"));
        Assertions.assertThrows(PathParsingException.class, ()->  new ValuePath(".a."));
        Assertions.assertThrows(PathParsingException.class, ()->  new ValuePath(".a.."));
    }

    @Test
    public void testParseSpecialCases() throws PathParsingException {
        ValuePath testParse = new ValuePath("a");
        Assertions.assertNull(testParse.getStart().getNext());
        Assertions.assertNull(testParse.getStart().getFormat());
        Assertions.assertEquals("a", testParse.getStart().getID().id());

        testParse = new ValuePath("[0]");
        Assertions.assertNull(testParse.getStart().getNext());
        Assertions.assertEquals(ValuePath.Format.ARRAY, testParse.getStart().getFormat());
        Assertions.assertEquals("0", testParse.getStart().getID().id());

        testParse = new ValuePath("[\"a\"].value");
        Assertions.assertNotNull(testParse.getStart().getNext());
        Assertions.assertEquals(ValuePath.Format.MAP, testParse.getStart().getFormat());
        Assertions.assertEquals("a", testParse.getStart().getID().id());
        Assertions.assertEquals(ValuePath.Format.PATH, testParse.getStart().getNext().getFormat());
        Assertions.assertEquals("value", testParse.getStart().getNext().getID().id());
    }

    @ParameterizedTest
    @MethodSource("provideTestPathsCombinations")
    public void testGetCleanedUniquePath(ValuePath test) {
        String uniquePathTest = test.toUniquePath();
        Assertions.assertEquals(uniquePathTest, test.getCleanedUniquePath().toUniquePath());
    }

    @ParameterizedTest
    @MethodSource("provideTestPathsCombinations")
    public void testGetCleanedValuePath(ValuePath test) {
        String valuePathTest = test.toValuePath();
        Assertions.assertEquals(valuePathTest, test.getCleanedValuePath().toValuePath());
    }

    @ParameterizedTest
    @MethodSource("providePathFormats")
    public void testWrongAppending(ValuePath.Format pathFormat) {
        ValuePath path = new ValuePath(pathFormat, new ID("0"));
        Assertions.assertThrows(RuntimeException.class, () -> path.append(new ID("")));
        Assertions.assertThrows(RuntimeException.class, () -> path.append(null));
    }

    @ParameterizedTest
    @MethodSource("providePathFormatsWithConcat")
    public void toValuePathConcatWithEmpties(ValuePath.Format pathFormat) {
        String expectedConcat = "value" + pathFormat.getStart() + "value";

        ValuePath path = new ValuePath(pathFormat, new ID(""));
        Assertions.assertEquals("", path.toValuePath());

        path = new ValuePath(pathFormat, new ID(""));
        path.append(pathFormat, new ID(""));
        Assertions.assertEquals("", path.toValuePath());

        path.append(pathFormat, new ID("value"));
        Assertions.assertEquals("value", path.toValuePath());

        path.append(pathFormat, new ID("value"));
        Assertions.assertEquals(expectedConcat, path.toValuePath());

        path.append(pathFormat, new ID(""));
        Assertions.assertEquals(expectedConcat, path.toValuePath());

        path.append(pathFormat, new ID(""));
        Assertions.assertEquals(expectedConcat, path.toValuePath());
    }

    @Test
    public void toValuePathArray() {
        ValuePath path = new ValuePath(new ID("0"));
        Assertions.assertEquals("0", path.toValuePath());

        path = new ValuePath(ValuePath.Format.ARRAY, new ID("0"));
        Assertions.assertEquals("[0]", path.toValuePath());

        path.append( ValuePath.Format.ARRAY, new ID("1"));
        path.append( ValuePath.Format.ARRAY, new ID("2"));
        Assertions.assertEquals("[0][1][2]", path.toValuePath());
    }

    @Test
    public void toValuePathArrayWrongFormat() {
        ValuePath path = new ValuePath(new ID("root"));
        Assertions.assertThrows(RuntimeException.class, () -> path.append(ValuePath.Format.ARRAY, new ID("")));

        ValuePath path1 = new ValuePath(new ID("root"));
        Assertions.assertThrows(RuntimeException.class,  () -> path1.append(ValuePath.Format.ARRAY, new ID("-1")));

        ValuePath path2 = new ValuePath(new ID("root"));
        Assertions.assertThrows(RuntimeException.class, () -> path2.append(ValuePath.Format.ARRAY, new ID("AAAA")));
    }

    @Test
    public void toValuePathMap() {
        ValuePath path = new ValuePath(new ID("root"));
        path.append(ValuePath.Format.MAP, new ID("parent"));
        Assertions.assertEquals("root[\"parent\"]", path.toValuePath());

        path.append(ValuePath.Format.MAP, new ID("key1"));
        path.append(ValuePath.Format.MAP, new ID(""));
        path.append(ValuePath.Format.MAP, new ID("key2"));
        Assertions.assertEquals("root[\"parent\"][\"key1\"][\"\"][\"key2\"]", path.toValuePath());
    }

    @Test
    public void toValuePathMixtureNoEmpties() {
        ValuePath path = new ValuePath(new ID("root"));
        path.append(ValuePath.Format.MAP, new ID("parent"));
        path.append(ValuePath.Format.PATH, new ID("test"));
        path.append(ValuePath.Format.PATH, new ID("value"));

        Assertions.assertEquals("root[\"parent\"].test.value", path.toValuePath());

        path = new ValuePath(new ID("root"));
        path.append(ValuePath.Format.PATH, new ID("parent"));
        path.append(ValuePath.Format.ARRAY, new ID("0"));
        path.append(ValuePath.Format.PATH, new ID("value"));

        Assertions.assertEquals("root.parent[0].value", path.toValuePath());

        path = new ValuePath(new ID("root"));
        path.append(ValuePath.Format.PATH, new ID("parent"));
        path.append(ValuePath.Format.MAP, new ID("object"));
        path.append(ValuePath.Format.PATH, new ID("value"));

        Assertions.assertEquals("root.parent[\"object\"].value", path.toValuePath());
    }

    @Test
    public void toValuePathMixtureWithEmpties() {
        ValuePath path;
        path = new ValuePath(new ID(""));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.MAP, new ID("parent"));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.PATH, new ID("value"));
        path.append(ValuePath.Format.PATH, new ID(""));

        Assertions.assertEquals("[\"parent\"].value", path.toValuePath());

        path = new ValuePath(new ID(""));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.PATH, new ID("parent"));
        path.append(ValuePath.Format.ARRAY, new ID("0"));
        path.append(ValuePath.Format.PATH, new ID("value"));
        path.append(ValuePath.Format.PATH, new ID(""));

        Assertions.assertEquals("parent[0].value", path.toValuePath());

        path = new ValuePath(new ID(""));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.PATH, new ID("parent"));
        path.append(ValuePath.Format.MAP, new ID("object"));
        path.append(ValuePath.Format.PATH, new ID("value"));
        path.append(ValuePath.Format.PATH, new ID(""));

        Assertions.assertEquals("parent[\"object\"].value", path.toValuePath());
    }

    @Test
    public void toUniquePath() {
        final UUID test = UUID.randomUUID();
        ValuePath path;
        path = new ValuePath(new ID(""));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.MAP, new ID("parent", test));
        path.append(ValuePath.Format.PATH, new ID("", test));
        path.append(ValuePath.Format.PATH, new ID("value"));
        path.append(ValuePath.Format.PATH, new ID("", test));

        Assertions.assertEquals("[\""+test+"\"]."+test+".value."+test, path.toUniquePath());

        path = new ValuePath(new ID("", test));
        path.append(ValuePath.Format.PATH, new ID("parent"));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.ARRAY, new ID("0", test));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.PATH, new ID("value", test));
        path.append(ValuePath.Format.PATH, new ID(""));

        Assertions.assertEquals(test+".parent["+test+"]."+test, path.toUniquePath());
    }

    @Test
    public void equals() {
        final UUID test = UUID.randomUUID();
        final UUID test2 = UUID.randomUUID();

        ValuePath path = new ValuePath(new ID(""));
        path.append(ValuePath.Format.PATH, new ID(""));
        path.append(ValuePath.Format.MAP, new ID("parent", test));
        path.append(ValuePath.Format.PATH, new ID("", test));
        path.append( ValuePath.Format.PATH, new ID("value"));
        path.append(ValuePath.Format.PATH, new ID(""));

        ValuePath path2 = new ValuePath(new ID(""));
        path2.append(ValuePath.Format.PATH, new ID(""));
        path2.append(ValuePath.Format.MAP, new ID("parent", test));
        path2.append(ValuePath.Format.PATH, new ID("", test));
        path2.append(ValuePath.Format.PATH, new ID("value"));
        path2.append( ValuePath.Format.PATH, new ID(""));

        Assertions.assertEquals(path, path2);
    }

    @Test
    public void pop() {
        ValuePath path = new ValuePath();
        path.append(new ID("test"));
        path.pop();
        Assertions.assertEquals("", path.toValuePath());
        Assertions.assertNull(path.pop());
    }

    @Test
    public void popAppend() {
        ValuePath path = new ValuePath();
        path.append(new ID("map"));
        path.append(ValuePath.Format.MAP, new ID("test"));
        path.pop();
        path.append(ValuePath.Format.MAP, new ID("test2"));
        Assertions.assertEquals("map[\"test2\"]", path.toValuePath());
    }

    @Test
    public void popLastPathSegmentPathFormat() {
        ValuePath path = new ValuePath();
        path.append(ValuePath.Format.MAP, new ID("test"));
        path.append(ValuePath.Format.MAP, new ID("test2"));
        path.pop();
        Assertions.assertEquals("[\"test\"]", path.toValuePath());

        path.pop();
        Assertions.assertEquals("", path.toValuePath());

        path.append(new ID("test2"));
        Assertions.assertEquals("test2", path.toValuePath());

        path.pop();
        path.append(ValuePath.Format.MAP, new ID("test2"));
        Assertions.assertEquals("[\"test2\"]", path.toValuePath());

        path.pop();
        path.pop();
        path.append(new ID("test2"));
        Assertions.assertEquals("test2", path.toValuePath());
    }

    @Test
    public void testIteratorBuildingIterativelyValuePath() {
        ValuePath.PathSegment p1Start = new ValuePath.PathSegment(new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p2Start = new ValuePath.PathSegment(new ID("test"));
        ValuePath.PathSegment p1 = new ValuePath.PathSegment(ValuePath.Format.PATH, p1Start.getID());
        ValuePath.PathSegment p2 = new ValuePath.PathSegment(ValuePath.Format.PATH, p2Start.getID());
        ValuePath.PathSegment p3 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p4 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p5 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test2", UUID.randomUUID()));
        ValuePath.PathSegment p6 = new ValuePath.PathSegment(ValuePath.Format.MAP, new ID("id"));
        ValuePath.PathSegment p7 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p8 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test4"));

        ValuePath path = new ValuePath();
        path.append(ValuePath.Format.PATH, new ID(""));
        path.appendSegment(p1);
        path.append(ValuePath.Format.PATH, new ID(""));
        ValuePath.CleanPathBuilder it = path.getValuePathIterator();
        while (it.nextNonEmpty() != null) {}

        Assertions.assertNull(it.getStart());
        Assertions.assertNull(it.getEnd());

        /* append next */
        ValuePath path2 = new ValuePath();
        path2.append(ValuePath.Format.PATH, new ID(""));
        path2.appendSegment(p2);
        path2.appendSegment(p3);
        it.setCurrent(path2.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertNull(it.getStart().getNext());

        /* append */
        ValuePath path3 = new ValuePath();
        path3.appendSegment(p4);
        path3.appendSegment(p5);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.appendSegment(p6);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.append(ValuePath.Format.PATH, new ID(""));
        it.setCurrent(path3.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertTrue(p4.equals(it.getStart().getNext(), false));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext(), false));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext(), false));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext());

        /* append */
        ValuePath path4 = new ValuePath();
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p7);
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p8);

        it.setCurrent(path4.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertTrue(p4.equals(it.getStart().getNext(), false));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext(), false));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext(), false));
        Assertions.assertTrue(p7.equals(it.getStart().getNext().getNext().getNext().getNext(), false));
        Assertions.assertTrue(p8.equals(it.getStart().getNext().getNext().getNext().getNext().getNext(), false));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext());
    }

    @Test
    public void testPathBuilderAppend() {
        ValuePath.PathSegment p1Start = new ValuePath.PathSegment(new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p2Start = new ValuePath.PathSegment(new ID("test"));
        ValuePath.PathSegment p1 = new ValuePath.PathSegment(ValuePath.Format.PATH, p1Start.getID());
        ValuePath.PathSegment p2 = new ValuePath.PathSegment(ValuePath.Format.PATH, p2Start.getID());
        ValuePath.PathSegment p3 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p4 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p5 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test2", UUID.randomUUID()));
        ValuePath.PathSegment p6 = new ValuePath.PathSegment(ValuePath.Format.MAP, new ID("id"));
        ValuePath.PathSegment p7 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p8 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test4"));

        ValuePath path = new ValuePath();
        path.append(ValuePath.Format.PATH, new ID(""));
        path.appendSegment(p1);
        path.append(ValuePath.Format.PATH, new ID(""));
        ValuePath.CleanPathBuilder it = path.getValuePathIterator();
        ValuePath sub = it.append(path);

        Assertions.assertNull(sub.getStart());
        Assertions.assertNull(sub.getEnd());
        Assertions.assertNull(it.getStart());
        Assertions.assertNull(it.getEnd());

        /* append next */
        ValuePath path2 = new ValuePath();
        path2.append(ValuePath.Format.PATH, new ID(""));
        path2.appendSegment(p2);
        path2.appendSegment(p3);
        sub = it.append(path2);

        Assertions.assertTrue(p2Start.equals(sub.getStart(), false));
        Assertions.assertNull(sub.getStart().getNext());

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertNull(it.getStart().getNext());

        /* append */
        ValuePath path3 = new ValuePath();
        path3.appendSegment(p4);
        path3.appendSegment(p5);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.appendSegment(p6);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.append(ValuePath.Format.PATH, new ID(""));
        sub = it.append(path3);

        Assertions.assertTrue(p4.equals(sub.getStart(), false));
        Assertions.assertTrue(p5.equals(sub.getStart().getNext(), false));
        Assertions.assertTrue(p6.equals(sub.getStart().getNext().getNext(), false));
        Assertions.assertNull(sub.getStart().getNext().getNext().getNext());

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertTrue(p4.equals(it.getStart().getNext(), false));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext(), false));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext(), false));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext());

        /* append */
        ValuePath path4 = new ValuePath();
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p7);
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p8);
        sub = it.append(path4);

        Assertions.assertTrue(p7.equals(sub.getStart(), false));
        Assertions.assertTrue(p8.equals(sub.getStart().getNext(), false));
        Assertions.assertNull(sub.getStart().getNext().getNext());

        Assertions.assertTrue(p2Start.equals(it.getStart(), false));
        Assertions.assertTrue(p4.equals(it.getStart().getNext(), false));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext(), false));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext(), false));
        Assertions.assertTrue(p7.equals(it.getStart().getNext().getNext().getNext().getNext(), false));
        Assertions.assertTrue(p8.equals(it.getStart().getNext().getNext().getNext().getNext().getNext(), false));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext());
    }


    @Test
    public void testIteratorBuildingIterativelyUniquePath() {
        ValuePath.PathSegment p1Start = new ValuePath.PathSegment(new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p1 = new ValuePath.PathSegment(ValuePath.Format.PATH, p1Start.getID());
        ValuePath.PathSegment p2 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test"));
        ValuePath.PathSegment p3 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("", UUID.randomUUID()));
        ValuePath.PathSegment p4 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p5 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test2", UUID.randomUUID()));
        ValuePath.PathSegment p6 = new ValuePath.PathSegment(ValuePath.Format.MAP, new ID("id"));
        ValuePath.PathSegment p7 = new ValuePath.PathSegment(ValuePath.Format.ARRAY, new ID("0", UUID.randomUUID()));
        ValuePath.PathSegment p8 = new ValuePath.PathSegment(ValuePath.Format.PATH, new ID("test4"));

        ValuePath path = new ValuePath();
        path.append(ValuePath.Format.PATH, new ID(""));
        path.appendSegment(p1);
        path.append(ValuePath.Format.PATH, new ID(""));
        ValuePath.CleanPathBuilder it = path.getUniquePathIterator();
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p1Start.equals(it.getStart(), true));
        Assertions.assertNull(it.getStart().getNext());

        /* append next */
        ValuePath path2 = new ValuePath();
        path2.append(ValuePath.Format.PATH, new ID(""));
        path2.appendSegment(p2);
        path2.appendSegment(p3);
        it.setCurrent(path2.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p1Start.equals(it.getStart(), true));
        Assertions.assertTrue(p2.equals(it.getStart().getNext(), true));
        Assertions.assertTrue(p3.equals(it.getStart().getNext().getNext(), true));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext());

        /* append */
        ValuePath path3 = new ValuePath();
        path3.appendSegment(p4);
        path3.appendSegment(p5);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.appendSegment(p6);
        path3.append(ValuePath.Format.PATH, new ID(""));
        path3.append(ValuePath.Format.PATH, new ID(""));
        it.setCurrent(path3.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p1Start.equals(it.getStart(), true));
        Assertions.assertTrue(p2.equals(it.getStart().getNext(), true));
        Assertions.assertTrue(p3.equals(it.getStart().getNext().getNext(), true));
        Assertions.assertTrue(p4.equals(it.getStart().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext().getNext().getNext(), true));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext());

        /* append */
        ValuePath path4 = new ValuePath();
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p7);
        path4.append(ValuePath.Format.PATH, new ID(""));
        path4.appendSegment(p8);

        it.setCurrent(path4.getStart());
        while (it.nextNonEmpty() != null) {}

        Assertions.assertTrue(p1Start.equals(it.getStart(), true));
        Assertions.assertTrue(p2.equals(it.getStart().getNext(), true));
        Assertions.assertTrue(p3.equals(it.getStart().getNext().getNext(), true));
        Assertions.assertTrue(p4.equals(it.getStart().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p5.equals(it.getStart().getNext().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p6.equals(it.getStart().getNext().getNext().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p7.equals(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext(), true));
        Assertions.assertTrue(p8.equals(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext().getNext(), true));
        Assertions.assertNull(it.getStart().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext());
    }
}
