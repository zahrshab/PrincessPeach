Super Mario, renewed

Overview

This project is a simple Mario-inspired game developed in Scala, utilizing Processing for the visuals and Minim for sound effects.
The player controls Princess Peach, who can collect coins, avoid obstacles, and reach the end flag to complete the level.

Gameplay

The player controls Princess Peach by using the arrow keys and the spacebar to jump.
The goal is to reach the flag, collect coins, and avoid obstacles.
If Princess Peach collides with an obstacle or falls off the screen, the game is over.

Controls

- Right Arrow: Move Princess Peach to the right
- Spacebar: Jump
- Enter: Start a new game (when the game is over)

How to start

- Click on the inputbox
- Enter your name
- Press enter
- Start moving right

Features

- Collectible Coins: The player can collect coins for points.
- Obstacles: Moving turtle shells appear on the screen that Mario must avoid.
- End Goal: Reach the flag at the end of the level to win the game.
- Sound Effects: Sound effects for coin collection, victory, and game over are integrated.
- Start and End Screens: The game begins with a start screen and ends with either a victory or game-over screen.
- Input: The game asks for the name of the player before the game starts

Project Structure

- game.scala: Contains the main game loop, input handling, and rendering logic.
- gameState.scala: Manages the game state, including Princess Peach's position, coins, obstacles, and the pole.
- princessPeach.scala: Manages Princess Peach's movement.
- shell.scala, tile.scala, floor.scala: subclasses of objects.scala, in which the movement of the objects is managed.
- Images and Sounds: All images and sound assets used in the game are located in the resources folder.

Recourses

- Images: Used background + supporting-images in PNG format.
The background and floor are designed by myself and the additional images are used from open sources.
- Sounds: MP3 files for various sound effects within the game.

Requirements running game

- Scala
- Processing Core
- Minim for audio playback

Future Improvements, which I could not improve due to lack of time:

- The jumping and moving right do not work perfectly enhanced, I would like to improve these features
- Coin collision: The detection of the coin area could be improved
- More levels
- Background music
- Enhanced animations
