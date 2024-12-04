package com.minechinima.model.utils;

import com.minechinima.model.exceptions.PathParsingException;
import com.minechinima.utils.ICopy;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * A doubly linked list of {@link PathSegment}.<br>
 * A value path consists out of path segments that are appended to each other.<br>
 * A path segment has a format for its ID and an ID (String identifier and optionally a UUID).
 * A path segment at the start of the list may have no format.
 */
public class ValuePath implements ICopy<ValuePath> {
    private PathSegment start;
    private PathSegment end;
    private int size = 0;

    public ValuePath(Format idFormat, ID id) {
        this.append(idFormat, id);
    }

    public ValuePath(ID id) {
        this.append(id);
    }

    /**
     * Try to build this ValuePath object from the given string.
     * This inherently means information will be lost,
     * like empty concatenations segments or how a segment with an uuid cannot be
     * converted back to the string identifier.
     * @param path
     * @throws PathParsingException for syntax errors in the path string.
     */
    public ValuePath(String path) throws PathParsingException {
        this.parse(path);
    }

    public ValuePath() { }

    public int getSize() {
        return this.size;
    }

    //TODO possibly performance optimizations of this algorithm
    protected void parse(String path) throws PathParsingException {
        final PathSegment prevStart = this.start;
        final PathSegment prevEnd = this.end;
        this.start = null;
        this.end = null;

        if (path.isEmpty()) return;

        /* if the first format is concat, it is a wrong syntax because the start cannot be concatenated */
        Format lastFound = this.findFormat(path, 0);
        if (lastFound != null && lastFound.getMode() == Format.Mode.CONCAT) {
            throw new PathParsingException("The first format beginning at the first char cannot be concat.");
        }

        StringBuilder currentID = new StringBuilder();

        try {
            int start = lastFound != null ? lastFound.getStart().length() : 0;
            for (int i = start; i < path.length(); i++) {
                /* the last found format was enclose -> search for the end of that format */
                if (lastFound != null && lastFound.getMode() == Format.Mode.ENCLOSE) {
                    /* found possible end formatting char */
                    if (path.charAt(i) == lastFound.getEnd().charAt(0)) {
                        String test = path.substring(i, Math.min(path.length(), i + lastFound.getEnd().length()));
                        /* complete end format found */
                        if (test.equals(lastFound.getEnd())) {
                            this.append(lastFound, this.getIDFromString(currentID.toString()));

                            Format next = null;
                            /*
                             * If the path has not ended yet, then the next path segment must begin with a format again.
                             * If there is no next format and only an id, there is a syntax error in the path string.*/
                            if (i + lastFound.getEnd().length() < path.length()) {
                                next = this.findFormat(path, i + lastFound.getEnd().length());
                                if (next == null) {
                                    throw new PathParsingException("The path did not end after an enclosing path segment, " +
                                            "but there is no next path format.");
                                }
                                i += next.getStart().length();
                            }

                            currentID.setLength(0);
                            i += lastFound.getEnd().length() - 1;
                            lastFound = next;
                            continue;
                        }
                    }
                }
                /* continue searching until we find the next formatting char */
                else {
                    Format nextFormat = this.findFormat(path, i);
                    if (nextFormat != null) {
                        /*
                         * If the last format was concat and the current id is empty, this is illegal, because empty ids
                         * cannot be concatenated
                         */
                        if (lastFound != null && lastFound.getMode() == Format.Mode.CONCAT && currentID.isEmpty()) {
                            throw new PathParsingException("There cannot be an empty id after a concatenation formatting char.");
                        }

                        if (lastFound == null) this.append(this.getIDFromString(currentID.toString()));
                        else this.append(lastFound, this.getIDFromString(currentID.toString()));

                        currentID.setLength(0);
                        lastFound = nextFormat;
                        /* begin at the next char where format ends */
                        i += nextFormat.getStart().length() - 1;
                        continue;
                    }
                }

                currentID.append(path.charAt(i));
            }

            /*
             * If the last-found format was enclosing, it would have set lastFound to null if the end was found.
             * If the last-found format was concat and the current id is empty, this is also a syntax error.
             */
            if (lastFound != null && lastFound.getMode() == Format.Mode.ENCLOSE) {
                throw new PathParsingException("Last found formatting char was enclose but no end was found");
            } else if (lastFound != null && lastFound.getMode() == Format.Mode.CONCAT && currentID.isEmpty()) {
                throw new PathParsingException("Last found formatting char was concatenation but nothing followed after it.");
            }

            /*
             * If we reached this point: the following cases could be true:
             * The last-found format was concat and the current id is not empty.
             * The last-found format is null, meaning not even a concat format was found before,
             * and the currentID is not empty, which must mean the path has no formats at all.
             */
            if (lastFound != null) {
                this.append(lastFound, this.getIDFromString(currentID.toString()));
            } else if (!currentID.isEmpty()) {
                this.append(this.getIDFromString(currentID.toString()));
            }
        } catch (PathParsingException e) {
            this.start = prevStart;
            this.end = prevEnd;
            throw e;
        } catch (Exception e) {
            this.start = prevStart;
            this.end = prevEnd;
            throw new PathParsingException(e);
        }
    }

