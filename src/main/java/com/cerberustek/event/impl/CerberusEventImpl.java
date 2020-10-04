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

package com.cerberustek.event.impl;

import com.cerberustek.event.EventHandler;
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.event.Event;
import com.cerberustek.event.EventListener;
import com.cerberustek.service.CerberusService;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class CerberusEventImpl implements CerberusEvent {

    private final HashSet<Thread> threads = new HashSet<>();
    private final HashMap<Class<? extends Event>, HashSet<EventListener>> listeners = new HashMap<>();

    @Override
    public void addListener(EventListener eventListener) {
        EventHandler eventHandler = eventListener.getClass().getAnnotation(EventHandler.class);

        for (Class<? extends Event> clazz : eventHandler.events())
            put(clazz, eventListener);
    }

    private void put(Class<? extends Event> eventClass, EventListener listener) {
        HashSet<EventListener> el = listeners.computeIfAbsent(eventClass, k -> new HashSet<>());
        el.add(listener);
    }

    @Override
    public void removeListener(EventListener eventListener) {
        EventHandler eventHandler = eventListener.getClass().getAnnotation(EventHandler.class);

        for (Class<? extends Event> clazz : eventHandler.events())
            remove(clazz, eventListener);
    }

    private void remove(Class<? extends Event> eventClass, EventListener eventListener) {
        HashSet<EventListener> el = listeners.get(eventClass);
        if (el != null) {
            el.remove(eventListener);

            if (el.isEmpty())
                listeners.remove(eventClass);
        }
    }

    @Override
    public boolean executeFullEIT(Event event) {
        HashSet<EventListener> el = listeners.get(event.getClass());
        boolean flag = false;
        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                if (listener.onEvent(event))
                    flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean executeFullEIF(Event event) {
        HashSet<EventListener> el = listeners.get(event.getClass());
        boolean flag = true;
        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                if (!listener.onEvent(event))
                    flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean executeArithmetic(Event event) {
        HashSet<EventListener> el = listeners.get(event.getClass());
        int positive = 0;
        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                if (listener.onEvent(event))
                    positive++;
            }
            return positive >= el.size() / 2;
        }
        return true;
    }

    @Override
    public boolean executeShortEIT(Event event) {
        HashSet<EventListener> el = listeners.get(event.getClass());
        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                if (listener.onEvent(event))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean executeShortEIF(Event event) {
        HashSet<EventListener> el = listeners.get(event.getClass());
        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                if (!listener.onEvent(event))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void executeAsync(final Event event) {
        updateThreads();
        final HashSet<EventListener> el = listeners.get(event.getClass());

        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (EventListener listener : copy)
                        listener.onEvent(event);
                    threads.remove(this);
                }
            };

            threads.add(thread);
            thread.start();
        }
    }

    @Override
    public void executeParallel(final Event event) {
        updateThreads();
        HashSet<EventListener> el = listeners.get(event.getClass());

        if (el != null) {
            HashSet<EventListener> copy = new HashSet<>(el);

            for (EventListener listener : copy) {
                Thread thread = new Thread(() -> listener.onEvent(event));
                threads.add(thread);
                thread.start();
            }
        }
    }

    private void updateThreads() {
        HashSet<Thread> toRemove = new HashSet<>();
        for (Thread t : threads) {
            if (!t.isAlive())
                toRemove.add(t);
        }
        threads.removeAll(toRemove);
    }

    @Override
    public void start() {
        CerberusRegistry.getInstance().info("Started event service!");
    }

    @Override
    public void stop() {
        CerberusRegistry.getInstance().info("Clearing event listeners...");
        listeners.values().forEach(HashSet::clear);
        listeners.clear();
        CerberusRegistry.getInstance().fine("... Done!");
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return CerberusEvent.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return threads;
    }
}
