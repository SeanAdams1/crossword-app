package client

import shared._
import scalacss.ScalatagsCss._
import scalacss.DevDefaults._
import scalatags.JsDom.all._
import scalatags.jsdom.Tags
import styles._
import org.scalajs.dom.raw.HTMLStyleElement

object Components {

  def getNumber(i: Int): Tag = if (i == 0) b("") else b(i.toString)

  def numberSquare(i: Int): Tag = {
    div(
      crosswordStyles.numberContent,
      fontFamily := "Verdana, Geneva, sans-serif",
      getNumber(i)
    )
  }

  val blackSquare: Tag = {
    input(
      cls := "black",
      crosswordStyles.squareInputBlack,
      tabindex := -1,
      maxlength := 0
    )

  }

  def whiteSquare(squareId: String): Tag = {
    input(
      cls := "white",
      id := squareId,
      crosswordStyles.squareInputWhite,
      maxlength := 1,
      tabindex := -1,
      fontFamily := "Verdana, Geneva, sans-serif",
      onfocus := "highlightClue(this)",
      onblur := "unHighlightClue()"
    )
  }

  def letterSquare(sqID: squareID, colour: CellColour): Tag = if (colour == White) whiteSquare(sqID.toString) else blackSquare


  def gridSquare(sqID: squareID, colour: CellColour, number: Int): Tag = {
    div(
      crosswordStyles.squareContainer,
      letterSquare(sqID, colour),
      numberSquare(number)
    )
  }

  def clueListItem(clue: Clue): Tag = {
    li(
      id := "A" + clue.id,
      whiteSpace := "normal",
      wordWrap := "break-word",
      clue.toString,
      onclick := "highlightWordClue(this)"
    )
  }

  def getClues(clues: List[Clue]): Tag = {
    ol(
      listStyle := "none",
      margin := "0",
      padding := "0",
      cursor := "pointer",
      for {clue <- clues} yield clueListItem(clue)
    )
  }

  def crosswordButton(text: String, onClick: String) : Tag = {
    button(
      crosswordStyles.button,
      text,
      onclick := onClick
    )
  }

  def grid(crosswordI: CrosswordClass) : Tag = {
    div(
      crosswordStyles.crossword,
      crosswordI.gridLayout.squares.map({ case (square, colour, number) => gridSquare(square, colour, number) })
    )
  }

  val buttons: List[(String,String)] = {
    List(
      // (Text, onClick)
      ("Clear selected", "clearSelected()"),
      ("Clear all", "clearAll()"),
      ("Check selected", "checkSelected()"),
      ("Check all", "checkAll()"),
      ("Reveal selected", "revealThis()"),
      ("Reveal all", "revealAll()")
    )
  }

  def clueColumn(header: String, clues: List[Clue]): Tag = {
    div(
      crosswordStyles.clueColumn,
      h2(header),
      getClues(clues)
    )
  }

  def crosswordPage(crossword: CrosswordClass): Tag = {
    html(
      head(
        crosswordStyles.render[HTMLStyleElement]
      ),
      body(
        div(
          crosswordStyles.crosswordColumn,
          grid(crossword),
          for {(text, onClick) <- buttons} yield crosswordButton(text, onClick)
        ),
        clueColumn("Across", crossword.acrossClues),
        clueColumn("Down", crossword.downClues)
      )
    )
  }
}
