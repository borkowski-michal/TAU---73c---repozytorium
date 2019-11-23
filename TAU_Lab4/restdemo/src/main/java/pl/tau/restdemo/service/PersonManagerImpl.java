package pl.tau.restdemo.service;

import org.springframework.stereotype.Component;
import pl.tau.restdemo.domain.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tp on 24.04.17.
 */
@Component
public class PersonManagerImpl implements  PersonManager {

    private Connection connection;

    private PreparedStatement addPersonStmt;
    private PreparedStatement getAllPersonsStmt;

    private PreparedStatement deletePersonStmt;

    private PreparedStatement getPersonStmt;

    private PreparedStatement updatePersonStmt;

	private PreparedStatement deleteAllPersonsStmt;

    public PersonManagerImpl(Connection connection) throws SQLException {
        this.connection = connection;
        if (!isDatabaseReady()) {
            createTables();
        }
        setConnection(connection);
    }

    public PersonManagerImpl() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/workdb");
        if (!isDatabaseReady()) {
            createTables();
        }
        this.setConnection(this.connection);
    }

    public void createTables() throws SQLException {
        connection.createStatement()
                .executeUpdate("CREATE TABLE " + "Person(id bigint GENERATED BY DEFAULT AS IDENTITY, "
                        + "name varchar(20) NOT NULL, " + "yob integer)");
    }

    public boolean isDatabaseReady() {
        try {
            ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
            boolean tableExists = false;
            while (rs.next()) {
                if ("Person".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    tableExists = true;
                    break;
                }
            }
            return tableExists;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int addPerson(Person person) {
        int count = 0;
        try {
            addPersonStmt.setString(1, person.getName());
            addPersonStmt.setInt(2, person.getYob());
            count = addPersonStmt.executeUpdate();
            ResultSet generatedKeys = addPersonStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                person.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        return count;
    }

    public List<Person> getAllPersons() {
        List<Person> persons = new LinkedList<>();
        try {
            ResultSet rs = getAllPersonsStmt.executeQuery();

            while (rs.next()) {
                Person p = new Person();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setYob(rs.getInt("yob"));
                persons.add(p);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        return persons;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        addPersonStmt = connection.prepareStatement(
            "INSERT INTO Person (name, yob) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        deletePersonStmt = connection.prepareStatement("DELETE FROM Person where id = ?");
        deleteAllPersonsStmt = connection.prepareStatement("DELETE FROM Person");
        getAllPersonsStmt = connection.prepareStatement("SELECT id, name, yob FROM Person ORDER BY id");
        getPersonStmt = connection.prepareStatement("SELECT id, name, yob FROM Person WHERE id = ?");
        updatePersonStmt = connection.prepareStatement("UPDATE Person SET name=?,yob=? WHERE id = ?");
    }

    @Override
    public int deletePerson(Person person) {
        try {
            deletePersonStmt.setLong(1, person.getId());
            return deletePersonStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
    }

    @Override
    public int updatePerson(Person person) throws SQLException {
        int count = 0;
        try {
            updatePersonStmt.setString(1, person.getName());
            updatePersonStmt.setInt(2, person.getYob());
            updatePersonStmt.setLong(3, person.getId());
            count = updatePersonStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        if (count <= 0)
            throw new SQLException("Person not found for update");
        return count;
    }

    @Override
    public Person getPerson(long id) throws SQLException {
        try {
            getPersonStmt.setLong(1, id);
            ResultSet rs = getPersonStmt.executeQuery();

            if (rs.next()) {
                Person p = new Person();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setYob(rs.getInt("yob"));
                return p;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
        throw new SQLException("Person with id " + id + " does not exist");
    }

    @Override
    public String introduceSelf() {
        return null;
    }

    @Override
    public int deleteAll() {
        try {
            return deleteAllPersonsStmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage() + "\n" + e.getStackTrace().toString());
        }
    }
}
