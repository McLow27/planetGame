#include <iostream>
#include <string>
#include <vector>
using namespace std;

const unsigned char Y_SEP = ';', X_SEP = ' ';
const string TERRA = "2x16", LEVEL = "1x36";

const char charset[36] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

enum Terra {
    SAND, ROCK, ICE
};

class Tile {
    public:
        Tile(Terra terra, unsigned short level) {
            this->terra = terra;
            this->level = level;
        }
        Terra getTerra() {
            return terra;
        }
        unsigned short getLevel() {
            return level;
        }
        ~Tile() = default;
    private:
        Terra terra;
        unsigned short level;
};

/**
 * @brief Prints the map.
 * 
 * @param map a vector containing vectors containing Tile constants
 */
void print(vector<vector<Tile>> map) {
    for (auto y = map.begin(); y != map.end(); ++y) {
        for (auto x = (*y).begin(); x != (*y).end(); ++x) {
            cout << X_SEP;
            cout << charset[(int) (*x).getTerra() / 16];
            cout << charset[(int) (*x).getTerra() % 16];
            cout << charset[(int) (*x).getLevel()];
        }
        cout << Y_SEP;
    }
}

int main(int argc, char *argv[]) {
    // Printing the parsing constants of the transfer protocol
    cout << "x_sep:" << X_SEP << endl;
    cout << "y_sep:" << Y_SEP << endl;
    cout << "terra_repr:" << TERRA << endl;
    cout << "level_repr:" << LEVEL << endl;
    // Map specifications
    int width = 0, height = 0;
    string type = "";
    // Retrieving map specs from args
    for (int i = 1; i < argc; ++i) {
        string s = argv[i];
        const string key = s.substr(0, s.find(':')), value = s.substr(s.find(':')+1);
        if (key == "width") width = stoi(value);
        else if (key == "height") height = stoi(value);
        else if (key == "type") type = value;
        else continue;
    }
    // XXX Generating map
    Terra terra = SAND;
    Tile soil(terra, (unsigned short) 0);
    vector<vector<Tile>> map;
    for (int y = 0; y < height; y++) {
        vector<Tile> line;
        for (int x = 0; x < width; x++)
            line.push_back(soil);
        map.push_back(line);
    }
    // Transfer the results
    print(map);

    return 0;
}
