package fastparse.parsers

import fastparse.Parser
import fastparse.core.Result.{Failure, Success}
import fastparse.core.{ParseCtx, Result}

/**
 * Created by haoyi on 5/10/15.
 */
object Transformers {
  /**
   * Applies a transformation [[f]] to the result of [[p]]
   */
  case class Mapper[T, V](p: Parser[T], f: T => V) extends Parser[V]{
    def parseRec(cfg: ParseCtx, index: Int) = {
      p.parseRec(cfg, index) match{
        case s: Success.Mutable[T] => success(s, f(s.value), s.index, s.cut)
        case f: Failure.Mutable => failMore(f, index, cfg.trace)
      }
    }
    override def toString = p.toString
  }

  case class FlatMapped[T, V](p1: fastparse.Parser[T],
                              func: T => fastparse.Parser[V])
    extends fastparse.Parser[V] {
    def parseRec(cfg: ParseCtx, index: Int): Result[V] = {
      p1.parseRec(cfg, index) match{
        case f: Result.Failure.Mutable => failMore(f, index, cfg.trace, false)
        case s: Result.Success.Mutable[T] => func(s.value).parseRec(cfg, s.index)
      }
    }
  }
}
