package princesspeach
import processing.core.{PApplet, PImage, PFont, PGraphics}
import processing.event.KeyEvent
import java.awt.event.KeyEvent._
import ddf.minim.{Minim, AudioPlayer, AudioSample}

class game extends PApplet{
  private val widthInPixels: Int = 900
  private val heightInPixels: Int = 600

  private var background_img: PImage = _
  private var floor_img: PImage = _
  private var tile_img: PImage = _
  private var coin_img: PImage = _
  private var princessPeach_img: PImage = _
  private var coinCounter: PImage = _
  private var gameOverText : PImage = _
  private var pressEnter : PImage = _
  private var greenShell : PImage = _
  private var congratulations : PImage = _
  private var startPicture : PImage = _
  private var pole : PImage = _
  private var playerName : PFont = _
  private var coincounterfont : PFont = _
  private var backgroundBuffer: PGraphics = _

  private var minim: Minim = _
  private var sound: AudioSample = _
  private var victorySound : AudioPlayer = _
  private var startScreen : AudioPlayer = _
  private var gameOverSound : AudioSample = _

  private var currentGame = gameState()
  private var restartTheGameAllowed = false
  private var inputText = "" // To store user input
  private var isActive = false // Tracks if the textbox is selected
  private val boxX = 350
  private val boxY = 350
  private val boxWidth = 250
  private val boxHeight = 30
  private var gameName = ""
  private var gameHasStarted = false

  private var finishingJump = 0
  private var gameOverStartTime: Long = 0
  private var gameOverDuration: Int = 0

  override def setup(): Unit = {
    surface.setSize(widthInPixels, heightInPixels)
    surface.setLocation(0, 0)
    loadResources()
    loadBackground()
    loadAudio()
  }

  override def settings(): Unit = {
    pixelDensity(displayDensity())
    size(widthInPixels, heightInPixels)
  }

  override def draw(): Unit = {
    displayEnvironment()
    if (!gameHasStarted) drawStartScreen()
    else handleGame()
  }

  override def mousePressed(): Unit = isActive = mouseX > boxX && mouseX < boxX + boxWidth && mouseY > boxY && mouseY < boxY + boxHeight

  private def handleGame(): Unit = {
    drawGameObjects()
    drawPrincess()
    handleMoves()
    movingMechanism()
    handleEatingCoin()
    handleVictory()
    ranking()
    handleGameOver()
    displayPoleNearEnd()
  }

  private def handleGameOver(): Unit = {
    if (currentGame.princessOutOfBoundaries() || currentGame.gameOver) {
      if (gameOverStartTime == 0) {
        gameOverSound.trigger()
        gameOverStartTime = millis() // Record the start time
      }
      currentGame = currentGame.setGameOver()
      if (millis() - gameOverStartTime >= gameOverDuration) {
        image(gameOverText, 250, 250, 400, 100)
        image(pressEnter, 250, 360, 400, 50)
      }
    }
  }

  private def handleEatingCoin(): Unit = {
    if (currentGame.ateACoin) {
      sound.trigger()
      currentGame = currentGame.copy(ateACoin = false)
    }
  }

  private def movingMechanism(): Unit = {
    if (!currentGame.reachedPole()) {
      currentGame = currentGame.spawnTiles()
      currentGame = currentGame.changingMechanism()
      currentGame = currentGame.deleteTiles()
      currentGame = currentGame.spawnShells()
    }
  }

  private def handleVictory(): Unit = {
    if (currentGame.finishedGame() && finishingJump < 250) {
      victorySound.play()
      image(congratulations, 250, 250, 400, 50)
      if (currentGame.princess.idle) currentGame = currentGame.startTheJump()
      finishingJump += 1
      if (finishingJump >= 60) {
        textSize(20)
        text("Press enter to play again", 285, 350)
      }
      if (finishingJump == 60) restartTheGameAllowed = true
    }
  }

  private def displayPoleNearEnd(): Unit = {
    if (currentGame.checkEndLevel()) image(pole, currentGame.finishingPole.position.x, currentGame.finishingPole.position.y, 80, 220)

  }

  private def drawGameObjects(): Unit = {
    currentGame.tiles.foreach(tile => image(tile_img, tile.position.x, tile.position.y, 60, 32))
    currentGame.coins.foreach(coin => image(coin_img, coin.position.x, coin.position.y, 50, 50))
    currentGame.shells.foreach(shell => image(greenShell, shell.position.x, shell.position.y, 50, 40))
  }

  private def drawPrincess(): Unit = {
    val topLeftCorner = currentGame.princess.position.y - 110
    image(princessPeach_img, currentGame.princess.position.x, topLeftCorner, 60, 110)
  }

