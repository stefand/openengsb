/**
 * Copyright 2010 OpenEngSB Division, Vienna University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openengsb.ui.web.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.openengsb.core.common.descriptor.AttributeDefinition;
import org.openengsb.ui.web.ArgumentModel;
import org.openengsb.ui.web.MethodUtil;
import org.openengsb.ui.web.model.MapModel;

@SuppressWarnings("serial")
public class BeanArgumentPanel extends Panel {

    private final Map<String, String> fieldViewIds = new HashMap<String, String>();

    public BeanArgumentPanel(String id, ArgumentModel argModel, Map<String, String> values) {
        super(id);
        add(new Label("index", "" + argModel.getIndex()));
        RepeatingView fields = new RepeatingView("fields");
        add(fields);

        List<AttributeDefinition> attributes = MethodUtil.buildAttributesList(argModel.getType());
        for (AttributeDefinition a : attributes) {
            String fieldViewId = fields.newChildId();
            fieldViewIds.put(a.getId(), fieldViewId);
            WebMarkupContainer row = new WebMarkupContainer(fieldViewId);
            fields.add(row);
            row.add(AttributeEditorUtil.createEditorField("row", new MapModel<String, String>(values, a.getId()), a));
        }
    }

    public String getFieldViewId(String fieldId) {
        return fieldViewIds.get(fieldId);
    }

}