package com.buct.blog.controller;
import com.buct.blog.domain.Category;
import com.buct.blog.domain.User;
import com.buct.blog.service.CategoryService;
import com.buct.blog.service.FileService;
import com.buct.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;


/**
 * @author 高谦
 * 实现后台管理系统的相关功能
 */
@Controller
public class BackManageController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    FileService fileService;

    /**
     * 后台管理页面数据准备接口
     * @param request 用于获取Session来判断是否登录
     * @return 返回模板页面
     */
    @GetMapping("/manage")
    public String manage(HttpServletRequest request,Map<String,Object> map){
        HttpSession session=request.getSession();
        if(session.getAttribute("user")==null){
            return "redirect:/manage/login";
        }
        User user=userService.getDefaultUser();
        map.put("user",user);
        return "backmanage/index";
    }

    /**
     * 用于注销登录
     * @param request 用于获取 session
     * @return 返回登录页面
     */
    @GetMapping("/manage/logout")
    public String logout(HttpServletRequest request){
        HttpSession session=request.getSession();
        session.setAttribute("user",null);
        return "redirect:/manage/login";
    }

    /**
     * 后台文章管理界面
     * @param request 获取用户的信息
     * @param map 前台数据传递
     * @return 返回文章管理页面
     */
    @GetMapping("/manage/article")
    public String manageArticle(HttpServletRequest request,Map<String,Object> map){
        User user=userService.getDefaultUser();
        map.put("user",user);
        return "backmanage/articleManage";
    }

    /**
     * 写文章索引页面
     * @param request 获取session
     * @param map 用于前台参数传递
     * @return 返回写文章的页面
     */
    @GetMapping("/manage/write")
    public String write(HttpServletRequest request,Map<String,Object>map){
        User user=userService.getDefaultUser();
        map.put("user",user);
        ArrayList<Category> categories=(ArrayList<Category>) categoryService.getCategoriesLimits(1000);
        map.put("categories",categories);
        return "backmanage/write";
    }

    /**
     * 返回用户资料修改界面
     * @param request 用户获取用户信息
     * @param map 用于前台参数传递
     * @return 返回用户修改信息的界面
     */
    @GetMapping("/manage/profile")
    public String profile(HttpServletRequest request,Map<String,Object>map){
        User user=userService.getDefaultUser();
        map.put("user",user);
        return "backmanage/profile";
    }

    /**
     * 修改用户的头像
     * @param file 上传的文件
     * @return 返回文件存储在服务器目录的名称
     */
    @PostMapping("/manage/chanagePic")
    @ResponseBody
    public String changePic(@RequestParam("file") MultipartFile file){
        try{
            String url="/upload/"+fileService.storeFile(file);
            userService.changePic(url);
            return  url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改用户基本信息
     * @param mail 邮箱
     * @param phone 电话
     * @param company 公司
     * @param position 位置
     * @return 返回修改后的信息
     */
    @PostMapping("/manage/updateBasic")
    @ResponseBody
    public User updateBasic(@RequestParam("mail") String mail,
                              @RequestParam("phone") String phone,
                              @RequestParam("company") String company,
                              @RequestParam("position") String position){
        User user=new User();
        user.setCompany(company);
        user.setEmail(mail);
        user.setPhone(phone);
        user.setPosition(position);
        userService.updateBasic(user);
        return user;
    }

    /**
     * 修改用户的其它信息
     * @param csdn csdn 博客地址
     * @param github github 地址
     * @return 返回修改之后的信息
     */
    @PostMapping("/manage/updateOther")
    @ResponseBody
    public User updateOther(@RequestParam("csdn") String csdn,
                            @RequestParam("github") String github){
        User user=new User();
        user.setGithub(github);
        user.setCsdn(csdn);
        userService.updateOther(user);
        return user;
    }
}