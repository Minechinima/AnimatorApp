package com.minechinima.depsgraph;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * Manages a datascope and ensures storage stays efficient.
 * Used to map nodes in the dependency graph to the data they represent.
 */
public class DataScope {
    /**
     * Overrides datatrees. Does not contain data trees.
     * @implNote should be final - adding later on could break uniqueness of mapping.
     */
    private final Set<IData> specialAttributes = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * Scope for an entire data tree and its children - adding later on could break uniqueness of mapping.
     */
    private final Set<IDataTree> dataTrees = Collections.newSetFromMap(new IdentityHashMap<>());

    public DataScope(Set<IData> attribtues, Set<IDataTree> tree) {
        this.specialAttributes.addAll(attribtues);
        this.dataTrees.addAll(tree);
        this.clean(this.specialAttributes, this.dataTrees);
    }

    public DataScope(IData... data) {
        this.specialAttributes.addAll(List.of(data));
        this.clean(this.specialAttributes, this.dataTrees);
    }

    public Set<IData> getSpecialAttributes() {
        return new HashSet<>(specialAttributes);
    }

    public Set<IDataTree> getDataTrees() {
        return new HashSet<>(dataTrees);
    }

    /**
     * @param data
     * @return true if the given data is in the scope of this data.
     */
    public boolean isInScope(IData data) {
        if (this.specialAttributes.contains(data)) return true;

        for (IDataTree tree : this.dataTrees) {
            if (data == tree || data.isChildOf(tree)) return true;
        }

        return false;
    }

    public boolean intersects(DataScope other) {
        Set<IData> unionThis = new HashSet<>();
        unionThis.addAll(this.specialAttributes);
        unionThis.addAll(this.dataTrees);

        /* first check if one of our attributes is in scope of the other
        * -> means one of our attributes is a child of the other data.
        * Does not cover the case when the other has an attribute that is a child of our scope */
        for (IData thisData : unionThis) {
            if (other.isInScope(thisData)) return true;
        }

        Set<IData> unionOther = new HashSet<>();
        unionOther.addAll(other.specialAttributes);
        unionOther.addAll(other.dataTrees);

        /* check if one of the other attributes is in our scope */
        for (IData otherData : unionOther) {
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
        Set<IData> attribtuesMerged = new HashSet<>();
        attribtuesMerged.addAll(this.specialAttributes);
        attribtuesMerged.addAll(other.specialAttributes);

        Set<IDataTree> dataTrees = new HashSet<>();
        dataTrees.addAll(this.dataTrees);
        dataTrees.addAll(other.dataTrees);

        return new DataScope(attribtuesMerged, dataTrees);
    }

    private void clean(Set<IData> attribtues, Set<IDataTree> trees) {
        /*
         * First move all attributes that are trees to the tree set
         * do this first to ensure that we can check if other attributes are children of the trees in the next step.
         * Possible case: a tree is at the end of the attribute set that is parent of attributes.
         */
        Iterator<IData> it = attribtues.iterator();
        while (it.hasNext()) {
            IData attribute = it.next();
            if (attribute instanceof IDataTree hierarchy) {
                it.remove();
                trees.add(hierarchy);
            }
        }

        /* second pass: remove datatrees that are child of other data trees
         * This can reduce the size of the data tree and improve performance of the next phase */
        Iterator<IDataTree> it2 = trees.iterator();
        while (it2.hasNext()) {
            IDataTree tree = it2.next();
            for (IDataTree otherTree : trees) {
                /* we iterate through the same set - skip the equal instance */
                if (otherTree == tree) continue;
                if (tree.isChildOf(otherTree)) {
                    it2.remove();
                    break;
                }
            }
        }

        /* remove attributes that are children of stored trees - unnecessary to store them separately */
        it = attribtues.iterator();
        while (it.hasNext()) {
            IData attribute = it.next();
            /* check for every tree if attribute is child */
            for (IDataTree tree : trees) {
                if (attribute.isChildOf(tree)) {
                    it.remove();
                    break;
                }
            }
        }
    }
}
