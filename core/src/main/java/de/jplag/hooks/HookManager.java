package de.jplag.hooks;

import java.util.*;
import java.util.logging.Logger;

/**
 * Manages hook for JPlag. Do not use this when using the JPlag api.
 */
public class HookManager {
    private static final ThreadLocal<HookManager> currentThreadHookManager = ThreadLocal.withInitial(() -> null);
    private static final Logger LOGGER = Logger.getLogger(HookManager.class.getName());
    private final Map<Class<? extends Hook<?>>, List<Hook<?>>> hooks;

    public HookManager() {
        this.hooks = new HashMap<>();
    }

    public <T extends Hook<?>> void setHook(Class<T> hookType, T value) {
        if (this.isHookTypeValid(hookType)) {
            this.hooks.putIfAbsent(hookType, new ArrayList<>());
            this.hooks.get(hookType).add(value);
        } else {
            throw new IllegalArgumentException(String.format("The type %s is not a valid hook type.", hookType.getName()));
        }
    }

    public <T extends Hook<?>> void setHook(T value) {
        if (!addHookByInferredTypes(value.getClass(), value)) {
            throw new IllegalArgumentException(
                    String.format("No valid hook type could be found in the inheritance tree of %s.", value.getClass().getName()));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Hook<?>> boolean addHookByInferredTypes(Class<?> type, T value) {
        boolean valid = false;

        if (isHookTypeValid(type)) {
            this.setHook((Class<T>) type, value);
            valid = true;
        }

        if (type.getSuperclass() != null) {
            if (addHookByInferredTypes(type.getSuperclass(), value)) {
                valid = true;
            }
        }

        for (Class<?> anInterface : type.getInterfaces()) {
            if (addHookByInferredTypes(anInterface, value)) {
                valid = true;
            }
        }

        return valid;
    }

    @SuppressWarnings("unchecked")
    private <T> void invokeHookByType(Class<? extends Hook<T>> type, T parameter) {
        this.hooks.getOrDefault(type, Collections.emptyList()).forEach(it -> ((Hook<T>) it).invoke(parameter));
    }

    private boolean isHookTypeValid(Class<?> type) {
        return type.isInterface() && Hook.class.isAssignableFrom(type) && !type.equals(Hook.class);
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

    public static <T extends Hook<?>> void createHook(Class<T> hookType, T value) {
        currentThreadHookManager.get().setHook(hookType, value);
    }

    /**
     * Registers the hook with all valid type in the inheritance tree of the given hook.
     * @param value The hook to add
     * @param <T> The type of the added hook
     */
    public static <T extends Hook<?>> void createHook(T value) {
        currentThreadHookManager.get().setHook(value);
    }

    public static <T> void invokeHook(Class<? extends Hook<T>> type, T parameter) {
        if (currentThreadHookManager.get() != null) {
            currentThreadHookManager.get().invokeHookByType(type, parameter);
        } else {
            LOGGER.info("Cannot invoke hooks, since no hook manager is currently available. This should only happen for internal tests of JPlag.");
        }
    }
}
