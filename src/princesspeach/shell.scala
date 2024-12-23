package princesspeach
import engine.graphics.Point

case class shell(position : Point) extends objects{

  def updatePosition(): shell = {
    val newPositionX = position.x - 2
    copy(position = Point(newPositionX, position.y))
  }


}

object shell {
  def apply(x: Int, y: Int): shell = {
    shell(Point(x, y))
  }
}