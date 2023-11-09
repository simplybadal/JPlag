package de.jplag.hooks;

import de.jplag.Submission;

public interface ParseHook extends Hook<Submission> {
    @Override
    default Class<? extends Hook<?>> hookType() {
        return ParseHook.class;
    }
}
