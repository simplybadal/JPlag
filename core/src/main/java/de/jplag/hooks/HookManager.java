package de.jplag.hooks;

import java.util.*;

/**
 * Manages hook for JPlag. Do not use this when using the JPlag api.
 */
public class HookManager {
    private static final ThreadLocal<HookManager> currentThreadHookManager = ThreadLocal.withInitial(() -> null);
    private final Map<Class<? extends Hook<?>>, List<Hook<?>>> hooks;

    public HookManager() {
        this.hooks = new HashMap<>();
    }

    public <T extends Hook<?>> void setHook(T value) {
        this.hooks.putIfAbsent(value.hookType(), new ArrayList<>());
        this.hooks.get(value.hookType()).add(value);
    }

    @SuppressWarnings("unchecked")
    private <T> void invokeHookByType(Class<? extends Hook<T>> type, T parameter) {
        this.hooks.getOrDefault(type, Collections.emptyList()).forEach(it -> ((Hook<T>) it).invoke(parameter));
    }

    public static void init(HookManager manager) {
        if (currentThreadHookManager.get() == null) {
            currentThreadHookManager.set(manager);
        }
    }

    public static void init() {
        init(new HookManager());
    }

    public static void destroy() {
        currentThreadHookManager.remove();
    }

    public static <T extends Hook<?>> void createHook(T value) {
        currentThreadHookManager.get().setHook(value);
    }

    public static <T> void invokeHook(Class<? extends Hook<T>> type, T parameter) {
        currentThreadHookManager.get().invokeHookByType(type, parameter);
    }
}
