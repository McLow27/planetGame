package src.srv;

import java.util.HashMap;

public class Test implements ServerInterface {
    
    public boolean connect() {
        return true;
    }

    public void disconnect() {

    }

    public HashMap<String, Integer> getPublicLobbies() {
        return new HashMap<String, Integer>();
    }

    public boolean lobbyExists(String token) {
        return true;
    }

    public boolean joinLobby(String token) {
        return true;
    }

    public String getMap() {
        String[] options = new String[] {"dune", "verglas", "hades", "poseidon", "divine", "zeus", "saturn"};
        return options[0];
    }

}
