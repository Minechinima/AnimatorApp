package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * Manages a datascope and ensures storage stays efficient.
 */
public class DataScope {
    /**
     * Overrides datatrees. Does not contain data trees.
     * @implNote should be final - adding later on could break uniqueness of mapping.
     */
    private final Set<BaseStructure> specialAttributes = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * Scope for an entire data tree and its children - adding later on could break uniqueness of mapping.
     */
    private final Set<BaseValueHierarchy> dataTrees = Collections.newSetFromMap(new IdentityHashMap<>());

    public DataScope(Set<BaseStructure> attribtues, Set<BaseValueHierarchy> tree) {
        this.specialAttributes.addAll(attribtues);
        this.dataTrees.addAll(tree);
        this.clean(this.specialAttributes, this.dataTrees);
    }

    public Set<BaseStructure> getSpecialAttributes() {
        return new HashSet<>(specialAttributes);
    }

    public Set<BaseValueHierarchy> getDataTrees() {
        return new HashSet<>(dataTrees);
    }

    /**
     * @param data
     * @return true if the given data is in the scope of this data.
     */
    public boolean isInScope(BaseStructure data) {
        if (this.specialAttributes.contains(data)) return true;

        for (BaseValueHierarchy tree : this.dataTrees) {
            if (data == tree || data.isChildOf(tree)) return true;
        }

        return false;
    }

    public boolean intersects(DataScope other) {
        Set<BaseStructure> unionThis = new HashSet<>();
        unionThis.addAll(this.specialAttributes);
        unionThis.addAll(this.dataTrees);

        /* first check if one of our attributes is in scope of the other
        * -> means one of our attributes is a child of the other data.
        * Does not cover the case when the other has an attribute that is a child of our scope */
        for (BaseStructure thisData : unionThis) {
            if (other.isInScope(thisData)) return true;
        }

        Set<BaseStructure> unionOther = new HashSet<>();
        unionOther.addAll(other.specialAttributes);
        unionOther.addAll(other.dataTrees);

        /* check if one of the other attributes is in our scope */
        for (BaseStructure otherData : unionOther) {
            if (this.isInScope(otherData)) return true;
        }

        return false;
    }

    /**
     * @param other
     * @return a new DataScope that is the merge of this and the other.
     */
    @Contract(pure = true)
    public DataScope merge(DataScope other) {
        Set<BaseStructure> attribtuesMerged = new HashSet<>();
        attribtuesMerged.addAll(this.specialAttributes);
        attribtuesMerged.addAll(other.specialAttributes);

        Set<BaseValueHierarchy> dataTrees = new HashSet<>();
        dataTrees.addAll(this.dataTrees);
        dataTrees.addAll(other.dataTrees);

        return new DataScope(attribtuesMerged, dataTrees);
    }

    private void clean(Set<BaseStructure> attribtues, Set<BaseValueHierarchy> trees) {
        Iterator<BaseStructure> it = attribtues.iterator();
        while (it.hasNext()) {
            /* move data nodes that are trees to the tree set */
            BaseStructure attribute = it.next();
            if (attribute instanceof BaseValueHierarchy<?> hierarchy) {
                it.remove();
                trees.add(hierarchy);
                continue;
            }

            /* remove attributes that are children of other trees - unnecessary to store them separately */
            for (BaseValueHierarchy<?> tree : trees) {
                if (attribute.isChildOf(tree)) {
                    it.remove();
                    break;
                }
            }
        }

        Iterator<BaseValueHierarchy> it2 = trees.iterator();
        while (it2.hasNext()) {
            BaseValueHierarchy tree = it2.next();
            /* remove trees that are children of other trees - unecessary to store them separately */
            for (BaseValueHierarchy<?> otherTree : trees) {
                if (tree.isChildOf(otherTree)) {
                    it2.remove();
                    break;
                }
            }
        }
    }
}
