package XMLValidator

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{Schema, SchemaFactory, Validator}

import shared._

import scala.util.Try
import scala.xml.{Elem, Node, XML}

object XMLValidator {

  def get(xmlFile: String, xsdFile: String): CrosswordClass = {
    validateAndLoadXML(xmlFile, xsdFile) match {
      case Right(x) => x
      case Left(x) => CrosswordClass(Nil, CrosswordGrid(5, "BBBBBBBBBBBBBBBBBBBBBBBBB"))
    }
  }

  def validateAndLoadXML(xmlFile: String, xsdFile: String): Either[Throwable, CrosswordClass] = {
    val schemaLang = "http://www.w3.org/2001/XMLSchema"

    for {
      factory <- getFactory(schemaLang)
      schema <- getSchema(factory, xsdFile)
      validator <- getValidator(schema)
      _ <- validate(xmlFile, validator)
      elem <- loadXML(xmlFile)
      grid <- getGrid(elem)
      across <- getClues(elem,Across,grid)
      down <- getClues(elem,Down,grid)
    } yield CrosswordClass((across ++ down).toList,grid)
  }

  def getFactory(schemaLang: String): Either[Throwable, SchemaFactory] = Try(SchemaFactory.newInstance(schemaLang)).toEither

  def getSchema(factory: SchemaFactory, schemaFile: String): Either[Throwable, Schema] = Try(factory.newSchema(new StreamSource(schemaFile))).toEither

  def getValidator(schema: Schema): Either[Throwable, Validator] = Try(schema.newValidator()).toEither

  def validate(xml: String, validator: Validator): Either[Throwable, Unit] = Try(validator.validate(new StreamSource(xml))).toEither

  def loadXML(xml: String): Either[Throwable, Elem] = Try(XML.loadFile(xml)).toEither

  def getGrid(elem: Elem): Either[Throwable, CrosswordGrid] = {
    val gridLayout = (elem \ "grid" \ "colours").text
    val gridLength = (elem \ "grid" \ "length").text.toInt
    val gridWidth = (elem \ "grid" \ "width").text.toInt

    Try({
      assert(gridWidth * gridLength == gridLayout.length, "Invalid grid!")
      CrosswordGrid(gridWidth, gridLength, gridLayout)
    }).toEither
  }

  def getClues(elem: Elem, direction: Direction, grid: CrosswordGrid): Either[Throwable,Seq[Clue]] = {
    Right {
      val directionTag = if (direction == Across) "across" else "down"
      for (node <- elem \ "clues" \ directionTag \ "clue") yield makeClue(node, direction, grid)
    }
  }

  private def makeClue(node: Node, direction: Direction, gridIn: CrosswordGrid): Clue = {
    implicit val grid: CrosswordGrid = gridIn
    val number = node.attribute("number").get.head.text.toInt
    val solution = (node \ "solution").text.split(" ").toList
    val text = (node \ "text").text
    val parentClue = (node \ "parent").map(_.text.toClueID).headOption
    val linkedClues = (node \ "linked").map(_.text.split(',').toList.map(_.toClueID)).headOption

    if (parentClue.isDefined) Clue(number, direction, Solution(Nil), s"See ${parentClue.get.number}", linkedClues.getOrElse(Nil), gridIn)
    else Clue(number, direction, Solution(solution), text, linkedClues.getOrElse(Nil), gridIn)

  }



}
