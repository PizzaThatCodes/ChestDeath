package me.pizzalover.chestdeath.database;


import me.pizzalover.chestdeath.Main;
import me.pizzalover.chestdeath.database.model.chestDataModel;
import me.pizzalover.chestdeath.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class database {

    private Connection connection;

    /**
     * Get a connection to a database
     * @return the connection
     */
    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            if(Main.getInstance().getDataFolder().mkdirs()) {
                Main.getInstance().getLogger().info("Created the plugin folder.");
            }
            String databasePath = Main.getInstance().getDataFolder().getAbsolutePath() + "/database.db"; // Path to your SQLite database file

            // Try to connect to SQLite
            String url = "jdbc:sqlite:" + databasePath;

            Connection connection = DriverManager.getConnection(url);

            this.connection = connection;

            Main.getInstance().getLogger().info("Connected to database.");

            return connection;
        } catch (SQLException e) {
            Main.getInstance().getLogger().info("Cannot connect to database, something is wrong...");
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
        return null;
    }

    /**
     * Initialize the database
     */
    public boolean initializeDatabase() {
        try {
            if(getConnection() == null) {
                Main.getInstance().getLogger().info("Cannot initialize database, something is wrong...");
                return false;
            }
            Statement statement = getConnection().createStatement();

            //Create the enchant_data table
            String sql = "CREATE TABLE IF NOT EXISTS chest_data ( " +
                    "id text primary key," +
                    "uuid varchar(36), " +
                    "items text)";

            statement.execute(sql);

            statement.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Cannot initialize database, maybe the configuration is wrong, error message:");
            Main.getInstance().getLogger().severe(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * Create a new player data
     * @param chestData the player data model
     */
    public void createInformation(chestDataModel chestData) {

        try {
            String items = "";
            for(ItemStack item : chestData.getChestItems()) {
                items += utils.encodeItem(item) + "[-__-]";
            }
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO chest_data(id, uuid, items) VALUES (?, ?, ?)");
            statement.setString(1, String.valueOf(chestData.getId()));
            statement.setString(2, String.valueOf(chestData.getUuid()));
            statement.setString(3, items);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().info("Cannot create player data, maybe the configuration is wrong");
        }
    }

        public void deleteInformation(chestDataModel chestData) {
            try {
                PreparedStatement statement = getConnection().prepareStatement("DELETE FROM chest_data WHERE id = ?");
                statement.setString(1, chestData.getId());

                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Main.getInstance().getLogger().info("Cannot delete player data, maybe the configuration is wrong");
            }
        }

        public chestDataModel findChestDataInformationByID(String code) {
            try {
                PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM chest_data WHERE id = ?");
                statement.setString(1, code);

                ResultSet resultSet = statement.executeQuery();

                chestDataModel chestDataModel;

                if (resultSet.next()) {
                    String[] items = resultSet.getString("items").split("[-__-]");
                    ArrayList<ItemStack> itemStacks = new ArrayList<>();
                    for(String item : items) {
                        itemStacks.add(utils.decodeItem(item));
                    }
                    chestDataModel = new chestDataModel(
                            UUID.fromString(resultSet.getString("uuid")),
                            resultSet.getString("id"),
                            itemStacks.toArray(new ItemStack[itemStacks.size()])

                    );

                    statement.close();

                    return chestDataModel;
                }

                statement.close();

                return null;
            } catch (SQLException e) {
                return null;
            }
        }



}
