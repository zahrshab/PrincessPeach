package princesspeach
import engine.graphics.Point

case class coin(position : Point) extends objects{

  def updatePosition(): coin = {
    val newPositionX = position.x - 2
    copy(position = Point(newPositionX, position.y))
  }
}

object coin {

  def apply(x: Int, y: Int): coin = {
    coin(Point(x, y))
  }
}
