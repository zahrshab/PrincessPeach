package engine.random

class ScalaRandomGen(sRandom: scala.util.Random) extends RandomGenerator {

  var last = 0
  def this() = this(new scala.util.Random())

  override def randomInt(upTo: Int): Int = {
    last = sRandom.nextInt(upTo) ; last
  }

  def randomIntBetween(min: Int, max: Int): Int = {
    require(min <= max, "min moet kleiner dan of gelijk aan max zijn")
    last = sRandom.nextInt((max - min) + 1) + min
    last
  }

}

