package src.srv;

import java.util.HashMap;

public interface ServerInterface {

    /**
     * Connects to the server.
     * 
     * @return true if the connection succeeded, false if any error was thrown
     */
    public boolean connect();

    /**
     * Disconnects from the server
     */
    public void disconnect();

    /**
     * Lists all public lobbies with their tokens and the player count.
     * 
     * @return a map of the lobbies with their token as key and the players out of
     *         six as value
     */
    public HashMap<String, Integer> getPublicLobbies();

    /**
     * Checks whether a private or public lobby with the given token exists.
     * 
     * @param token the token of the lobby
     * @return whether one can join the lobby
     */
    public boolean lobbyExists(String token);

    /**
     * Join a lobby; all further information can be requested with other methods.
     * 
     * @param token the token of the lobby
     * @return whether the lobby could be joined (should not return false)
     */
    public boolean joinLobby(String token);

    /**
     * Returns the name of the map that the player owns.
     * 
     * @return a string (lowercase) identifying the map
     */
    public String getMap();

}
