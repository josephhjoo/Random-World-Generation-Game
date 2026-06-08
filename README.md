# Random World Generation Game
 
A 2D tile-based game built in Java where every world is procedurally generated from a seed. The world is guaranteed to be fully traversable (every room and hallway is connected), but no two seeds produce the same layout.
 
## Features
 
- **Procedural generation** - enter any numeric seed and get a unique but fully connected world of rooms and hallways
- **Spotlight mode** - press `M` to toggle a limited field of view, restricting visibility to a few tiles around your character
- **Save and load** - press `:Q` mid-game to save your position and world state; press `L` from the main menu to pick up where you left off
- **Music** - different tracks for the main menu and in-game
## Controls
 
| Key | Action |
|-----|--------|
| `W A S D` | Move up, left, down, right |
| `M` | Toggle spotlight mode |
| `:Q` | Save and quit |
 
## Running the game
 
1. Clone the repo and open it in your IDE
2. Run `Main.java`
3. From the main menu, press `N` for a new game or `L` to load a saved one
4. Enter a numeric seed followed by `S` to generate your world
## Notes
 
- Save files are written to the project root as `seed.txt`, `x.txt`, `y.txt`, and `pressedKey.txt`
- Music files (`mainmenumusic.wav`, `gamemusic.wav`) need to be in the project root to play
 
