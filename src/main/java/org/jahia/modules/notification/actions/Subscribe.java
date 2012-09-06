package org.jahia.modules.notification.actions;

import org.apache.commons.lang.ArrayUtils;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.apache.jackrabbit.value.WeakReferenceValue;
import org.apache.jackrabbit.webdav.jcr.property.ValuesProperty;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.bin.Render;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRNodeWrapperImpl;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.nodetypes.ValueImpl;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.jcr.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 7/26/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Subscribe extends Action {
    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        if (!resource.getNode().isNodeType("jmix:subscribers")) {
            resource.getNode().addMixin("jmix:subscribers");
            session.save();
        }
        List<Value> subscribers = new ArrayList<Value>();
        if (resource.getNode().hasProperty("subscribers")) {
             subscribers.addAll(Arrays.asList((Value[]) resource.getNode().getProperty("subscribers").getValues()));
        }
       Value currentValue = null;
       boolean hasUser = false;
        for  (Value subscriber : subscribers ) {
            Node n = ((JCRValueWrapper) subscriber).getNode();
            if (n.getPath().equals(renderContext.getUser().getLocalPath())) {
                hasUser = true;
                currentValue = subscriber;
            }
        }
        if (!hasUser) {
            subscribers.add(session.getValueFactory().createValue(session.getNode(renderContext.getUser().getLocalPath()).getIdentifier(), PropertyType.WEAKREFERENCE));
        } else {
            subscribers.remove(currentValue);
        }
        if (subscribers.size() > 0) {
            resource.getNode().setProperty("subscribers",  subscribers.toArray(new Value[subscribers.size()]));
        } else {
            resource.getNode().removeMixin("jmix:subscribers");
        }
        session.save();
        return new ActionResult(HttpServletResponse.SC_OK);
    }
}
