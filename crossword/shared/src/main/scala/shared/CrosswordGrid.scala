package shared

//CrosswordGrid
case class CrosswordGrid(width: Int, height: Int, cellString: String) {
  assert(width >= 5, "Width must be at least 5")
  assert(height >= 5, "Height must be at least 5")
  require(cellString.length == width * height, "Incorrect number of cells for the grid size")

  override def toString: String = cellString
  val WHITE = 'W'
  val gridSize: Int = width * height

  def isAcross(ls :List[squareID]): Boolean = ls.head.rowID == ls(1).rowID

  def getLinkedSquares(rowLists: List[List[(Char,squareID)]]): List[squareList] =
    {for {i <- rowLists}  yield  splitRow(i)}.flatten.filter(_.lengthCompare(1) > 0)

  def splitRow(row: List[(Char,squareID)]): List[squareList] = {
   val res =  row.foldRight(Nil:squareList, Nil: List[squareList])({ case ((c, s), (ls1, ls)) =>
      if (c == WHITE) (s :: ls1, ls) else (Nil, ls1 :: ls) })
    res._1 :: res._2
  }

  def gridList(s:String,width:Int):List[List[(Char,squareID)]] = s.zipWithIndex.map({case (a:Char,b:Int) => (a,squareID(b + 1,width))}).grouped(width).toList.map(_.toList)

  def transposedGridList(s:String,width:Int):List[List[(Char,squareID)]] = s.zipWithIndex.map({case (a:Char,b:Int) => (a,squareID(b + 1,width),b)}).groupBy(_._3%width).values.map(_.map(a => (a._1,a._2)).toList).toList

  def allLinks(grid:String,width:Int): List[squareList] =  getLinkedSquares(gridList(grid,width)) ++ getLinkedSquares(transposedGridList(grid,width))

  def getDirection(sl: squareList): Direction = if (isAcross(sl)) Across else Down

  def charStringToBigInt(s:String):BigInt = s.foldRight(BigInt("0"),BigInt("1"))((a,b) => (b._2 * (a.toInt - 64) + b._1,26 * b._2))._1

  def matchesDirection(x:squareID,current:squareID,direction:Direction): Boolean = {
    direction match {
      case Across => x.rowID == current.rowID
      case Down => x.columnID == current.columnID
    }
  }

  def isEarlierSquare(x:squareID,current:squareID,direction:Direction):Boolean = {
    direction match {
      case Across => BigInt(x.columnID) <= BigInt(current.columnID)
      case Down => charStringToBigInt(x.rowID) <= charStringToBigInt(current.rowID)
    }
  }




  val clues: List[(squareList,Direction)] = allLinks(cellString,width).sortWith(_<_).map(a => (a,getDirection(a)))


  def addClueNumbers(ls: List[(squareList,Direction)]): List[(ClueID,squareList)] = {
    def loop(ls:List[(squareList,Direction)], clueNumber: Int, prev: squareID, outList: List[(ClueID,squareList)]):List[(ClueID,squareList)]
    = { ls match {
      case Nil => outList
      case _ => val squares = ls.head._1
        if (prev < squares.head) loop(ls.tail, clueNumber + 1, squares.head, (ClueID(clueNumber + 1,ls.head._2),squares) :: outList)
        else loop(ls.tail,clueNumber, prev, (ClueID(clueNumber,ls.head._2),squares) :: outList)
    }

    }

    loop(ls,0,squareID(-1,width),Nil:List[(ClueID,squareList)])
  }


  val gridClues : Map[ClueID,squareList] = addClueNumbers(clues).toMap

  def getSquares(id: ClueID): squareList = gridClues.getOrElse(id,Nil)
  def clueList: List[ClueID] = gridClues.keys.toList.sortWith(_<_)
  private val numberMap = clueList.map(a => (getSquares(a).head,a.number)).distinct.toMap.withDefaultValue(0)

  val squares: List[(squareID,CellColour,Int)] = cellString.map(cellColourMap).zipWithIndex.map(
    {
      case (a, b) =>
        val id = squareID(b + 1, width)
        (id, a, numberMap(id))
    }
  ).toList

}


//companion object
object CrosswordGrid {
  //If we want a square grid then we can just pass in one length
  def apply(side: Int, cellString: String) = new CrosswordGrid(side, side, cellString)
}

