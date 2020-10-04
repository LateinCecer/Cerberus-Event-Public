/*
 * Cerberus-Event is a simple event management library
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek;

import com.cerberustek.event.Event;
import com.cerberustek.event.EventListener;
import com.cerberustek.service.CerberusService;

public interface CerberusEvent extends CerberusService {

    /**
     * Adds an EventListener.
     *
     * The EventListeners class has to have an EventHandler
     * Annotation with names of the events which the Event-
     * Listener should catch.
     *
     * @param eventListener listener
     */
    void addListener(EventListener eventListener);

    /**
     * Removes an EventListener
     *
     * @param eventListener listener
     */
    void removeListener(EventListener eventListener);

    /**
     * Executes an event.
     *
     * This method will return true, if one of the EventListeners
     * catching the {@code event} returns true. It will still
     * execute all other listeners, but ignore their return
     * values.
     *
     * @param event the event
     * @return endresult
     */
    boolean executeFullEIT(Event event);

    /**
     * Executes an event.
     *
     * This method will return false, if one of the EventListeners
     * catching the {@code event} returns false. It will still execute
     * all other listeners, but ignore their return value.
     *
     * @param event the event
     * @return endresult
     */
    boolean executeFullEIF(Event event);

    /**
     * Executes an event.
     *
     * This method will return true, if at least 50 percent of the
     * listeners return true for the specified event. Otherwise it
     * will return false.
     *
     * @param event the event
     * @return endresult
     */
    boolean executeArithmetic(Event event);

    /**
     * Executes an event.
     *
     * This method will return true, if one of the EventListeners
     * catching the {@code event} returns true. Once an EventLister
     * returns true, all Listeners which have not since been called,
     * will not be called.
     *
     * @param event the event
     * @return endresult
     */
    boolean executeShortEIT(Event event);

    /**
     * Executes an event.
     *
     * This method will return false, if one of the EventListeners
     * catching the {@code event} returns false. Once an EventListener
     * returns true, all Listeners which have not since been called,
     * will not be called.
     *
     * @param event the event
     * @return endresult
     */
    boolean executeShortEIF(Event event);

    /**
     * Executes an event.
     *
     * This method will be asynchronous to the method caller
     *
     * @param event the event
     */
    void executeAsync(Event event);

    /**
     * Executes an event.
     *
     * The event calls will be distributed between different threads.
     *
     * @param event the event
     */
    void executeParallel(Event event);
}