    /**
     * Find a path format that begins at the given start index.
     * @param path
     * @param startIndex
     * @return
     * @throws PathParsingException
     */
    private Format findFormat(String path, int startIndex) throws PathParsingException {
        if (startIndex >= path.length()) return null;
        Format currentlyFound = null;
        /* go through all formats and find the one that fits best */
        for (Format format : Format.values()) {
            /* possible formatting found, check if the next chars match the entire format */
            if (path.charAt(startIndex) == format.getStart().charAt(0)) {
                String test = path.substring(startIndex, Math.min(path.length(), startIndex + format.getStart().length()));

                if (test.equals(format.getStart())) {
                    /* if the last found format is smaller than the new found format, the new one is correct */
                    if (currentlyFound == null || (currentlyFound.getStart().length() < format.getStart().length())) {
                        currentlyFound = format;
                    } else if (currentlyFound != null && currentlyFound.getStart().length() == format.getStart().length()) {
                        throw new PathParsingException("Found two formatting starts with the same length...");
                    }
                }
            }
        }

        return currentlyFound;
    }

    /**
     * Parses the string as either id or uuid.
     * @param idOrUUID
     * @return
     */
    private ID getIDFromString(String idOrUUID) {
        try {
            UUID uuidTest = UUID.fromString(idOrUUID);
            if (uuidTest.toString().equals(idOrUUID)) {
                return new ID(String.valueOf(Integer.MAX_VALUE), uuidTest);
            }
        } catch (Exception e) { }
        return new ID(idOrUUID);
    }

    @Nullable
    public PathSegment getStart() {
        return this.start;
    }

    @Nullable
    public PathSegment getEnd() {
        return this.end;
    }

    /**
     * Removes as many path segments until the specified size is reached.
     */
    public void reduceToSize(int newSize) {
        while (this.size > newSize) {
            this.pop();
        }
    }

    public PathSegment pop() {
        if (this.start == null || this.end == null) {
            return null;
        }

        PathSegment prevEnd = this.end;
        this.end = prevEnd.prev;
        prevEnd.removeThis();
        if (this.end == null) this.start = null;
        this.size--;

        return prevEnd;
    }

    public void replaceLastID(ID newID) {
        if (this.end == null) return;

        PathSegment newEnd = new PathSegment(this.end.idFormat, newID);
        this.pop();
        this.appendSegment(newEnd);
    }

    /**
     *
     * @param id
     * @throws RuntimeException for illegal node operations
     */
    public void append(ID id) {
        this.appendSegment(new PathSegment(id));
    }

    /**
     *
     * @param idFormat
     * @param id
     * @throws RuntimeException for illegal node operations
     */
    public void append(Format idFormat, ID id) {
        this.appendSegment(new PathSegment(idFormat, id));
    }

    protected void appendSegment(PathSegment segment) {
        if (this.start == null || this.end == null) {
            this.start = segment;
            this.end = this.start;
            this.size++;
        } else {
            this.end.append(segment);
            this.end = segment;
            this.size++;
        }
    }

    /**
     * Builds a path using {@link Format#getEnclosedIdentifier(String)}.
     * <br>Empty identifiers with concatenation mode are skipped.
     * <br>For concatenation mode, the concatenation character is only added between non empty identifiers.
     * @throws IllegalStateException when the ID does not adhere to the enclosure format
     * @return the value path with identifiers.
     */
    public String toValuePath() {
        return this.pathBuilding(false);
    }

