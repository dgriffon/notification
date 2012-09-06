<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="workflow" uri="http://www.jahia.org/tags/workflow" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="utils" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>

<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="propertyDefinition" type="org.jahia.services.content.nodetypes.ExtendedPropertyDefinition"--%>
<%--@elvariable id="type" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<c:set var="notifiedList" value="${ui:getBindedComponent(currentNode, renderContext, 'j:bindedComponent')}"/>
<c:choose>
    <c:when test="${empty renderContext.user.properties['j:email']}">
        <fmt:message key="label.notification.missingMail">
            <fmt:param value="${notifiedList.displayableName}"/>
        </fmt:message>
    </c:when>
    <c:otherwise>
        <c:set var="actionLabel" value="subscribe"/>
        <c:if test="${!empty notifiedList.properties['subscribers']}">
            <c:forEach items="${notifiedList.properties['subscribers']}" var="subscriber">
                <c:if test="${subscriber.node.path eq renderContext.user.localPath}">
                    <c:set var="actionLabel" value="unsubscribe"/>
                </c:if>
            </c:forEach>
        </c:if>
        <template:tokenizedForm>
            <form action="<c:url value='${url.base}/${notifiedList.path}.subscribeAction.do'/>" method="post" id="newCommentForm">
                <input type="hidden" name="jcrRedirectTo" value="<c:url value='${url.base}${renderContext.mainResource.node.path}'/>"/>
                <input type="hidden" name="jcrNewNodeOutputFormat" value="html"/>
                <c:forEach items="${currentNode.properties['notifyOnNodeType']}" var="v"><c:set var="n" value="${v}${' '}${n}"/></c:forEach>
                <input type="hidden" name="notifyOnNodeType" value="${n}"/>
                <input type="submit" value="${actionLabel}"/>
            </form>
        </template:tokenizedForm>
    </c:otherwise>
</c:choose>
