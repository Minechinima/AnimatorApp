package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.api.IData;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.proxy.schema.ProxyFactoryRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * To map a database {@link BaseStructure} to its corresponding proxy. This mapping instance should be shared amongst all
 * proxies in one context where a 1-1 mapping is guaranteed.
 */
public class BasestructureProxyPool {
    private final BaseValueHierarchy<?> root;
    private final ProxyFactoryRegistry factoryRegistry;
    private final Map<BaseStructure, IData> nodeToProxy = new IdentityHashMap<>();
    private final Map<IData, BaseStructure> proxyToNode = new IdentityHashMap<>();

    public BasestructureProxyPool(@NotNull BaseValueHierarchy<?> root, @NotNull ProxyFactoryRegistry factoryRegistry) {
        this.root = root;
        this.factoryRegistry = factoryRegistry;
        this.root.getEventBus().subscribe(this);
    }

    /**
     * Stop lifecycle tracking and clear all mappings for this context.
     */
    public void detach() {
        this.root.getEventBus().unsubscribe(this);
        this.nodeToProxy.clear();
        this.proxyToNode.clear();
    }

    @SubscribeEvent(type = SubscribeEvent.Type.IN_CHILDREN)
    private void onRemove(RemoveFromTreeEvent event) {
        this.unmapSubTree(event.getTrigger());
    }

    @SubscribeEvent(type = SubscribeEvent.Type.IN_CHILDREN)
    private void onAdd(AddToTreeEvent event) {
        this.repairSubTreeMappings(event.getTrigger());
    }

    public IData getProxyForNode(@NotNull BaseStructure structure) {
        IData mapped = this.nodeToProxy.get(structure);
        if (mapped != null) {
            return mapped;
        }

        IData proxy = this.factoryRegistry.createProxyForNode(structure, this);
        this.mapOrThrow(structure, proxy);
        return proxy;
    }

    public @Nullable BaseStructure getNodeForProxy(@Nullable IData proxy) {
        if (proxy == null) return null;
        return this.proxyToNode.get(proxy);
    }

    private void mapOrThrow(@NotNull BaseStructure node, @NotNull IData proxy) {
        IData existingProxy = this.nodeToProxy.get(node);
        if (existingProxy != null && existingProxy != proxy) {
            throw new IllegalStateException("1-1 proxy mapping violated: node is already mapped to another proxy.");
        }

        BaseStructure existingNode = this.proxyToNode.get(proxy);
        if (existingNode != null && existingNode != node) {
            throw new IllegalStateException("1-1 proxy mapping violated: proxy is already mapped to another node.");
        }

        this.nodeToProxy.put(node, proxy);
        this.proxyToNode.put(proxy, node);
    }

    private void repairSubTreeMappings(@NotNull BaseStructure node) {
        IData proxy = this.nodeToProxy.get(node);
        if (proxy != null) {
            this.mapOrThrow(node, proxy);
        }

        if (node instanceof BaseValueHierarchy<?> hierarchy) {
            for (BaseStructure child : hierarchy.getChildren()) {
                this.repairSubTreeMappings(child);
            }
        }
    }

    private void unmapSubTree(@NotNull BaseStructure node) {
        if (node instanceof BaseValueHierarchy<?> hierarchy) {
            for (BaseStructure child : hierarchy.getChildren()) {
                this.unmapSubTree(child);
            }
        }

        IData removedProxy = this.nodeToProxy.remove(node);
        if (removedProxy != null) {
            this.proxyToNode.remove(removedProxy);
        }
    }
}
