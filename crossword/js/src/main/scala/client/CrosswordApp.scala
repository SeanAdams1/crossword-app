package client


import org.scalajs.dom
import dom.{document, Node, NodeListOf, DOMList}
import org.scalajs.dom.raw.{HTMLInputElement, HTMLLIElement}
import scalajs.js.annotation.{JSExport, JSExportTopLevel}
import language.postfixOps

import shared._
import Components._

@JSExportTopLevel("CrosswordApp")
object CrosswordApp {

  // Set up the crossword

  def gridStr: String = "WBWBWBWBWBWBWBWWWWWWWWWWBWWWWWWBWBWBWBWBWBWBWWWWWWBWWWWWWWWWBBWBWBWBWBBBWBBWWWWWWWBWWWWWWWWBBBWBBBWBWBWBWWWWWWBWWWBWWWWWWBWBWBWBBBWBBBWWWWWWWWBWWWWWWWBBWBBBWBWBWBWBBWWWWWWWWWBWWWWWWBWBWBWBWBWBWBWWWWWWBWWWWWWWWWWBWBWBWBWBWBWBW"

  implicit def grid: CrosswordGrid = CrosswordGrid(15, 15, gridStr)

  val clues = List(Clue(9, Across, Solution(List("BARBARIAN")), "Wild Boys, initially song and note getting stick at first", List("9A".toClueID)), Clue(10, Across, Solution(List("ON", "AIR")), "Playing song live", List("10A".toClueID)), Clue(11, Across, Solution(List("TASTY")), "Attractive model with a pen", List("11A".toClueID)), Clue(12, Across, Solution(List("NOTORIOUS")), "Rejection for Duran Duran song", List("12A".toClueID)), Clue(13, Across, Solution(List("FAN", "BELT")), "Admirer hit part of car", List("13A".toClueID)), Clue(14, Across, Solution(List()), "See 3", List("3D".toClueID, "14A".toClueID)), Clue(17, Across, Solution(List("DOGGO")), "Still in party? Slip away after girl's left", List("17A".toClueID)), Clue(19, Across, Solution(List("WAD")), "Hunk from poster after woman", List("19A".toClueID)), Clue(20, Across, Solution(List("RESET")), "Place again on the box", List("20A".toClueID)), Clue(21, Across, Solution(List("PRETEND")), "Put on pressure to split, film's captured", List("21A".toClueID)), Clue(22, Across, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID)), Clue(24, Across, Solution(List("ALLERGIST")), "Steal girl? Fancy one seeing someone highly sensitive?", List("24A".toClueID)), Clue(26, Across, Solution(List("MARIE","ROSE")), "Planet Earth features one topless star dressing", List("26A".toClueID, "25D".toClueID)), Clue(28, Across, Solution(List("DUNKS")), "Drops in drink revenue primarily from boozers", List("28A".toClueID)), Clue(29, Across, Solution(List("GOOD", "SENSE")), "Wit and idols seen playing around circuit", List("29A".toClueID)), Clue(1, Down, Solution(List("ABUT")), "Touch up member of band", List("1D".toClueID)), Clue(2, Down, Solution(List("ORISON")), "Group of stars penning intro to Save a Prayer", List("2D".toClueID)), Clue(3, Down, Solution(List("MARYLEBONE","STATION")), "Upset at teary Simon Le Bon interrupting where some tracks end", List("3D".toClueID, "14A".toClueID)), Clue(4, Down, Solution(List("LINNET")), "Bird left pub  before start of extra time", List("4D".toClueID)), Clue(5, Down, Solution(List("UNITISED")), "Sex appeal wearing kinky undies turned into a single piece", List("5D".toClueID)), Clue(6, Down, Solution(List("FOUR", "WEDDINGS", "AND", "A", "FUNERAL")), "Crazy Duran Duran fans go wild - fee for picture", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID)), Clue(7, Down, Solution(List("CANOPIES")), "One inside models for covers", List("7D".toClueID)), Clue(8, Down, Solution(List("BRAS")), "British like supporters", List("8D".toClueID)), Clue(13, Down, Solution(List("FED", "UP")), "Bored following half of band, ran off during record", List("13D".toClueID)), Clue(15, Down, Solution(List("AGRONOMIST")), "Mutation of organism to one studying land?", List("15D".toClueID)), Clue(16, Down, Solution(List("NATAL")), "Cheeky? Section of arena talked", List("16D".toClueID)), Clue(18, Down, Solution(List("GREMLINS")), "Film men wrestling with girls", List("18D".toClueID)), Clue(19, Down, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID)), Clue(22, Down, Solution(List("FATHOM")), "Flab reduced in work out", List("22D".toClueID)), Clue(23, Down, Solution(List("RARING")), "Artist and band full of enthusiasm", List("23D".toClueID)), Clue(24, Down, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID)), Clue(25, Down, Solution(List()), "See 26", List("26A".toClueID, "25D".toClueID)), Clue(27, Down, Solution(List("EXES")), "Old lovers need tablet - nooky when erect?", List("27D".toClueID)))

  val cw = CrosswordClass(clues, grid)


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


  // global variables to hold the current clue - Don't judge me!
  val noClue: Clue = Clue(0,Across,Solution(Nil),"",Nil)
  var useCurrentClue = false
  var currentClueGlobal: Clue = noClue

  def getCurrentSelected: String = document.activeElement.asInstanceOf[dom.html.Input].getAttribute("id")

  @JSExport
  def main(): Unit = {

    import cw._
    import cw.gridLayout.width

    // Render the crossword to the page
    document.getElementById("contents").appendChild(crosswordPage(cw).render)

    def setNextTab(current: String): Unit = {
      val nextClue = getNextClue(currentClueGlobal)
      val nextClueStart = nextClue.squares.head
      currentClueGlobal = nextClue
      useCurrentClue = true
      document.getElementById(nextClueStart.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setPreviousTab(current: String): Unit = {
      val lastClue = getLastClue(currentClueGlobal)
      val lastClueStart = getSquares(lastClue).head
      currentClueGlobal = lastClue
      useCurrentClue = true
      document.getElementById(lastClueStart.toString).asInstanceOf[dom.html.Input].focus()

    }

    def setNextSquare(current: String, currentClue: Clue): Unit = {
      val (nextSquare, nextClue) = getNextSquare(current.toSquareID(width), currentClue)
      currentClueGlobal = nextClue
      useCurrentClue = true
      document.getElementById(nextSquare.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setPreviousSquare(current: String): Unit = {
      val (previousSquare, previousClue) = getPreviousSquare(current.toSquareID(width), currentClueGlobal)
      currentClueGlobal = previousClue
      useCurrentClue = true
      document.getElementById(previousSquare.toString).asInstanceOf[dom.html.Input].focus()
    }

    def setThisSquareValue(letter: Char): Unit = currentSelection.value = letter.toString

    def currentSelection = document.getElementById(getCurrentSelected).asInstanceOf[dom.html.Input]

    def doBackspace(): Unit = if (currentSelection.value == " ") focusLast(currentSelection) else currentSelection.value = " "

    def focusLast(input: dom.html.Input): Unit = {
      val currentID = input.getAttribute("id")
      setPreviousSquare(currentID)
      currentSelection.value = " "

    }

    def doCharacterPress(letter: Char): Unit = {
      setThisSquareValue(letter)
      setNextSquare(getCurrentSelected, currentClueGlobal)
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
    val specialKeys: Set[Key] = Set('C','R','X').map(_.toInt)

    def canMove(f: possibleMove)(implicit id: squareID): Boolean = {
      f(id).isDefined
    }

    def move(movement: Move)(implicit id: squareID): Unit = {
      val nextSquare = movement(id)
      currentClueGlobal = getClue(nextSquare)
      document.getElementById(nextSquare.toString).asInstanceOf[dom.html.Input].focus()
    }


    def canMoveUp(implicit current: squareID) = canMove(squareAbove)

    def canMoveDown(implicit current: squareID) = canMove(squareBelow)

    def canMoveRight(implicit current: squareID) = canMove(squareRight)

    def canMoveLeft(implicit current: squareID) = canMove(squareLeft)

    def moveUp(implicit current: squareID): Unit = move(getSquare(squareAbove))

    def moveDown(implicit current: squareID): Unit = move(getSquare(squareBelow))

    def moveLeft(implicit current: squareID): Unit = move(getSquare(squareLeft))

    def moveRight(implicit current: squareID): Unit = move(getSquare(squareRight))

    def doArrowKey(key: Key): Unit = {
      val current = getCurrentSelected.toSquareID(width)
      key match {
        case UP if canMoveUp(current) => moveUp(current)
        case DOWN if canMoveDown(current) => moveDown(current)
        case LEFT if canMoveLeft(current) => moveLeft(current)
        case RIGHT if canMoveRight(current) => moveRight(current)
        case _ =>
      }
    }

    val C = 'C'.toInt
    val X = 'X'.toInt
    val R = 'R'.toInt
    val I = 'I'.toInt

    def doCntlFunction(key: Key): Unit = {
      key match {
        case C => checkSelected()
        case R => revealThis()
        case X => clearSelected()
      }
    }
//    def invertColours(): Unit = {
//      val whiteSquares: NodeListOf[HTMLInputElement] = document.getElementsByClassName("white").asInstanceOf[NodeListOf[HTMLInputElement]]
//      val blackSquares: NodeListOf[HTMLInputElement] = document.getElementsByClassName("black").asInstanceOf[NodeListOf[HTMLInputElement]]
//
//      whiteSquares foreach {s => {s.setAttribute("background-color","black")
//        s.setAttribute("color","white")}}
//
//      blackSquares foreach {s => s.setAttribute("background-color","white")}
//    }

    def doCtrlShiftFunction(key: Key): Unit = {
      key match {
        case C => checkAll()
        case R => revealAll()
        case X => clearAll()
      }
    }

    dom.window.onkeydown = (e: dom.KeyboardEvent) => {
      if (invalidKeys.contains(e.keyCode)){
        e.preventDefault()
      }
      else if (letterKeys.contains(e.keyCode)) {
        e.preventDefault()
        (e.shiftKey,e.ctrlKey) match {
          case (false, true) if specialKeys.contains(e.keyCode) => doCntlFunction(e.keyCode)
          case (true, true) if specialKeys.contains(e.keyCode) => doCtrlShiftFunction(e.keyCode)
          case (_, _) => doCharacterPress(e.keyCode.toChar)
        }
      }
      else if (e.keyCode == tab) {
        e.preventDefault()
        if (!e.shiftKey) setNextTab(getCurrentSelected) else setPreviousTab(getCurrentSelected)
      }
      else if (e.keyCode == backspace) {
        e.preventDefault()
        doBackspace()
      }
      else if (arrowKeys.contains(e.keyCode)) {
        e.preventDefault()
        doArrowKey(e.keyCode)
      }
    }

  }


  //Highlights the squares that correspond to the current clue

   @JSExportTopLevel("highlightClue")
  def highlightClue(input: HTMLInputElement): Unit = {
    import cw._
    import cw.gridLayout.width

    val thisSquare = input.id.toSquareID(width)
    currentClueGlobal = if (!useCurrentClue) getClue(thisSquare) else currentClueGlobal

    val linkedSquares = getSquares(currentClueGlobal).filter(_ != thisSquare)
    linkedSquares.foreach { s => {
      val square = document.getElementById(s.toString).asInstanceOf[dom.html.Input]
      square.setAttribute("style", "background-color:#33adff")
    }
    }
    input.setAttribute("style", "background-color:#b3e0ff")
    document.getElementById("A" + currentClueGlobal.id.toString).asInstanceOf[dom.html.LI].setAttribute("style", "background-color:#99d6ff")
    useCurrentClue = false
  }

  @JSExportTopLevel("highlightWordClue")
  def highlightWordClue(li: HTMLLIElement): Unit = {
    import cw._

    //Just focus on the first square of the clue and the rest should handle itself
    val clue = getClue(li.id.tail.toClueID)
    val linkedSquares = getSquares(clue)
    currentClueGlobal = clue
    useCurrentClue = true
    document.getElementById(linkedSquares.head.toString).asInstanceOf[dom.html.Input].focus
  }

  // Set all squares back to white
  @JSExportTopLevel("unHighlightClue")
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

  @JSExportTopLevel("clearSelected")
  def clearSelected(): Unit = {
    import cw._

    val selectedSquares = getSquares(currentClueGlobal)

    selectedSquares.foreach { s => {
      val square = document.getElementById(s.toString).asInstanceOf[dom.html.Input]
      square.value = " "
    }
    }
    document.getElementById(getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }

  @JSExportTopLevel("clearAll")
  def clearAll(): Unit = {
    import cw._
    val squares = document.getElementsByClassName("white").asInstanceOf[NodeListOf[dom.html.Input]]
    squares.foreach { s => s.value = " " }
    currentClueGlobal = getNextClue(noClue)
    useCurrentClue= true
    document.getElementById(getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }

  @JSExportTopLevel("revealThis")
  def revealThis(): Unit = {
    import cw._
    reveal(currentClueGlobal)
    document.getElementById(getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }

  def reveal(clue:Clue): Unit = {
    import cw._
    val answer = clue.solution.toString.toList.filter(_ != ' ')
    val linkedSquares = getSquares(clue)
    val x = linkedSquares.zip(answer)
    x.foreach {
      {
        case (square, letter) =>
          val input = document.getElementById(square.toString).asInstanceOf[dom.html.Input]
          input.value = letter.toString
      }
    }

  }

  @JSExportTopLevel("revealAll")
  def revealAll(): Unit = {import cw._
    sortedClues.foreach {clue => reveal(clue)}
    currentClueGlobal = getNextClue(noClue)
    useCurrentClue = true
    document.getElementById(getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }


  @JSExportTopLevel("checkSelected")
  def checkSelected(): Unit = {
    check(currentClueGlobal)
    document.getElementById(cw.getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }


  def check(clue:Clue): Unit = {
    val solution = clue.solution.toString.toList.filter(_ != ' ')
    val squares = cw.getSquares(clue).zip(solution)

    squares.foreach {
      {case (square,letter) => val input = document.getElementById(square.toString).asInstanceOf[dom.html.Input]
        if (input.value != letter.toString) input.value = " "
      }
    }

  }

  @JSExportTopLevel("checkAll")
  def checkAll(): Unit = {
    import cw._
    sortedClues.foreach { clue => check(clue)}
    currentClueGlobal = getNextClue(noClue)
    useCurrentClue = true
    document.getElementById(getSquares(currentClueGlobal).head.toString).asInstanceOf[dom.html.Input].focus()
  }

}
