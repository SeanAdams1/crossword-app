package client

import java.awt.Window

import org.scalajs.dom
import dom._
import org.scalajs.dom.raw.{HTMLInputElement, HTMLLIElement}

import scalajs.js.annotation.JSExport
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import language.postfixOps
import shared._
import Components._
import autowire._


object Ajaxer extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer] {
  override def doCall(req: Request) = {
    dom.ext.Ajax.post(
      url = "/ajax/" + req.path.mkString("/"),
      data = upickle.default.write(req.args)
    ).map(_.responseText)
  }

  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)

  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

@JSExport
object CrosswordApp extends {

  //Allows foreach to be used on a nodelist

  implicit class NodeListSeq[T <: Node](nodes: DOMList[T]) extends IndexedSeq[T] {
    override def foreach[U](f: T => U): Unit = {
      for (i <- 0 until nodes.length) {
        f(nodes(i))
      }
    }

    override def length: Int = nodes.length

    override def apply(idx: Int): T = nodes(idx)
  }

  var currentClueGlobal: Clue = _

  def getCurrentSelected: String = document.activeElement.asInstanceOf[dom.html.Input].getAttribute("id")

  @JSExport
  def main(): Unit = {

    var useCurrentClue = false

    def setNextTab(current: String, cw: CrosswordClass): Unit = {
      val nextClue = cw.getNextClue(currentClueGlobal)
      val nextClueStart = nextClue.squares.head
      currentClueGlobal = nextClue
      useCurrentClue = true
      document.getElementById(nextClueStart.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setPreviousTab(current: String)(implicit cw: CrosswordClass): Unit = {
      val lastClue = cw.getLastClue(currentClueGlobal)
      val lastClueStart = cw.getSquares(lastClue).head
      currentClueGlobal = lastClue
      useCurrentClue = true
      document.getElementById(lastClueStart.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setNextSquare(current: String, currentClue: Clue)(implicit cw: CrosswordClass): Unit = {
      val (nextSquare, nextClue) = cw.getNextSquare(current.toSquareID(cw.gridLayout.width), currentClue)
      currentClueGlobal = nextClue
      useCurrentClue = true
      document.getElementById(nextSquare.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setPreviousSquare(current: String)(implicit cw: CrosswordClass): Unit = {
      val (previousSquare, previousClue) = cw.getPreviousSquare(current.toSquareID(cw.gridLayout.width), currentClueGlobal)
      currentClueGlobal = previousClue
      useCurrentClue = true
      document.getElementById(previousSquare.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setThisSquareValue(letter: Char): Unit = currentSelection.value = letter.toString

    def currentSelection = document.getElementById(getCurrentSelected).asInstanceOf[dom.html.Input]

    def doBackspace(cw: CrosswordClass): Unit = if (currentSelection.value == " ") focusLast(currentSelection, cw) else currentSelection.value = " "

    def focusLast(input: dom.html.Input, cw: CrosswordClass): Unit = {
      val currentID = input.getAttribute("id")
      setPreviousSquare(currentID)(cw)
      currentSelection.value = " "
    }

    def doCharacterPress(letter: Char, cw: CrosswordClass): Unit = {
      setThisSquareValue(letter)
      setNextSquare(getCurrentSelected, currentClueGlobal)(cw)
    }

    type Key = Int
    type possibleMove = squareID => Option[squareID]
    type Move = squareID => squareID
    val UP: Key = 38
    val LEFT: Key = 37
    val DOWN: Key = 40
    val RIGHT: Key = 39
    val backspace: Key = 8
    val tab: Key = 9

    val arrowKeys: Set[Key] = Set(UP, DOWN, LEFT, RIGHT)
    val letterKeys: Set[Key] = ('A' to 'Z').map(_.toInt).toSet
    val numberKeys: Set[Key] = (48 to 57).toSet
    val keypadKeys: Set[Key] = (96 to 111).toSet
    val symbolKeys: Set[Key] = ((186 to 192) ++ (219 to 222)).toSet
    val invalidKeys = numberKeys ++ keypadKeys ++ symbolKeys
    val specialKeys: Set[Key] = Set('C', 'R', 'X').map(_.toInt)

    def canMove(f: possibleMove)(implicit id: squareID): Boolean = {
      f(id).isDefined
    }

    def move(movement: Move)(implicit id: squareID, cw: CrosswordClass): Unit = {
      val nextSquare = movement(id)
      currentClueGlobal = cw.getClue(nextSquare)
      document.getElementById(nextSquare.toString).asInstanceOf[dom.html.Input].focus()
    }

    def canMoveUp(implicit current: squareID, cw: CrosswordClass) = canMove(cw.squareAbove)

    def canMoveDown(implicit current: squareID, cw: CrosswordClass) = canMove(cw.squareBelow)

    def canMoveRight(implicit current: squareID, cw: CrosswordClass) = canMove(cw.squareRight)

    def canMoveLeft(implicit current: squareID, cw: CrosswordClass) = canMove(cw.squareLeft)

    def moveUp(implicit current: squareID, cw: CrosswordClass): Unit = move(cw.getSquare(cw.squareAbove))

    def moveDown(implicit current: squareID, cw: CrosswordClass): Unit = move(cw.getSquare(cw.squareBelow))

    def moveLeft(implicit current: squareID, cw: CrosswordClass): Unit = move(cw.getSquare(cw.squareLeft))

    def moveRight(implicit current: squareID, cw: CrosswordClass): Unit = move(cw.getSquare(cw.squareRight))

    def doArrowKey(key: Key, cw: CrosswordClass): Unit = {
      val current = getCurrentSelected.toSquareID(cw.gridLayout.width)
      key match {
        case UP if canMoveUp(current, cw) => moveUp(current, cw)
        case DOWN if canMoveDown(current, cw) => moveDown(current, cw)
        case LEFT if canMoveLeft(current, cw) => moveLeft(current, cw)
        case RIGHT if canMoveRight(current, cw) => moveRight(current, cw)
        case _ =>
      }
    }

    val C = 'C'.toInt
    val X = 'X'.toInt
    val R = 'R'.toInt
    val I = 'I'.toInt

    def clearSelected(implicit cw: CrosswordClass): Unit = {

      val selectedSquares = cw.getSquares(currentClueGlobal)

      selectedSquares.foreach { s => {
        val square = document.getElementById(s.toString).asInstanceOf[dom.html.Input]
        square.value = " "
      }
      }
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def clearAll(implicit cw: CrosswordClass): Unit = {

      val squares = document.getElementsByClassName("white").asInstanceOf[NodeListOf[dom.html.Input]]
      squares.foreach { s => s.value = " " }
      currentClueGlobal = cw.getNextClue(cw.noClue)
      useCurrentClue = true
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def revealThis(implicit cw: CrosswordClass): Unit = {

      reveal(currentClueGlobal)
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def reveal(clue: Clue)(implicit cw: CrosswordClass): Unit = {
      val answer = clue.solution.toString.toList.filter(_ != ' ')
      val linkedSquares = cw.getSquares(clue)
      val x = linkedSquares.zip(answer)
      x.foreach {
        {
          case (square, letter) =>
            val input = document.getElementById(square.toString).asInstanceOf[dom.html.Input]
            input.value = letter.toString
        }
      }
    }

    def revealAll(implicit cw: CrosswordClass): Unit = {
      cw.sortedClues.foreach { clue => reveal(clue) }
      currentClueGlobal = cw.getNextClue(cw.noClue)
      useCurrentClue = true
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def checkSelected(implicit cw: CrosswordClass): Unit = {
      check(currentClueGlobal)
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def check(clue: Clue)(implicit cw: CrosswordClass): Unit = {
      val solution = clue.solution.toString.toList.filter(_ != ' ')
      val squares = cw.getSquares(clue).zip(solution)
      squares.foreach {
        {
          case (square, letter) => val input = document.getElementById(square.toString).asInstanceOf[dom.html.Input]
            if (input.value != letter.toString) input.value = " "
        }
      }
    }

    def checkAll(implicit cw: CrosswordClass): Unit = {
      cw.sortedClues.foreach { clue => check(clue) }
      currentClueGlobal = cw.getNextClue(cw.noClue)
      useCurrentClue = true
      document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
    }

    def doCntlFunction(key: Key, cw: CrosswordClass): Unit = {
      key match {
        case C => checkSelected(cw)
        case R => revealThis(cw)
        case X => clearSelected(cw)
      }
    }

    def doCtrlShiftFunction(key: Key, cw: CrosswordClass): Unit = {
      key match {
        case C => checkAll(cw)
        case R => revealAll(cw)
        case X => clearAll(cw)
      }
    }

    def highlightClue(input: HTMLInputElement, cw: CrosswordClass): Unit = {

      val thisSquare = input.id.toSquareID(cw.gridLayout.width)
      currentClueGlobal = if (!useCurrentClue) cw.getClue(thisSquare) else currentClueGlobal

      val linkedSquares = cw.getSquares(currentClueGlobal).filter(_ != thisSquare)
      linkedSquares.foreach { s => {
        val square = document.getElementById(s.toString).asInstanceOf[dom.html.Input]
        square.setAttribute("style", "background-color:#33adff")
      }
      }
      input.setAttribute("style", "background-color:#b3e0ff")
      document.getElementById("A" + currentClueGlobal.id.toString).asInstanceOf[dom.html.LI].setAttribute("style", "background-color:#99d6ff")
      useCurrentClue = false
    }

    def highlightWordClue(li: HTMLLIElement, cw: CrosswordClass): Unit = {

      //Just focus on the first square of the clue and the rest should handle itself
      val clue = cw.getClue(li.id.tail.toClueID)
      val linkedSquares = cw.getSquares(clue)
      currentClueGlobal = clue
      useCurrentClue = true
      document.getElementById(linkedSquares.head.toString).asInstanceOf[dom.html.Input].focus
    }

    // Set all squares back to white
    def unHighlightClue(): Unit = {
      val inputs = document.getElementsByClassName("white").asInstanceOf[NodeListOf[dom.html.Input]]
      inputs.foreach { s => {
        s.setAttribute("style", "background-color:white")
      }
      }
      val clues = document.getElementsByTagName("li").asInstanceOf[NodeListOf[dom.html.LI]]
      clues.foreach { s => {
        s.setAttribute("style", "background-color:white")
      }
      }
    }

      def doCrossword(cw: CrosswordClass): Unit = {

        document.getElementById("contents").appendChild(crosswordPage(cw).render)
        document.getElementById("check_selected").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => checkSelected(cw)
        document.getElementById("check_all").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => checkAll(cw)
        document.getElementById("clear_selected").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => clearSelected(cw)
        document.getElementById("clear_all").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => clearAll(cw)
        document.getElementById("reveal_selected").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => revealThis(cw)
        document.getElementById("reveal_all").asInstanceOf[html.Button].onclick = (e: dom.MouseEvent) => revealAll(cw)
        document.getElementsByClassName("white").asInstanceOf[NodeListOf[html.Input]].foreach{s => s.onfocus = (e: FocusEvent) => highlightClue(s,cw)}
        document.getElementsByClassName("white").asInstanceOf[NodeListOf[html.Input]].foreach{s => s.onblur = (e: FocusEvent) => unHighlightClue()}
        document.getElementsByClassName("clue_list_item").asInstanceOf[NodeListOf[html.LI]].foreach{s => s.onclick = (e: MouseEvent) => highlightWordClue(s,cw)}

          dom.window.onkeydown = (e: dom.KeyboardEvent) => {
          if (invalidKeys.contains(e.keyCode)) {
            e.preventDefault()
          }
          else if (letterKeys.contains(e.keyCode)) {
            e.preventDefault()
            //document.activeElement.asInstanceOf[dom.html.Input].value = " "
            (e.shiftKey, e.ctrlKey) match {
              case (false, true) if specialKeys.contains(e.keyCode) => doCntlFunction(e.keyCode, cw)
              case (true, true) if specialKeys.contains(e.keyCode) => doCtrlShiftFunction(e.keyCode, cw)
              case _ => doCharacterPress(e.keyCode.toChar, cw)
            }
          }
          else if (e.keyCode == tab) {
            e.preventDefault()
            if (!e.shiftKey) setNextTab(getCurrentSelected, cw) else setPreviousTab(getCurrentSelected)(cw)
          }
          else if (e.keyCode == backspace) {
            e.preventDefault()
            doBackspace(cw)
          }
          else if (arrowKeys.contains(e.keyCode)) {
            e.preventDefault()
            doArrowKey(e.keyCode, cw)
          }
        }
      }

      Ajaxer[Api].getCrossword().call().foreach { x => doCrossword(x)}
    }
}



