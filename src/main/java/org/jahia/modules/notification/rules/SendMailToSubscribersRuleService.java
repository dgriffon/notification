package org.jahia.modules.notification.rules;

import org.drools.spi.KnowledgeHelper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.content.rules.AddedNodeFact;
import org.jahia.services.mail.MailService;
import org.jahia.settings.SettingsBean;
import org.jahia.utils.LanguageCodeConverters;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.script.ScriptException;
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

    public void sendMailToSubscribers(final AddedNodeFact nodeFact, KnowledgeHelper drools) throws RepositoryException,ScriptException {
        JCRNodeWrapper n = nodeFact.getNode();
        JCRNodeWrapper p = n.getParent();
        from = "".equals(from)? SettingsBean.getInstance().getMail_from():from;
        final Map<String,Object> bindings = new HashMap<String,Object>();
        bindings.put("node",n);
        if (p.hasProperty("subscribers")) {
            for (Value v:p.getProperty("subscribers").getValues()) {
                JCRUserNode u = (JCRUserNode) n.getSession().getNodeByUUID(v.getString());
                bindings.put("currentUser",u);
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
}
