import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

class Pipe(var x: Double, val gapY: Double) {
  val width: Double = 80
  val gapHeight: Double = 200
  val xVelocity: Double = -5.0
  val imagePath: String = "/pipe.png" // Path to the pipe image

  // Load the pipe image
  lazy val image: Image = new Image(getClass.getResourceAsStream(imagePath))

  def update(): Unit = {
    x += xVelocity
  }

  def draw(gc: GraphicsContext): Unit = {
    gc.drawImage(image, x, 0, width, gapY) // Top pipe
    gc.drawImage(image, x, gapY + gapHeight, width, 600 - gapY - gapHeight) // Bottom pipe
  }

  def isOffScreen: Boolean = {
    x + width < 0
  }

  def intersects(player: Player): Boolean = {
    player.x < x + width &&
      player.x + player.width > x &&
      (player.y < gapY || player.y + player.height > gapY + gapHeight)
  }
}
