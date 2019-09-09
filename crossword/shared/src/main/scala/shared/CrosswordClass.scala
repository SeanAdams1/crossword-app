package shared

import upickle.default.{ReadWriter => RW, macroRW}

case class CrosswordClass(clues: List[Clue], gridLayout: CrosswordGrid){

  val sortedClues: List[Clue] = clues.sortWith(_.id < _.id)
  val sortedWithoutLinked: List[Clue] = sortedClues.filter(a => a.linkedClues.head == a.id)
  val acrossClues: List[Clue] = sortedClues.takeWhile(_.direction.isAcross)
  val downClues: List[Clue] = sortedClues.dropWhile(_.direction.isAcross)
  val squareMap: Map[Clue,squareList] = sortedClues.map(a => (a,getSquares(a))).toMap.withDefaultValue(Nil)
  val clueMap: Map[squareID,List[Clue]] = squareMap.toList.flatMap({case(a,b) => b.map(b => (b,a))}).groupBy(_._1).map({case (b,ls) => (b,ls.map(_._2).sortWith(_.id < _.id))})
  val noClue: Clue = Clue(0,Across,Solution(Nil),"",Nil,gridLayout)

  def getNextClueFrom(ls:List[Clue])(clue:Clue): Clue = {
    if (clue.id == "0A".toClueID) ls.head
    else ls.headlessDropWhile(_ != clue).headOption.getOrElse(ls.head)
  }

  def getNextClue(id: Clue): Clue = getNextClueFrom(sortedWithoutLinked)(id)
  def getLastClue(id: Clue): Clue =  getNextClueFrom(sortedWithoutLinked.reverse)(id)
  def getClue(clueId: ClueID): Clue = clues.dropWhile(_.id != clueId).head

  def getClue(sqID: squareID): Clue = getClue(clueMap.filter(_._1 == sqID).head._2.head.linkedClues.head)

  def getSquares(clue: Clue): squareList = (for {i <- clue.linkedClues } yield gridLayout.getSquares(i)).foldRight(Nil:squareList)((a,b) => a ++ b)

  def getNextSquare(sqID:squareID,clue:Clue): (squareID,Clue) = {
    val nextSquare = squareMap(clue).headlessDropWhile(_!=sqID)
    if (nextSquare.nonEmpty) (nextSquare.head,clue)
    else {val nextClue = getNextClue(clue)
      (squareMap(nextClue).head,nextClue)}
  }

  def getPreviousSquare(sqID:squareID,id:Clue): (squareID,Clue) = {
    val lastSquare = squareMap(id).takeWhile(_!=sqID)
    if (lastSquare.nonEmpty) (lastSquare.last,id)
    else {val lastClue = getLastClue(id)
      (squareMap(lastClue).last,lastClue)}
  }

  def squareInDirection(f: Int => Int,g: squareID => Boolean = _ => true)(sqID: squareID): Option[squareID] = {
    val squareId = squareID(f(sqID.position),gridLayout.width)
    val square = gridLayout.squares.dropWhile(_._1!=squareId).headOption
    if (square.isDefined && square.get._2 == White && g(square.get._1)) Some(square.get._1) else None
  }

  def areSameRow(a:squareID,b:squareID): Boolean = a.rowID == b.rowID
  def squareAbove(sqID: squareID): Option[squareID] = squareInDirection(_ - gridLayout.width)(sqID)
  def squareBelow(sqID: squareID): Option[squareID] = squareInDirection(_ + gridLayout.width)(sqID)
  def squareLeft(sqID: squareID): Option[squareID] = squareInDirection(_ - 1, areSameRow(_,sqID))(sqID)
  def squareRight(sqID: squareID): Option[squareID] = squareInDirection(_ + 1, areSameRow(_,sqID))(sqID)

  def getSquare(f:squareID => Option[squareID])(sqID: squareID): squareID = f(sqID).get
}

object CrosswordClass {
  implicit val rw: RW[CrosswordClass] = macroRW
}

