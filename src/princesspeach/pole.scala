package princesspeach
import engine.graphics.Point

case class pole(position : Point){

  def updatePosition(): pole = {
    val newPositionX = position.x - 1
    copy(position = Point(newPositionX, position.y))
  }

}

object pole {

  def apply(): pole = {
    pole(Point(900, 295))
  }
}