    /**
     * Builds a path using {@link Format#getEnclosedIdentifier(String)} and if present {@link Format#getEnclosedIdentifier(String)}.
     * <br>Empty identifiers with concatenation mode are skipped.
     * <br>For concatenation mode, the concatenation character is only added between non empty identifiers.
     * @throws IllegalStateException when the ID does not adhere to the enclosure format
     * @return a path that also contains UUIDs where the ID provides it.
     */
    public String toUniquePath() {
        return this.pathBuilding(true);
    }

    /**
     * @throws IllegalArgumentException when the id does not adhere to the enclosure format, e.g. array requires an integer.
     * @param uniquePath whether to use the UUIDs for building the path string.
     * @return
     */
    private String pathBuilding(boolean uniquePath) {
        if (this.start == null) return "";

        CleanPathBuilder iterator = new CleanPathBuilder(this.start, uniquePath);
        PathSegment current = iterator.nextNonEmpty();
        StringBuilder path = new StringBuilder();

        for (int i = 0; current != null; i++, current = iterator.nextNonEmpty()) {
            if (current.idFormat == null && i != 0) {
                throw new IllegalStateException("Found a path segment with format=null that is not at the start.");
            }

            String idValue = uniquePath && current.id.uuid() != null ?
                    current.id.uuid().toString() : current.id.id();

            if (current.idFormat != null) {
                if (current.idFormat.getMode() == Format.Mode.ENCLOSE) {
                    if (uniquePath && current.id.uuid() != null) {
                        idValue = current.idFormat.getEnclosedUUID(current.id.uuid());
                    } else {
                        idValue = current.idFormat.getEnclosedIdentifier(idValue);
                    }
                } else if (current.idFormat.getMode() == Format.Mode.CONCAT) {
                    path.append(current.idFormat.getStart());
                }
            }

            path.append(idValue);
        }

        return path.toString();
    }

    /**
     * Subtracts the given path from this (copy) path from the start of the path.
     * @param path
     * @return a new value path where the specified path is subtracted from, which can result in an empty path.
     * Returns null if the given path is not at the start of this path.
     */
    @Nullable
    public ValuePath subtractFromStart(ValuePath path) {
        if (path.size > this.size) {
            return null;
        }

        if (path.size == this.size && this.equals(path)) {
            return new ValuePath();
        } else if (path.size == this.size) {
            return null;
        } else if (path.size == 0) {
            return null;
        }

        ValuePath subtracted = new ValuePath();
        for (PathSegment current = this.start, subtractCurrent = path.start;
            current != null; current = current.next, subtractCurrent = subtractCurrent == null ? null : subtractCurrent.next) {
            /* the full path needs to be at the start of this path */
            if (subtractCurrent != null && !subtractCurrent.equals(current)) {
                return null;
            } else if (subtractCurrent == null) {
                subtracted.appendSegment(new PathSegment(current));
            }
        }

        return subtracted;
    }

    /**
     * @return a cleaned value path copy where empty segments are removed
     * and formats are corrected so no syntax error could be produced.
     * The path is corrected in such a way that it could be simply compiled
     * to a correct syntax by appending formatting chars and ids.
     * If the path is empty, start and end will be null.
     */
    public ValuePath getCleanedUniquePath() {
        return this.getCleanedPath(true);
    }

    /**
     * @return a cleaned value path copy where empty segments are removed
     * and formats are corrected so no syntax error could be produced.
     * The path is corrected in such a way that it could be simply compiled
     * to a correct syntax by appending formatting chars and ids.
     * If the path is empty, start and end will be null.
     */
    public ValuePath getCleanedValuePath() {
        return this.getCleanedPath(false);
    }

    protected ValuePath getCleanedPath(boolean unique) {
        if (this.start == null) return new ValuePath();
        CleanPathBuilder iterator = new CleanPathBuilder(this.start, unique);

        while (iterator.nextNonEmpty() != null) { }

        return iterator.list;
    }

    public CleanPathBuilder getValuePathIterator() {
        return new CleanPathBuilder(this.start, false);
    }

    public CleanPathBuilder getUniquePathIterator() {
        return new CleanPathBuilder(this.start, true);
    }

