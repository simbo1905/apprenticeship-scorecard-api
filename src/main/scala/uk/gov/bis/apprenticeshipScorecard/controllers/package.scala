package uk.gov.bis.apprenticeshipScorecard

import atto.ParseResult.Done
import com.wellfactored.restless.QueryAST.Path
import com.wellfactored.restless.QueryParser
import play.api.libs.json._
import play.api.mvc.{Request, Result}
import uk.gov.bis.apprenticeshipScorecard.controllers.Selector.Params

import scala.util.Try
import scala.util.control.NonFatal

package object controllers {

  import play.api.mvc.Results._

  def jsonAction[T: Reads, W: Writes](json: JsValue)(body: T => W): Result = {
    json.validate[T].fold(
      invalid => BadRequest("bad parameter format"),
      t => Ok(Json.toJson(body(t)))
    )
  }

  def withCollectionParams(f: Params => Result)(implicit request: Request[String]): Result =
    extractParams.fold(result => result, params => f(params))

  def extractParams(implicit request: Request[String]): Either[Result, Params] = {
    request.method match {
      case "POST" => extractFromJson
      case "GET" => extractFromQueryParams(request.queryString)
      case m => Left(BadRequest(s"Invalid html method type: $m"))
    }
  }

  /**
    * TODO: handle errors in the query parser
    * TODO: handle errors in the fields parser
    */
  def extractFromQueryParams(params: Map[String, Seq[String]]): Either[Result, Params] = {

    import atto._
    import Atto._

    val pageNumber = params.get("page_number").flatMap(_.headOption.map(_.toInt))
    val pageSize = params.get("page_size").flatMap(_.headOption.map(_.toInt))
    val maxResults = params.get("max_results").flatMap(_.headOption.map(_.toInt))
    val query = params.get("query").flatMap {
      _.headOption.map { qs =>
        QueryParser.query.parseOnly(qs) match {
          case Done(_, q) => Right(q)
          case _ => Left("failed to parse query string")
        }
      }
    }
    val fields = params.get("fields").flatMap {
      _.headOption.flatMap { s =>
        Try(Json.parse(s)).toOption.flatMap { jv =>
          jv match {
            case JsArray(vs) => Some(vs.toList.flatMap(_.validate[String].asOpt.map(f => Path(f.split('.').toList))))
            case _ => None
          }
        }
      }
    }

    Right(Params(pageNumber, pageSize, maxResults, query.map(_.right.get), fields))
  }

  def extractFromJson(implicit request: Request[String]): Either[Result, Params] = {
    try {
      Json.parse(request.body).validate[Params].fold(
        errs => Left(BadRequest(errs.toString())),
        params => Right(params)
      )
    } catch {
      case NonFatal(e) => Left(BadRequest(e.getMessage))
    }
  }
}