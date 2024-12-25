package princesspeach

import engine.graphics.Point

case class princessPeach(oldPosition: Point, position : Point, jump : Boolean, fallingDown : Boolean, walking : Boolean, idle : Boolean, velocityY: Int = 0, maxVelocityY : Int = 0, walkFrame : Int = 0) {

  def updatePositionLeft(): princessPeach = {
    val newPositionX = position.x - 2
    copy(position = Point(newPositionX, position.y))
  }

  def initialJump(): princessPeach = copy(velocityY = -16, jump = true, idle = false, fallingDown = false) //van -17 naar -16 gezet
  def walk(): princessPeach = copy(walking = true, idle = false)

  def initialFall(newMaxVelocity: Int) : princessPeach = {
    copy(velocityY = 0, maxVelocityY =  newMaxVelocity, jump = false, fallingDown = true, idle = false) //eventually wil je en kunnen vallen en kunnen lopen maar als ik nu die false weghaal wordt het raar
  }

  def jumpOrFall(newPositionY : Int, newVelocity : Int) : princessPeach = {
    copy(position = Point(position.x, newPositionY), velocityY = newVelocity)
  }



  def fallDown(newVelocity : Int, uitgangspunt : Int): princessPeach = {

    val newPositionY = position.y + newVelocity
    if (newVelocity == 16) //dit is nu met uitgangspunt vloer
    {
      copy(position = Point(position.x, uitgangspunt), velocityY = 0, fallingDown = false, idle = true)
    }
    else {
      copy(position = Point(position.x, newPositionY), velocityY = newVelocity)
    }
  }



  def updatePositionWalk(): princessPeach = {

    val newPositionX = position.x + 8
    if (walkFrame > 13) {
      copy(position = Point(newPositionX, position.y), walking = false, walkFrame = 0, idle = true)
    } else {
      copy(position = Point(newPositionX, position.y), walkFrame = walkFrame + 1)
    }
  }

 def landBack(newPositionY: Int): princessPeach = {
    copy(position = Point(position.x, newPositionY), velocityY = 0, idle = true, jump = false, fallingDown = false)
  }

  def updatePositionJump(newPosition: Point, velocity : Int) : princessPeach = {
      copy(position = newPosition, velocityY = velocity)
  }
}

object princessPeach{

  def apply(): princessPeach = {
    princessPeach(Point(400,510), Point(400,510), false, false, false, true, 80, 120)
  }
}
