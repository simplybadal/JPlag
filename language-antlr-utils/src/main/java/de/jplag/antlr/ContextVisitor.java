package de.jplag.antlr;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import de.jplag.TokenType;
import de.jplag.semantics.CodeSemantics;
import de.jplag.semantics.VariableRegistry;

/**
 * The visitor for nodes, or contexts.
 * @param <T> The antlr type of the node.
 */
public class ContextVisitor<T extends ParserRuleContext> extends AbstractVisitor<T> {
    private final List<Consumer<HandlerData<T>>> exitHandlers;
    private TokenType exitTokenType;
    private Function<T, CodeSemantics> exitSemantics;

    ContextVisitor(Predicate<T> condition) {
        super(condition);
        this.exitHandlers = new ArrayList<>();
    }

    /**
     * Add an action the visitor runs upon exiting the entity.
     * @param handler The action, takes the entity and the variable registry as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onExit(BiConsumer<T, VariableRegistry> handler) {
        exitHandlers.add(handlerData -> handler.accept(handlerData.entity(), handlerData.variableRegistry()));
        return this;
    }

    /**
     * Add an action the visitor runs upon exiting the entity.
     * @param handler The action, takes the entity as parameter.
     * @return Self
     */
    public AbstractVisitor<T> onExit(Consumer<T> handler) {
        exitHandlers.add(handlerData -> handler.accept(handlerData.entity()));
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon exiting the entity. Should only be invoked once per visitor.
     * @param tokenType The type of the token.
     * @return Self
     */
    public ContextVisitor<T> mapExit(TokenType tokenType) {
        exitTokenType = tokenType;
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering and one upon exiting the entity. Should only be
     * invoked once per visitor.
     * @param enterTokenType The type of the token generated on enter.
     * @param exitTokenType The type of the token generated on exit.
     * @return Self
     */
    public ContextVisitor<T> mapEnterExit(TokenType enterTokenType, TokenType exitTokenType) {
        mapEnter(enterTokenType);
        mapExit(exitTokenType);
        return this;
    }

    /**
     * Tell the visitor that it should generate a token upon entering and one upon exiting the entity. Should only be
     * invoked once per visitor. Alias for {@link #mapEnterExit(TokenType, TokenType)}.
     * @param enterTokenType The type of the token generated on enter.
     * @param exitTokenType The type of the token generated on exit.
     * @return Self
     */
    public ContextVisitor<T> map(TokenType enterTokenType, TokenType exitTokenType) {
        mapEnterExit(enterTokenType, exitTokenType);
        return this;
    }

    @Override
    public ContextVisitor<T> withSemantics(Function<T, CodeSemantics> semantics) {
        super.withSemantics(semantics);
        this.exitSemantics = semantics;
        return this;
    }

    @Override
    public ContextVisitor<T> withSemantics(Supplier<CodeSemantics> semantics) {
        withSemantics(ignore -> semantics.get());
        return this;
    }

    /**
     * Tell the visitor that if it generates a token upon entering the entity, it should have semantics of type loop begin,
     * same for the exit and loop end.
     * @return Self
     */
    public ContextVisitor<T> withLoopSemantics() {
        super.withSemantics(CodeSemantics::createLoopBegin);
        this.exitSemantics = ignore -> CodeSemantics.createLoopEnd();
        return this;
    }

    /**
     * Tell the visitor that the entity represents a local scope.
     * @return Self
     */
    public ContextVisitor<T> addLocalScope() {
        onEnter((ignore, variableRegistry) -> variableRegistry.enterLocalScope());
        onExit((ignore, variableRegistry) -> variableRegistry.exitLocalScope());
        return this;
    }

    /**
     * Tell the visitor that the entity represents a class scope.
     * @return Self
     */
    public ContextVisitor<T> addClassScope() {
        onEnter((ignore, variableRegistry) -> variableRegistry.enterClass());
        onExit((ignore, variableRegistry) -> variableRegistry.exitClass());
        return this;
    }

    /**
     * Exit a given entity, injecting the needed dependencies.
     */
    void exit(HandlerData<T> data) {
        addToken(data, exitTokenType, exitSemantics, ParserRuleContext::getStop);
        exitHandlers.forEach(handler -> handler.accept(data));
    }

    Token extractEnterToken(T entity) {
        return entity.getStart();
    }
}
