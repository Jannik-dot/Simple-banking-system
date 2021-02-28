package banking.database;

import banking.creditcard.CreditCard;
import org.sqlite.SQLiteDataSource;

import java.sql.*;

/**
 * Initializes a light Database and has several methods to interact with it
 */
public class DataBase {
    private Connection conn;

    public DataBase(String fileName) {
        String url = "jdbc:sqlite:" + fileName;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try {
            this.conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initCardsTable();
    }

    /**
     * Adds a credit card to the database
     * @param card
     */
    public  void insertCardQuery(CreditCard card) {
        String query = "INSERT INTO card (number, pin, balance) VALUES(?,?,?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, card.getCardNumber());
            statement.setString(2, card.getPassword());
            statement.setInt(3, card.getBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a creditcard in the database by its cardnumber and returns it
     * @param cardNumber
     * @return Creditcard
     */
    public CreditCard getCard(String cardNumber) {
        CreditCard card = new CreditCard();
        try (Statement statement = conn.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT * FROM card WHERE number = " + cardNumber);
            card.setCardNumber(set.getString("number"));
            card.setPassword(set.getString("pin"));
            card.setBalance(set.getInt("balance"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return card;
    }

    /**
     * Adds income to the specified creditcard
     * @param amount
     * @param card
     */
    public void addIncomeToCard(int amount, CreditCard card) {
        String query = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, amount);
            statement.setString(2, card.getCardNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the specified creditcard
     * @param card
     */
    public void deleteCard(CreditCard card) {
        String query = "DELETE FROM card WHERE number = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, card.getCardNumber());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes specified balance from the given credit card and adds it to the target credit card
     * @param userCard
     * @param targetCardNumber
     * @param amount
     */
    public void makeTransaction(CreditCard userCard, String targetCardNumber, int amount) {
        String queryAdd = "UPDATE card SET balance = balance + ? WHERE number = ?";
        String querySub = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try (PreparedStatement statementAdd = conn.prepareStatement(queryAdd);
             PreparedStatement statementSub = conn.prepareStatement(querySub)) {

            statementAdd.setInt(1, amount);
            statementAdd.setString(2, targetCardNumber);
            statementAdd.executeUpdate();

            statementSub.setInt(1, amount);
            statementSub.setString(2, userCard.getCardNumber());
            statementSub.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initializes the table if it doesnt exist
     */
    private void initCardsTable() {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER," +
                    "number VARCHAR(256)," +
                    "pin VARCHAR(256)," +
                    "balance INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
