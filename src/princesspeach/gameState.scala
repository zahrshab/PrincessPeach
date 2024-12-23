package princesspeach
import engine.graphics.Point
import engine.random.ScalaRandomGen

case class gameState(finishingPole : pole, gameStatus : Int, spawnCounterTile : Int, spawnCounterShell: Int, princess : princessPeach, oldPositionPrincess : Point, ground : List[floor], gameObjects : List[objects], detectedPoint : Point, var random : ScalaRandomGen, coinsAte : Int, gameOver : Boolean, ateACoin : Boolean) {

  val tiles: List[tile] = gameObjects.collect { case t: tile => t }
  val coins: List[coin] = gameObjects.collect { case c: coin => c }
  val shells: List[shell] = gameObjects.collect { case s: shell => s }

  def setGameOver(): gameState = copy(gameOver = true)
  def reachedPole(): Boolean = finishingPole.position.x == 800
  def princessOutOfBoundaries(): Boolean = princess.position.x < -80
  def startTheJump(): gameState = copy(princess = princess.initialJump(), oldPositionPrincess =  princess.position)


  def changingMechanism(): gameState = {
    val princessNew = princess.updatePositionLeft()
    val updateGround = ground.map(floor => {
      val movedFloor = floor.updatePosition()
      if (movedFloor.position.x <= -900) movedFloor.setToStart() else movedFloor
    })
    val updatedObjects = gameObjects.map(objects => objects.updatePosition())
    val newPole : pole = if(checkEndLevel()) finishingPole.updatePosition() else finishingPole
    copy(gameObjects = updatedObjects, princess = princessNew, ground = updateGround, finishingPole = newPole)
  }

  private def tileDetectionSystem(currentPositionInTheSky: Point): Point = {
    val detectedTilePosition = gameObjects.collect { case tile: tile => tile }
      .find(tile =>
        currentPositionInTheSky.x <= tile.position.x + 60 &&
          currentPositionInTheSky.x >= tile.position.x - 80 &&
          tile.position.y >= currentPositionInTheSky.y
      )
      .map(_.position)
    detectedTilePosition.getOrElse(Point(900, 900))
  }

  private def initialVelocityInTheSky(tileY : Float, positionY : Float): Int = {
    val gravity = 1.0
    val height = tileY - positionY
    val initialVelocity = Math.sqrt(2 * gravity * height).toFloat
    initialVelocity.toInt
  }

  private def FallingDown(): gameState = {
    val tileDetected = tileDetectionSystem(princess.position)
    if(tileDetected!= Point(900,900)) {
      val initialVelocity = initialVelocityInTheSky(tileDetected.y, princess.position.y)
      val princessNew = princess.initialFall(initialVelocity)
      copy(princess = princessNew, detectedPoint = tileDetected)
    }
    else fallToTheGround()
  }

  private def fallToTheGround() : gameState = {
    val initialVelocity = initialVelocityInTheSky(510, princess.position.y)
    val princessNew = princess.initialFall(initialVelocity)
    copy(princess = princessNew, detectedPoint = Point(princess.position.x, 510))
  }

  private def calculateNewVelocity(): Int = {
    val gravity = 1
    princess.velocityY + gravity
  }

  def jump(): gameState = {
    val newPositionY = princess.position.y + calculateNewVelocity()
    if (calculateNewVelocity() == 1) FallingDown()
    else {
      val princessNew = princess.jumpOrFall(newPositionY.toInt, calculateNewVelocity())
      copy(princess = princessNew)
    }
  }

    def fallDown(): gameState = {
      val newPositionY = princess.position.y + calculateNewVelocity()
      if (checkIfTouchedShell(Point(princess.position.x, newPositionY))) copy(gameOver = true)
      if (calculateNewVelocity() >= princess.maxVelocityY)
      {
        val newP = princess.landBack(detectedPoint.y.toInt)
        copy(princess = newP)
      }
      else {
        val princessNew = princess.jumpOrFall(newPositionY.toInt, calculateNewVelocity())
        copy(princess = princessNew)
      }
    }

  private def checkIfTouchedShell(position: Point): Boolean = {
    gameObjects.exists {
      case shell: shell =>
        val shellBoundsX = position.x + 60  >= shell.position.x && shell.position.x + 40 >= position.x
        val shellBoundsY = shell.position.y <= position.y
        shellBoundsX && shellBoundsY
      case _ => false
    }
  }


  def walk(): gameState = {
    var coinAte = false
    val newPosition = princess.walk()
    var amountCoinsAte = coinsAte
    val remainingCoins = gameObjects.collect { case coin: coin => coin }
      .filterNot { coin =>
        if (newPosition.position.x + 80 >= coin.position.x &&
          newPosition.position.x <= coin.position.x + 50 &&
          newPosition.position.y == coin.position.y + 50) {
          amountCoinsAte += 1
          coinAte = true
          true
        } else false
      }

    val newGameObjects = remainingCoins ++ gameObjects.filterNot(_.isInstanceOf[coin])
    copy(princess = newPosition, gameObjects = newGameObjects, coinsAte = amountCoinsAte, ateACoin = coinAte)
  }

  def checkIfFallen(): gameState = {
    val tileDetected = tileDetectionSystem(princess.position)
    if(princess.position.y != 510 && tileDetected == Point(900,900)) fallToTheGround()
    else this
  }

  def updatewalk(): gameState = {
    val newPrincess = princess.updatePositionWalk()
    if (checkIfTouchedShell(newPrincess.position)) {
      setGameOver()
      copy(princess = newPrincess, gameOver = true)
    }
    else copy(princess = newPrincess)
  }

  def finishedGame() : Boolean = princess.position.x + 60 >= finishingPole.position.x - 10 && finishingPole.position.x == 800
  def checkEndLevel() : Boolean = gameStatus == 10

def spawnShells(): gameState = {
  var totalObjects: List[objects] = gameObjects

  if (spawnCounterShell >= 100 && gameStatus < 9) {
    totalObjects = if (math.random() < 0.4) shell(900, 470) +: gameObjects
    else totalObjects
    copy(gameObjects = totalObjects, spawnCounterShell = 0)
  }
  else copy(spawnCounterShell = spawnCounterShell + 1)
}


  def spawnTiles(): gameState = {
    if (spawnCounterTile >= 120 && gameStatus < 9) {
      val gameStatusNew = gameStatus + 1
      val positionsTile = List(300, 400, 400, 350)
      val randomY = random.randomInt(3)
      val Y = positionsTile(randomY)
      val randomMath = math.random()

      val newObjects = if (randomMath < 0.4) {
        val tile1 = tile(900, Y)
        val coin1 = if (math.random() < 0.5) Some(coin(900, Y - 50)) else None
        val tile2 = tile(960, Y)
        val coin2 = if (math.random() < 0.5) Some(coin(960, Y - 50)) else None
        val tile3 = tile(1020, Y)
        val coin3 = if (math.random() < 0.3) Some(coin(1020, Y - 50)) else None

        List(tile1, tile2, tile3) ++ List(coin1, coin2, coin3).flatten
      }
      else {
        val newCoin = if (math.random() < 0.5) Some(coin(900, Y - 50)) else None
        List(tile(900, Y)) ++ List(newCoin).flatten
      }
      copy(gameStatus = gameStatusNew, gameObjects = newObjects ++ gameObjects, spawnCounterTile = 0)
    }
    else if(spawnCounterTile >= 120 && gameStatus == 9 ){
      val gameStatusNew = gameStatus + 1
      copy(gameStatus = gameStatusNew, spawnCounterTile = spawnCounterTile + 1)
    }
    else copy(spawnCounterTile = spawnCounterTile + 1)
  }


  def deleteTiles(): gameState = {
    val updatedTiles = gameObjects.collect { case tile: tile => tile.updatePosition() }
      .filter(_.position.x > -70)
    val remainingObjects = gameObjects.filterNot(_.isInstanceOf[tile])
    copy(gameObjects = updatedTiles ++ remainingObjects)
  }

}

object gameState{

  def apply(): gameState = {
      val firstFloor = floor(0)
      val secondFloor = floor(900)
      gameState(pole(), gameStatus =  0, spawnCounterTile = 0, spawnCounterShell = 0, princessPeach(), Point(100,400), List(firstFloor, secondFloor), List(), Point(900,900), new ScalaRandomGen(), coinsAte = 0, gameOver = false, ateACoin = false)
  }
}

