package wondertrade;

import com.google.gson.JsonObject;
import org.eclipse.jetty.server.Request;
import org.json.JSONObject;
import passwords.Passwords;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.UUID;
import java.util.stream.Collectors;

public class WondertradeManager {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE_NAME = "infinitefusion";
    private static final String USERNAME = "root";
    private static final String PASSWORD = Passwords.DB_ROOT_PASSWORD;
    private Connection connection;


    public WondertradeManager() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        try {
            String connectionUrl = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DATABASE_NAME;
            this.connection = DriverManager.getConnection(connectionUrl, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void handleWondertradeRequest(Request request, HttpServletResponse response) {
        JSONObject body = parseRequestBody(request);

        PokemonDto sentPokemon = parsePokemonInfoFromJson(body);
        try {
            addNewEntryToTable(sentPokemon);
            PokemonDto receivedPokemon = obtainNewPokemon(sentPokemon.getPlayerId(), sentPokemon.getNbBadges());
            if (receivedPokemon == null) {
                //could not find a trade partner
                response.setStatus(HttpServletResponse.SC_FOUND);
            } else {
                deleteReceivedPokemonFromDB(receivedPokemon.getTradeId());
                JsonObject jsonResponse = buildJsonResponse(receivedPokemon);
                response.setContentType("application/json");
                response.getWriter().print(jsonResponse.toString());
            }

        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private JsonObject buildJsonResponse(PokemonDto receivedPokemon) {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("trainer_name", receivedPokemon.getTrainerName());
        jsonResponse.addProperty("trainer_id", receivedPokemon.getPlayerId());
        jsonResponse.addProperty("original_trainer_name", receivedPokemon.getOriginalTrainerName());
        jsonResponse.addProperty("original_trainer_id", receivedPokemon.getOriginalTrainerId());
        jsonResponse.addProperty("pokemon_species", receivedPokemon.getPokemonSpecies());
        jsonResponse.addProperty("level", receivedPokemon.getPokemonLevel());
        jsonResponse.addProperty("nickname", receivedPokemon.getPokemonNickname());
        jsonResponse.addProperty("trainer_gender", receivedPokemon.getTrainerGender());
        jsonResponse.addProperty("head_shiny", receivedPokemon.isHeadShiny());
        jsonResponse.addProperty("body_shiny", receivedPokemon.isBodyShiny());
        jsonResponse.addProperty("debug_shiny", receivedPokemon.isDebugShiny());
        return jsonResponse;
    }


    private void deleteReceivedPokemonFromDB(String tradeId) {
        String sqlQuery = "DELETE FROM infinitefusion.wondertrade WHERE TradeID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, tradeId);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private PokemonDto obtainNewPokemon(String playerId, int nbBadges) {
        String sqlQuery = "SELECT * FROM infinitefusion.wondertrade " +
                " WHERE NbBadges <= " + nbBadges +
                " AND PlayerId != " + playerId +
                " ORDER BY NbBadges DESC, TradeDate ASC LIMIT 1";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                PokemonDto pokemonDto = new PokemonDto();
                pokemonDto.setTradeId(resultSet.getString("TradeID"));
                pokemonDto.setPlayerId(resultSet.getString("PlayerId"));
                pokemonDto.setTrainerName(resultSet.getString("TrainerName"));
                pokemonDto.setOriginalTrainerName(resultSet.getString("OriginalTrainerName"));
                pokemonDto.setOriginalTrainerId(resultSet.getString("OriginalTrainerId"));
                pokemonDto.setPokemonSpecies(resultSet.getString("PokemonSpecies"));
                pokemonDto.setPokemonNickname(resultSet.getString("PokemonNickname"));
                pokemonDto.setNbBadges(resultSet.getInt("NbBadges"));
                pokemonDto.setPokemonLevel(resultSet.getInt("PokemonLevel"));
                pokemonDto.setTradeDate(resultSet.getTimestamp("TradeDate"));
                pokemonDto.setTrainerGender(resultSet.getInt("TrainerGender"));
                pokemonDto.setHeadShiny(resultSet.getBoolean("HeadShiny"));
                pokemonDto.setHeadShiny(resultSet.getBoolean("BodyShiny"));
                pokemonDto.setHeadShiny(resultSet.getBoolean("DebugShiny"));
                return pokemonDto;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private PokemonDto parsePokemonInfoFromJson(JSONObject body) {
        PokemonDto pokemonDto = new PokemonDto();
        pokemonDto.setPokemonSpecies(body.getString("given_pokemon"));
        pokemonDto.setPokemonNickname(body.getString("nickname"));
        pokemonDto.setPlayerId(body.getString("trainer_id"));
        pokemonDto.setTrainerName(body.getString("trainer_name"));
        pokemonDto.setOriginalTrainerName(body.getString("original_trainer_name"));
        pokemonDto.setPokemonLevel(body.getInt("level"));
        pokemonDto.setNbBadges(body.getInt("nb_badges"));
        pokemonDto.setTrainerGender(body.getInt("trainer_gender"));
        pokemonDto.setHeadShiny(body.getBoolean("head_shiny"));
        pokemonDto.setBodyShiny(body.getBoolean("body_shiny"));
        pokemonDto.setDebugShiny(body.getBoolean("debug_shiny"));

        return pokemonDto;
    }

    private void addNewEntryToTable(PokemonDto pokemonDto) throws SQLException {
        String SQL_QUERY = "INSERT INTO infinitefusion.wondertrade(tradeID,PlayerId,TrainerName,TrainerGender,OriginalTrainerName,OriginalTrainerId,PokemonSpecies,PokemonNickname,NbBadges, PokemonLevel, TradeDate, BodyShiny, HeadShiny, DebugShiny) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(SQL_QUERY);
        String tradeId = UUID.randomUUID().toString();
        statement.setString(1, tradeId);
        statement.setString(2, pokemonDto.getPlayerId());
        statement.setString(3, pokemonDto.getTrainerName());
        statement.setInt(4, pokemonDto.getTrainerGender());
        statement.setString(5, pokemonDto.getOriginalTrainerName());
        statement.setString(6, pokemonDto.getOriginalTrainerId());
        statement.setString(7, pokemonDto.getPokemonSpecies());
        statement.setString(8, pokemonDto.getPokemonNickname());
        statement.setInt(9, pokemonDto.getNbBadges());
        statement.setInt(10, pokemonDto.getPokemonLevel());
        statement.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
        statement.setBoolean(12, pokemonDto.isBodyShiny());
        statement.setBoolean(13, pokemonDto.isHeadShiny());
        statement.setBoolean(14, pokemonDto.isDebugShiny());

        statement.executeUpdate();
        statement.close();
    }

    private JSONObject parseRequestBody(Request request) {
        try {
            String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return new JSONObject(body);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject("");
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }
}
