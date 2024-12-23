package engine.graphics

case class Dimensions(width : Int, height : Int, cellsInWidth : Int, cellsInHeight : Int) {
//  def allPointsInside : Seq[Point] =
//    for(y <- 0 until height; x <- 0 until width)
//      yield Point(x,y)
  def topLeftPointsOfCells: Seq[Point] =
    for {
      y <- 0 until cellsInHeight
      x <- 0 until cellsInWidth
    } yield Point((x * width).toInt, (y * height).toInt)
}

