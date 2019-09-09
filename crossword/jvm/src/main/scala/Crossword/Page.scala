package crossword

import scalatags.Text.all._

object Page{
  val boot =
    "client.CrosswordApp().main()"
  val skeleton =
    html(
      head(
        script(src:="/crossword-app-fastopt.js"),
        link(
          rel:="stylesheet",
          href:="https://cdnjs.cloudflare.com/ajax/libs/pure/0.5.0/pure-min.css"
        )
      ),
      body(
        onload:=boot,
        fontSize:= "100%",
        div(id:="contents")
      )
    )
}