    /**
     * @param o
     * @return true if both have the same path segments, not necessarily reference equal.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValuePath valuePath)) return false;

        if (valuePath.size != this.size) return false;
        if (valuePath.start == null && this.start == null) return true;

        PathSegment currentCompare = valuePath.start;
        PathSegment currentThis = this.start;
        for (;currentCompare != null && currentThis != null;
             currentCompare = currentCompare.next, currentThis = currentThis.next) {
            if (!currentCompare.equals(currentThis)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toValuePath());
    }

    @Override
    public String toString() {
        return "{Unique path: " + this.toUniquePath() +"; value path: " + this.toValuePath() + "}";
    }

    @Override
    public ValuePath copy() {
        ValuePath copy = new ValuePath();

        for (PathSegment current = this.start; current != null; current = current.next) {
            copy.appendSegment(new PathSegment(current));
        }

        return copy;
    }

    /**
     * A doubly linked list node.
     */
    public static class PathSegment {
        @Nullable
        private PathSegment prev;
        /**
         * The format used for this id.
         * Can be null, but only at the beginning of the linked list.
         */
        @Nullable
        private final Format idFormat;
        private final ID id;
        @Nullable
        private PathSegment next;

        public PathSegment(ID id) {
            this(null, id);
        }

        public PathSegment(@Nullable Format format, ID id) {
            if (id == null) {
                throw new NullPointerException("ID cannot be null when format is not null");
            }

            if (format != null) {
                if (!format.isValidStringForEnclosure(id.id())) {
                    throw new IllegalArgumentException("The given id is not a valid string for the format!");
                }
            }

            this.id = id;
            this.idFormat = format;
        }

        public PathSegment(PathSegment copyFrom) {
            this(copyFrom.idFormat, copyFrom.id);
        }

        public ID getID() {
            return this.id;
        }

        @Nullable
        public Format getFormat() {
            return this.idFormat;
        }

        @Nullable
        public PathSegment getNext() {
            return this.next;
        }

        @Nullable
        public PathSegment getPrev() {
            return this.prev;
        }



        /* NODE OPERATIONS */


        /**
         *
         * @param path
         * @throws IllegalStateException if the last format is null, another path segment cannot be appended.
         */
        protected void append(PathSegment path) {
            if (path.idFormat == null) {
                throw new IllegalArgumentException("Can't append a path segment when it has no format.");
            }

            if (path.id == null) {
                throw new IllegalArgumentException("Cannot append a path segment with no id.");
            }

            if (this.next == null) {
                this.next = path;
                path.prev = this;
            } else {
                this.next.append(path);
            }
        }

        /**
         * Removes this node
         */
        protected void removeThis() {
            if (this.prev != null) {
                this.prev.next = this.next;
                if (this.next != null) this.next.prev = this.prev;
                this.prev = null;
                this.next = null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof PathSegment s1)) {
                return false;
            }

            if ((this.getFormat() == null || s1.getFormat() == null) && (this.getFormat() != s1.getFormat())) {
                return false;
            }

            return this.getFormat() == s1.getFormat() && this.id.equals(s1.id);
        }

