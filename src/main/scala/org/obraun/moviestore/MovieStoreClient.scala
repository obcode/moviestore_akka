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

import akka.actor.Actor.remote._
import akka.actor.Actor._

class MovieStoreClient(userID: Int) {

  val movieStore =
    actorFor(
      "moviestore:service",
      "localhost",
      9999
    )

  def login = movieStore ! Login(userID)

  def logout = movieStore ! Logout(userID)

  def rent(serial: Int) = {
    val result = movieStore !! Rent(userID, serial)
    result match {
      case Some(SuccessfullyRent(_)) =>
        println("Successfully rent movie #"+serial)
      case Some(Error(msg)) =>
        println("error: "+msg)
      case msg =>
        println("error: something unexpected happened "
                +msg)
    }
  }

  def show = {
    val result = movieStore !! ShowAvailable(userID)
    result match {
      case Some(ResultList(movies)) =>
        for((serial,title,filmrating) <- movies)
          println(serial+" "+title+" ("+filmrating+")")
      case msg =>
        println("error while receiving movielist: "+msg)
    }
  }

  def returnM(serial: Int) = movieStore ! Return(serial)

}
// vim: set ts=2 sw=4 et:
