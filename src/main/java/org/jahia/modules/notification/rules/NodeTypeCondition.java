package org.jahia.modules.notification.rules;

import org.drools.spi.KnowledgeHelper;
import org.jahia.services.content.rules.AddedNodeFact;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 9/6/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeTypeCondition {

    public static boolean parentHasNodeType (AddedNodeFact n,String t) throws RepositoryException {
        try {
            if (n.getTypes().contains(t)) {
                return true;
            } else {
                return parentHasNodeType(n.getParent(), t);
            }
        } catch (RepositoryException e) {
            return false;
        }
    }

    public boolean childHasNodeType (AddedNodeFact n,String t) throws RepositoryException {
        Query q = n.getNode().getSession().getWorkspace().getQueryManager().createQuery(
                "select * from [" + t +"] as n where isDecenscendantNode (n," + n.getPath() + ")",
                Query.JCR_SQL2);
        if (q.execute().getNodes().hasNext()) {
            return true;
        };
        return false;

    }
}