        public boolean equals(PathSegment s1, boolean useUnique) {
            if ((this.getFormat() == null || s1.getFormat() == null) && (this.getFormat() != s1.getFormat())) {
                return false;
            }

            String id0 = this.getID().id();
            String id1 = s1.getID().id();

            if (useUnique) {
                id0 = this.getID().uuid() != null ? this.getID().uuid().toString() : id0;
                id1 = s1.getID().uuid() != null ? s1.getID().uuid().toString() : id1;
            }

            return this.getFormat() == s1.getFormat() && id0.equals(id1);
        }
    }

    /**
     * Builds a new linked list where no empty elements are contained.
     */
    public static class CleanPathBuilder {
        private PathSegment current;
        /**
         * Whether to use the uuid for checking for empty elements.
         */
        private final boolean uniquePath;

        /**
         * The new fully copied cleaned linked list of PathSegments.
         */
        private final ValuePath list = new ValuePath();

        protected CleanPathBuilder(PathSegment start, boolean uniquePath) {
            this.current = start;
            this.uniquePath = uniquePath;
        }

        public void setCurrent(PathSegment current) {
            this.current = current;
        }

        public ValuePath getBuiltList() {
            return this.list;
        }

        @Nullable
        public PathSegment getStart() {
            return this.list.start;
        }

        @Nullable
        public PathSegment getEnd() {
            return this.list.end;
        }

        /**
         * Appends a path and cleans it.
         * @return the appended path but cleaned (copied). Could be an empty path.
         */
        public ValuePath append(ValuePath append) {
            this.current = append.start;
            PathSegment newStart = this.nextNonEmpty();
            PathSegment newEnd = newStart;
            PathSegment current;
            int i = newStart != null ? 1 : 0;

            while ((current = this.nextNonEmpty()) != null) {
                newEnd = current;
                i++;
            }

            ValuePath path = new ValuePath();
            path.start = newStart;
            path.end = newEnd;
            path.size = i;

            return path;
        }

        @Nullable
        public PathSegment nextNonEmpty() {
            for (;this.current != null; this.current = this.current.next) {
                boolean currentIDEmpty = this.current.id.id().isEmpty();

                if (this.uniquePath) {
                    currentIDEmpty = this.current.id.uuid() == null && currentIDEmpty;
                }

                /* condition to find a start element */
                if (this.list.end == null) {
                    /*
                     * if id is not empty, we can append as the format does not matter
                     * if id is empty we can only append if the format is not concat, as an empty id cannot be concatenated
                     */
                    if (!currentIDEmpty || (this.current.idFormat != null && this.current.idFormat.getMode() != Format.Mode.CONCAT)) {
                        /* a start element cannot have concat, as there is nothing before that element to concatenate with  */
                        if (this.current.idFormat != null && this.current.idFormat.getMode() != Format.Mode.CONCAT) {
                            this.list.appendSegment(new PathSegment(this.current));
                        } else {
                            this.list.append(this.current.id);
                        }

                        this.current = this.current.next;
                        return this.list.end;
                    }
                } else {
                    if (this.current.idFormat == null) {
                        throw new IllegalStateException("A path segment with no format was found that is not at the start.");
                    }

                    /*
                     * if the current id is not empty, the formatting of it does not matter
                     * if the current id is empty, it can only be appended if it is not concat
                     */
                    if (!currentIDEmpty || this.current.idFormat.getMode() != Format.Mode.CONCAT) {
                        this.list.appendSegment(new PathSegment(this.current));
                        this.current = this.current.next;
                        return this.list.end;
                    }
                }
            }

            return null;
        }
    }

    public enum Format {
        /*
         * Every end, start value must be unique, so you can get the pathformat by the char in a string.
         */
        PATH("."),
        ARRAY("[", "]"),
        MAP("[\"", "\"]");

        private final String start;
        private final String end;
        private final Mode mode;

        Format(String start, String end) {
            this.start = start;
            this.end = end;
            this.mode = Mode.ENCLOSE;
        }

        Format(String concat) {
            this.start = concat;
            this.end = concat;
            this.mode = Mode.CONCAT;
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }

        public Mode getMode() {
            return this.mode;
        }

        /**
         * <h2>Conditions for the value:</h2>
         * <h3>ARRAY</h3>
         * <ul>
         *     <li>cannot be empty</li>
         *     <li>must be an integer</li>
         *     <li>cannot be a negative integer</li>
         * </ul>
         * @param value the string to test
         * @return false when the conditions are not met
         */
        public boolean isValidStringForEnclosure(String value) {
            if (this == ARRAY) {
                if (value.isEmpty()) return false;

                try {
                    int number = Integer.parseInt(value);
                    return number >= 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            return true;
        }

        /**
         * @throws IllegalStateException when this identifier does not adhere to the enclosure format.
         */
        public final String getEnclosedIdentifier(String identifier) {
            if (!this.isValidStringForEnclosure(identifier)) {
                throw new IllegalStateException("The ID " + identifier + " does not adhere to its enclosure format of " + this);
            }

            if (this.getMode() == Mode.ENCLOSE) {
                identifier = this.getStart() + identifier + this.getEnd();
            }

            return identifier;
        }

        public final String getEnclosedUUID(UUID uuid) {
            String string = uuid.toString();
            if (this.getMode() == Mode.ENCLOSE) {
                string = this.getStart() + string + this.getEnd();
            }

            return string;
        }

        /**
         * The mode in which the enclosing type is to be used.
         * Example: Concat means you can't add the start value of the enclosing type if there hasn't been any value before
         * the to be enclosed value.
         */
        public enum Mode {
            /**
             * Only enclose the value fully if there have been values before it.
             */
            CONCAT,
            /**
             * Always enclose the value fully with start and end values of {@link Format}.
             */
            ENCLOSE
        }
    }
}
