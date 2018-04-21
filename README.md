# Java Checkers
## Description
A checkers game with a simple computer player. The computer player works by assigning a weight for each move. When a move weight is calculated, it is based off information such as move safety (e.g. if the move results in the checker being taken by the other player, it is less likely to make that move). It checks a number of other factors and assigns a final weight. The move with the highest weight is chosen as the move. To ensure computer players are less predictable, if multiple moves have the same weight then one is randomly chosen.

## Compile and Run
### Manual
1. In terminal/command prompt, navigate to `src/`
1. Compile with `javac ui/*.java model/*.java logic/*.java`
1. Run with `java ui.Main`
