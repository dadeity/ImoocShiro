package com.imooc.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.*;

/**
 * Created by  OSCAR on 2018/12/19
 * 实现自定义realm
 */
public class CustomRealm extends AuthorizingRealm {

    Map<String,String> userMap = new HashMap<String, String>(16);//起始容量：16
    {
        userMap.put("Mark","b798a144446d18ce0deaa86ff469586c");//123456加密后的密码

        super.setName("customRealm");
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456","OSCAR");//密码加盐，一般用随机数，此处用OSCAR
        System.out.println(md5Hash.toString());
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        //自定义方法模拟从数据库或者缓存中获取角色数据
        Set<String> roles = getRolesByUsername(username);
        Set<String> permissions = getPermissionsByUsername(username);
        //返回角色和权限信息给调用者
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        info.setRoles(roles);

        return info;
    }

    private Set<String> getPermissionsByUsername(String username) {
        Set<String> sets = new HashSet<String>();
        sets.add("user:delete");
        sets.add("user:update");
        sets.add("user:add");
        return sets;
    }

    private Set<String> getRolesByUsername(String username) {
        Set<String> sets = new HashSet<String>();
        sets.add("admin");
        sets.add("user");
        return  sets;
    }

    /**
     * 认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //1从主体传过来的认证信息中，获得用户名
        String username = (String) authenticationToken.getPrincipal();

        //通过用户名到数据库中获取凭证
        String password = getPasswordByUserName(username);
        //如果密码不存在
        if (password==null){
            return null;
        }
        //如果密码存在，创建返回对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo
                ("Mark",password,"customRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("OSCAR"));
        return authenticationInfo;
    }

    /**
     * 模拟数据库查询凭证
     * @param username
     * @return
     */
    private String getPasswordByUserName(String username) {
         return userMap.get(username);
    }

}
