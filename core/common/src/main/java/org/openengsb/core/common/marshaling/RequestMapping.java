/**
 * Licensed to the Austrian Association for Software Tool Integration (AASTI)
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. The AASTI licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.core.common.marshaling;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.openengsb.core.api.remote.MethodCall;

public class RequestMapping extends MethodCall {

    public RequestMapping() {
    }

    public RequestMapping(MethodCall call) {
        super(call.getMethodName(), call.getArgs(), call.getMetaData(), call.getCallId(), call.isAnswer(), call
            .getClasses());
    }

    /**
     * Converts the Args read by Jackson into the correct classes that have to be used for calling the method.
     */
    public void resetArgs() {
        if (getClasses().size() != getArgs().length) {
            throw new IllegalStateException("Classes and Args have to be the same");
        }
        ObjectMapper mapper = createObjectMapper();
        Iterator<String> iterator = getClasses().iterator();

        List<Object> values = new ArrayList<Object>();

        for (Object arg : getArgs()) {
            Class<?> class1;
            try {
                class1 = Class.forName(iterator.next());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            values.add(mapper.convertValue(arg, class1));
        }
        setArgs(values.toArray());
    }

    public String convertToMessage() throws IOException {
        ObjectMapper mapper = createObjectMapper();
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue(stringWriter, this);
        return stringWriter.toString();
    }

    public static RequestMapping createFromMessage(String message) throws IOException {
        ObjectMapper mapper = createObjectMapper();
        RequestMapping mapping = mapper.readValue(new StringReader(message), RequestMapping.class);
        return mapping;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector primaryIntrospector = new JacksonAnnotationIntrospector();
        AnnotationIntrospector secondaryIntrospector = new JaxbAnnotationIntrospector();
        AnnotationIntrospector introspector =
            new AnnotationIntrospector.Pair(primaryIntrospector, secondaryIntrospector);
        mapper.getDeserializationConfig().withAnnotationIntrospector(introspector);
        mapper.getSerializationConfig().withAnnotationIntrospector(introspector);
        return mapper;
    }
}