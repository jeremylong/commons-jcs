package org.apache.commons.jcs.yajcache.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.jcs.yajcache.lang.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection related Utilities.
 */
@CopyRightApache
public enum CollectionUtils {
    inst;

    // Convenient methods originated from:
    // http://www.artima.com/forums/flat.jsp?forum=106&thread=79394

    public <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K,V>();
    }
    public <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap(int initialCapacity)
    {
        return new ConcurrentHashMap<K,V>(initialCapacity);
    }
    public <K,V> ConcurrentHashMap<K,V> newConcurrentHashMap(
            int initialCapacity, float loadFactor, int concurrencyLevel)
    {
        return new ConcurrentHashMap<K,V>(initialCapacity);
    }
}
