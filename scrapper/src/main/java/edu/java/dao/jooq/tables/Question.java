/*
 * This file is generated by jOOQ.
 */
package edu.java.dao.jooq.tables;


import edu.java.dao.jooq.DefaultSchema;
import edu.java.dao.jooq.Keys;
import edu.java.dao.jooq.tables.records.QuestionRecord;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Question extends TableImpl<QuestionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>QUESTION</code>
     */
    public static final Question QUESTION = new Question();

    /**
     * The class holding records for this type
     */
    @Override
    @NotNull
    public Class<QuestionRecord> getRecordType() {
        return QuestionRecord.class;
    }

    /**
     * The column <code>QUESTION.LINK_ID</code>.
     */
    public final TableField<QuestionRecord, Long> LINK_ID = createField(DSL.name("LINK_ID"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>QUESTION.ANSWER_COUNT</code>.
     */
    public final TableField<QuestionRecord, Integer> ANSWER_COUNT = createField(DSL.name("ANSWER_COUNT"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>QUESTION.COMMENT_COUNT</code>.
     */
    public final TableField<QuestionRecord, Integer> COMMENT_COUNT = createField(DSL.name("COMMENT_COUNT"), SQLDataType.INTEGER, this, "");

    private Question(Name alias, Table<QuestionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Question(Name alias, Table<QuestionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>QUESTION</code> table reference
     */
    public Question(String alias) {
        this(DSL.name(alias), QUESTION);
    }

    /**
     * Create an aliased <code>QUESTION</code> table reference
     */
    public Question(Name alias) {
        this(alias, QUESTION);
    }

    /**
     * Create a <code>QUESTION</code> table reference
     */
    public Question() {
        this(DSL.name("QUESTION"), null);
    }

    public <O extends Record> Question(Table<O> child, ForeignKey<O, QuestionRecord> key) {
        super(child, key, QUESTION);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    @NotNull
    public UniqueKey<QuestionRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_E9;
    }

    @Override
    @NotNull
    public List<ForeignKey<QuestionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CONSTRAINT_E);
    }

    private transient Link _link;

    /**
     * Get the implicit join path to the <code>PUBLIC.LINK</code> table.
     */
    public Link link() {
        if (_link == null)
            _link = new Link(this, Keys.CONSTRAINT_E);

        return _link;
    }

    @Override
    @NotNull
    public Question as(String alias) {
        return new Question(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public Question as(Name alias) {
        return new Question(alias, this);
    }

    @Override
    @NotNull
    public Question as(Table<?> alias) {
        return new Question(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Question rename(String name) {
        return new Question(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Question rename(Name name) {
        return new Question(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    @NotNull
    public Question rename(Table<?> name) {
        return new Question(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public Row3<Long, Integer, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Long, ? super Integer, ? super Integer, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Long, ? super Integer, ? super Integer, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
