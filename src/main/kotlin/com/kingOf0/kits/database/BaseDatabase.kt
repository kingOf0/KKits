package com.kingOf0.kits.database

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.base.PlayerData
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

abstract class BaseDatabase(name: String) : IDatabase {

    init {
        LOGGER.info("+ Using Database: '$name'")
    }

    override suspend fun test(): Boolean {
        return runCatching { executeStatement("select * from config") }.isSuccess
    }

    override suspend fun setConfig(key: String, data: Any?) {
        executePreparedStatement("replace into config(key, value) values(?, ?)") {
            it.setString(1, key)
            it.setString(2, data.toString())
        }
    }

    override suspend fun loadConfig(key: String): String? {
        return executePreparedFind(
            "select value from config where key = ?",
            { it.setString(1, key) },
            { it.getString("value") }
        )
    }

    override suspend fun initialize() {
        executeStatement("create table if not exists config(" +
                "key text not null primary key ," +
                "value text not null);")

        executeStatement("create table if not exists player_data(" +
                "uuid varchar(36) not null primary key ," +
                "data text not null);")
    }

    override suspend fun save(uuid: UUID, data: PlayerData) {
        executePreparedStatement("replace into player_data(uuid, data) values(?, ?)") {
            it.setString(1, uuid.toString())
            it.setString(2, data.serializeData())
        }
    }

    override suspend fun load(uuid: UUID): PlayerData? {
        return executePreparedFind(
            "select data from player_data where uuid = ?",
            { it.setString(1, uuid.toString()) },
            { PlayerData(it.getString("data")) }
        )
    }

    private suspend fun <T> executeQuery(sql: String, strategy: (ResultSet) -> T): MutableList<T>? {
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            statement = connection.createStatement() ?: throw IllegalStateException("Could not create statement")
            resultSet = statement.executeQuery(sql) ?: throw IllegalStateException("Could not execute query")
            val list = mutableListOf<T>()
            while (resultSet.next()) {
                list.add(strategy(resultSet))
            }
            return list
        } finally {
            close(resultSet)
            close(statement)
            close(connection)
        }
    }

    private suspend fun <T> executePreparedQuery(sql: String, consumer: Consumer<PreparedStatement>, strategy: (ResultSet) -> T): MutableList<T>? {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            preparedStatement = connection.prepareStatement(sql) ?: throw IllegalStateException("Could not create prepared statement")
            consumer.accept(preparedStatement)
            resultSet = preparedStatement.executeQuery() ?: throw IllegalStateException("Could not execute query")
            val list = mutableListOf<T>()
            while (resultSet.next()) {
                list.add(strategy(resultSet))
            }
            return list
        } finally {
            close(resultSet)
            close(preparedStatement)
            close(connection)
        }
    }

    /**
     * Executes a query and returns a map of the results with the given strategy
     * @param sql The query to execute
     * @param consumer The consumer to set the prepared statement
     * @param strategy The strategy to get the result
     */
    private suspend fun <K, V> executePreparedQueryMap(sql: String, consumer: Consumer<PreparedStatement>, strategy: (ResultSet) -> Pair<K, V>): HashMap<K, V>? {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            preparedStatement = connection.prepareStatement(sql) ?: throw IllegalStateException("Could not create prepared statement")
            consumer.accept(preparedStatement)
            resultSet = preparedStatement.executeQuery() ?: throw IllegalStateException("Could not execute query")
            val map = HashMap<K, V>()
            while (resultSet.next()) {
                val pair = strategy(resultSet)
                map[pair.first] = pair.second
            }
            return map
        } finally {
            close(resultSet)
            close(preparedStatement)
            close(connection)
        }
    }

    /**
     * Executes a prepared statement and returns the result.
     * @param strategy The strategy that will be used to get the __first__ result.
     */
    private suspend fun <T> executeFind(sql: String, strategy: (ResultSet) -> T): T? {
        var connection: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            statement = connection.createStatement() ?: throw IllegalStateException("Could not create statement")
            resultSet = statement.executeQuery(sql) ?: throw IllegalStateException("Could not execute query")
            return if (resultSet.next()) strategy(resultSet)
            else null
        } finally {
            close(resultSet)
            close(statement)
            close(connection)
        }
    }

    /**
     * Executes a prepared statement and returns the result.
     * @param consumer The consumer that will be used to set the values of the prepared statement.
     * @param strategy The strategy that will be used to get the __first__ result.
     */
    private suspend fun <T> executePreparedFind(sql: String, consumer: Consumer<PreparedStatement>, strategy: (ResultSet) -> T): T? {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            preparedStatement = connection.prepareStatement(sql) ?: throw IllegalStateException("Could not create prepared statement")
            consumer.accept(preparedStatement)
            resultSet = preparedStatement.executeQuery() ?: throw IllegalStateException("Could not execute query")
            return if (resultSet.next()) strategy(resultSet)
            else null
        } finally {
            close(resultSet)
            close(preparedStatement)
            close(connection)
        }
    }

    private suspend fun executeStatement(sql: String): Int {
        var connection: Connection? = null
        var statement: Statement? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            statement = connection.createStatement() ?: throw IllegalStateException("Could not create statement")
            return statement.executeUpdate(sql)
        } finally {
            close(statement)
            close(connection)
        }
    }

    private suspend fun executePreparedStatement(sql: String, consumer: Consumer<PreparedStatement>): Int? {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            preparedStatement = connection.prepareStatement(sql) ?: throw IllegalStateException("Could not create prepared statement")
            consumer.accept(preparedStatement)
            return preparedStatement.executeUpdate()
        } finally {
            close(preparedStatement)
            close(connection)
        }
    }

    /**
     * Executes batch statement
     * @param sql SQL statement
     * @param consumer Consumer that will be called for one time. Before all the batches. Use this to set the parameters that wont change.
     * @param batch Consumer that will be called for each batch. Use this to set the parameters that will change.
     */
    private suspend fun <T, E> executePreparedBatch(sql: String, data: T, consumer: Consumer<PreparedStatement>, batch: BiConsumer<PreparedStatement, E>): IntArray? where T : Iterable<E> {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection() ?: throw IllegalStateException("Could not get connection")
            preparedStatement = connection.prepareStatement(sql) ?: throw IllegalStateException("Could not create prepared statement")
            consumer.accept(preparedStatement)
            for (datum: E in data) {
                batch.accept(preparedStatement, datum)
                preparedStatement.addBatch()
            }
            return preparedStatement.executeBatch()
        } finally {
            close(preparedStatement)
            close(connection)
        }
    }


    override suspend fun setup() {
        register()
        initialize()
    }

    override suspend fun close(closeable: AutoCloseable?) {
        runCatching{ closeable?.close() }
    }

    abstract override suspend fun getConnection(): Connection?


}