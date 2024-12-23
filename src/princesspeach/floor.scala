package princesspeach
import engine.graphics.Point

case class floor(position: Point){

  def updatePosition(): floor = {
    val newPositionX = position.x - 1
    copy(position = Point(newPositionX, position.y))
  }

  def setToStart(): floor = copy(position = Point(900, position.y))
}
object floor {

  def apply(x: Int): floor = {
    floor(Point(x, 500))
  }
}
