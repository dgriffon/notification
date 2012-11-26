package org.jahia.modules.notification.rules;

import org.apache.commons.lang.StringUtils;
import org.drools.spi.KnowledgeHelper;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.content.rules.AddedNodeFact;
import org.jahia.services.mail.MailService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.URLResolver;
import org.jahia.services.render.URLResolverFactory;
import org.jahia.services.seo.urlrewrite.UrlRewriteService;
import org.jahia.settings.SettingsBean;
import org.jahia.utils.LanguageCodeConverters;
import org.tuckey.web.filters.urlrewrite.RewrittenUrl;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: david
 * Date: 8/23/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class SendMailToSubscribersRuleService {

    private MailService mailService;
    private String mailTemplatePath;
    private String from;
    private URLResolverFactory urlResolverFactory;
    private String URLGenericPrefix;
    private UrlRewriteService urlRewriteService;

    public void sendMailToSubscribers(final AddedNodeFact nodeFact, KnowledgeHelper drools) throws RepositoryException,ScriptException {
        JCRNodeWrapper n = nodeFact.getNode();
        JCRNodeWrapper p = n.getParent();
        from = "".equals(from)? SettingsBean.getInstance().getMail_from():from;
        final Map<String,Object> bindings = new HashMap<String,Object>();
        bindings.put("node",n);
        // Resolve Node url

        JCRNodeWrapper displayableNode = JCRContentUtils.findDisplayableNode(n,new RenderContext(null,null,n.getUser()));

        String url = URLGenericPrefix.replace("##serverName##",n.getResolveSite().getServerName()) + "/##lang##" + displayableNode.getPath() + ".html";

        if (p.hasProperty("subscribers")) {
            for (Value v:p.getProperty("subscribers").getValues()) {
                JCRUserNode u = (JCRUserNode) n.getSession().getNodeByUUID(v.getString());
                bindings.put("currentUser",u);
                bindings.put("displayNode",displayableNode);
                bindings.put("urlDisplayNode",url);
                String to = u.getProperty("j:email").getString();
                Locale l = LanguageCodeConverters.languageCodeToLocale(u.getProperty("preferredLanguage").getString());
                mailService.sendMessageWithTemplate(mailTemplatePath,bindings,to,from,null,null,l,"notification");
            }
        }
    }

    public void setMailTemplatePath(String mailTemplatePath) {
        this.mailTemplatePath = mailTemplatePath;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setUrlResolverFactory(URLResolverFactory urlResolverFactory) {
        this.urlResolverFactory = urlResolverFactory;
    }

    public void setUrlRewriteService(UrlRewriteService urlRewriteService) {
        this.urlRewriteService = urlRewriteService;
    }

    public void setURLGenericPrefix(String URLGenericPrefix) {
        this.URLGenericPrefix = URLGenericPrefix;
    }
}
