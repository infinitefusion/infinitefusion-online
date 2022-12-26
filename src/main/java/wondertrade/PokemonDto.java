package wondertrade;

import java.sql.Timestamp;

public class PokemonDto {
    private String tradeId;
    private String playerId;
    private String trainerName;
    private String originalTrainerName;
    private String originalTrainerId;
    private String pokemonSpecies;
    private String pokemonNickname;
    private int nbBadges;
    private int pokemonLevel;
    private Timestamp tradeDate;
    private int trainerGender;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getOriginalTrainerName() {
        return originalTrainerName;
    }

    public void setOriginalTrainerName(String originalTrainerName) {
        this.originalTrainerName = originalTrainerName;
    }

    public String getPokemonSpecies() {
        return pokemonSpecies;
    }

    public void setPokemonSpecies(String pokemonSpecies) {
        this.pokemonSpecies = pokemonSpecies;
    }

    public String getPokemonNickname() {
        return pokemonNickname;
    }

    public void setPokemonNickname(String pokemonNickname) {
        this.pokemonNickname = pokemonNickname;
    }

    public int getNbBadges() {
        return nbBadges;
    }

    public void setNbBadges(int nbBadges) {
        this.nbBadges = nbBadges;
    }

    public int getPokemonLevel() {
        return pokemonLevel;
    }

    public void setPokemonLevel(int pokemonLevel) {
        this.pokemonLevel = pokemonLevel;
    }

    public Timestamp getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Timestamp tradeDate) {
        this.tradeDate = tradeDate;
    }

    public int getTrainerGender() {
        return trainerGender;
    }

    public void setTrainerGender(int trainerGender) {
        this.trainerGender = trainerGender;
    }

    public String getOriginalTrainerId() {
        return originalTrainerId;
    }

    public void setOriginalTrainerId(String originalTrainerId) {
        this.originalTrainerId = originalTrainerId;
    }
}
