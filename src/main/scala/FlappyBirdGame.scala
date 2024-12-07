import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import scalafx.scene.control.Button
import scalafx.scene.input.KeyEvent
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.image.Image
import scalafx.scene.image.ImageView

object FlappyBirdGame extends JFXApp {

  // Game state variables
  var player: Player = _
  var pipes: List[Pipe] = _
  var gameOver = false
  var gameWon = false
  var score = 0
  var totalScore = 0 // Track total score across games
  val winTime = 20.0 // Time in seconds to reach the end of the terrain
  var elapsedTime = 0.0
  var lastTime: Long = _
  var pipeSpawnTime = 0.0

  // Timer variables
  var timerStartTime: Long = _
  var currentTime: Long = _

  // Load the background image
  val startPageBackgroundPath = "/startpagebckgrnd.jpg"
  lazy val startPageBackground: Image = new Image(getClass.getResourceAsStream(startPageBackgroundPath))

  val gameBackgroundPath = "/background.jpg"
  lazy val gameBackground: Image = new Image(getClass.getResourceAsStream(gameBackgroundPath))

  // Function to reset the game state
  def restartGame(): Unit = {
    player = new Player(100, 300)
    pipes = List[Pipe]()
    gameOver = false
    gameWon = false
    score = 0 // Reset score for the new game session
    elapsedTime = 0.0
    lastTime = System.nanoTime()
    pipeSpawnTime = 0.0
    timerStartTime = System.currentTimeMillis() // Set the start time for the timer
  }

  // Function to update the score when winning
  def updateScore(): Unit = {
    if (gameWon) {
      totalScore += 100 // Add a fixed amount to the total score for winning
    }
  }

  // Create the start page UI with centered, larger buttons and a background image
  val startPage = new StackPane {
    // Create an ImageView for the background
    val backgroundImageView = new ImageView(startPageBackground) {
      fitWidth = 800 // Set the width to match the scene
      fitHeight = 600 // Set the height to match the scene
      preserveRatio = true // Preserve aspect ratio
    }

    // Create a VBox for buttons
    val buttonBox = new VBox {
      spacing = 20 // Space between buttons
      padding = Insets(20) // Padding around the VBox
      alignment = Pos.Center // Center-align the VBox contents
      children = Seq(
        new Button {
          text = "Start Game"
          prefWidth = 200 // Set preferred width for the button
          prefHeight = 60 // Set preferred height for the button
          onAction = _ => {
            stage.scene = createGameScene()
          }
        },
        new Button {
          text = "Quit"
          prefWidth = 200 // Set preferred width for the button
          prefHeight = 60 // Set preferred height for the button
          onAction = _ => {
            sys.exit()
          }
        }
      )
    }

    // Add the background image and buttons to the StackPane
    children = Seq(backgroundImageView, buttonBox)
  }

  // Create the game scene
  def createGameScene(): Scene = {
    val canvas = new Canvas(800, 600)
    val gc = canvas.graphicsContext2D

    // Initialize the game state
    restartGame()

    new Scene(800, 600) {
      content = List(canvas, restartButton) // Add both canvas and button to content

      // Game controls and logic
      var upPressed = false
      var downPressed = false

      // Key event handlers
      onKeyPressed = (e: KeyEvent) => {
        if (!gameOver && !gameWon) {
          e.code match {
            case KeyCode.Space => player.flap()
            case KeyCode.Up => upPressed = true
            case KeyCode.Down => downPressed = true
            case _ =>
          }
        }
      }

      onKeyReleased = (e: KeyEvent) => {
        e.code match {
          case KeyCode.Up => upPressed = false
          case KeyCode.Down => downPressed = false
          case _ =>
        }
      }

      // Animation timer
      val timer = AnimationTimer(t => {
        val currentTimeNano = System.nanoTime()
        val delta = (currentTimeNano - lastTime) / 1000000000.0
        lastTime = currentTimeNano
        pipeSpawnTime += delta
        elapsedTime += delta

        if (!gameOver && !gameWon) {
          // Update player movement
          if (upPressed) player.moveUp()
          if (downPressed) player.moveDown()
          player.update()

          // Handle pipes (terrain)
          if (pipeSpawnTime > 2) {
            pipes = pipes :+ new Pipe(800, 100 + Math.random() * 300)
            pipeSpawnTime = 0.0
          }

          pipes.foreach(_.update())
          pipes = pipes.filterNot(_.isOffScreen)

          // Check collisions
          if (pipes.exists(_.intersects(player)) || player.outOfBounds()) {
            gameOver = true
            restartButton.visible = true // Show button when game is over
            updateScore() // Update score when game is over
          }

          // Check if the player has reached the end of the terrain
          if (elapsedTime >= winTime) {
            gameWon = true
            restartButton.visible = true // Show button when game is won
            updateScore() // Update score when game is won
          }

          // Update timer
          currentTime = System.currentTimeMillis()
          val timeElapsed = (currentTime - timerStartTime) / 1000.0

          // Draw everything
          gc.drawImage(gameBackground, 0, 0, 800, 600) // Draw the background image
          player.draw(gc)
          pipes.foreach(_.draw(gc))

          gc.fill = Color.Black
          gc.fillText(f"Time: $timeElapsed%.1f seconds", 10, 40)
          gc.fillText(s"Total Score: $totalScore", 10, 20)
        } else if (gameWon) {
          gc.fill = Color.Black
          gc.fillText("You Win!", 350, 300)
          gc.fillText(s"Final Score: $score", 350, 330)
          gc.fillText(s"Total Score: $totalScore", 350, 360)
        } else if (gameOver) {
          gc.fill = Color.Black
          gc.fillText("Game Over", 350, 300)
          gc.fillText(s"Final Score: $score", 350, 330)
          gc.fillText(s"Total Score: $totalScore", 350, 360)
        }
      })

      timer.start()
    }
  }

  // Create the restart button
  val restartButton = new Button("Restart") {
    layoutX = 350
    layoutY = 500
    visible = false // Initially hidden
    onAction = _ => {
      restartGame()
      visible = false // Hide button after restart
    }
  }

  stage = new PrimaryStage {
    title = "Flappy Bird"
    scene = new Scene(800, 600) {
      root = startPage // Set the start page as the initial scene
    }
  }
}
