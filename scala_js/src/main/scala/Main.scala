package Elegoat

import scala.scalajs.js.JSApp
import org.scalajs.dom

object MainApp extends JSApp {
  object KeyCodes {
    val Up = 38
    val Down = 40
  }
  private val canvas = dom.document.getElementById("elegoat").asInstanceOf[dom.html.Canvas]
  private val keys = scala.collection.mutable.Set.empty[Int]

  def main(): Unit= {
    dom.document.onkeydown = { (e: dom.KeyboardEvent) =>
      keys.add(e.keyCode)
    }
    dom.document.onkeyup = { (e: dom.KeyboardEvent) =>
      keys.remove(e.keyCode)
    }
    dom.setInterval(() => update(), 15)
  }

  var score = 0
  var letters = List.empty[Letter]
  val imgWidth = 120
  val imgHeight = 120
  val goatImg = dom.document.getElementById("goat").asInstanceOf[dom.html.Image]
  var y = 0
  def update(): Unit = {
    sendNewLatter()
    if (keys(KeyCodes.Up)) {
      y = y - 5
    }
    if (keys(KeyCodes.Down)) {
      y = y + 5
    }
    if (y < 0) y = 0
    if (y > (canvas.height - imgHeight)) y = canvas.height - imgHeight

    letters = letters.filter(x => x.x <= canvas.width)
    val (targets, others) = letters.partition(letter => {
      if (letter.x > canvas.width - imgWidth) {
        if (letter.y > y && letter.y < y + imgHeight) {
          true
        } else if (letter.y + letter.h > y && letter.y + letter.h < y + imgHeight) {
          true
        } else {
          false
        }
      } else {
        false
      }
    })
    score = score + targets.size
    letters = others
    draw()
  }

  def draw(): Unit = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    letters.foreach(_.move)
    letters.foreach(_.draw(ctx))
    ctx.drawImage(goatImg, canvas.width - imgWidth, y, imgWidth, imgHeight)
    ctx.font = "bold 20px Century Gothic"
    ctx.fillText(s"Score : ${score}", canvas.width - 200, canvas.height - 25)
  }

  var prev = scalajs.js.Date.now()
  def sendNewLatter(): Unit = {
    val current = scalajs.js.Date.now()
    if (current - prev > (scala.util.Random.nextInt(500) + 1500.00)) {
      val y = scala.util.Random.nextInt(canvas.width - 50)
      letters = letters.+:(new Letter(y, dom.document.getElementById("letter").asInstanceOf[dom.html.Image]))
      prev = current
    }
  }
}

class Letter(_y:Int, img: dom.html.Image) {
  var x: Int = 0
  var y: Int = _y
  val w = 50
  val h = 50
  def move(): Unit = x += 5
  def draw(ctx: dom.CanvasRenderingContext2D): Unit = {
    ctx.drawImage(img, x, y, w, h)
  }
}
