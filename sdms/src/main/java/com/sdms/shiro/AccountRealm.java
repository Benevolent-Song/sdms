package com.sdms.shiro;

import com.sdms.entity.User;
import com.sdms.service.UserService;
import com.sdms.util.JwtUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    //根据token判断此Authenticator是否使用该realm
    //必须重写不然shiro会报错
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如@RequiresRoles,@RequiresPermissions之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("授权~~~~~");
        String token=principals.toString();
        String username= JwtUtils.getUsername(token);
        User user=userService.getUser(username);
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        //查询数据库来获取用户的角色
        info.addRole(user.getRole());
        //查询数据库来获取用户的权限
        info.addStringPermission(user.getPerm());
        return info;
    }


    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可，在需要用户认证和鉴权的时候才会调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证~~~~~~~");
        String jwt= (String) token.getCredentials();
        String username= null;
        //decode时候出错，可能是token的长度和规定好的不一样了
        try {
            username= JwtUtils.getUsername(jwt);
        }catch (Exception e){
            throw new AuthenticationException("token非法，不是规范的token，可能被篡改了，或者过期了");
        }
        if (!JwtUtils.verify(jwt)||username==null){
            throw new AuthenticationException("token认证失效，token错误或者过期，重新登陆");
        }
        User user=userService.getUser(username);
        if (user==null){
            throw new AuthenticationException("该用户不存在");
        }

        return new SimpleAuthenticationInfo(jwt,jwt,"MyRealm");
    }
}

