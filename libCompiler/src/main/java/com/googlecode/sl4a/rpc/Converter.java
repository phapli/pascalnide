/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.sl4a.rpc;

/**
 * A converter can take a String and turn it into an instance of operator T (the operator parameter to the
 * converter).
 *
 * @author igor.v.karp@gmail.com (Igor Karp)
 */
public interface Converter<T> {

    /**
     * Convert a string into operator T.
     */
    T convert(String value);
}
