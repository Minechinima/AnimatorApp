package com.minechinima.model.utils;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtils {
    /**
     * @param s0
     * @param s1
     * @param useUnique whether to use the uuid, if available, for comparison.
     * @return true if both segments have the same path format and the same id.
     */
    @Deprecated(forRemoval = true)
    public static boolean compareSegments(ValuePath.PathSegment s0, ValuePath.PathSegment s1, boolean useUnique) {
        if ((s0.getFormat() == null || s1.getFormat() == null) && (s0.getFormat() != s1.getFormat())) {
            return false;
        }

        String id0 = s0.getID().id();
        String id1 = s1.getID().id();

        if (useUnique) {
            id0 = s0.getID().uuid() != null ? s0.getID().uuid().toString() : id0;
            id1 = s1.getID().uuid() != null ? s1.getID().uuid().toString() : id1;
        }

        return s0.getFormat() == s1.getFormat() && id0.equals(id1);
    }

    /**
     * Checks for path collisions and automatically resolve them with a suffix.
     * @param newIdentifier the id to check for path collisions.
     * @param element the element to use for the path collision algorithm
     * @return the resolved id that is unique again or the same id if there were no path collisions.
     * @throws IllegalStateException
     */
    public static String resolvePathCollisions(String newIdentifier, BaseStructure element) throws IllegalStateException {
        if (element.getParent() == null) return newIdentifier;

        BaseValueHierarchy<?> uniquenessContext = element.getParent().getPathCollisionContext();
        List<BaseStructure> neighbors = uniquenessContext.getChildrenForPathCollisions();

        /* we are a child of that parent, so we must be in that list too! We are not a neighbor of ourselves! */
        neighbors.remove(element);
        String[] idComponents = extractSuffix(newIdentifier);
        /* path collisions are first checked without suffix */
        String idSuffixFree = idComponents[0];
        String idSuffix = idComponents[1] == null ? "" : idComponents[1];

        ValuePath testPath = element.getRelativePath(uniquenessContext);
        if (testPath == null) throw new IllegalStateException("Inconsistency in tree: relative path to parent could not be build.");
        testPath.replaceLastID(new ID(idSuffixFree));
        String testPathSuffixFree = testPath.toValuePath();
        /*
         * Check for path collisions without suffixes.
         * Without suffix, we get the duplicate names, which we can check for equality and then further check how to
         * resolve the suffix.
         *
         * Example: "name", "name.001" and renaming an element to "name", we need to find
         * the highest suffix "001" so we can resolve the collision to "name.002".
         */
        List<Integer> suffixes = new ArrayList<>();
        boolean duplicateSuffix = false;

        for (BaseStructure neighbor : neighbors) {
            ValuePath neighborPath = neighbor.getRelativePath(uniquenessContext);
            if (neighborPath == null) throw new IllegalStateException("Inconsistency in tree: relative path to parent could not be build.");
            String[] childPathComponents = extractSuffix(neighbor.getIdentifier());
            neighborPath.replaceLastID(new ID(childPathComponents[0]));

            String neighborPathSuffixFree = neighborPath.toValuePath();
            String neighborSuffix = childPathComponents[1];

            if (neighborPathSuffixFree.equals(testPathSuffixFree)) {
                /* Add all the suffixes of duplicates to a list to later test what's the smallest suffix
                 * to use for resolving the path collision.
                 * Suffix 0 means there is a duplicate path without a suffix.
                 * If there is no suffix 0, it means we don't need to resolve the path collision with a suffix
                 * and can just use the suffix free name.
                 */
                if (neighborSuffix != null) {
                    if (neighborSuffix.equals(idSuffix)) duplicateSuffix = true;
                    suffixes.add(Integer.parseInt(neighborSuffix));
                } else {
                    suffixes.add(0);
                }
            }
        }

        int[] testZeroSuffix = {0};
        suffixes.forEach(i -> {if (i == 0) testZeroSuffix[0]++;});
        if (testZeroSuffix[0] > 1) {
            throw new IllegalStateException("Inconsistency in paths: " +
                    "multiple children that are equal to the test path with no suffix " +
                    "is impossible and implies path collisions.");
        }

        if (!suffixes.isEmpty() && (duplicateSuffix || idSuffix.isEmpty())) {
            suffixes.sort(Integer::compare);

            int suffix = suffixes.get(suffixes.size() - 1) + 1;
            /* find the next available suffix */
            for (int i = 0; i < suffixes.size(); i++) {
                /*
                 * if the current suffix is not what it should have been
                 * according to an ascending order, it means that value is
                 * free to use as the smallest available suffix
                 */
                if (suffixes.get(i) != i) {
                    suffix = i;
                    break;
                }
            }

            if (suffix != 0) {
                idSuffix = String.valueOf(suffix);

                while (idSuffix.length() < 3) {
                    idSuffix = "0" + idSuffix;
                }
            }
        }

        String resolvedID = idSuffixFree;
        if (!idSuffix.isEmpty()) resolvedID += "." + idSuffix;

        return resolvedID;
    }

    /**
     * For path collisions we add a number to the id to make it unique again like this "duplicate.001"
     * @param str the string to extract the number suffix in form of .DDD where d is a digit.
     * @return an array where the first element is always the string without the suffix. The second element can be
     *         null, if there was no suffix. If there was a suffix, it is the suffix without the dot.
     */
    public static String[] extractSuffix(String str) {
        if (str.isEmpty()) return new String[]{str, null};

        Pattern pattern = Pattern.compile("\\.(\\d{3,})$");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String suffix = matcher.group(1);
            int suffixLength = suffix != null ? 1 + suffix.length() : 0;
            return new String[]{str.substring(0, str.length() - suffixLength), suffix};
        } else {
            return new String[]{str, null};
        }
    }
}
