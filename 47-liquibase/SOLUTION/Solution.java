package com.learning.liquibase;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeLog;
import liquibase.change.AddColumnChange;
import liquibase.change.CreateTableChange;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;

public class LiquibaseSolution {

    public Liquibase createLiquibase(DataSource dataSource) throws Exception {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
            new liquibase.database.jdbc.JdbcConnection(dataSource.getConnection())
        );
        return new Liquibase("db/changelog.xml", new File("."), database);
    }

    public void update(Liquibase liquibase) throws Exception {
        liquibase.update(new Contexts(), new LabelExpression());
    }

    public void rollback(Liquibase liquibase, String tag) throws Exception {
        liquibase.rollback(tag, new Contexts(), new LabelExpression());
    }

    public void rollbackCount(Liquibase liquibase, int count) throws Exception {
        liquibase.rollback(count, new Contexts(), new LabelExpression());
    }

    public List<ChangeSet> getPendingChangeSets(Liquibase liquibase) throws Exception {
        return liquibase.getChangeSetIterator(new Contexts(), new LabelExpression()).toList();
    }

    public void tag(Liquibase liquibase, String tag) throws Exception {
        liquibase.tag(tag);
    }

    public void clearCheckSums(Liquibase liquibase) throws Exception {
        liquibase.clearCheckSums();
    }

    public void validate(Liquibase liquibase) throws Exception {
        liquibase.validate();
    }

    public CreateTableChange createTable(String tableName, String... columns) {
        CreateTableChange change = new CreateTableChange();
        change.setTableName(tableName);
        return change;
    }

    public AddColumnChange addColumn(String tableName, String columnName, String type) {
        AddColumnChange change = new AddColumnChange();
        change.setTableName(tableName);
        change.setColumnName(columnName);
        change.setType(type);
        return change;
    }
}