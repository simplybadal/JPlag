package de.jplag.hooks;

public class HookBuilder {
    private final HookManager manager;

    public HookBuilder() {
        this.manager = new HookManager();
    }

    public <T extends Hook<?>> HookBuilder register(T hook) {
        this.manager.setHook(hook);
        return this;
    }

    public HookManager buildHookManager() {
        return this.manager;
    }
}
