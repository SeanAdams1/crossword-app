package object shared {

  //Solution
  case class Solution(solution: List[String]) {
    assert(solution forall (word => word == word.toUpperCase), "Solution must be uppercase")

    override def toString: String = solution mkString " "

    val numberOfWords: Int = solution.length
    val solutionLength: Int = solution.map(_.length).sum
    val wordLengths: String = if (solution.isEmpty) "" else s"(${solution.map(_.length) mkString ","})"
  }

  //ClueID
  case class ClueID(number: Int, direction: Direction) {
    def ==(that: ClueID): Boolean = this.number == that.number && this.direction == that.direction

    def directionMatches(that: ClueID): Boolean = this.direction == that.direction

    def <(that: ClueID): Boolean = {
      if (directionMatches(that)) this.number < that.number
      else this.direction == Across
    }

    override def toString: String = s"$number${direction.toString.head}"
  }

  //Direction
  sealed trait Direction {
    def isAcross: Boolean = this == Across
  }

  case object Across extends Direction

  case object Down extends Direction

  // CellColour
  sealed trait CellColour extends Product with Serializable {
    def isBlack: Boolean = this == Black
  }

  case object White extends CellColour

  case object Black extends CellColour

  // squareID
  case class squareID(position: Int, width: Int) {
    private def getLetter(i: Int): String = ('Z' :: ('A' to 'Z').toList) (i).toString

    private def addToString(s: String, i: Int): String = getLetter(i) + s

    private def columnNumber(i: Int, width: Int): Int = if (i % width == 0) width else i % width

    private def getRowLetters(i: Int, width: Int): String = {
      def loop(i: Int, s: String): String = {
        if (i <= 26) addToString(s, i) else loop((i - i % 26) / 26, addToString(s, i % 26))
      }

      loop((i - 1) / width + 1, "")
    }

    lazy val id: String = getRowLetters(position, width) + columnNumber(position, width).toString
    lazy val rowID: String = id.takeWhile(!_.isDigit)
    lazy val columnID: String = id.dropWhile(!_.isDigit)

    override def toString: String = id

    def <(that: squareID): Boolean = this.position < that.position
  }

  // Clue
  case class Clue(number: Int, direction: Direction, solution: Solution, clue: String, linkedClues: List[ClueID])(implicit grid: CrosswordGrid) {

    val id: ClueID = ClueID(number, direction)
    val squares: squareList = grid.getSquares(id)
    override def toString: String = s"$number ${if (number < 10) " " else ""}$clue ${if (solution.solutionLength > 0) solution.wordLengths else ""}"
  }

  type squareList = List[squareID]

  implicit class squareListFunctions(sl: squareList) {
    def <(that: squareList): Boolean = (sl.head < that.head) || (sl.head == that.head && sl(1) < that(1))
  }

  implicit class crosswordStringFunctions(s: String) {
    def toClueID: ClueID = {
      assert(s.last == 'A' || s.last == 'D', s"Failed on $s")
      assert(s.dropRight(1).forall(_.isDigit), s"Failed on $s")
      if (s.last == 'A') ClueID(s.dropRight(1).toInt, Across) else ClueID(s.dropRight(1).toInt, Down)
    }

    private def toNumber(c: Char): Int = ('Z' :: ('A' to 'Y').toList).indexOf(c)

    def toSquareID(w: Int): squareID = {
      val letters = s.takeWhile(!_.isDigit)
      val number = BigInt(s.dropWhile(!_.isDigit)).toInt
      val number2 = letters.foldRight((0, 0))({ case (c, (total, power)) => (total + (math.pow(26, power).toInt * toNumber(c)), power + 1) })._1 - 1
      val position = w * number2 + number
      squareID(position, w)
    }
  }

    implicit class listOps[A](ls: List[A]) {
      def headlessDropWhile(f: A => Boolean): List[A] = ls.dropWhile(f).tail
    }

    def cellColourMap = Map(('W', White), ('B', Black))


}