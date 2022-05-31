package com.kong.newcoder.Service;

import com.kong.newcoder.dao.LoginTicketMapper;
import com.kong.newcoder.dao.UserMapper;
import com.kong.newcoder.entity.LoginTicket;
import com.kong.newcoder.entity.User;
import com.kong.newcoder.util.CommunityConstant;
import com.kong.newcoder.util.CommunityUtil;
import com.kong.newcoder.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.security.util.Password;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author shijiu
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    //激活码中要有域名和项目名+
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //注册业务层 注册方法
    public Map<String,Object> register(User user){
        Map<String,Object>map=new HashMap<>();
        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        //验证账号  是否已经存在于数据库中
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已经存在");
            return map;
        }
        //验证邮箱  是否已经存在于数据库中
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        //注册用户
        //加盐 为了确保密码安全性
        //生成随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //加密
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //默认普通用户
        user.setType(0);
        //默认没有激活
        user.setStatus(0);
        //设置激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //设置头像
        user.setHeaderurl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        //设置参数
        context.setVariable("email",user.getEmail());
        //生成连接
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        //生成内容
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }
    //激活方法
    public int activation(int userid,String code){
        User user = userMapper.selectById(userid);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userid,1);
            return ACTIVATION_SUCCESS;
        }
        else{
            return ACTIVATION_FAILURE;
        }

    }


    public User findUserById(int id){
       return userMapper.selectById(id);
    }

    //保存登录信息
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        HashMap<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //合法性验证账号
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        //未激活
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
        }
        //验证密码
        //加密
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();

        loginTicket.setUserid(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }
    //退出
    public void logOut(String ticket){
        loginTicketMapper.updataStatus(ticket,1);
    }

    //查询凭证
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新头像路径
    public int updateHeader(int userid,String headerurl){
        int i = userMapper.updateHeader(userid, headerurl);
        return i;
    }

    //修改密码
    public Map<String,Object> updatePassword(String username,String oldPassword,String newPassword,String confrimPassword){
        HashMap<String, Object> map = new HashMap<>();
        //首先根据username查出此用户
        User user = userMapper.selectByName(username);
        //判断空值
        if(StringUtils.isBlank(oldPassword)){
            map.put("passwordMsg","原始密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(newPassword)){
            map.put("passwordMsg","新密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(confrimPassword)){
            map.put("passwordMsg","新密码不能为空");
            return map;
        }
        //判断原始密码是否正确

        //加密
        //解密

        oldPassword=CommunityUtil.md5(oldPassword+user.getSalt());


        if(!oldPassword.equals(user.getPassword())){
            map.put("passwordMsg","原密码不正确");
            return map;
        }
        //判断两次输入新密码是否相同
        if(!newPassword.equals(confrimPassword)){
            map.put("passwordMsg","两次输入新密码不一致");
            return map;
        }

        //开始修改
        //改密码要加盐值 重新登陆重新来 不急 先去吃个饭 2022年5月29日17:46:05
        //改不动了 好累 2022年5月29日20:02:35
        String salt = CommunityUtil.generateUUID().substring(0,5);
        //加密
        oldPassword=CommunityUtil.md5(oldPassword+salt);

        int i = userMapper.updatePassword(user.getId(), oldPassword);
        //修改完毕 返回map
        return map;
    }
}
