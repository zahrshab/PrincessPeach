package princesspeach

import engine.graphics.Point

case class tile(position : Point) extends objects{

  def updatePosition(): tile = {
    val newPositionX = position.x - 1
    copy(position = Point(newPositionX, position.y))
  }

}

object tile {

  def apply(x: Int, y: Int): tile = {
    tile(Point(x, y))
  }
}