  private def drawStartScreen(): Unit = {
    drawNameInputBox()
    image(startPicture, 250, 80, 450, 250)
    startScreen.play()
  }

  private def handleMoves(): Unit = {
    if (currentGame.princess.jump) currentGame = currentGame.jump()
    if (currentGame.princess.fallingDown) currentGame = currentGame.fallDown()
    if (currentGame.princess.walking) {
      currentGame = currentGame.updatewalk()
      currentGame = currentGame.checkIfFallen()
    }
  }

  private def displayEnvironment(): Unit = {
    image(backgroundBuffer, 0, 0)
    currentGame.ground.foreach(floor => image(floor_img, floor.position.x, 500, widthInPixels, 100))
  }

  private def ranking(): Unit = {
    text("Player: " + gameName, 40, 40)
    image(coinCounter, 55, 60, 80, 40)
    fill(255, 255, 255)
    textFont(coincounterfont)
    text("X " + currentGame.coinsAte, 144, 92)
  }

  def gameOver(): Unit = {
    textSize(60)
    fill(255, 255, 255)
    text("GAME OVER", 300, 300)
    textSize(40)
    text("Press enter to start again", 300, 370)
  }

  private def drawNameInputBox(): Unit = {
    stroke(0);
    if (isActive) fill(200)
    else fill(255)
    rect(boxX, boxY, boxWidth, boxHeight);

    fill(0);
    textSize(16);
    text(inputText, boxX + 15, boxY + 22);
    if(!isActive){
      fill(0)
      text("Enter your name here", boxX + 15, boxY + 22)
    }
  }

  private def loadResources(): Unit = {
    val basePath = sketchPath("lib/src/main/resources")

    // Load images
    background_img = loadImage(s"$basePath/background_design.png")
    background_img.resize(900, 500)
    floor_img = loadImage(s"$basePath/floor.png")
    tile_img = loadImage(s"$basePath/tile.png")
    princessPeach_img = loadImage(s"$basePath/princess.png")
    coin_img = loadImage(s"$basePath/coin.png")
    gameOverText = loadImage(s"$basePath/gameOver.png")
    pressEnter = loadImage(s"$basePath/pressEnter_text.png")
    greenShell = loadImage(s"$basePath/greenShell.png")
    coinCounter = loadImage(s"$basePath/coinCounter.png")
    pole = loadImage(s"$basePath/pole.png")
    congratulations = loadImage(s"$basePath/congratulations_text.png")
    startPicture = loadImage(s"$basePath/startingPicture.png")

    // Load Fonts
    playerName = createFont(s"${sketchPath("src/data/Gameplay.ttf")}", 20)
    coincounterfont = createFont(s"${sketchPath("src/data/Gameplay.ttf")}", 24)
    textFont(playerName)
  }

  private def loadBackground(): Unit = {
    backgroundBuffer = createGraphics(widthInPixels, heightInPixels)
    backgroundBuffer.beginDraw()
    backgroundBuffer.image(background_img, 0, 0, 900, 500)
    backgroundBuffer.endDraw()
  }

  private def loadAudio(): Unit = {
    minim = new Minim(this)

    sound = minim.loadSample("src/data/coinCatcher.mp3")
    gameOverSound = minim.loadSample("src/data/gameOver.mp3")
    startScreen = minim.loadFile("src/data/startingScreen.mp3")
    victorySound = minim.loadFile("src/data/victorySound.mp3")

    gameOverDuration = gameOverSound.length()
  }

  override def keyPressed(event: KeyEvent): Unit = {
    if (isActive) {
      event.getKeyCode match {
        case VK_BACK_SPACE if inputText.nonEmpty => inputText = inputText.substring(0, inputText.length - 1)
        case VK_ENTER =>
          gameHasStarted = true
          gameName = inputText
          startScreen.close()
          isActive = false
        case _ => inputText += event.getKey
      }
    } else {
      event.getKeyCode match {
        case VK_RIGHT if !currentGame.gameOver && !currentGame.finishedGame() =>
          currentGame = currentGame.walk()
          currentGame = currentGame.copy(princess = currentGame.princess.updatePositionWalk())
        case VK_SPACE if !currentGame.gameOver && !currentGame.finishedGame() =>
          currentGame = currentGame.startTheJump()
        case VK_ENTER if currentGame.gameOver || restartTheGameAllowed =>
          currentGame = gameState()
          finishingJump = 0
          victorySound.close()
          gameOverStartTime = 0
        case _ => ()
      }
    }
  }

}

object game {
  def main(args: Array[String]): Unit = {
    PApplet.main("supermario.game")
  }
}
