import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

trait Sprite {
  val imagePath: String
  val width: Double
  val height: Double

  lazy val image: Image = new Image(getClass.getResourceAsStream(imagePath))

  def draw(gc: GraphicsContext, x: Double, y: Double): Unit = {
    gc.drawImage(image, x, y, width, height)
  }
}
