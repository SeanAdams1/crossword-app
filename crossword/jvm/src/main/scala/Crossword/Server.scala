package crossword

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import shared._
import autowire._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

object Router extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer]{
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

// Extend Api so we can use list
object Server extends Api{
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val port = Properties.envOrElse("PORT", "8181").toInt
    val route = {
      get{
        pathSingleSlash{
          complete{
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              Page.skeleton.render
            )
          }
        } ~
          getFromResourceDirectory("")
      } ~
        post{
          path("ajax"/ Segments){ s =>
            entity(as[String]) { e =>
              complete {
                Router.route[Api](Server)(
                  autowire.Core.Request(s,upickle.default.read[Map[String,String]](e)
                  )
                )
              }
            }
          }
        }
    }
    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }

  def getCrossword() : CrosswordClass = {
    def gridStr: String = "WBWBWBWBWBWBWBWWWWWWWWWWBWWWWWWBWBWBWBWBWBWBWWWWWWBWWWWWWWWWBBWBWBWBWBBBWBBWWWWWWWBWWWWWWWWBBBWBBBWBWBWBWWWWWWBWWWBWWWWWWBWBWBWBBBWBBBWWWWWWWWBWWWWWWWBBWBBBWBWBWBWBBWWWWWWWWWBWWWWWWBWBWBWBWBWBWBWWWWWWBWWWWWWWWWWBWBWBWBWBWBWBW"

    implicit def grid: CrosswordGrid = CrosswordGrid(15, 15, gridStr)

    val clues = List(Clue(9, Across, Solution(List("BARBARIAN")), "Wild Boys, initially song and note getting stick at first", List("9A".toClueID),grid), Clue(10, Across, Solution(List("ON", "AIR")), "Playing song live", List("10A".toClueID), grid), Clue(11, Across, Solution(List("TASTY")), "Attractive model with a pen", List("11A".toClueID), grid), Clue(12, Across, Solution(List("NOTORIOUS")), "Rejection for Duran Duran song", List("12A".toClueID), grid), Clue(13, Across, Solution(List("FAN", "BELT")), "Admirer hit part of car", List("13A".toClueID), grid), Clue(14, Across, Solution(List()), "See 3", List("3D".toClueID, "14A".toClueID), grid), Clue(17, Across, Solution(List("DOGGO")), "Still in party? Slip away after girl's left", List("17A".toClueID), grid), Clue(19, Across, Solution(List("WAD")), "Hunk from poster after woman", List("19A".toClueID), grid), Clue(20, Across, Solution(List("RESET")), "Place again on the box", List("20A".toClueID), grid), Clue(21, Across, Solution(List("PRETEND")), "Put on pressure to split, film's captured", List("21A".toClueID), grid), Clue(22, Across, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID), grid), Clue(24, Across, Solution(List("ALLERGIST")), "Steal girl? Fancy one seeing someone highly sensitive?", List("24A".toClueID), grid), Clue(26, Across, Solution(List("MARIE","ROSE")), "Planet Earth features one topless star dressing", List("26A".toClueID, "25D".toClueID), grid), Clue(28, Across, Solution(List("DUNKS")), "Drops in drink revenue primarily from boozers", List("28A".toClueID), grid), Clue(29, Across, Solution(List("GOOD", "SENSE")), "Wit and idols seen playing around circuit", List("29A".toClueID), grid), Clue(1, Down, Solution(List("ABUT")), "Touch up member of band", List("1D".toClueID), grid), Clue(2, Down, Solution(List("ORISON")), "Group of stars penning intro to Save a Prayer", List("2D".toClueID), grid), Clue(3, Down, Solution(List("MARYLEBONE","STATION")), "Upset at teary Simon Le Bon interrupting where some tracks end", List("3D".toClueID, "14A".toClueID), grid), Clue(4, Down, Solution(List("LINNET")), "Bird left pub  before start of extra time", List("4D".toClueID), grid), Clue(5, Down, Solution(List("UNITISED")), "Sex appeal wearing kinky undies turned into a single piece", List("5D".toClueID), grid), Clue(6, Down, Solution(List("FOUR", "WEDDINGS", "AND", "A", "FUNERAL")), "Crazy Duran Duran fans go wild - fee for picture", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID), grid), Clue(7, Down, Solution(List("CANOPIES")), "One inside models for covers", List("7D".toClueID), grid), Clue(8, Down, Solution(List("BRAS")), "British like supporters", List("8D".toClueID), grid), Clue(13, Down, Solution(List("FED", "UP")), "Bored following half of band, ran off during record", List("13D".toClueID), grid), Clue(15, Down, Solution(List("AGRONOMIST")), "Mutation of organism to one studying land?", List("15D".toClueID), grid), Clue(16, Down, Solution(List("NATAL")), "Cheeky? Section of arena talked", List("16D".toClueID), grid), Clue(18, Down, Solution(List("GREMLINS")), "Film men wrestling with girls", List("18D".toClueID), grid), Clue(19, Down, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID), grid), Clue(22, Down, Solution(List("FATHOM")), "Flab reduced in work out", List("22D".toClueID), grid), Clue(23, Down, Solution(List("RARING")), "Artist and band full of enthusiasm", List("23D".toClueID), grid), Clue(24, Down, Solution(List()), "See 6", List("6D".toClueID, "19D".toClueID, "24D".toClueID, "22A".toClueID), grid), Clue(25, Down, Solution(List()), "See 26", List("26A".toClueID, "25D".toClueID), grid), Clue(27, Down, Solution(List("EXES")), "Old lovers need tablet - nooky when erect?", List("27D".toClueID), grid))

    CrosswordClass(clues, grid)
  }
 
}