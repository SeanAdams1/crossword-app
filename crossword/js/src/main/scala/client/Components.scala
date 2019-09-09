package client

import shared._
import scalacss.ScalatagsCss._
import scalacss.DevDefaults._
import scalatags.JsDom.all._
import scalatags.jsdom.Tags
import styles._
import org.scalajs.dom.raw.HTMLStyleElement

object Components {

  def divit(x:String): Tag = div(s)

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
      fontFamily := "Verdana, Geneva, sans-serif"
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
      cls := "clue_list_item",
      id := "A" + clue.id,
      whiteSpace := "normal",
      wordWrap := "break-word",
      clue.toString,
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

  def crosswordButton(text: String,idText:String) : Tag = {
    button(
      crosswordStyles.button,
      text,
      id := idText
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
      // (Text)
      ("Clear selected", "clear_selected"),
      ("Clear all", "clear_all"),
      ("Check selected", "check_selected"),
      ("Check all", "check_all"),
      ("Reveal selected", "reveal_selected"),
      ("Reveal all", "reveal_all")
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
          for {(text,idText) <- buttons} yield crosswordButton(text,idText)
        ),
        clueColumn("Across", crossword.acrossClues),
        clueColumn("Down", crossword.downClues)
      )
    )
  }
}
