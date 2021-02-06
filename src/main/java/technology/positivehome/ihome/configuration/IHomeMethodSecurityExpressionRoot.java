package technology.positivehome.ihome.configuration;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import technology.positivehome.ihome.security.service.DenyAllIotSecurityPermissionEvaluator;
import technology.positivehome.ihome.security.service.IHomeSecurityPermissionEvaluator;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;
import technology.positivehome.ihome.security.util.IHomeApiTargetType;

import java.io.Serializable;

/**
 * Created by maxim on 2/6/21.
 **/
public class IHomeMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private final MethodInvocation invocation;
    private IHomeSecurityPermissionEvaluator iHomeSecurityPermissionEvaluator = new DenyAllIotSecurityPermissionEvaluator();


    public IHomeMethodSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        super(authentication);
        this.invocation = invocation;
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this"
     * property of the {@code JoinPoint} representing the method invocation which is being
     * protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }

    public boolean hasAccessPermission(String targetType, String accessType, Serializable... ids) {
        return iHomeSecurityPermissionEvaluator.hasPermission(authentication, IHomeApiTargetType.of(targetType), IHomeApiTargetAccessType.of(accessType), ids);
    }

    public boolean hasAccessPermission(String accessType, Object targetDomainObject) {
        return iHomeSecurityPermissionEvaluator.hasPermission(authentication, targetDomainObject, IHomeApiTargetAccessType.of(accessType));
    }

    public void setIHomeSecurityPermissionEvaluator(IHomeSecurityPermissionEvaluator iotSecurityPermissionEvaluator) {
        this.iHomeSecurityPermissionEvaluator = iotSecurityPermissionEvaluator;
    }
}
