package CrosswordParser

import XMLValidator.XMLValidator
import scala.util.Try
import scala.xml._

//
//object CrosswordParser{
//  val schema = "C:\\Users\\Sean Adams\\Desktop\\Crossword.Crossword.xsd"
//
//  // Load the crossword from the xml stored in the file
//  def loadCrossword(file:String): Either[Throwable,Crossword.Crossword] = {
//    for {
//      _ <- XMLValidator.validateXML(file, schema)
//      xml <- XMLValidator.loadXML(file)
//
//    }  yield Crossword.Crossword((across ++ down).toList,grid.head.get)
//  }
