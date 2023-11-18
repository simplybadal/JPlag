package de.jplag.hooks;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jplag.JPlag;
import de.jplag.TestBase;
import de.jplag.exceptions.ExitException;
import de.jplag.java.JavaLanguage;
import de.jplag.options.JPlagOptions;

public class HooksTest extends TestBase {
    @Test
    void testHooksInvoked() throws ExitException {
        JPlag jPlag = new JPlag(new JPlagOptions(new JavaLanguage(), Set.of(new File(getBasePath("FilesAsSubmissions"))), Collections.emptySet()));
        AtomicInteger countImplicit = new AtomicInteger();
        AtomicInteger countExplicit = new AtomicInteger();

        jPlag.registerHook((ParseHook) (it) -> countImplicit.getAndIncrement());
        jPlag.registerHook(ParseHook.class, (it) -> countExplicit.getAndIncrement());

        jPlag.run();

        Assertions.assertEquals(2, countImplicit.get());
        Assertions.assertEquals(2, countExplicit.get());
    }
}
