package styles

import scalacss.DevDefaults._
import scalacss.internal.Media

import language.postfixOps

// css styles
object crosswordStyles extends StyleSheet.Inline {

  import dsl._

  def common: StyleS = mixin(boxSizing.borderBox)

  val  numberContent: StyleA = style(
    common,
    backgroundColor transparent,
    position absolute,
    height(50 %%),
    width(25 %%),
    top(0 %%),
    left(0 %%),
    fontSize(33 %%),
    textAlign center
  )

  val squareContainer: StyleA = style(
    common,
    width(6.66 %%),
    paddingBottom(6.66 %%),
    backgroundColor white,
    position relative,
    float left,
    margin(0 %%)
  )

  def squareStyle: StyleS = mixin(
    common,
    position absolute,
    height(100 %%),
    width(100 %%),
    borderStyle solid,
    borderColor black,
    borderWidth thin,
    top(0 px),
    left(0 px)
  )


  val squareInputWhite: StyleA = style(
    squareStyle,
    textTransform uppercase,
    textAlign center,
    backgroundColor white,
    color blue,
    fontSize(100 %%),
    &.focus(outlineColor(rgb(255,102,0)))
  )

  val squareInputBlack: StyleA = style(
    squareStyle,
    backgroundColor black,
    outline none
  )

  val crossword: StyleA = style(
    common,
    width(100 %%),
    paddingBottom(100 %%),
    marginBottom(10 px)
  )

  val crosswordColumn: StyleA = style(
    common,
    width(38 %%),
    fontSize((0.38 * 5.26) vw),
    float left,
    smallScreen(width(100 %%),
      fontSize(5.26 vw)),
    mediumScreen(width(66 %%),
      fontSize((0.66 * 5.26) vw))
  )

  val clueColumn: StyleA = style(
    common,
    width(25 %%),
    float left,
    marginLeft(5 px),
    marginRight(5 px),
    fontSize(18 px),
    smallScreen(width(100 %%)),
    mediumScreen(width(50 %%))
  )

  val button: StyleA = style(
    common,
    width(40 %%),
    fontSize(1.5 vw),
    marginTop(0.75 vw),
    marginBottom(0.75 vw),
    marginRight(0.75 vw),
    marginLeft(0.75 vw)
  )

  def smallScreen: Media.Query = media.maxWidth(600 px)
  def mediumScreen: Media.Query = media.maxWidth(800 px).minWidth(601 px)
}
