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
package org.openengsb.core.security;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.concurrent.SubjectAwareExecutorService;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.openengsb.core.api.security.Credentials;
import org.openengsb.core.common.util.ContextAwareCallable;
import org.openengsb.core.common.util.ContextAwareRunnable;
import org.openengsb.core.common.util.ThreadLocalUtil;
import org.openengsb.core.security.internal.OpenEngSBAuthenticationToken;
import org.openengsb.core.security.internal.RootSubjectHolder;

/**
 * provides util-methods for security purposes
 */
public final class SecurityContext {

    /**
     * Wraps the given argument to a shiro- {@link org.apache.shiro.authc.AuthenticationToken} to authenticate.
     */
    public static void login(String username, Credentials credentials) throws AuthenticationException {
        OpenEngSBAuthenticationToken token = new OpenEngSBAuthenticationToken(username, credentials);
        SecurityUtils.getSubject().login(token);
    }

    /**
     * Logout the current subject. The ThreadContext is emptied.
     */
    public static void logout() {
        Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            return;
        }
        subject.logout();
    }

    /**
     * returns the authenticated user or null if no authentication was found.
     */
    public static Object getAuthenticatedPrincipal() {
        Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            return null;
        }
        return subject.getPrincipal();
    }

    /**
     * returns a list of principals of the currently authenticated subject.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getAllAuthenticatedPrincipals() {
        Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            return null;
        }
        return subject.getPrincipals().asList();
    }

    /**
     * Executes the given task with root-permissions. Use with care.
     * 
     * @throws ExecutionException if an exception occurs during the execution of the task
     */
    public static <ReturnType> ReturnType executeWithSystemPermissions(Callable<ReturnType> task)
        throws ExecutionException {
        ContextAwareCallable<ReturnType> contextAwareCallable = new ContextAwareCallable<ReturnType>(task);
        return RootSubjectHolder.getRootSubject().execute(contextAwareCallable);
    }

    /**
     * wraps an existing ExecutorService to handle context- and security-related threadlocal variables
     */
    public static void executeWithSystemPermissions(Runnable task) {
        ContextAwareRunnable contextAwaretask = new ContextAwareRunnable(task);
        RootSubjectHolder.getRootSubject().execute(contextAwaretask);
    }

    /**
     * Wrap the given executor so that it takes authentication-information and context are inherited to tasks when they
     * are submitted.
     */
    public static ExecutorService getSecurityContextAwareExecutor(ExecutorService original) {
        SubjectAwareExecutorService subjectAwareExecutor = new SubjectAwareExecutorService(original);
        return ThreadLocalUtil.contextAwareExecutor(subjectAwareExecutor);
    }

    private SecurityContext() {
    }
}
