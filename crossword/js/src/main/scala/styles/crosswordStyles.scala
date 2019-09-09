package styles

import scalacss.DevDefaults._
import scalacss.internal.Media
import scalacss.internal.Media.Landscape

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
    color black,
    fontSize(12 px),
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
      fontSize((0.66 * 5.26) vw)),
      mobile(width(100 %%),
      fontSize(12 pt))
  )

  val clueColumn: StyleA = style(
    common,
    width(25 %%),
    float left,
    marginLeft(5 px),
    marginRight(5 px),
    fontSize(18 px),
    smallScreen(width(100 %%)),
    mediumScreen(width(50 %%)),
    mobile(width(100 %%))
  )

  val button: StyleA = style(
    common,
    float left,
    width(30 %%),
    height(20 %%),
    fontSize(75 %%),
    marginTop(0.75 vw),
    marginBottom(0.75 vw),
    marginRight(0.75 vw),
    marginLeft(0.75 vw),
    backgroundColor rgb(194, 58, 224),
    borderRadius(12 px),
    borderColor rgb(194,48,200),
    &.hover(borderColor yellow),
    mobile(width(20 %%))
  )

  def smallScreen: Media.Query = media.maxWidth(600 px)
  def mediumScreen: Media.Query = media.maxWidth(800 px).minWidth(601 px)
  def mobile: Media.Query = media.orientation(Media.Portrait)
}
