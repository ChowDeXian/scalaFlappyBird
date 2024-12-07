import scalafx.scene.canvas.GraphicsContext

class Player(var x: Double, var y: Double) extends Sprite {
  // Specify the image path for the player
  override val imagePath: String = "/player.png"
  // Adjust the width and height according to the actual image size
  override val width: Double = 86  // Adjust to your image width
  override val height: Double = 75 // Adjust to your image height

  var yVelocity: Double = 0
  val gravity: Double = 0.05 // Gravity affecting falling speed
  val flapStrength: Double = -8.0 // Flap strength
  val moveSpeed: Double = 10.0 // Move speed for up and down controls
  val maxFallingSpeed: Double = 3.0 // Maximum falling speed

  def flap(): Unit = {
    yVelocity = flapStrength
  }

  def moveUp(): Unit = {
    y -= moveSpeed
  }

  def moveDown(): Unit = {
    y += moveSpeed
  }

  def update(): Unit = {
    // Apply gravity to yVelocity
    yVelocity += gravity
    // Cap the falling speed
    if (yVelocity > maxFallingSpeed) yVelocity = maxFallingSpeed
    // Update y position based on yVelocity
    y += yVelocity
  }

  def draw(gc: GraphicsContext): Unit = {
    super.draw(gc, x, y)
  }

  def intersects(pipe: Pipe): Boolean = {
    x < pipe.x + pipe.width &&
      x + width > pipe.x &&
      (y < pipe.gapY || y + height > pipe.gapY + pipe.gapHeight)
  }

  def outOfBounds(): Boolean = {
    y + height >= 600 || y <= 0
  }
}
