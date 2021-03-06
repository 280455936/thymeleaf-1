/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.expression.ExpressionObjects;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public abstract class AbstractProcessingContext implements IProcessingContext {

    private final IEngineConfiguration configuration;
    private final IVariablesMap variablesMap;
    private final boolean web;
    private IExpressionObjects expressionObjects;



    protected AbstractProcessingContext(
            final IEngineConfiguration configuration,
            final Locale locale, final Map<String, Object> variables) {

        super();

        Validate.notNull(configuration, "Engine Configuration cannot be null");

        this.configuration = configuration;
        this.variablesMap = new VariablesMap(locale, variables);
        this.web = false;

    }


    protected AbstractProcessingContext(
            final IEngineConfiguration configuration,
            final IContext context) {

        super();

        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(context, "Context cannot be null");

        this.configuration = configuration;
        this.variablesMap = buildVariablesMap(context);
        this.web = this.variablesMap instanceof IWebVariablesMap;

    }




    public final IEngineConfiguration getConfiguration() {
        return this.configuration;
    }


    public IExpressionObjects getExpressionObjects() {
        if (this.expressionObjects == null) {
            this.expressionObjects = new ExpressionObjects(this, this.configuration.getExpressionObjectFactory());
        }
        return this.expressionObjects;
    }

    public final Locale getLocale() {
        return this.variablesMap.getLocale();
    }

    public final boolean isWeb() {
        return this.web;
    }

    public final IVariablesMap getVariables() {
        return this.variablesMap;
    }





    private static IVariablesMap buildVariablesMap(final IContext context) {

        final Set<String> variableNames = context.getVariableNames();
        if (variableNames == null || variableNames.isEmpty()) {
            if (context instanceof IWebContext) {
                final IWebContext webContext = (IWebContext)context;
                return new WebVariablesMap(webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), webContext.getLocale(), Collections.EMPTY_MAP);
            }
            return new VariablesMap(context.getLocale(), Collections.EMPTY_MAP);
        }

        final Map<String,Object> variables = new LinkedHashMap<String, Object>(variableNames.size() + 1, 1.0f);
        for (final String variableName : variableNames) {
            variables.put(variableName, context.getVariable(variableName));
        }
        if (context instanceof IWebContext) {
            final IWebContext webContext = (IWebContext)context;
            return new WebVariablesMap(webContext.getRequest(), webContext.getResponse(), webContext.getServletContext(), webContext.getLocale(), variables);
        }

        return new VariablesMap(context.getLocale(), variables);

    }


}
