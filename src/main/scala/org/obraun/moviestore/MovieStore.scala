/**
 * Copyright (c) 2010 Oliver Braun
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of his contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHORS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.obraun.moviestore

import se.scalablesolutions.akka.actor.Actor

trait MovieStore extends Actor {

  import se.scalablesolutions.akka.stm.local.Ref
  protected val available: Ref[Map[Int,Movie]]
  protected val rent: Ref[Map[Int,Movie]]

  import se.scalablesolutions.akka.stm.local.atomic
  def addToStore(movie: Movie) {
    MovieStore.serial += 1
    atomic {
      available alter (_ + (MovieStore.serial -> movie))
    }
  }

  def addToStore(movies: Traversable[Movie]) {
    movies foreach (addToStore(_))
  }

  def rentMovieAge(age: Int, serial: Int): Option[Movie] =
    atomic {
      val movieOption = available.get.get(serial)
      movieOption match {
        case None => None
        case Some(movie @ Movie(_,r)) =>
          if (r <= age) {
            available alter (_ - serial)
            rent alter (_ + (serial -> movie))
            movieOption
          } else None
      }
    }

  def returnMovie(serial: Int) = {
    atomic {
      val movie = rent.get.apply(serial)
      rent alter (_ - serial)
      available alter (_ + (serial -> movie))
    }
  }

  def availableMoviesForAge(age: Int) =
    atomic {
      available.get.filter{
        case (_,Movie(_,r)) => r <= age
      }
    }

  def receive = rentalManagement

  protected def rentalManagement: Receive = {
    case AvailableList(age) =>
      val result = availableMoviesForAge(age)
      println("Calculated "+result)
      self.reply(
        ResultList(
          result.toList.map {
            case (s,movie) =>
              (s,movie.title,movie.filmrating)
          }
        )
      )
    case RentMovie(age, serial) =>
      val maybeMovie = rentMovieAge(age,serial)
      maybeMovie match {
        case None => self.reply(Error("Movie not available"))
        case Some(movie) => self.reply(SuccessfullyRent(serial))
      }
    case Return(serial) => returnMovie(serial)
  }

}
object MovieStore {
  private var serial = 0
}
// vim: set ts=2 sw=4 et:
