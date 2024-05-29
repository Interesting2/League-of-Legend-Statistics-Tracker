package major_project.Model;

import java.util.*;
import java.io.File;
import java.sql.*;

/** 
 * This class is used to cache data to the database
*/
public class DatabaseModel {
    
    private static final String dbName = "gameData.db";
    private static final String dbURL = "jdbc:sqlite:";

    /** 
     * This constructor is to create and set the database
     */
    public DatabaseModel() {
        // create database
        createDB();
        setupDB();
    }

    /** 
     * This method is used to create the database
    */ 
    private void createDB() {
        File gameDBFile = new File(dbName);

        if (gameDBFile.exists()) {
            System.out.println("Game API database already created");
            return;
        }

        try (Connection ignored = DriverManager.getConnection(dbURL + dbName)) {
            // If we get here that means no exception raised from getConnection - meaning it worked
            System.out.println("A new Game API database has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    /** 
     * This method is used to setup the database by creating summoner, league, miniseries tables
    */ 
    private void setupDB() {
        // create a game state table    

        String summonerTableSQL = """
            CREATE TABLE IF NOT EXISTS Summoner (
                summonerId VARCHAR(255) PRIMARY KEY,
                accountId VARCHAR(255),
                puuid VARCHAR(255),
                name VARCHAR(255),
                profileIconId INTEGER,
                revisionDate INTEGER,
                summonerLevel INTEGER
              );
        """;


        String leagueTableSQL = """
            CREATE TABLE IF NOT EXISTS League (
                leagueId VARCHAR(255) PRIMARY KEY,
                queueType VARCHAR(255),
                summonerId VARCHAR(255),
                summonerName VARCHAR(255),
                tier VARCHAR(255),
                rank VARCHAR(255),
                leaguePoints INTEGER,
                wins INTEGER,
                losses INTEGER,
                veteran INTEGER,
                inactive INTEGER,
                freshBlood INTEGER,
                hotStreak INTEGER,
                haveMiniSeries INTEGER,
                miniSeriesId INTEGER,

                FOREIGN KEY(summonerId) REFERENCES Summoner(summonerId),
                FOREIGN KEY(miniSeriesId) REFERENCES MiniSeries(miniSeriesId)
            ); 
        """;

        String miniSeriesTableSQL = """
                CREATE TABLE IF NOT EXISTS MiniSeries (
                    miniSeriesId INTEGER PRIMARY KEY AUTOINCREMENT,
                    losses INTEGER,
                    target INTEGER,
                    wins INTEGER,
                    progress VARCHAR(255)
                );
        """;

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            Statement statement = conn.createStatement();
            statement.execute(summonerTableSQL);
            statement.execute(leagueTableSQL);
            statement.execute(miniSeriesTableSQL);
            System.out.println("Created Sumonner, League, MiniSeries tables");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } 
    }


    /** 
     * This method is used to get the summoner from the database given the name
     * @param name the name of the summoner
     * @return the summoner object
    */ 
    public Summoner getSummoner(String name) {
        Summoner summoner = null;
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Summoner WHERE LOWER(name) = ?");
            statement.setString(1, name.toLowerCase());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                summoner = new Summoner();
                summoner.setId(result.getString("summonerId"));
                summoner.setAccountId(result.getString("accountId"));
                summoner.setPuuid(result.getString("puuid"));
                summoner.setName(result.getString("name"));
                summoner.setProfileIconId(result.getInt("profileIconId"));
                summoner.setRevisionDate(Long.valueOf(result.getInt("revisionDate")));
                summoner.setSummonerLevel(result.getInt("summonerLevel"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return summoner;
    }


    /** 
     * This method is used to get the miniseries of a league from the database given the miniseries id
     * @param miniSeriesId the id of the miniseries
     * @return the miniseries object
    */ 
    private MiniSeries getMiniSeries(String miniSeriesId) {
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM MiniSeries WHERE miniSeriesId = ?");
            statement.setString(1, miniSeriesId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                MiniSeries miniSeries = new MiniSeries();
                miniSeries.setLosses(result.getInt("losses"));
                miniSeries.setProgress(result.getString("progress"));
                miniSeries.setTarget(result.getInt("target"));
                miniSeries.setWins(result.getInt("wins"));
                return miniSeries;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return null;

    }

    /** 
     * This method is used to get the league from the database given the summoner id
     * @param summonerId the id of the summoner
     * @return a list of league object
    */ 
    public League[] getLeagues(String summonerId) {
        
        League[] leagues = null;
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM League WHERE summonerId = ?");
            statement.setString(1, summonerId);
            ResultSet result = statement.executeQuery();
            ArrayList<League> leagueList = new ArrayList<League>();

            League league = new League();
            while (result.next()) {
                league.setLeagueId(result.getString("leagueId"));
                league.setQueueType(result.getString("queueType"));
                league.setSummonerId(result.getString("summonerId"));
                league.setSummonerName(result.getString("summonerName"));
                league.setTier(result.getString("tier"));
                league.setRank(result.getString("rank"));
                league.setLeaguePoints(result.getInt("leaguePoints"));
                league.setWins(result.getInt("wins"));
                league.setLosses(result.getInt("losses"));
                league.setVeteran(result.getInt("veteran") == 1 ? true : false);
                league.setInactive(result.getInt("inactive") == 1 ? true : false);
                league.setFreshBlood(result.getInt("freshBlood") == 1 ? true : false);
                league.setHotStreak(result.getInt("hotStreak") == 1 ? true : false);

                int haveMiniSeries = result.getInt("haveMiniSeries");
                if (haveMiniSeries == 1) {
                    league.setMiniSeries(getMiniSeries(result.getString("miniSeriesId")));
                } else {
                    league.setMiniSeries(null);
                }

                leagueList.add(league);
            }
            leagues = leagueList.toArray(new League[leagueList.size()]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return leagues;
    }

    /** 
     * This method is used to update the cached summoner from the database if summoner id already exists
     * @param summoner the summoner object
    */
    private void updateSummoner(Summoner summoner) {
        System.out.println("Update Summoner");

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE Summoner SET accountId = ?, puuid = ?, name = ?, profileIconId = ?, revisionDate = ?, summonerLevel = ? WHERE summonerId = ?");
            statement.setString(1, summoner.getAccountId());
            statement.setString(2, summoner.getPuuid());
            statement.setString(3, summoner.getName());
            statement.setInt(4, summoner.getProfileIconId());
            statement.setLong(5, summoner.getRevisionDate());
            statement.setInt(6, summoner.getSummonerLevel());
            statement.setString(7, summoner.getId());
            statement.executeUpdate();

            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    /** 
     * This method is used to save the summoner data to the database
     * @param summoner the summoner object
    */ 
    public void saveSummoner(Summoner summoner) {
        // Maissidaltame
        System.out.println("Caching summoner data");
        
        boolean exists = checkSummonerExists(summoner.getId());
        if (exists) {
            // update the row with same summonerId
            updateSummoner(summoner);
            return;
        }
        String addSummonerSQL = """
            INSERT INTO Summoner (
                summonerId,
                accountId,
                puuid,
                name,
                profileIconId,
                revisionDate,
                summonerLevel
            ) VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement(addSummonerSQL);
            statement.setString(1, summoner.getId());
            statement.setString(2, summoner.getAccountId());
            statement.setString(3, summoner.getPuuid());
            statement.setString(4, summoner.getName());
            statement.setInt(5, summoner.getProfileIconId());
            statement.setLong(6, summoner.getRevisionDate());
            statement.setInt(7, summoner.getSummonerLevel());
            statement.executeUpdate();
            System.out.println("Added summoner to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    /** 
     * This method is to if the summoner id exists in the database
     * @param summonerId the id of the summoner
     * @return true if the summoner id exists in the database else false
    */ 
    private boolean checkSummonerExists(String summonerId) {
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Summoner WHERE summonerId = ?");
            statement.setString(1, summonerId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return false;
    }

    // check if summoner name exists in the database
    // return the summoner if true
    // return null if false
    /** 
     * This method is used to get the summoner from the database given the summoner name
     * @param summonerName the name of the summoner
     * @return the summoner object
    */
    public boolean checkSummonerNameExists(String summonerName) {
        System.out.println("Check summoner name exists in the database");
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Summoner WHERE LOWER(name) = ?");
            statement.setString(1, summonerName.toLowerCase());
            ResultSet result = statement.executeQuery();

            // return whether result is empty
            return result.next();

            // if (result.next()) {
            //     summoner = new Summoner();
            //     summoner.setId(result.getString("summonerId"));
            //     summoner.setAccountId(result.getString("accountId"));
            //     summoner.setPuuid(result.getString("puuid"));
            //     summoner.setName(result.getString("name"));
            //     summoner.setProfileIconId(result.getInt("profileIconId"));
            //     summoner.setRevisionDate(Long.valueOf(result.getInt("revisionDate")));
            //     summoner.setSummonerLevel(result.getInt("summonerLevel"));
            // }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return false;
    }

    /** 
     * This method is used to get the id of the last added miniseries 
     * @return the id of the last added miniseries
    */ 
    private int getLastId() {
        // get the last added miniSeries id
        String getLastIdSQL = """
            SELECT MAX(miniSeriesId) FROM MiniSeries;
        """;

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(getLastIdSQL);
            boolean exist = result.next();
            if (exist) {
                return result.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return 0;

    }

    /** 
     * This method is used to save the mini series data to the database
     * @param lastId the id of the last added miniseries
     * @param miniSeries the mini series object
    */ 
    private void saveMiniSeries(int lastId, MiniSeries miniSeries) {

        // INSERT INTO MiniSeries

        String addMiniSeriesSQL = """
            INSERT INTO MiniSeries (
                miniSeriesId,
                losses,
                target,
                wins,
                progress
            ) VALUES (
                ?,
                ?,
                ?,
                ?,
                ?
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement(addMiniSeriesSQL);
            statement.setInt(1, lastId);
            statement.setInt(2, miniSeries.getLosses());
            statement.setInt(3, miniSeries.getTarget());
            statement.setInt(4, miniSeries.getWins());
            statement.setString(5, miniSeries.getProgress());
            statement.executeUpdate();
            System.out.println("Added miniSeries to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    private void updateMiniSeries(int miniSeriesId, MiniSeries miniSeries) {
        // update statement
        System.out.println("Update Mini Series");
        String updateMiniSeriesSQL = """
            UPDATE MiniSeries SET
                losses = ?,
                target = ?,
                wins = ?,
                progress = ?
            WHERE miniSeriesId = ?;
        """;

        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement(updateMiniSeriesSQL);
            statement.setInt(1, miniSeries.getLosses());
            statement.setInt(2, miniSeries.getTarget());
            statement.setInt(3, miniSeries.getWins());
            statement.setString(4, miniSeries.getProgress());
            statement.setInt(5, miniSeriesId);
            statement.executeUpdate();
            System.out.println("Updated miniSeries to database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    /** 
     * This method is used to update the leagues linked with a given summoner
     * @param summonerId the id of the summoner
     * @param league the league object of the summoner
    */ 
    private void updateLeague(String summonerId, League league) {
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("UPDATE League SET tier = ?, rank = ?, queueType = ?, summonerName = ?, leaguePoints = ?, wins = ?, losses = ?, veteran = ?, inactive = ?, freshBlood = ?, hotStreak = ?, haveMiniSeries = ?, miniSeriesId = ? WHERE summonerId = ? AND leagueId = ?");

            statement.setString(1, league.getTier());
            statement.setString(2, league.getRank());
            statement.setString(3, league.getQueueType());
            statement.setString(4, league.getSummonerName());
            statement.setInt(5, league.getLeaguePoints());
            statement.setInt(6, league.getWins());
            statement.setInt(7, league.getLosses());
            statement.setInt(8, league.getVeteran() ? 1 : 0);
            statement.setInt(9, league.getInactive() ? 1 : 0);
            statement.setInt(10, league.getFreshBlood() ? 1 : 0);
            statement.setInt(11, league.getHotStreak() ? 1 : 0);

            if (league.getMiniSeries() != null) {
                statement.setInt(12, 1);
                statement.setInt(13, getLastId());

                updateMiniSeries(getLastId(), league.getMiniSeries());
            } else {
                statement.setInt(12, 0);
                statement.setNull(13, java.sql.Types.INTEGER);
            }
            statement.setString(14, summonerId);
            statement.setString(15, league.getLeagueId());
            statement.executeUpdate();


        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private void updateLeagues(String summonerId, League[] leagues) {
        System.out.println("Update Leagues");
        // update league information for each league in leagues
        for (League league : leagues) {
            updateLeague( summonerId, league);
        }
    }

    /** 
     * This method is used to check if the summoner id exists in one of the row of the league table
     * @param summonerId the id of the summoner
     * @return true if the summoner id exists in the league table, false otherwise
    */ 
    private boolean checkSummonerIdInLeague(String summonerId) {
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM League WHERE summonerId = ?");
            statement.setString(1, summonerId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        return false;
    }

    /**
     * This method is used to save the league information to the database
     * @param summonerId the id of the summoner
     * @param leagues the list of league object of the summoner
    */
    public void saveLeague(String summonerId, League[] leagues) {
        System.out.println("Caching league data");

        if (checkSummonerIdInLeague(summonerId)) {
            // update the row with same summonerId
            updateLeagues(summonerId, leagues);
            return;
        }

        String addLeagueSQL = """
            INSERT INTO League (
                leagueId,
                queueType,
                summonerId,
                summonerName,
                tier,
                rank,
                leaguePoints,
                wins,
                losses,
                veteran,
                inactive,
                freshBlood,
                hotStreak,
                haveMiniSeries,
                miniSeriesId
            ) VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            );
        """;

        // insert once for each league
        for (League league : leagues) {
            try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
                PreparedStatement statement = conn.prepareStatement(addLeagueSQL);
                statement.setString(1, league.getLeagueId());
                statement.setString(2, league.getQueueType());
                statement.setString(3, summonerId);
                statement.setString(4, league.getSummonerName());
                statement.setString(5, league.getTier());
                statement.setString(6, league.getRank());
                statement.setInt(7, league.getLeaguePoints());
                statement.setInt(8, league.getWins());
                statement.setInt(9, league.getLosses());
                statement.setInt(10, league.getVeteran() ? 1 : 0);
                statement.setInt(11, league.getInactive() ? 1 : 0);
                statement.setInt(12, league.getFreshBlood() ? 1 : 0);
                statement.setInt(13, league.getHotStreak() ? 1 : 0);

                // check if miniSeries is null
                if (league.getMiniSeries() == null) {
                    statement.setInt(14, 0);
                    statement.setNull(15, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(14, 1);
                    int lastId = getLastId() + 1;
                    statement.setInt(15, lastId);

                    // save miniSeries
                    saveMiniSeries(lastId, league.getMiniSeries());
                }

                statement.executeUpdate();
                System.out.println("Added league to database");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

    /** 
     * This method is used to get all the cached summoner data from the database
     * @return The cached summoners' data
    */
    public Summoner[] getAllSummoners() {
        // get all cached summoners

        List<Summoner> allSummoners = new ArrayList<Summoner>();
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM Summoner");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Summoner summoner = new Summoner();
                summoner.setId(result.getString("summonerId"));
                summoner.setName(result.getString("name"));
                summoner.setProfileIconId(result.getInt("profileIconId"));
                summoner.setRevisionDate(result.getLong("revisionDate"));
                summoner.setSummonerLevel(result.getInt("summonerLevel"));
                allSummoners.add(summoner);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        return allSummoners.toArray(new Summoner[allSummoners.size()]);
    }

    /**
     * This method is used to delete the mini series data from the database given the miniseries Ids
     * @param miniSeriesIds the list of miniseries Ids 
    */
    private void deleteMiniSeries(List<Integer> miniSeriesIds) {
        System.out.println("Clearing cache for miniseries");
        
        String deleteMiniSeriesSQL = """
            DELETE FROM MiniSeries WHERE miniSeriesId = ?;
        """;
        for (Integer miniSeriesId : miniSeriesIds) {
            try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
                PreparedStatement statement = conn.prepareStatement(deleteMiniSeriesSQL);
                System.out.println(miniSeriesId);
                statement.setInt(1, miniSeriesId);
                statement.executeUpdate();
                
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
        
    }

    /**
     * This method is used to delete all cached data from database related to the summoner id
     * @param summonerId the id of the summoner
    */ 
    public void clearCache(String summonerId) {
        // clear cache for summonerId
        System.out.println("Clearing cache for summoner");
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM Summoner WHERE summonerId = ?");
            statement.setString(1, summonerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        // clear cache for league and return the miniSeries
        // return miniSeriesId of the deleted row from league table

        System.out.println("Clearing cache for league");
        List<Integer> miniSeriesIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbURL + dbName)) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM League WHERE summonerId = ? RETURNING miniSeriesId, haveMiniSeries");
            statement.setString(1, summonerId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                int miniSeriesId = result.getInt("miniSeriesId");
                boolean haveMiniSeries = result.getBoolean("haveMiniSeries");
                if (haveMiniSeries) {
                    miniSeriesIds.add(miniSeriesId);
                } 
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        // delete miniSeries
        deleteMiniSeries(miniSeriesIds);
    }
}
